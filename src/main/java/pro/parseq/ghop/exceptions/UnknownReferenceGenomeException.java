package pro.parseq.ghop.exceptions;

import java.util.Set;

public class UnknownReferenceGenomeException extends RuntimeException {

	private static final long serialVersionUID = 4546260233000370811L;

	private final String referenceGenomeName;
	private final Set<String> availableReferenceGenomeNames;

	public UnknownReferenceGenomeException(String referenceGenomeName,
			Set<String> availableReferenceGenomeNames) {

		super(String.format("Unknown reference genome name '%s'; available are: %s",
				referenceGenomeName, availableReferenceGenomeNames));

		this.referenceGenomeName = referenceGenomeName;
		this.availableReferenceGenomeNames = availableReferenceGenomeNames;
	}

	public String getReferenceGenomeName() {
		return referenceGenomeName;
	}

	public Set<String> getAvailableReferenceGenomeNames() {
		return availableReferenceGenomeNames;
	}
}
