package pro.parseq.ghop.data;

public class GenomicCoordinate {

	private final String referenceGenome;
	private final String contig;
	private final long coord;

	public GenomicCoordinate(String referenceGenome, String contig, long coord) {

		this.referenceGenome = referenceGenome;
		this.contig = contig;
		this.coord = coord;
	}

	public String getReferenceGenome() {
		return referenceGenome;
	}

	public String getContig() {
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

		return referenceGenome.equals(((GenomicCoordinate) obj).referenceGenome)
				&& contig.equals(((GenomicCoordinate) obj).contig)
				&& coord == ((GenomicCoordinate) obj).coord;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {

		return new StringBuilder(referenceGenome)
				.append(":").append(contig)
				.append(":").append(coord)
				.toString();
	}
}
