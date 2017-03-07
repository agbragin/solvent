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

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.attributes.StringAttribute;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.BedBand;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.BedUtils;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.IdGenerationUtils;
import pro.parseq.ghop.utils.PredicateUtils;
import pro.parseq.ghop.utils.BedUtils.Region;

@Relation(collectionRelation = "dataSources")
@JsonInclude(Include.NON_NULL)
public class BasicBedFileDataSource extends AbstractDataSource<BedBand> {

	private static final int NAME_ATTRIBUTE_IDX = 0;
	private static final String NAME_ATTRIBUTE = "name";
	private static final Set<Attribute<?>> attributes;

	protected final Track track;
	protected Comparator<GenomicCoordinate> comparator;
	protected final String referenceGenomeName;

	@JsonUnwrapped
	protected final FilterQuery query;

	static {

		StringAttribute regionName = new StringAttribute
				.StringAttributeBuilder("name").description("BED name field")
				.build();

		attributes = Stream.of(regionName).collect(Collectors.toSet());
	}

	public BasicBedFileDataSource(Track track, InputStream bed,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		this(BedUtils.getBands(
					referenceGenomeName, 
					bed, 
					new BasicBedBandBuilder(track, new ReferenceGenome(referenceGenomeName))),
				track, comparator, referenceGenomeName);
	}

	public BasicBedFileDataSource(BasicBedFileDataSource dataSource, FilterQuery query) {

		super(IdGenerationUtils.generateDataSourceId(),
				BasicBedFileDataSource.filterBands(dataSource.getBands(), query),
				dataSource.getComparator());

		this.track = dataSource.track;
		this.referenceGenomeName = dataSource.referenceGenomeName;
		this.query = query;
	}

	protected BasicBedFileDataSource(List<BedBand> bands, Track track,
			Comparator<GenomicCoordinate> comparator, String referenceGenomeName) {

		super(IdGenerationUtils.generateDataSourceId(), bands, comparator);

		this.track = track;
		this.referenceGenomeName = referenceGenomeName;
		this.query = null;
	}

	private static List<BedBand> filterBands(List<BedBand> bands, FilterQuery query) {

		return bands.stream()
				.filter(PredicateUtils.aggregatePredicate(
						query.getFilters(), query.getAggregates()))
				.collect(Collectors.toList());
	}

	@Override
	public Set<Attribute<?>> attributes() {
		return attributes;
	}

	@Override
	@JsonProperty("type")
	public DataSourceType getType() {
		return DataSourceType.BASIC_BED;
	}

	@Override
	public DataSource<BedBand> filter(FilterQuery query) {
		return new BasicBedFileDataSource(this, query);
	}

	public static class BasicBedBandBuilder implements BandBuilder<BedBand> {

		private final Track track;
		private final ReferenceGenome referenceGenome;

		public BasicBedBandBuilder(Track track,	ReferenceGenome referenceGenome) {
			this.track = track;
			this.referenceGenome = referenceGenome;
		}

		@Override
		public BedBand build(Region region) {

			GenomicCoordinate startCoord = new GenomicCoordinate(
					new Contig(this.referenceGenome, region.getChrom()),
					region.getChromStart());
			GenomicCoordinate endCoord = new GenomicCoordinate(
					new Contig(this.referenceGenome, region.getChrom()),
					region.getChromEnd());

			List<String> opts = region.getOpts();
			String name = (opts.size() > 0) ? opts.get(NAME_ATTRIBUTE_IDX) : null;

			ObjectNode properties = JsonNodeFactory.instance.objectNode();
			properties.put(NAME_ATTRIBUTE, name);

			return new BedBand(this.track, startCoord, endCoord, name, properties);
		}
	}
}
