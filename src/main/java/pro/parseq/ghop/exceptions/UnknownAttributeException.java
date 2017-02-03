package pro.parseq.ghop.exceptions;

public class UnknownAttributeException extends RuntimeException {

	private static final long serialVersionUID = -7571994357971235756L;

	private final long id;

	public UnknownAttributeException(long id) {

		super(String.format("Attribute of id: %d is not found", id));

		this.id = id;
	}

	public long getId() {
		return id;
	}
}
