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
package pro.parseq.solvent.datasources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.attributes.AttributeRange;
import pro.parseq.solvent.datasources.attributes.InclusionType;
import pro.parseq.solvent.datasources.attributes.DoubleAttribute.DoubleAttributeBuilder;
import pro.parseq.solvent.datasources.attributes.SetAttribute.SetAttributeBuilder;
import pro.parseq.solvent.datasources.attributes.StringAttribute.StringAttributeBuilder;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.entities.VariantBand;
import pro.parseq.solvent.exceptions.VcfFileDataSourceException;
import pro.parseq.solvent.utils.AttributeUtils;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.IdGenerationUtils;
import pro.parseq.solvent.utils.PredicateUtils;
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
@Relation(collectionRelation = "dataSources")
@JsonInclude(Include.NON_NULL)
public final class VcfFileDataSource extends AbstractDataSource<VariantBand> {

	private static final Logger logger = LoggerFactory.getLogger(VcfFileDataSource.class);

	/**
	 * Sample-specific properties are flattened by concatenation with sample names with this delimeter
	 */
	final static String SAMPLE_ATTRIBUTE_DELIMETER = ":";
	final static String INFO_ATTRIBUTE_PREFIX = "INFO:";
	final static String FORMAT_ATTRIBUTE_PREFIX = "FORMAT:";

	private final String referenceGenomeName;
	private final VcfExplorer vcfExplorer;
	private final Track track;

	// Using ordered set to preserve attributes order
	private final Set<Attribute<?>> attributes;

	@JsonUnwrapped
	protected FilterQuery query;

	public VcfFileDataSource(Track track, File vcfFile,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName)
					throws FileNotFoundException {
		this(track, new FileInputStream(vcfFile), comparator, referenceGenomeName);
	}

	/**
	 * Create data source from VCF file provided.
	 * 
	 * @param track track to bind data source to
	 * @param vcfStream VCF file content
	 * @param comparator GenomicCoordinate compatator object
	 * @param referenceGenomeName name of reference genome for the track
	 */
	public VcfFileDataSource(Track track, InputStream vcfStream,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		this(new VcfExplorer(new InputStreamVcfReader(vcfStream), new VcfParserImpl()),
				track, comparator, referenceGenomeName);
	}

	private VcfFileDataSource(VcfExplorer vcfExplorer, Track track,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		this(getBands(vcfExplorer, track, referenceGenomeName),
				vcfExplorer, track, comparator, referenceGenomeName);
	}

	private VcfFileDataSource(VcfExplorer vcfExplorer, Track track,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName, FilterQuery query) {

		this(filterBands(
				getBands(vcfExplorer, track, referenceGenomeName), query),
				vcfExplorer, track, comparator, referenceGenomeName);
		this.query = query;
	}

	private VcfFileDataSource(List<VariantBand> bands, VcfExplorer vcfExplorer,
			Track track, Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		super(IdGenerationUtils.generateDataSourceId(), bands, comparator);

		this.attributes = getAttributes(vcfExplorer);
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
				.peek(variant -> logger.debug("Creating band from variant: {}:{}:{}>{}", 
						variant.getChrom(), variant.getPos(), variant.getRef(), variant.getAlt()))
				.map(variant -> {
					// Node that we perform conversion to ZBHO here
					GenomicCoordinate startCoord = new GenomicCoordinate(
							new Contig(referenceGenomeName, variant.getChrom()),
							variant.getPos() - 1);
					GenomicCoordinate endCoord = new GenomicCoordinate(
							new Contig(referenceGenomeName, variant.getChrom()),
							variant.getPos() + variant.getRef().length() - 1);

					String variantName = generateVariantName(variant);
					JsonNode properties = getProperties(vcfExplorer, variant);

					return new VariantBand(track, startCoord, endCoord, variantName, properties);
				})
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new IllegalStateException(
					String.format("VCF file provided has wrong format. Exception: %s", e));
		}
	}
	
	/**
	 * Generate variant name to be used in cases when variant ID is undefined.
	 * 
	 * @param variant variant object to generate name for
	 * @return variant name
	 */
	private static String generateVariantName(Variant variant) {
		logger.debug("Generating name for the variant: {}", variant);
		String variantName = variant.getIds().stream()
				.map(Object::toString)
				.collect(Collectors.joining(";"));
		
		if (variantName.isEmpty()) {
			variantName = String.format("%s>%s", variant.getRef(), variant.getAlt());
		}
		
		return variantName;
	}
	
	/**
	 * Generate properties for a given variant as a JsonNode suitable to be used in PredicateUtils.
	 * 
	 * @param vcfExplorer
	 * @param variant
	 * @return
	 */
	static JsonNode getProperties(VcfExplorer vcfExplorer, Variant variant) {

		logger.debug("Get properties for variant: {}", variant);
		
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
		putInfoFields(properties, vcfExplorer, variant.getInfo()); 

		// Format attributes
		putFormatFields(properties, vcfExplorer, variant.getFormats());

		return properties;
	}

	private static ObjectNode putInfoFields(ObjectNode properties, VcfExplorer vcfExplorer, 
			Map<String, List<? extends Serializable>> infos) {

		logger.debug("Processing infos: {} and storing to properties: {}", infos, properties);
		
		// Processing all but flag fields
		// Flag fields should be processed separately since in case they are false 
		// we have no way to retrieve them from variant data
		infos.entrySet().stream()
			.peek(entry -> logger.debug("Creating INFO field: {} from info data: {}", 
					entry.getKey(), entry.getValue()))
			.forEachOrdered(entry -> {
				String infoKey = entry.getKey();
				String infoName = createInfoAttributeName(infoKey);
				List<? extends Serializable> infoValues = entry.getValue();
				
				Information infoFieldDescription = vcfExplorer.getVcfData().getInfos().get(infoKey);
				if (infoFieldDescription == null) {
					throw new VcfFileDataSourceException(String.format(
							"Information field %s for is absent from VCF header",
							infoKey));
				}
				
				// If Flag, do nothing here
				if (infoFieldDescription.getType().equals(InfoFieldType.FLAG)) {
					return;
				}
				
				Class<?> attributeClass = getInfoAttributeClass(infoFieldDescription.getType());
				
				if (isMultiValueNumber(infoFieldDescription.getNumber())) {
					// Field is declared as having multiple values
					// Store values in the array
					addArrayNode(properties, infoName, attributeClass, infoValues);
					
				} else {
					// Field is declared as having single value
					// Check this and add value to properties according to its declared type
					if (infoValues.size() != 1) {
						throw new VcfFileDataSourceException(String.format(
								"Info field {} is not multivalued but multiple values found: {}",
								infoFieldDescription.getId(), infoValues));
					}
					
					addNode(properties, infoName, attributeClass, infoValues.get(0));
					
				}
			});
		
		// Processing flag fields
		vcfExplorer.getVcfData().getInfos().entrySet().stream()
			.filter(entry -> entry.getValue().getType().equals(InfoFieldType.FLAG))
			.map(Entry::getKey)
			.forEachOrdered(infoId -> {
				String infoName = createInfoAttributeName(infoId);
				if (infos.containsKey(infoId)) {
					properties.put(infoName, true);
				} else {
					properties.put(infoName, false);
				}
			});

		logger.debug("Properties with info fields added: {}", properties);
		return properties;
	}

	private static ObjectNode putFormatFields(ObjectNode properties, VcfExplorer vcfExplorer, 
			Map<String, Map<String, List<? extends Serializable>>> formatFields) {

		logger.debug("Processing formats: {} and storing to properties: {}", formatFields, properties);
		formatFields.entrySet().stream()
			// For each sample
			.forEachOrdered(sampleEntry -> {

				String sample = sampleEntry.getKey();
				Map<String, List<? extends Serializable>> formats = sampleEntry.getValue();

				// For each format in sample
				formats.entrySet().stream()
					.peek(entry -> logger.debug("Processing entry: {} for sample: {}", entry.getKey(), sample))
					.forEachOrdered(formatEntry -> {

						String formatKey = formatEntry.getKey();
						List<? extends Serializable> formatValues = formatEntry.getValue();
						
						String formatName = createFormatAttributeName(sample, formatKey);
						Format format = vcfExplorer.getVcfData().getFormats()
								.get(formatKey);
						
						if (format == null) {
							throw new VcfFileDataSourceException(String.format(
									"Format field {} is declared for variant but missed in metadata {}",
									formatKey, vcfExplorer.getVcfData().getFormats()));
						}
						
						Class<?> attributeClass = getFormatAttributeClass(format.getType());
						
						logger.debug("Format name: {}, class: {}, values: {}", formatName, attributeClass, formatValues);
						
						if (isMultiValueNumber(format.getNumber())) {

							addArrayNode(properties, formatName, attributeClass, formatValues);
							
						} else {
							if (formatValues.size() != 1) {
								throw new VcfFileDataSourceException(String.format(
										"Format field %s is not multivalued but multiple (or no) values found: %s",
										formatKey, formatValues));
							}
							
							addNode(properties, formatName, attributeClass, formatValues.get(0));
							
						}
					});
			});

		logger.debug("Properties with format fields added: {}", properties);
		return properties;
	}

	
	private static void addArrayNode(ObjectNode parent, String name, 
			Class<?> clazz, Collection<? extends Serializable> values) {
		
		logger.trace("Adding array node {} for values: {}", name, values);
		
		ArrayNode arrayNode = parent.putArray(name);
		
		if (values == null) {
			return;
		}
		
		if (clazz.equals(Integer.class)) {
			values.stream().forEach(it -> arrayNode.add((Integer) it));
		} else if (clazz.equals(Double.class)) {
			values.stream().forEach(it -> arrayNode.add((Double) it));
		} else {
			values.stream().forEach(it -> arrayNode.add(it.toString()));
		}
	}
	
	private static void addNode(ObjectNode parent, String name, Class<?> clazz, Serializable value) {
		
		logger.trace("Adding node {} for value: {}", name, value);
		
		if (value == null) {
			parent.put(name, "null");
		} else if (clazz.equals(Integer.class)) {
			parent.put(name, (Integer) value);
		} else if (clazz.equals(Double.class)) {
			parent.put(name, (Double) value);
		} else {
			parent.put(name, value.toString());
		}
		
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
				generateVcfAttributes(vcfFile).stream(),
					// Add file-specific attributes
					Stream.concat(
						// Add INFO attributes
						createInfoAttributes(vcfFile).stream(),
						// Add FORMAT attributes
						createFormatAttributes(vcfFile).stream()
				)).collect(Collectors.toList())
			);
	}
	
	static String createInfoAttributeName(String infoName) {
		return String.format("%s%s", 
				INFO_ATTRIBUTE_PREFIX, infoName);
	}
	
	static String createFormatAttributeName(String sampleName, String formatName) {
		return String.format("%s%s%s%s", 
				FORMAT_ATTRIBUTE_PREFIX, sampleName, SAMPLE_ATTRIBUTE_DELIMETER, formatName);
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
					generateFilterAttribute(vcfFile)
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
						createInfoAttributeName(info.getId()), 
						info.getDescription(), 
						info.getType(), 
						values);
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
									createFormatAttributeName(
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
					.and(it -> Integer.valueOf(it) > 1)
					.test(number) 
				|| isPerAlleleNumber
					.or(isPerAlleleWithRefNumber)
					.or(isPerGenotypeNumber)
					.or(isUnbounded)
					.test(number);
	}
}
