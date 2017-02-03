package pro.parseq.ghop.datasources.attributes;

import java.util.Collection;

import org.springframework.hateoas.Identifiable;

import pro.parseq.ghop.datasources.filters.FilterOperator;

/**
 * Track properties that are user to compose track filters.
 * 
 * @author abragin
 *
 * @param <T>
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
}
