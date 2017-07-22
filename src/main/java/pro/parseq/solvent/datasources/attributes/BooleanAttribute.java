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
import java.util.HashSet;
import java.util.Set;

import org.springframework.hateoas.core.Relation;

import pro.parseq.solvent.datasources.filters.FilterOperator;
import pro.parseq.solvent.exceptions.IllegalAttributeValueException;

@Relation(collectionRelation = "attributes")
public class BooleanAttribute extends SetAttribute<Boolean> {

	private static final long serialVersionUID = 4641569087913061720L;
	private static final Set<Boolean> values = new HashSet<>();
	
	static {
		values.add(Boolean.TRUE);
		values.add(Boolean.FALSE);
	}
	
	private BooleanAttribute(String name, String description) {
		super(name, description, BooleanAttribute.values, Boolean.class);
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
	
	@Override
	public AttributeType getType() {
		return AttributeType.BOOLEAN;
	}

	public static final class BooleanAttributeBuilder implements AttributeBuilder<Boolean> {

		private final String name;
		private String description;

		public BooleanAttributeBuilder(String name) {
			this.name = name;
		}

		@Override
		public BooleanAttributeBuilder description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public BooleanAttribute build() {
			return new BooleanAttribute(name, description);
		}
	}
}
