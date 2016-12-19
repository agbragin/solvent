package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.entities.Contig;

public class ContigNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -879329963784570173L;

	private final Contig contig;

	public ContigNotFoundException(Contig contig) {

		super(String.format("Contig '%s' not found for reference genome: %s",
				contig.getId(), contig.getReferenceGenome()));

		this.contig = contig;
	}

	public Contig getContig() {
		return contig;
	}
}
