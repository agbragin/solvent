package pro.parseq.solvent.entities;

import org.springframework.hateoas.core.Relation;

import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.Nucleotide;

@Relation(collectionRelation = "bands")
public class NucleotideBand extends AbstractBand {

	private final Nucleotide nucleotide;

	public NucleotideBand(Track track,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			Nucleotide nucleotide) {

		super(track, startCoord, endCoord, nucleotide.toString());

		this.nucleotide = nucleotide;
	}

	public Nucleotide getNucleotide() {
		return nucleotide;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NucleotideBand)) {
			return false;
		}

		return getTrack().equals(((NucleotideBand) obj).getTrack())
				&& getStartCoord().equals(((NucleotideBand) obj).getStartCoord())
				&& getEndCoord().equals(((NucleotideBand) obj).getEndCoord())
				&& getNucleotide().equals(((NucleotideBand) obj).getNucleotide());
	}
}
