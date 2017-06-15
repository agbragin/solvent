package pro.parseq.solvent.exceptions;

public class IllegalFilesystemPathException extends IllegalArgumentException {

	private static final long serialVersionUID = -8056074390129357723L;

	private final String path;

	public IllegalFilesystemPathException(String path, String message) {

		super(message);

		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
