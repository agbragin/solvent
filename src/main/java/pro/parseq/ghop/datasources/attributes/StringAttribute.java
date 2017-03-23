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

	public static final class StringAttributeBuilder implements AttributeBuilder<String> {

		private final String name;
		private String description;

		public StringAttributeBuilder(String name) {
			this.name = name;
		}

		@Override
		public StringAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public StringAttribute build() {
			return new StringAttribute(name, description);
		}
	}
}
