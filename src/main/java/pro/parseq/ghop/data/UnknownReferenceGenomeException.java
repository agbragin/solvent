package pro.parseq.ghop.data;

public class UnknownReferenceGenomeException extends RuntimeException {

	private static final long serialVersionUID = 2018762601767789944L;

	private final String referenceGenome;

	public UnknownReferenceGenomeException(String referenceGenome) {

		super(String.format("Unknown reference genome: %s", referenceGenome));

		this.referenceGenome = referenceGenome;
	}

	public String getReferenceGenome() {
		return referenceGenome;
	}
}
