package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.utils.GenomicCoordinate;

public class InvalidGenomicCoordinateException extends RuntimeException {

	private static final long serialVersionUID = -7639037198003605431L;

	private final GenomicCoordinate coord;

	public InvalidGenomicCoordinateException(GenomicCoordinate coord, String message) {

		super(message);

		this.coord = coord;
	}

	public GenomicCoordinate getCoord() {
		return coord;
	}
}
