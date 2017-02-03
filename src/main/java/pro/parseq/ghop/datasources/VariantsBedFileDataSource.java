package pro.parseq.ghop.datasources;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.attributes.AttributeRange;
import pro.parseq.ghop.datasources.attributes.BooleanAttribute;
import pro.parseq.ghop.datasources.attributes.DoubleAttribute;
import pro.parseq.ghop.datasources.attributes.EnumAttribute;
import pro.parseq.ghop.datasources.attributes.InclusionType;
import pro.parseq.ghop.datasources.attributes.IntegerAttribute;
import pro.parseq.ghop.datasources.attributes.StringAttribute;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.BedBand;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.exceptions.IllegalBedFileDataLineException;
import pro.parseq.ghop.utils.BedUtils;
import pro.parseq.ghop.utils.BedUtils.Region;
import pro.parseq.ghop.utils.ClinicalSignificance;
import pro.parseq.ghop.utils.GenomicCoordinate;

@Relation(collectionRelation = "dataSources")
@JsonInclude(Include.NON_NULL)
public class VariantsBedFileDataSource extends BasicBedFileDataSource {

	private static final int NAME_ATTRIBUTE_IDX = 0;
	private static final int WEIGHT_ATTRIBUTE_IDX = 1;
	private static final int ACCESSION_ATTRIBUTE_IDX = 2;
	private static final int MUTATION_ATTRIBUTE_IDX = 3;
	private static final int FREQUENCY_ATTRIBUTE_IDX = 4;
	private static final int SIGNIFICANCE_ATTRIBUTE_IDX = 5;
	private static final String NAME_ATTRIBUTE = "name";
	private static final String WEIGHT_ATTRIBUTE = "weight";
	private static final String ACCESSION_ATTRIBUTE = "accession";
	private static final String MUTATION_ATTRIBUTE = "mutation";
	private static final String FREQUENCY_ATTRIBUTE = "frequency";
	private static final String SIGNIFICANCE_ATTRIBUTE = "significance";

	private static final Set<Attribute<?>> attributes;

	static {

		StringAttribute variantName = new StringAttribute
				.StringAttributeBuilder(NAME_ATTRIBUTE)
				.description("Variant name").build();
		IntegerAttribute variantWeight = new IntegerAttribute
				.IntegerAttributeBuilder(WEIGHT_ATTRIBUTE)
				.description("Cinvar WGT")
				.range(new AttributeRange<Integer>(0, Integer.MAX_VALUE, InclusionType.CLOSED))
				.build();
		StringAttribute variantAccession = new StringAttribute
				.StringAttributeBuilder(ACCESSION_ATTRIBUTE)
				.description("Clinvar CLNACC: Variant Accession and Versions")
				.build();
		BooleanAttribute variantMutation = new BooleanAttribute
				.BooleanAttributeBuilder(MUTATION_ATTRIBUTE)
				.description("Clinvar MUT").build();
		DoubleAttribute variantFrequency = new DoubleAttribute
				.DoubleAttributeBuilder(FREQUENCY_ATTRIBUTE)
				.description("Allele frequency based on 1000Genomes")
				.range(new AttributeRange<Double>(0., 1., InclusionType.OPEN))
				.build();
		EnumAttribute<ClinicalSignificance> variantSignificance = new EnumAttribute
				.EnumAttributeBuilder<ClinicalSignificance>(SIGNIFICANCE_ATTRIBUTE, ClinicalSignificance.class)
						.description("Clinvar CLNSIG: Variant Clinical Significance")
						.build();

		attributes = Stream.of(variantName, variantWeight, variantAccession,
						variantMutation, variantFrequency, variantSignificance)
				.collect(Collectors.toSet());
	}

	public VariantsBedFileDataSource(Track track, InputStream bed,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		super(BedUtils.getBands(
						referenceGenomeName,
						bed,
						new VariantsBedBandBuilder(
								track, new ReferenceGenome(referenceGenomeName))),
				track, comparator, referenceGenomeName);
	}

	public VariantsBedFileDataSource(VariantsBedFileDataSource dataSource, FilterQuery query) {
		super(dataSource, query);
	}

	@Override
	public Set<Attribute<?>> attributes() {
		return attributes;
	}

	@Override
	@JsonProperty("type")
	public DataSourceType getType() {
		return DataSourceType.VARIANTS_BED;
	}

	public static class VariantsBedBandBuilder extends BasicBedBandBuilder {

		public VariantsBedBandBuilder (Track track,	ReferenceGenome referenceGenome) {
			super(track, referenceGenome);
		}

		@Override
		public BedBand build(Region region) {

			BedBand band = super.build(region);

			List<String> opts = region.getOpts();
			if (opts.size() != attributes.size()) {
				// TODO: do it another way (more specific to the datasource)
				throw new IllegalBedFileDataLineException(region.toString());
			}

			ObjectNode properties = JsonNodeFactory.instance.objectNode();
			properties.put(NAME_ATTRIBUTE, opts.get(NAME_ATTRIBUTE_IDX));
			properties.put(WEIGHT_ATTRIBUTE, Integer.parseInt(opts.get(WEIGHT_ATTRIBUTE_IDX)));
			properties.put(ACCESSION_ATTRIBUTE, opts.get(ACCESSION_ATTRIBUTE_IDX));
			properties.put(MUTATION_ATTRIBUTE, Boolean.parseBoolean(opts.get(MUTATION_ATTRIBUTE_IDX)));
			properties.put(FREQUENCY_ATTRIBUTE, Double.parseDouble(opts.get(FREQUENCY_ATTRIBUTE_IDX)));
			properties.put(SIGNIFICANCE_ATTRIBUTE, opts.get(SIGNIFICANCE_ATTRIBUTE_IDX));

			band.setProperties(properties);

			return band;
		}
	}
}
