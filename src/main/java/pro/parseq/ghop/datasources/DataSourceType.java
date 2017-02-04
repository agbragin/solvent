package pro.parseq.ghop.datasources;

import org.springframework.hateoas.core.Relation;

@Relation(collectionRelation = "dataSourceTypes")
public enum DataSourceType {

	BASIC_BED("basic_bed"),
	VARIANTS_BED("variants_bed"),
	VCF("vcf");
	// etc.

	private String value;

	private DataSourceType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public static final DataSourceType getEnum(String value) {

		for (DataSourceType enumValue: values()) {
			if (enumValue.value.toLowerCase().equals(value.toLowerCase())) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException(
				String.format("BedType: Unknown value [%s]", value));
	}
}
