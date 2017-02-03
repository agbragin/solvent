package pro.parseq.ghop.datasources.attributes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InclusionType {

	OPEN("()"),
	LEFT_SEMI_CLOSED("[)"),
	RIGHT_SEMI_CLOSED("(]"),
	CLOSED("[]");

	private String value;

	private InclusionType(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return value;
	}

	public static final InclusionType getEnum(String value) {

		for (InclusionType enumValue: values()) {
			if (enumValue.value.equals(value)) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException(
				String.format("InclusionSettings: Unknown value [%s]", value));
	}
}
