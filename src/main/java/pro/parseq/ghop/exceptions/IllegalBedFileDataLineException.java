package pro.parseq.ghop.exceptions;

public class IllegalBedFileDataLineException extends RuntimeException {

	private static final long serialVersionUID = -9084953325445256636L;

	private final String dataLine;

	public IllegalBedFileDataLineException(String dataLine) {

		super(String.format("Malformed BED file dataline: %s", dataLine));

		this.dataLine = dataLine;
	}

	public String getDataLine() {
		return dataLine;
	}
}
