package pro.parseq.ghop.datasources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.utils.GenomicCoordinate;

public abstract class AbstractDataSource<T extends Band> implements DataSource<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDataSource.class);

	private final long id;

	private final Comparator<GenomicCoordinate> comparator;
	private final List<T> bands;
	private final List<GenomicCoordinate> borders;

	public AbstractDataSource(long id, List<T> bands, Comparator<GenomicCoordinate> comparator) {

		this.id = id;
		this.bands = bands;
		this.comparator = comparator;

		if (comparator == null) {
			throw new IllegalArgumentException("Genomic coordinate comparator should be non null");
		}

		logger.debug("Bands received: {}", bands);

		borders = bands.stream()
				.flatMap(band -> Stream.of(band.getStartCoord(), band.getEndCoord()))
				.sorted(comparator)
				.distinct()
				.collect(Collectors.toList());

		logger.debug("Borders created: {}", borders);
	}

	public List<T> getBands() {
		return new ArrayList<>(bands);
	}

	public Comparator<GenomicCoordinate> getComparator() {
		return comparator;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord) {

		int idx = Collections.binarySearch(borders, coord, comparator);
		if (idx < 0) {
			return ((-(idx + 1)) <= count) ? borders.subList(0, -(idx + 1))
					: borders.subList(-(idx + 1) - count, -(idx + 1));
		} else {
			return (idx <= count) ? borders.subList(0, idx + 1)
					: borders.subList(idx - count, idx + 1);
		}
	}

	@Override
	public List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord) {

		int idx = Collections.binarySearch(borders, coord, comparator);
		if (idx < 0) {
			return ((borders.size() - (-(idx + 1))) <= count) ?
					borders.subList(-(idx + 1), borders.size())
					: borders.subList(-(idx + 1), -(idx + 1) + count);
		} else {
			return ((borders.size() - idx) <= count) ?
					borders.subList(idx, borders.size())
					: borders.subList(idx, idx + count + 1);
		}
	}

	@Override
	public Set<T> borderGenerants(GenomicCoordinate coord) {

		return bands.stream()
				.filter(band -> band.getStartCoord().equals(coord)
						|| band.getEndCoord().equals(coord))
				.collect(Collectors.toSet());
	}

	@Override
	public Set<T> coverage(GenomicCoordinate coord) {

		return bands.stream()
				.filter(band -> comparator.compare(band.getStartCoord(), coord) != 1
						&& comparator.compare(band.getEndCoord(), coord) != -1)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<T> leftBordersGenerants(int borderCount, GenomicCoordinate coord) {

		return leftBorders(borderCount, coord).stream()
				.flatMap(border -> borderGenerants(border).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<T> rightBordersGenerants(int borderCount, GenomicCoordinate coord) {

		return rightBorders(borderCount, coord).stream()
				.flatMap(border -> borderGenerants(border).stream())
				.collect(Collectors.toSet());
	}
}
