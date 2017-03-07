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
import java.util.EnumSet;

import org.springframework.hateoas.core.Relation;

import pro.parseq.ghop.datasources.filters.FilterOperator;
import pro.parseq.ghop.utils.StringUtils;

@Relation(collectionRelation = "attributes")
public class EnumAttribute<T extends Enum<T>> extends SetAttribute<T> {

	private final Class<T> declaringClass;

	private EnumAttribute(String name, String description, Class<T> enumerationClass) {
		super(name, description, EnumSet.allOf(enumerationClass));
		this.declaringClass = enumerationClass;
	}

	@Override
	public T parseValue(String s) {
		return Enum.valueOf(genericType(), StringUtils.enumValueString(s));
	}

	@Override
	public Collection<FilterOperator> operators() {
		return Arrays.asList(FilterOperator.IN);
	}

	protected Class<T> genericType() {
		return this.declaringClass;
	};
	
	
	public static class EnumAttributeBuilder<T extends Enum<T>> {
		
		private final String name;
		private final Class<T> enumerationClass;
		private String description;

		public EnumAttributeBuilder(String name, Class<T> enumerationClass) {
			this.name = name;
			this.enumerationClass = enumerationClass;
		}

		public EnumAttributeBuilder<T> description(String description) {
			this.description = description;
			return this;
		}

		public EnumAttribute<T> build() {
			return new EnumAttribute<T>(name, description, enumerationClass);
		}
		
	}
}
