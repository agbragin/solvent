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
package pro.parseq.solvent.datasources.attributes;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.core.Relation;

import pro.parseq.solvent.datasources.filters.FilterOperator;
import pro.parseq.solvent.exceptions.IllegalAttributeValueException;

/**
 * 
 * TODO: Consider parameter change from Integer to Long.
 * Now this is impossible since VcfExplorer stores integer values as Integer.
 *
 */
@Relation(collectionRelation = "attributes")
public class IntegerAttribute extends AbstractAttribute<Integer> {

	private static final long serialVersionUID = 184910187499483599L;

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

	public static final class IntegerAttributeBuilder implements AttributeBuilder<Integer> {

		private final String name;
		private String description;
		private AttributeRange<Integer> range;

		public IntegerAttributeBuilder(String name) {
			this.name = name;
		}

		@Override
		public IntegerAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		public IntegerAttributeBuilder range(AttributeRange<Integer> range) {
			this.range = range;
			return this;
		}

		@Override
		public IntegerAttribute build() {
			return new IntegerAttribute(name, description, range);
		}
	}
}
