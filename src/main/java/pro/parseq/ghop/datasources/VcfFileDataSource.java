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
import pro.parseq.ghop.datasources.attributes.BooleanAttribute.BooleanAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.DoubleAttribute.DoubleAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.InclusionType;
import pro.parseq.ghop.datasources.attributes.IntegerAttribute.IntegerAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.SetAttribute.SetAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.StringAttribute.StringAttributeBuilder;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.entities.VariantBand;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.IdGenerationUtils;
import pro.parseq.ghop.utils.PredicateUtils;
import pro.parseq.vcf.VcfExplorer;
import pro.parseq.vcf.exceptions.InvalidVcfFileException;
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
	 * Maximum number of categories that will retain the property as enum,
	 * otherwise string attribute will be created
	 */
	private final static int MAX_ENUM_CATEGORIES = 10;

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
				vcfExplorer, track, getComparator(), referenceGenomeName, query);
	}

	@Override
	public Set<Attribute<?>> attributes() {
		return attributes;
	}

	private static List<VariantBand> filterBands(List<VariantBand> bands, FilterQuery query) {

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

		try {

			vcfExplorer.parse(FaultTolerance.FAIL_FAST);
			VcfFile vcfData = vcfExplorer.getVcfData();

			return vcfData.getVariants().stream()
				.map(variant -> {

					GenomicCoordinate startCoord = new GenomicCoordinate(
							new Contig(referenceGenomeName, variant.getChrom()),
							variant.getPos());
					GenomicCoordinate endCoord = new GenomicCoordinate(
							new Contig(referenceGenomeName, variant.getChrom()),
							variant.getPos() + variant.getRef().length());

					JsonNode properties = VcfFileDataSource.getProperties(vcfExplorer, variant);
					String variantName = variant.getIds().stream()
							.map(Object::toString)
							.collect(Collectors.joining(";"));

					return new VariantBand(track, startCoord, endCoord, variantName, properties);
				})
				.collect(Collectors.toList());
		} catch (InvalidVcfFileException e) {

			throw new IllegalStateException(
					String.format("VCF file provided has wrong format: %s", e.getMessage()));
		}
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
			.map(filter -> filter.getId())
			.forEach(value -> filterNode.add(value));

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
					logger.debug("INFO value field: {}", infoKey);
					Serializable value = infoValues.get(0);

					switch (infoAttribute.getType()) {
						case FLOAT:
							properties.put(infoKey, (Double) value);
							break;

						case INTEGER:
							properties.put(infoKey, (Integer) value);
							break;

						case CHARACTER:
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
					/**
					 * This validation is performed only because VCF Explorer
					 * doesn't support per genotype number parameter value yet
					 * 
					 * TODO: get rid of this validation
					 */
					if (!isMultiValueNumber(number)) {

						logger.error("Field {} has unknown \"Number\" parameter: {}. "
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
						case STRING:
							infoValues.stream().forEach(it -> arrayNode.add((String) it));
							break;

						default:
							/**
							 * In fact, this will never be reached, if VCF Explorer works properly,
							 * as it validate field type while parsing phase
							 */
							logger.error("Wrong array INFO field type: {}", infoAttribute.getType());
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

									logger.error("Field {} has unknown \"Number\" parameter: {}. "
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
	 * @param vcfExplorer
	 * @return
	 */
	private static Set<Attribute<?>> getAttributes(VcfExplorer vcfExplorer) {

		VcfFile vcfFile = vcfExplorer.getVcfData();

		return new LinkedHashSet<>(
				Stream.concat(
				// Get common VCF attributes
				VcfFileDataSource.generateVcfAttributes(vcfFile).stream(),
					Stream.concat(
						// Add INFO attributes
						VcfFileDataSource.generateInfoAttributes(vcfFile).stream(),
						// Add FORMAT attributes
						VcfFileDataSource.generateFormatAttributes(vcfFile).stream()
				)).collect(Collectors.toList())
			);
	}

	private static String createFormatAttributeName(String sampleName, String formatName) {
		return String.format("%s/%s", sampleName, formatName);
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
				VcfFileDataSource.generateFilterAttributes(vcfFile)).stream()
						.collect(Collectors.toSet());
	}

	/**
	 * Generate file-specific FILTER attributes.
	 * 
	 * @param vcfFile File to generate attributes for
	 * @return Set of Attribute objects
	 */
	static Attribute<String> generateFilterAttributes(VcfFile vcfFile) {

		// Grab all possible filter values as strings
		// TODO: think about grabbing filter parameters from VCF header
		Set<String> values = vcfFile.getVariants().stream()
				.map((Variant it) -> {
					// Get variant filters
					return it.getFilters();
				})
				// Transforming VCF values to strings
				.flatMap((List<?> it) -> it.stream())
				.map(it -> it.toString())
				.collect(Collectors.toSet());

		return new SetAttributeBuilder<String>("FILTER")
				.setDescription("Filter status")
				.setValues(values)
				.build();
	}

	/**
	 * Generate file-specific INFO attributes.
	 * 
	 * @param vcfFile File to generate attributes for
	 * @return Set of Attribute objects
	 */
	static Set<Attribute<?>> generateInfoAttributes(VcfFile vcfFile) {

		return vcfFile.getInfos().entrySet()
			.stream()
			.map((Map.Entry<String, Information> it) -> {

				String id = it.getKey();
				Information info = it.getValue();

				assert id.equals(info.getId()) : "INFO id equals field id";

				return createInfoAttribute(
						info.getId(), info.getDescription(), info.getType(),
						getInfoValues(info, vcfFile));
			})
			.collect(Collectors.toSet());
	}

	/**
	 * Generate file-specific FORMAT attributes.
	 * 
	 * @param vcfFile File to generate attributes for
	 * @return Set of Attribute objects
	 */
	static Set<Attribute<?>> generateFormatAttributes(VcfFile vcfFile) {

		return vcfFile.getFormats().entrySet().stream()
				.flatMap(formatEntry -> {

					String id = formatEntry.getKey();
					Format format = formatEntry.getValue();

					assert id.equals(format.getId()) : "FORMAT id equals field id";

					// Generate format attributes for every sample
					return vcfFile.getSampleNames().stream()
							.map(sample -> createFormatAttribute(
									VcfFileDataSource.createFormatAttributeName(
											sample, format.getId()),
									format.getDescription(),
									format.getType(),
									getFormatValues(format, vcfFile)));
				})
				.collect(Collectors.toSet());
	}

	private static Attribute<?> createInfoAttribute(String name, String description,
			InfoFieldType type, Set<String> values) {

		Attribute<?> attribute = null;

		switch (type) {
		case INTEGER:
			attribute = new IntegerAttributeBuilder(name)
				.description(description)
				.build();
			break;

		case FLOAT:
			attribute = new DoubleAttributeBuilder(name)
				.description(description)
				.build();
			break;

		case FLAG:
			attribute = new BooleanAttributeBuilder(name)
				.description(description)
				.build();
			break;

		case CHARACTER:
		case STRING:
			attribute = createAttributeForValues(name, description,
					values, MAX_ENUM_CATEGORIES);
			break;

		default:
			throw new IllegalStateException(
					String.format("Specified INFO field type is unknown: %s", type));
		}

		return attribute;
	}

	private static Attribute<?> createFormatAttribute(String name, String description,
			FormatFieldType type, Set<String> values) {

		Attribute<?> attribute = null;

		switch (type) {
		case INTEGER:
			attribute = new IntegerAttributeBuilder(name)
				.description(description)
				.build();
			break;

		case FLOAT:
			attribute = new DoubleAttributeBuilder(name)
				.description(description)
				.build();
			break;

		case CHARACTER:
		case STRING:
			attribute = createAttributeForValues(name, description,
					values, MAX_ENUM_CATEGORIES);
			break;

		default:
			throw new IllegalStateException(
					String.format("Specified INFO field type is unknown: %s", type));
		}

		return attribute;
	}

	private static Set<String> getInfoValues(Information info, VcfFile vcfFile) {

		if (info.getType().equals(InfoFieldType.FLAG)) {

			return Stream.of(true, false)
				.map(String::valueOf)
				.collect(Collectors.toSet());
		} else {

			return vcfFile.getVariants().stream()
					// Get corresponding INFO field values for the given variant
					.map(variant -> variant.getInfo().get(info.getId()))
					// Flatten INFO values list
					.flatMap((List<?> values) -> values.stream())
					// Transforming VCF values to strings
					.map(value -> value.toString())
					.collect(Collectors.toSet());
		}
	}

	private static Set<String> getFormatValues(Format format, VcfFile vcfFile) {

		return vcfFile.getVariants().stream()
				// TODO: group FORMAT values by samples
				// Get FORMAT values regardless of sample it belongs to
				.flatMap(variant -> variant.getFormats().values().stream())
				// Get target FORMAT value and convert it to string
				.map(formats -> formats.get(format.getId()).toString())
				.collect(Collectors.toSet());
	}

	/**
	 * Create attribute for textual fields accounting values diversity.
	 * 
	 * Since VCF is small we can select String or Enum attribute type analyzing VCF records. 
	 * 
	 * @param info INFO field to create attribute for
	 * @param vcfData Content of VCF file
	 * @param maxEnumCategories Maximum number of categories for Enum attribute. If the limit is exceeded returning String attribute 
	 * @return
	 */
	static <T extends Comparable<T>> Attribute<?> createAttributeForValues(String name, String description, Set<T> values, int maxEnumCategories) {

		Attribute<?> attribute;
		if (values.size() > maxEnumCategories) {

			logger.debug("Number of values: {} exceeds maximum Emum categories: {}. "
					+ "Creating String attribute.", values.size(), maxEnumCategories);
			attribute = new StringAttributeBuilder(name)
					.description(description)
					.build();
		} else {

			logger.debug("Creating Enum attribute with number of categories: {}",
					values.size());
			attribute = new SetAttributeBuilder<T>(name)
					.setDescription(description)
					.setValues(values)
					.build();
		}

		return attribute;
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
			// TODO: move this literal to VcfGrammar
			return t.equals("G");
		}
	};

	public static final boolean isMultiValueNumber(String number) {

		return PredicateUtils.isNumeric
				.or(isPerAlleleNumber)
				.or(isPerAlleleWithRefNumber)
				.or(isPerGenotypeNumber)
				.test(number);
	}
}
