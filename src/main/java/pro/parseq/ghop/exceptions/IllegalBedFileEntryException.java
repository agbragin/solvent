package pro.parseq.ghop.exceptions;

public class IllegalBedFileEntryException extends RuntimeException {

	private static final long serialVersionUID = -264594450367805299L;

	private final String entry;

	public IllegalBedFileEntryException(String entry) {

		super(String.format("Malformed BED dataline: %s", entry));

		this.entry = entry;
	}

	public String getEntry() {
		return entry;
	}
}
