package pro.parseq.ghop.utils;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.JsonNode;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.AttributeFilter;
import pro.parseq.ghop.entities.AttributeFilterAggregateEntity;
import pro.parseq.ghop.exceptions.IllegalAggregateOperatorException;
import pro.parseq.ghop.exceptions.UnsupportedAttributeFilterOperatorException;
import pro.parseq.ghop.utils.BedUtils.Region;

public class PredicateUtils {

	public static final Predicate<Region> regionIdentity = new Predicate<Region>() {

		@Override
		public boolean test(Region t) {
			return true;
		}
	};

	public static final <P extends PropertiesAware, C extends Comparable<C>> Predicate<P> attributePredicate(
			AttributeFilter<C> attributeFilter) {

		return new Predicate<P>() {

			@Override
			public boolean test(P t) {

				if (!t.getProperties().has(attributeFilter.getAttribute().getName())) {
					return attributeFilter.isIncludeNulls();
				}

				Attribute<C> attribute = attributeFilter.getAttribute();
				JsonNode property = t.getProperties().get(attribute.getName());
				switch (attribute.getType()) {
				case BOOLEAN:
					switch (attributeFilter.getOperator()) {
					case EQUALS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) == 0);

					case NOTEQUALS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) != 0);

					default:
						throw new UnsupportedAttributeFilterOperatorException(
								attribute, attributeFilter.getOperator());
					}

				case INTEGER:
				case FLOAT:
					switch (attributeFilter.getOperator()) {
					case EQUALS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) == 0);

					case NOTEQUALS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) != 0);
					case GREATER:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) > 0);

					case GREATEREQ:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) >= 0);

					case LESS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) < 0);

					case LESSEQ:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) <= 0);

					default:
						throw new UnsupportedAttributeFilterOperatorException(
								attribute, attributeFilter.getOperator());
					}

				case STRING:
					switch (attributeFilter.getOperator()) {
					case EQUALS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) == 0);

					case NOTEQUALS:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) != 0);

					case LIKE:
						return attributeFilter.getValues().stream()
								.allMatch(value -> property.asText()
										.contains(value.toString()));

					case ILIKE:
						return attributeFilter.getValues().stream()
								.allMatch(value -> property.asText()
										.toLowerCase()
										.contains(value.toString().toLowerCase()));

					default:
						throw new UnsupportedAttributeFilterOperatorException(
								attribute, attributeFilter.getOperator());
					}

				case ENUM:
					switch (attributeFilter.getOperator()) {
					case IN:
						return attributeFilter.getValues().stream()
								.allMatch(value -> attribute
										.parseValue(property.asText())
										.compareTo(value) == 0);

					default:
						throw new UnsupportedAttributeFilterOperatorException(
								attribute, attributeFilter.getOperator());
					}

				default:
					return false;
				}
			}
		};
	}

	public static final Predicate<PropertiesAware> aggregatePredicate(
			List<AttributeFilter<?>> filters,
			List<AttributeFilterAggregateEntity> aggregates) {

		Map<Long, Predicate<PropertiesAware>> predicates = new HashMap<>();
		filters.stream().forEach(filter -> predicates.put(filter.getId(),
				PredicateUtils.attributePredicate(filter)));

		if (aggregates == null || aggregates.size() == 0) {
			return predicates.values().stream().reduce(Predicate::and).get();
		}

		aggregates.stream().forEachOrdered(agg -> {

			switch (agg.getOperator()) {
			case AND:
				predicates.put(agg.getId(), agg.getFilters().stream()
						.map(predicates::get).reduce(Predicate::and).get());
				break;

			case OR:
				predicates.put(agg.getId(), agg.getFilters().stream()
						.map(predicates::get).reduce(Predicate::or).get());
				break;

			case XOR:
				predicates.put(agg.getId(), agg.getFilters().stream()
						.map(predicates::get).reduce(PredicateUtils::xor).get());
				break;

			default:
				throw new IllegalAggregateOperatorException(agg.getOperator().toString());
			}

			agg.getFilters().stream().forEach(predicates::remove);
		});

		return predicates.values().stream().reduce(Predicate::and).get();
	}

	public static final <T> Predicate<T> xor(Predicate<T> i, Predicate<T> j) {

		Predicate<T> isI = i.and(j.negate());
		Predicate<T> isJ = j.and(i.negate());

		return isI.or(isJ);
	}

	public static final Predicate<String> isNumeric = new Predicate<String>() {

		@Override
		public boolean test(String t) {
			return t.chars().allMatch(new IntPredicate() {

				@Override
				public boolean test(int value) {
					return Character.isDigit(value);
				}
			});
		}
	};
}
