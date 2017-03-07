package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.utils.GenomicCoordinate;

public class CoordinateOutOfBoundsException extends IndexOutOfBoundsException {

	private static final long serialVersionUID = 2486672168803206476L;

	private final GenomicCoordinate coord;
	private final GenomicCoordinate lowerBound;
	private final GenomicCoordinate upperBound;

	public CoordinateOutOfBoundsException(GenomicCoordinate coord, GenomicCoordinate lowerBound, GenomicCoordinate upperBound) {

		super(String.format("Coordinate is out of range: %s", coord));

		this.coord = coord;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public GenomicCoordinate getCoord() {
		return coord;
	}

	public GenomicCoordinate getLowerBound() {
		return lowerBound;
	}

	public GenomicCoordinate getUpperBound() {
		return upperBound;
	}
}
