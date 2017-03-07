/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.exceptions.IllegalAggregateOperatorException;
import pro.parseq.ghop.exceptions.UnsupportedAttributeFilterOperatorException;
import pro.parseq.ghop.services.ContigSequence;
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
								.anyMatch(value -> attribute
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

	public static final Predicate<ContigSequence> isFragmentOf(Contig contig) {

		return new Predicate<ContigSequence>() {

			@Override
			public boolean test(ContigSequence t) {
				return contig.equals(t.getContig());
			}
		};
	}
}
