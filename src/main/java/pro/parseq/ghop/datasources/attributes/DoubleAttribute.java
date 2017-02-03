package pro.parseq.ghop.datasources.attributes;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.core.Relation;

import pro.parseq.ghop.datasources.filters.FilterOperator;
import pro.parseq.ghop.exceptions.IllegalAttributeValueException;

@Relation(collectionRelation = "attributes")
public class DoubleAttribute extends AbstractAttribute<Double> {

	private DoubleAttribute(String name, String description, AttributeRange<Double> range) {
		super(name, AttributeType.FLOAT, description, range);
	}

	@Override
	public Double parseValue(String s) {

		try {
			return Double.parseDouble(s);
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalAttributeValueException(this, s);
		}
	}

	@Override
	public Collection<FilterOperator> operators() {

		return Arrays.asList(FilterOperator.EQUALS, FilterOperator.NOTEQUALS,
				FilterOperator.GREATER, FilterOperator.GREATEREQ,
				FilterOperator.LESS, FilterOperator.LESSEQ);
	}

	public static final class DoubleAttributeBuilder {

		private final String name;
		private String description;
		private AttributeRange<Double> range;

		public DoubleAttributeBuilder(String name) {
			this.name = name;
		}

		public DoubleAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		public DoubleAttributeBuilder range(AttributeRange<Double> range) {
			this.range = range;
			return this;
		}

		public DoubleAttribute build() {
			return new DoubleAttribute(name, description, range);
		}
	}
}
