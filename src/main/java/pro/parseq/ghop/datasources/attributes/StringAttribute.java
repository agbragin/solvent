package pro.parseq.ghop.datasources.attributes;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.core.Relation;

import pro.parseq.ghop.datasources.filters.FilterOperator;

@Relation(collectionRelation = "attributes")
public class StringAttribute extends AbstractAttribute<String> {

	private StringAttribute(String name, String description) {
		super(name, AttributeType.STRING, description, null);
	}

	@Override
	public String parseValue(String s) {
		return s;
	}

	@Override
	public Collection<FilterOperator> operators() {

		return Arrays.asList(FilterOperator.EQUALS, FilterOperator.NOTEQUALS,
				FilterOperator.LIKE, FilterOperator.ILIKE);
	}

	public static final class StringAttributeBuilder {

		private final String name;
		private String description;

		public StringAttributeBuilder(String name) {
			this.name = name;
		}

		public StringAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		public StringAttribute build() {
			return new StringAttribute(name, description);
		}
	}
}
