package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.FilterOperator;

public class UnsupportedAttributeFilterOperatorException extends RuntimeException {

	private static final long serialVersionUID = 8373445986263311854L;

	private final Attribute<?> attribute;
	private final FilterOperator operator;

	public UnsupportedAttributeFilterOperatorException(Attribute<?> attribute, FilterOperator operator) {

		super(String.format("Attribute '%s' does not support filter operator: %s, only: %s",
				attribute.getName(), operator, attribute.operators()));

		this.attribute = attribute;
		this.operator = operator;
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}

	public FilterOperator getOperator() {
		return operator;
	}
}
