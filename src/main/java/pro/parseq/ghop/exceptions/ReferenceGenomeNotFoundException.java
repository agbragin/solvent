package pro.parseq.ghop.exceptions;

public class ReferenceGenomeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7616074261125595081L;

	private final String referenceGenomeName;

	public ReferenceGenomeNotFoundException(String referenceGenomeName) {

		super(String.format("Reference genome not found: %s", referenceGenomeName));

		this.referenceGenomeName = referenceGenomeName;
	}

	public String getReferenceGenomeName() {
		return referenceGenomeName;
	}
}
