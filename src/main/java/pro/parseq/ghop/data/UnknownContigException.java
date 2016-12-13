package pro.parseq.ghop.data;

public class UnknownContigException extends RuntimeException {

	private static final long serialVersionUID = -6477649275490188765L;

	private final String referenceGenome;
	private final String contig;

	public UnknownContigException(String referenceGenome, String contig) {

		super(String.format("Unknown contig's name '%s' for reference genome: %s",
				contig, referenceGenome));

		this.referenceGenome = referenceGenome;
		this.contig = contig;
	}

	public String getReferenceGenome() {
		return referenceGenome;
	}

	public String getContig() {
		return contig;
	}
}
