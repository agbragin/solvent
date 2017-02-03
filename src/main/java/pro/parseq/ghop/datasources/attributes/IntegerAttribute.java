package pro.parseq.ghop.datasources.attributes;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.core.Relation;

import pro.parseq.ghop.datasources.filters.FilterOperator;
import pro.parseq.ghop.exceptions.IllegalAttributeValueException;

@Relation(collectionRelation = "attributes")
public class IntegerAttribute extends AbstractAttribute<Integer> {

	private IntegerAttribute(String name, String description, AttributeRange<Integer> range) {
		super(name, AttributeType.INTEGER, description, range);
	}

	@Override
	public Integer parseValue(String s) {

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new IllegalAttributeValueException(this, s);
		}
	}

	@Override
	public Collection<FilterOperator> operators() {

		return Arrays.asList(FilterOperator.EQUALS, FilterOperator.NOTEQUALS,
				FilterOperator.GREATER, FilterOperator.GREATEREQ,
				FilterOperator.LESS, FilterOperator.LESSEQ);
	}

	public static final class IntegerAttributeBuilder {

		private final String name;
		private String description;
		private AttributeRange<Integer> range;

		public IntegerAttributeBuilder(String name) {
			this.name = name;
		}

		public IntegerAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		public IntegerAttributeBuilder range(AttributeRange<Integer> range) {
			this.range = range;
			return this;
		}

		public IntegerAttribute build() {
			return new IntegerAttribute(name, description, range);
		}
	}
}
