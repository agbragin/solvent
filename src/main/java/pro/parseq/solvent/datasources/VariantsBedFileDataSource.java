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

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.attributes.AttributeRange;
import pro.parseq.solvent.datasources.attributes.BooleanAttribute;
import pro.parseq.solvent.datasources.attributes.DoubleAttribute;
import pro.parseq.solvent.datasources.attributes.EnumAttribute;
import pro.parseq.solvent.datasources.attributes.InclusionType;
import pro.parseq.solvent.datasources.attributes.IntegerAttribute;
import pro.parseq.solvent.datasources.attributes.StringAttribute;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.BedBand;
import pro.parseq.solvent.entities.ReferenceGenome;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.exceptions.IllegalBedFileDataLineException;
import pro.parseq.solvent.utils.BedUtils;
import pro.parseq.solvent.utils.ClinicalSignificance;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.BedUtils.Region;

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
