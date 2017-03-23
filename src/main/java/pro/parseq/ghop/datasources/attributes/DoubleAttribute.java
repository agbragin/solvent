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

	public static final class DoubleAttributeBuilder implements AttributeBuilder<Double> {

		private final String name;
		private String description;
		private AttributeRange<Double> range;

		public DoubleAttributeBuilder(String name) {
			this.name = name;
		}

		public DoubleAttributeBuilder range(AttributeRange<Double> range) {
			this.range = range;
			return this;
		}
		
		@Override
		public DoubleAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public DoubleAttribute build() {
			return new DoubleAttribute(name, description, range);
		}
	}
}
