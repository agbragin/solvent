package pro.parseq.solvent.utils;

import pro.parseq.solvent.entities.Contig;

public class GenomicCoordinateUtils {

	public static final GenomicCoordinate firstCoordinateOf(Contig contig) {
		return new GenomicCoordinate(contig, 0);
	}

	public static final GenomicCoordinate lastCoordinateOf(Contig contig) {
		return new GenomicCoordinate(contig, contig.getLength());
	}

	public static final GenomicCoordinate incrementCoordinate(GenomicCoordinate coord) {
		return new GenomicCoordinate(coord.getContig(), coord.getCoord() + 1);
	}

	public static final GenomicCoordinate decrementCoordinate(GenomicCoordinate coord) {
		return new GenomicCoordinate(coord.getContig(), coord.getCoord() - 1);
	}

	public static final boolean isContigLeftmost(GenomicCoordinate coord) {
		return coord.getCoord() == 0;
	}

	public static final boolean isContigRightMost(GenomicCoordinate coord) {
		return coord.getCoord() == (coord.getContig().getLength());
	}

	public static final boolean isContigExternal(GenomicCoordinate coord) {
		return isContigLeftmost(coord) || isContigRightMost(coord);
	}
}
