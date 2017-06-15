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

import java.util.Collection;

import org.springframework.hateoas.Identifiable;

import pro.parseq.solvent.datasources.filters.FilterOperator;

/**
 * Track properties that are user to compose track filters.
 * 
 * @author Anton Bragin <a href="mailto:abragin@parseq.pro">abragin@parseq.pro</a>
 * 
 * @param <T> refers to attribute type
 */
public interface Attribute<T extends Comparable<T>> extends Identifiable<Long> {

	/**
	 * Get attribute name that can be displayed. 
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Type of the attribute.
	 * 
	 * @return {@link AttributeType} object
	 */
	AttributeType getType();

	/**
	 * Get detailed attribute description.
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * Range of possible values the attribute may have.
	 * 
	 * @return {@link AttributeRange} object
	 */
	AttributeRange<T> getRange();

	/**
	 * Function to compare attribute values.
	 * 
	 * Used in predicates creation.
	 * 
	 * @param a First object to compare
	 * @param b Second object to compare
	 * @return
	 */
	int compare(T a, T b);

	/**
	 * Create Attribute from string representation.
	 * 
	 * @param s String to create value for
	 * @return throws {@link NoSuchElementException} if no value matches the string provided
	 */
	T parseValue(String s);

	/**
	 * Collection of operator that may be used for filters creation with this attribute.
	 * 
	 * @return Collection of {@link FilterOperator} objects
	 */
	Collection<FilterOperator> operators();
	
	public static interface AttributeBuilder<T extends Comparable<T>> {
		
		public AttributeBuilder<T> description(String description);
		public Attribute<T> build();
		
	}
}
