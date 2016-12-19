package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.entities.ReferenceGenome;

public class ReferenceGenomeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7616074261125595081L;

	private final ReferenceGenome referenceGenome;

	public ReferenceGenomeNotFoundException(ReferenceGenome referenceGenome) {

		super(String.format("Reference genome not found: %s", referenceGenome));

		this.referenceGenome = referenceGenome;
	}

	public ReferenceGenome getReferenceGenome() {
		return referenceGenome;
	}
}
