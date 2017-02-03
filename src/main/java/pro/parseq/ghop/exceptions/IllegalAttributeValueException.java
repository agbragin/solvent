package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.datasources.attributes.Attribute;

public class IllegalAttributeValueException extends RuntimeException {

	private static final long serialVersionUID = 5342888998701253034L;

	private final Attribute<?> attribute;
	private final String value;

	public IllegalAttributeValueException(Attribute<?> attribute, String value) {

		super(String.format("Attribute '%s' is of type: %s; can't parse value: %s",
				attribute.getName(), attribute.getType(), value));

		this.attribute = attribute;
		this.value = value;
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}
}
