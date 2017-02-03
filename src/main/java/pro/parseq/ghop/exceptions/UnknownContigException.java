package pro.parseq.ghop.exceptions;

import java.util.List;

public class UnknownContigException extends RuntimeException {

	private static final long serialVersionUID = -2627070873847041797L;

	private final String referenceGenomeName;
	private final String contigName;
	private final List<String> availableContigNames;

	public UnknownContigException(String referenceGenomeName,
			String contigName, List<String> availableContigNames) {

		super(String.format("Unknown contig name '%s' for reference genome '%s'; available are: %s",
				contigName, referenceGenomeName, availableContigNames));

		this.referenceGenomeName = referenceGenomeName;
		this.contigName = contigName;
		this.availableContigNames = availableContigNames;
	}

	public String getReferenceGenomeName() {
		return referenceGenomeName;
	}

	public String getContigName() {
		return contigName;
	}

	public List<String> getAvailableContigNames() {
		return availableContigNames;
	}
}
