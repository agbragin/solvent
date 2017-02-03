package pro.parseq.ghop.datasources.filters;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterOperator {

	LESS("<"),
	LESSEQ("<="),
	GREATEREQ(">="),
	GREATER(">"),
	LIKE("~"),
	ILIKE("~*"),
	EQUALS("="),
	NOTEQUALS("!="),
	IN("|");

	private String value;

	private FilterOperator(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return value;
	}

	public static final FilterOperator getEnum(String value) {

		for (FilterOperator enumValue: values()) {
			if (enumValue.value.equals(value)) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException(
				String.format("FilterOperator: Unknown value [%s]", value));
	}
}
