package pro.parseq.solvent.services;

import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.exceptions.CoordinateOutOfBoundsException;
import pro.parseq.solvent.utils.GenomicCoordinate;

public class ContigSequence implements Sequence {

	private final Contig contig;
	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;
	private final String sequence;

	public ContigSequence(GenomicCoordinate startCoord,
			GenomicCoordinate endCoord, String sequence) {

		if (!startCoord.getContig().equals(endCoord.getContig())) {
			throw new IllegalArgumentException(
					String.format("ContigSequence can not have start and end coordinates located on a different contigs,"
							+ " but they were: %s, %s", startCoord, endCoord));
		}

		this.contig = startCoord.getContig();
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.sequence = sequence;
	}

	public Contig getContig() {
		return contig;
	}

	public ContigSequence contigSubsequence(GenomicCoordinate startCoord, GenomicCoordinate endCoord) {
		return new ContigSequence(startCoord, endCoord, substring(startCoord, endCoord));
	}

	public ContigSequence contigSubsequence(GenomicCoordinate startCoord) {
		return contigSubsequence(startCoord, endCoord);
	}

	@Override
	public GenomicCoordinate startCoord() {
		return startCoord;
	}

	@Override
	public GenomicCoordinate endCoord() {
		return endCoord;
	}

	@Override
	public String sequence() {
		return sequence;
	}

	@Override
	public String substring(GenomicCoordinate startCoord, GenomicCoordinate endCoord) {

		if ((!startCoord.getContig().equals(this.startCoord.getContig()))
				|| startCoord.getCoord() < this.startCoord.getCoord()) {
			throw new CoordinateOutOfBoundsException(startCoord, this.startCoord, this.endCoord);
		}
		if ((!endCoord.getContig().equals(this.endCoord.getContig()))
				|| endCoord.getCoord() > this.endCoord.getCoord()) {
			throw new CoordinateOutOfBoundsException(endCoord, this.startCoord, this.endCoord);
		}

		return sequence.substring((int) (startCoord.getCoord() - this.startCoord.getCoord()),
				(int) (endCoord.getCoord() - this.startCoord.getCoord()));
	}

	@Override
	public long length() {
		return sequence.length();
	}

	@Override
	public String toString() {
		return sequence;
	}
}
