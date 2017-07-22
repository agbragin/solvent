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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.core.Relation;

import pro.parseq.solvent.datasources.filters.FilterOperator;
import pro.parseq.solvent.exceptions.IllegalAttributeValueException;

/**
 * Any attribute that can be expressed as a set of values.
 * 
 * @author abragin
 *
 * @param <T> Type of underlying object
 */
@Relation(collectionRelation = "attributes")
public class SetAttribute<T extends Comparable<T>> extends AbstractAttribute<T> {

	private static final long serialVersionUID = 6535752266242780467L;

	private static final Logger logger = LoggerFactory.getLogger(SetAttribute.class);
	
	private final Map<String, T> valueMap;
	private final Class<T> valueClass;
	
	protected SetAttribute(String name, String description, Set<? extends T> values, Class<T> valueClass) {
		super(name, AttributeType.SET, description, new AttributeRange<T>(new ArrayList<T>(values)));
		logger.debug("Creating SET attribute: {}, values: {}, class: {}", name, values, valueClass);
		this.valueMap = values.stream()
			.peek(it -> logger.trace("Value: {}, value class: {}", it, it.getClass()))
			.collect(Collectors.toMap(Object::toString, Function.identity()));
		this.valueClass = valueClass;
	}
	
	@Override
	public T parseValue(String s) {
		try {
			logger.trace("Retrieving value of: {} Result: {}, class: {}", 
					s, this.valueMap.get(s), this.valueMap.get(s).getClass());
			return this.valueMap.get(s);
		} catch (Exception e) {
			throw new IllegalAttributeValueException(this, s);
		}
	}

	@Override
	public Collection<FilterOperator> operators() {
		return Arrays.asList(FilterOperator.IN);
	}
	
	public Class<T> getValueClass() {
		return this.valueClass;
	}
	
	public static class SetAttributeBuilder<T extends Comparable<T>> implements AttributeBuilder<T> {
		
		private final String name;
		private String description;
		private Set<? extends T> values;
		private final Class<T> valueClass;
		
		public SetAttributeBuilder(String name, Class<T> valueClass) {
			this.name = name;
			this.valueClass = valueClass;
		}
		
		public SetAttributeBuilder<T> values(Set<? extends T> values) {
			this.values = values;
			return this;
		}
		
		@Override
		public SetAttributeBuilder<T> description(String description) {
			this.description = description;
			return this;
		}
		
		@Override
		public SetAttribute<T> build() {
			return new SetAttribute<T>(this.name, this.description, this.values, this.valueClass);
		}

	}

}
