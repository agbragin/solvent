package pro.parseq.ghop.datasources;

import java.util.Set;

import pro.parseq.ghop.utils.GenomicCoordinate;

public class QueryForBands {

	// Bearing genomic coordinate
	private final GenomicCoordinate coord;
	// Left borders count (positive, 0-based)
	private final int left;
	// Right borders count (positive, 0-based)
	private final int right;
	// Target data sources to retrieve bands from
	private final Set<DataSource<?>> dataSources;

	// TODO: add tracks correlation

	public QueryForBands(GenomicCoordinate coord, int left, int right, Set<DataSource<?>> dataSources) {

		this.coord = coord;
		this.left = left;
		this.right = right;
		this.dataSources = dataSources;
	}

	public GenomicCoordinate getCoord() {
		return coord;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public Set<DataSource<?>> getDataSources() {
		return dataSources;
	}
}
