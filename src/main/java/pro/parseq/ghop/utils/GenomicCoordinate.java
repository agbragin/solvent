package pro.parseq.ghop.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pro.parseq.ghop.entities.Contig;

public class GenomicCoordinate {

	private final Contig contig;
	private final long coord;

	public GenomicCoordinate(Contig contig, long coord) {
		this.contig = contig;
		this.coord = coord;
	}

	@JsonCreator
	public GenomicCoordinate(@JsonProperty("genome") String referenceGenomeId,
			@JsonProperty("contig") String contigId, @JsonProperty("coord") long coord) {
		contig = new Contig(referenceGenomeId, contigId);
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

		return new HashCodeBuilder(29, 2017)
				.append(contig)
				.append(coord)
				.toHashCode();
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
