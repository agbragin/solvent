package pro.parseq.ghop.data.source.utils;

public class IllegalBedFileEntryException extends RuntimeException {

	private static final long serialVersionUID = -874869452505550260L;

	private final String entry;

	public IllegalBedFileEntryException(String entry) {
		super(String.format("Malformed BED dataline: %s", entry));
		this.entry = entry;
	}

	public String getEntry() {
		return entry;
	}
}
