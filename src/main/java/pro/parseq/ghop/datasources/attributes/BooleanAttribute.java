package pro.parseq.ghop.datasources.attributes;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.core.Relation;

import pro.parseq.ghop.datasources.attributes.AbstractAttribute;
import pro.parseq.ghop.datasources.filters.FilterOperator;
import pro.parseq.ghop.exceptions.IllegalAttributeValueException;

@Relation(collectionRelation = "attributes")
public class BooleanAttribute extends AbstractAttribute<Boolean> {

	private BooleanAttribute(String name, String description) {
		super(name, AttributeType.BOOLEAN, description, null);
	}

	@Override
	public Boolean parseValue(String s) {

		if (s.toLowerCase().equals("true")) {
			return true;
		} else if (s.toLowerCase().equals("false")) {
			return false;
		}

		throw new IllegalAttributeValueException(this, s);
	}

	@Override
	public Collection<FilterOperator> operators() {
		return Arrays.asList(FilterOperator.EQUALS, FilterOperator.NOTEQUALS);
	}

	public static final class BooleanAttributeBuilder {

		private final String name;
		private String description;

		public BooleanAttributeBuilder(String name) {
			this.name = name;
		}

		public BooleanAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		public BooleanAttribute build() {
			return new BooleanAttribute(name, description);
		}
	}
}
