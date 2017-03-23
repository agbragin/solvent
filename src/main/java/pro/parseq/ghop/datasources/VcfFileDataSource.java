/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package pro.parseq.ghop.datasources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.attributes.AttributeRange;
import pro.parseq.ghop.datasources.attributes.AttributeType;
import pro.parseq.ghop.datasources.attributes.DoubleAttribute.DoubleAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.InclusionType;
import pro.parseq.ghop.datasources.attributes.SetAttribute.SetAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.StringAttribute.StringAttributeBuilder;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.entities.VariantBand;
import pro.parseq.ghop.utils.AttributeUtils;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.IdGenerationUtils;
import pro.parseq.ghop.utils.PredicateUtils;
import pro.parseq.vcf.VcfExplorer;
import pro.parseq.vcf.fields.Filter;
import pro.parseq.vcf.fields.Format;
import pro.parseq.vcf.fields.Information;
import pro.parseq.vcf.fields.types.FormatFieldType;
import pro.parseq.vcf.fields.types.InfoFieldType;
import pro.parseq.vcf.types.Variant;
import pro.parseq.vcf.types.VcfFile;
import pro.parseq.vcf.utils.FaultTolerance;
import pro.parseq.vcf.utils.InputStreamVcfReader;
import pro.parseq.vcf.utils.VcfGrammar;
import pro.parseq.vcf.utils.VcfParserImpl;

/**
 * VCF file as data source for genome browser.
 * 
 * This is in memory implementation suitable for small VCF files that fits RAM.
 * 
 * @author abragin
 *
 */
public final class VcfFileDataSource extends AbstractDataSource<VariantBand> {

	private static final Logger logger = LoggerFactory.getLogger(VcfFileDataSource.class);

	/**
	 * Sample-specific properties are flattened by concatenation with sample names with this delimeter
	 */
	final static String SAMPLE_ATTRIBUTE_DELIMETER = ":";

	private final String referenceGenomeName;
	private final VcfExplorer vcfExplorer;
	private final Track track;

	// Using ordered set to preserve attributes ordering
	private final Set<Attribute<?>> attributes;

	public VcfFileDataSource(Track track, File vcfFile,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName)
					throws FileNotFoundException {
		this(track, new FileInputStream(vcfFile), comparator, referenceGenomeName);
	}

	public VcfFileDataSource(Track track, InputStream vcfStream,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		this(new VcfExplorer(new InputStreamVcfReader(vcfStream), new VcfParserImpl()),
				track, comparator, referenceGenomeName);
	}

	private VcfFileDataSource(VcfExplorer vcfExplorer, Track track,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		this(VcfFileDataSource.getBands(vcfExplorer, track, referenceGenomeName),
				vcfExplorer, track, comparator, referenceGenomeName);
	}

	private VcfFileDataSource(VcfExplorer vcfExplorer, Track track,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName, FilterQuery query) {
		
		this(VcfFileDataSource.filterBands(
				VcfFileDataSource.getBands(vcfExplorer, track, referenceGenomeName), query),
				vcfExplorer, track, comparator, referenceGenomeName);
	}

	private VcfFileDataSource(List<VariantBand> bands, VcfExplorer vcfExplorer,
			Track track, Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		super(IdGenerationUtils.generateDataSourceId(), bands, comparator);

		this.attributes = VcfFileDataSource.getAttributes(vcfExplorer);
		// TODO: check that the same reference genome is used when creating VCF
		this.referenceGenomeName = referenceGenomeName;
		this.track = track;
		this.vcfExplorer = vcfExplorer;
	}

	@Override
	public DataSourceType getType() {
		return DataSourceType.VCF;
	}

	@Override
	public DataSource<VariantBand> filter(FilterQuery query) {

		return new VcfFileDataSource(
				this.vcfExplorer, this.track, this.getComparator(), this.referenceGenomeName, query);
	}

	@Override
	public Set<Attribute<?>> attributes() {
		return attributes;
	}

	private static List<VariantBand> filterBands(List<VariantBand> bands, FilterQuery query) {

		logger.debug("Bands to filter: {}", bands.size());
		
		return bands.stream()
				.filter(PredicateUtils.aggregatePredicate(query.getFilters(), query.getAggregates()))
				.collect(Collectors.toList());
	}

	/**
	 * Get variant data from VCF file provided. 
	 * 
	 * @param vcfExplorer
	 * @return
	 */
	private static List<VariantBand> getBands(VcfExplorer vcfExplorer, Track track,
			String referenceGenomeName) {

		logger.debug("Requesting bands from: {}", vcfExplorer);
		
		try {

			vcfExplorer.parse(FaultTolerance.FAIL_FAST);
			VcfFile vcfData = vcfExplorer.getVcfData();

			return vcfData.getVariants().stream()
				.map(variant -> {
					// Node that we perform conversion to ZBHO here
					GenomicCoordinate startCoord = new GenomicCoordinate(
							new Contig(referenceGenomeName, variant.getChrom()),
							variant.getPos() - 1);
					GenomicCoordinate endCoord = new GenomicCoordinate(
							new Contig(referenceGenomeName, variant.getChrom()),
							variant.getPos() + variant.getRef().length() - 1);

					String variantName = generateVariantName(variant);
					JsonNode properties = VcfFileDataSource.getProperties(vcfExplorer, variant);

					return new VariantBand(track, startCoord, endCoord, variantName, properties);
				})
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new IllegalStateException(
					String.format("VCF file provided has wrong format: %s", e.getMessage()));
		}
	}
	
	
	private static String generateVariantName(Variant variant) {
		
		String variantName = variant.getIds().stream()
				.map(Object::toString)
				.collect(Collectors.joining(";"));
		
		if (variantName.isEmpty()) {
			variantName = String.format("%s>%s", variant.getRef(), variant.getAlt());
		}
		
		return variantName;
		
	}
	

	static JsonNode getProperties(VcfExplorer vcfExplorer, Variant variant) {

		ObjectNode properties = JsonNodeFactory.instance.objectNode();

		// VCF attributes
		properties.put("ID", variant.getIds().stream().collect(Collectors.joining(",")));
		properties.put("REF", variant.getRef());
		properties.put("ALT", variant.getAlt());
		properties.put("QUAL", variant.getQual());

		// Filter attributes		
		ArrayNode filterNode = properties.putArray("FILTER");
		variant.getFilters().stream()
			.map(Filter::getId)
			.forEach(filterNode::add);

		// Info attributes
		VcfFileDataSource.putInfoFields(properties, vcfExplorer, variant.getInfo()); 

		// Format attributes
		VcfFileDataSource.putFormatFields(properties, vcfExplorer, variant.getFormats());

		return properties;
	}

	private static ObjectNode putInfoFields(ObjectNode properties, VcfExplorer vcfExplorer, 
			Map<String, List<? extends Serializable>> infos) {

		vcfExplorer.getVcfData().getInfos().entrySet().stream()
			.forEach(entry -> {

				String infoKey = entry.getKey();
				Information infoAttribute = entry.getValue();

				List<? extends Serializable> infoValues = infos.get(infoKey);

				String number = infoAttribute.getNumber();
				switch (number) {
				case ".":
					// No value FLAG field
					logger.debug("INFO flag field: {}", infoKey);
					if (infoValues != null) {
						properties.put(infoKey, true);
					} else {
						properties.put(infoKey, false);
					}

					break;
				case "1":
					// Single-value field
					if (infoValues == null) {
						return;
					}
					
					logger.debug("INFO value field: {}, values: {}", infoKey, infoValues);
					Serializable value = infoValues.get(0);

					switch (infoAttribute.getType()) {
						case FLOAT:
							properties.put(infoKey, (Double) value);
							break;
						case INTEGER:
							properties.put(infoKey, (Integer) value);
							break;
						case CHARACTER:
							/* Fall through */
						case STRING:
							properties.put(infoKey, (String) value);
							break;
						case FLAG:
							/**
							 * Maybe it is more reliable to do this
							 * validation in VCF Explorer itself,
							 * since FLAG never can be a value field
							 * 
							 * TODO: get rid of this validation
							 * when it would be implemented in VCF Explorer
							 */
							logger.error("INFO field {} typed as Flag has Number=1: {}",
									infoKey, infoValues);
							break;
						default:
							/**
							 * In fact, this will never be reached, if VCF Explorer works properly,
							 * as it validate field type while parsing phase
							 */
							logger.error("Unknown INFO field type: {}", infoAttribute.getType());
					}
					break;
				default:
					/*
					 * Flag field should be specified with Number = 0 according to VCF specification
					 * 
					 * TODO: clarify practical '.' and '0' usage as Number designator in Flag info fields
					 *  
					 */
					if (infoAttribute.getType().equals(InfoFieldType.FLAG)) {
						logger.debug("INFO flag field: {}", infoKey);
						if (infoValues != null) {
							properties.put(infoKey, true);
						} else {
							properties.put(infoKey, false);
						}
						return;
					}
					
					/**
					 * This validation is performed only because VCF Explorer
					 * doesn't support per genotype number parameter value yet
					 */
					if (infoValues == null) {
						return;
					}
					
					if (!isMultiValueNumber(number)) {

						logger.error("Field {} has unknown \"Number\" parameter: \"{}\". "
								+ "Skip metadata addition.", infoKey, infoAttribute.getNumber());
						break;
					}

					// Multi-value field, i.e. with exact values number or one of the {A,R,G}
					logger.debug("INFO array field: {}", infoKey);
					ArrayNode arrayNode = properties.putArray(infoKey);

					switch (infoAttribute.getType()) {
						case FLOAT:
							infoValues.stream().forEach(it -> arrayNode.add((Double) it));
							break;
						case INTEGER:
							infoValues.stream().forEach(it -> arrayNode.add((Integer) it));
							break;
						case CHARACTER:
							/* Fall through */
						case STRING:
							infoValues.stream().forEach(it -> arrayNode.add((String) it));
							break;
						default:
							/**
							 * In fact, this will never be reached, if VCF Explorer works properly,
							 * as it validate field type while parsing phase
							 */
							logger.error("Wrong array INFO field type: {}, array field: {}", infoAttribute.getType(), infoKey);
					}
			}
		});

		return properties;
	}

	private static ObjectNode putFormatFields(ObjectNode properties, VcfExplorer vcfExplorer, 
			Map<String, Map<String, List<? extends Serializable>>> formatFields) {

		formatFields.entrySet().stream()
			.forEachOrdered(sampleEntry -> {

				String sample = sampleEntry.getKey();
				Map<String, List<? extends Serializable>> formats = sampleEntry.getValue();

				formats.entrySet().stream()
					.forEachOrdered(formatEntry -> {

						String propertyName = VcfFileDataSource
								.createFormatAttributeName(sample, formatEntry.getKey());
						Format format = vcfExplorer.getVcfData().getFormats()
								.get(formatEntry.getKey());

						String number = format.getNumber();
						switch (number) {
							case "1":
								switch (format.getType()) {
									case FLOAT:
										properties.put(propertyName,
												(Double) formatEntry.getValue().get(0));
										break;

									case INTEGER:
										properties.put(propertyName,
												(Integer) formatEntry.getValue().get(0));
										break;

									case CHARACTER:
									case STRING:
										properties.put(propertyName,
												(String) formatEntry.getValue().get(0));
										break;

									default:
										logger.error("Unknown INFO field type: {}", format.getType());
								}

								break;

							default:
								// TODO: see the same comment for INFO fields
								if (!isMultiValueNumber(number)) {

									logger.error("Field {} has unknown \"Number\" parameter: \"{}\". "
											+ "Skip metadata addition.",
											format.getId(), format.getNumber());
									break;
								}

								// Multi-value field, i.e. with exact values number or one of the {A,R,G}
								ArrayNode arrayNode = properties.putArray(propertyName);

								switch (format.getType()) {
									case FLOAT:
										formatEntry.getValue().stream()
												.forEach(it -> arrayNode.add((Double) it));
										break;
									case INTEGER:
										formatEntry.getValue().stream()
												.forEach(it -> arrayNode.add((Integer) it));
										break;
									case CHARACTER:
										/* Fall through */
									case STRING:
										formatEntry.getValue().stream()
												.forEach(it -> arrayNode.add((String) it));
										break;
									default:
										/**
										 * In fact, this will never be reached, if VCF Explorer works properly,
										 * as it validate field type while parsing phase
										 */
										logger.error("Unknown FORMAT field type: {}",
												format.getType());
								}
						}
					});
			});

		return properties;
	}

	/**
	 * Get attributes from VCF file provided.
	 * 
	 * @param vcfExplorer vcfExplorer object to retrieve attributes for.
	 * @return set of Attribute objects
	 */
	private static Set<Attribute<?>> getAttributes(VcfExplorer vcfExplorer) {

		VcfFile vcfFile = vcfExplorer.getVcfData();

		return new LinkedHashSet<>(
				Stream.concat(
				// Get common VCF attributes
				VcfFileDataSource.generateVcfAttributes(vcfFile).stream(),
					// Add file-specific attributes
					Stream.concat(
						// Add INFO attributes
						VcfFileDataSource.createInfoAttributes(vcfFile).stream(),
						// Add FORMAT attributes
						VcfFileDataSource.createFormatAttributes(vcfFile).stream()
				)).collect(Collectors.toList())
			);
	}

	private static String createFormatAttributeName(String sampleName, String formatName) {
		return String.format("%s%s%s", sampleName, SAMPLE_ATTRIBUTE_DELIMETER, formatName);
	}

	/**
	 * Generate obligatory VCF file attributes.
	 * 
	 * @return Set of Attribute objects
	 */
	static Set<Attribute<?>> generateVcfAttributes(VcfFile vcfFile) {

		//Create obligatory VCF attributes
		return Arrays.asList(
					new StringAttributeBuilder("ID")
							.description("Variant identifier")
							.build(),
					new StringAttributeBuilder("REF")
							.description("Reference base(s)")
							.build(),
					new StringAttributeBuilder("ALT")
							.description("Alternate base(s)")
							.build(),
					new DoubleAttributeBuilder("QUAL")
							.description("Phred-scaled quality score")
							.range(new AttributeRange<Double>(
									0.0, Double.POSITIVE_INFINITY, InclusionType.OPEN))
							.build(),
					VcfFileDataSource.generateFilterAttribute(vcfFile)
				).stream()
				.collect(Collectors.toSet());
		
	}

	/**
	 * Generate file-specific FILTER attributes.
	 * 
	 * @param vcfFile File to generate attributes for
	 * @return Set of Attribute objects
	 */
	static Attribute<String> generateFilterAttribute(VcfFile vcfFile) {

		return new SetAttributeBuilder<String>("FILTER", String.class)
				.description("Filter status")
				.values(vcfFile.getFilters().keySet())
				.build();
	}

	/**
	 * Generate file-specific INFO attributes.
	 * 
	 * @param vcfFile File to generate attributes for
	 * @return Set of Attribute objects
	 */
	@SuppressWarnings("unchecked")
	static Set<Attribute<?>> createInfoAttributes(VcfFile vcfFile) {
		// This double casting is required for javac to sleep well. Eclipse compiler is fine without it
		return (Set<Attribute<?>>) (Set<?>) vcfFile.getInfos().entrySet()
			.stream()
			.map((Map.Entry<String, Information> entry) -> {

				String id = entry.getKey();
				Information info = entry.getValue(); 

				assert id.equals(info.getId()) : "INFO id equals field id";
				
				/*
				 * TODO: adapt VCF explorer and change this code to get rid of warnings
				 * 
				 * We have type clash here.
				 * 
				 * VcfExplorer.getInfos returns map of Serializable while Attribute classes
				 * want to work with Comparable values.
				 * 
				 * It's seems desirable to specify VCF explorer types and narrow set of 
				 * possible values (e.g. use Double for Double and Float, Long for Long, Integer and Byte)
				 * 
				 */
				@SuppressWarnings({ "rawtypes" })
				Set<? extends Comparable> values = (Set<? extends Comparable>) getInfoValues(info, vcfFile);

				return createInfoAttribute(
						info.getId(), info.getDescription(), info.getType(), values);
			})
			.collect(Collectors.toSet());
	}

	/**
	 * Generate file-specific FORMAT attributes.
	 * 
	 * @param vcfFile File to generate attributes for
	 * @return Set of Attribute objects
	 */
	@SuppressWarnings("unchecked")
	static Set<Attribute<?>> createFormatAttributes(VcfFile vcfFile) {
		// This double casting is required for javac to sleep well. Eclipse compiler is fine without it
		return (Set<Attribute<?>>) (Set<?>) vcfFile.getFormats().entrySet().stream()
				.flatMap(entry -> {

					String id = entry.getKey();
					Format format = entry.getValue();

					assert id.equals(format.getId()) : "FORMAT id equals field id";

					/*
					 * TODO: adapt VCF explorer and change this code to get rid of warnings
					 * 
					 * We have type clash here.
					 * 
					 * VcfExplorer.getFormats returns map of Serializable while Attribute classes
					 * want to work with Comparable values.
					 * 
					 * It's seems desirable to specify VCF explorer types and narrow set of 
					 * possible values (e.g. use Double for Double and Float, Long for Long, Integer and Byte)
					 * 
					 */
					@SuppressWarnings({ "rawtypes" })
					Set<? extends Comparable> values = (Set<? extends Comparable>) getFormatValues(format, vcfFile);
					
					// Generate format attributes for every sample
					return vcfFile.getSampleNames().stream()
							.map(sample -> createFormatAttribute(
									VcfFileDataSource.createFormatAttributeName(
											sample, format.getId()),
									format.getDescription(),
									format.getType(),
									values));
				})
				.collect(Collectors.toSet());

	}

	private static <T extends Comparable<T>> Attribute<T> createInfoAttribute(String name, String description,
			InfoFieldType type, Set<T> values) {

		// TODO: Java type mapping information should be moved to InfoFieldType itself.
		@SuppressWarnings("unchecked")
		final Class<T> attributeClazz = (Class<T>) getInfoAttributeClass(type);
		Attribute<T> attribute = AttributeUtils.createAttributeForValues(name, description, values, attributeClazz);

		return attribute;
	}
	
	/**
	 * Determine Java class for field type of Info attribute;
	 * 
	 * @param type type of Info attribute
	 * @return Java class
	 */
	private static Class<?> getInfoAttributeClass(InfoFieldType type) {
		
		switch (type) {
		case INTEGER:
			return Integer.class;
		case FLOAT:
			return Double.class;
		case FLAG:
			return Boolean.class;
		case CHARACTER:
			/* Fall through */
		case STRING:
			return String.class;
		default:
			throw new IllegalStateException(
					String.format("Specified INFO field type is unknown: %s", type));
		}
		
	}
	
	private static <T extends Comparable<T>> Attribute<T> createFormatAttribute(String name, String description,
			FormatFieldType type, Set<T> values) {
		
		// TODO: Java type mapping information should be moved to InfoFieldType itself.
		@SuppressWarnings("unchecked")
		final Class<T> attributeClazz = (Class<T>) getFormatAttributeClass(type);
		Attribute<T> attribute = AttributeUtils.createAttributeForValues(name, description, values, attributeClazz);

		return attribute;
	}
	
	/**
	 * Determine Java class for field type of Format attribute;
	 * 
	 * @param type type of Format attribute
	 * @return Java class
	 */
	private static Class<?> getFormatAttributeClass(FormatFieldType type) {
		
		switch (type) {
		case INTEGER:
			return Integer.class;
		case FLOAT:
			return Double.class;
		case CHARACTER:
			/* Fall through */
		case STRING:
			return String.class;
		default:
			throw new IllegalStateException(
					String.format("Specified FORMAT field type is unknown: %s", type));
		}
	}

	/**
	 * Retrieve all possible values of info field from VCF file provided.
	 * 
	 * The method is used to find value diversity and set AttributeType accordingly.
	 * 
	 * @param info
	 * @param vcfFile
	 * @return Set<? extends Serializable> the signature is influenced vcf-explorer and should be changed
	 */
	private static Set<? extends Serializable> getInfoValues(Information info, VcfFile vcfFile) {

		if (info.getType().equals(InfoFieldType.FLAG)) {
			// Flag type can only be true or false
			// It does not depend on file content
			return Stream.of(true, false)
				.collect(Collectors.toSet());
		} else {
			return vcfFile.getVariants().stream()
					// Get corresponding INFO field values for the given variant
					.map(variant -> variant.getInfo().get(info.getId()))
					.filter(Objects::nonNull)
					// Flatten INFO values list
					.flatMap(List::stream)
					// Transforming VCF values to strings
					.collect(Collectors.toSet());
		}
	}

	/**
	 * Retrieve all possible values of format field from VCF file provided.
	 * 
	 * The method is used to find value diversity and set AttributeType accordingly.
	 * 
	 * @param format
	 * @param vcfFile
	 * @return Set<? extends Serializable> the signature is influenced vcf-explorer and should be changed
	 */
	private static Set<? extends Serializable> getFormatValues(Format format, VcfFile vcfFile) {

		return vcfFile.getVariants().stream()
				// Get FORMAT values regardless of sample it belongs to
				.flatMap(variant -> variant.getFormats().entrySet().stream())
				.flatMap(entrySet -> {
					Map<String, List<? extends Serializable>> formats = entrySet.getValue();
					// Get format values for exact format key if any
					List<? extends Serializable> formatValues = formats.get(format.getId());
					if (formatValues == null) {
						// No format records, returning empty stream
						return Stream.empty();
					} else {
						// Return all possible values
						return formatValues.stream();
					}
				})
				.collect(Collectors.toSet());
	}


	public static final Predicate<String> isPerAlleleNumber = new Predicate<String>() {

		@Override
		public boolean test(String t) {
			return t.equals(VcfGrammar.VALUE_PER_ALLELE);
		}
	};

	public static final Predicate<String> isPerAlleleWithRefNumber = new Predicate<String>() {

		@Override
		public boolean test(String t) {
			return t.equals(VcfGrammar.VALUE_PER_ALLELE_WITH_REF);
		}
	};

	public static final Predicate<String> isPerGenotypeNumber = new Predicate<String>() {

		@Override
		public boolean test(String t) {
			return t.equals(VcfGrammar.VALUE_PER_GENOTYPE);
		}
	};
	
	public static final Predicate<String> isUnbounded = new Predicate<String>() {

		@Override
		public boolean test(String t) {
			return t.equals(VcfGrammar.UNBOUNDED_VALUE);
		}
	};

	public static final boolean isMultiValueNumber(String number) {

		return PredicateUtils.isNumeric
				.or(isPerAlleleNumber)
				.or(isPerAlleleWithRefNumber)
				.or(isPerGenotypeNumber)
				.or(isUnbounded)
				.test(number);
	}
}
