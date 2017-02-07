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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.springframework.hateoas.core.Relation;

import pro.parseq.ghop.datasources.filters.FilterOperator;

/**
 * Any attribute that can be expressed as a set of values.
 * 
 * @author abragin
 *
 * @param <T> Type of underlying object
 */
@Relation(collectionRelation = "attributes")
public class SetAttribute<T extends Comparable<T>> implements Attribute<T> {

	private final AbstractAttribute<T> attribute;
	
	protected SetAttribute(String name, String description, Set<T> values) {
		// Create attribute range from set of values
		AttributeRange<T> attributeRange = new AttributeRange<T>(new ArrayList<>(values));
		
		this.attribute = new AbstractAttribute<T>(name, AttributeType.ENUM, description, attributeRange) {
			
			@Override
			public T parseValue(String s) {
				return this.getRange().getValues()
					.stream()
					.filter(it -> it.toString().equals(s))
					.findFirst().get();
			}

			@Override
			public Collection<FilterOperator> operators() {
				return Arrays.asList(FilterOperator.IN);
			}
			
		};
		
	}
	
	@Override
	public T parseValue(String s) {
		return this.attribute.parseValue(s);
	}

	@Override
	public Collection<FilterOperator> operators() {
		return this.attribute.operators();
	}

	@Override
	public Long getId() {
		return attribute.getId();
	}

	@Override
	public String getName() {
		return attribute.getName();
	}

	@Override
	public AttributeType getType() {
		return attribute.getType();
	}

	@Override
	public String getDescription() {
		return attribute.getDescription();
	}

	@Override
	public AttributeRange<T> getRange() {
		return attribute.getRange();
	}

	@Override
	public int compare(T a, T b) {
		return attribute.compare(a, b);
	}
	
	@Override
	public int hashCode() {
		return this.attribute.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.attribute.equals(obj);
	}

	@Override
	public String toString() {
		return this.attribute.toString();
	}
	
	public static class SetAttributeBuilder<T extends Comparable<T>> {
		
		private final String name;
		private String description;
		private Set<T> values;
		
		public SetAttributeBuilder(String name) {
			this.name = name;
		}
		
		public SetAttributeBuilder<T> setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public SetAttributeBuilder<T> setValues(Set<T> values) {
			this.values = values;
			return this;
		}
		
		public SetAttribute<T> build() {
			return new SetAttribute<T>(this.name, this.description, this.values);
		}
	}

}
