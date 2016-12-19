package pro.parseq.ghop.utils;

import pro.parseq.ghop.entities.Contig;

public class GenomicCoordinate {

	private final Contig contig;
	private final long coord;

	public GenomicCoordinate(Contig contig, long coord) {
		this.contig = contig;
		this.coord = coord;
	}

	public Contig getContig() {
		return contig;
	}

	public long getCoord() {
		return coord;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GenomicCoordinate)) {
			return false;
		}

		return contig.equals(((GenomicCoordinate) obj).contig)
				&& coord == ((GenomicCoordinate) obj).coord;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {

		return new StringBuilder()
				.append(contig)
				.append(":")
				.append(coord)
				.toString();
	}
}
