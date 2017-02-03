package pro.parseq.ghop.exceptions;

public class AttributeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2610007495504812055L;

	private final long id;

	public AttributeNotFoundException(long id) {

		super(String.format("Attribute of id: %d is not found", id));

		this.id = id;
	}

	public long getId() {
		return id;
	}
}
