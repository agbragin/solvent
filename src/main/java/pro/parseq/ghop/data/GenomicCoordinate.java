package pro.parseq.ghop.data;

import java.util.Comparator;

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

	public static class CoordinateComparator implements Comparator<GenomicCoordinate> {

		private static final int stringComparisonNormalizer(int comparisonResult) {

			if (comparisonResult < 0) {
				return -1;
			}
			if (comparisonResult > 0) {
				return 1;
			}

			return 0;
		}

		@Override
		public int compare(GenomicCoordinate o1, GenomicCoordinate o2) {

			if (!o1.getReferenceGenome().equals(o2.getReferenceGenome())) {
				return stringComparisonNormalizer(o1.getReferenceGenome().compareTo(o2.getReferenceGenome()));
			}
			if (!o1.getContig().equals(o2.getContig())) {
				return stringComparisonNormalizer(o1.getContig().compareTo(o2.getContig()));
			}

			if (o1.getCoord() == o2.getCoord()) {
				return 0;
			}
			if (o1.getCoord() < o2.getCoord()) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
