package pro.parseq.solvent.datasources;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.attributes.StringAttribute;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.ChromosomeBand;
import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.IdGenerationUtils;
import pro.parseq.solvent.utils.PredicateUtils;

@JsonInclude(Include.NON_NULL)
public class ChromosomeDataSource extends AbstractDataSource<ChromosomeBand> {

	private final Track track;

	private static final Set<Attribute<?>> attributes;
	static {

		StringAttribute chrName = new StringAttribute
				.StringAttributeBuilder("name").description("Chromosome name")
				.build();

		attributes = Stream.of(chrName).collect(Collectors.toSet());
	}

	private final Comparator<GenomicCoordinate> comparator;

	@JsonUnwrapped
	private final FilterQuery query;

	public ChromosomeDataSource(Track track, List<Contig> contigs,
			Comparator<GenomicCoordinate> comparator) {

		super(IdGenerationUtils.generateDataSourceId(),
				constructBands(track, contigs),
				comparator);

		this.track = track;
		this.comparator = comparator;
		this.query = null;
	}

	public ChromosomeDataSource(ChromosomeDataSource dataSource, FilterQuery query) {

		super(IdGenerationUtils.generateDataSourceId(),
				ChromosomeDataSource.filterBands(dataSource.getBands(), query),
				dataSource.comparator);

		this.track = dataSource.track;
		this.comparator = dataSource.comparator;
		this.query = query;
	}

	@Override
	public DataSourceType getType() {
		return DataSourceType.CHROMOSOME;
	}

	@Override
	public Set<Attribute<?>> attributes() {
		return attributes;
	}

	@Override
	public DataSource<ChromosomeBand> filter(FilterQuery query) {
		return new ChromosomeDataSource(this, query);
	}

	/**
	 * TODO: this should be shared across data sources holding properties aware bands
	 */
	private static final List<ChromosomeBand> filterBands(List<ChromosomeBand> bands, FilterQuery query) {

		return bands.stream()
				.filter(PredicateUtils.aggregatePredicate(
						query.getFilters(), query.getAggregates()))
				.collect(Collectors.toList());
	}

	private static final List<ChromosomeBand> constructBands(Track track, List<Contig> contigs) {

		return contigs.stream()
				.map(it -> new ChromosomeBand(track, it))
				.collect(Collectors.toList());
	}
}
