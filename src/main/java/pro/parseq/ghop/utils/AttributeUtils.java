package pro.parseq.ghop.utils;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.attributes.Attribute.AttributeBuilder;
import pro.parseq.ghop.datasources.attributes.AttributeRange;
import pro.parseq.ghop.datasources.attributes.IntegerAttribute.IntegerAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.SetAttribute.SetAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.StringAttribute.StringAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.DoubleAttribute.DoubleAttributeBuilder;
import pro.parseq.ghop.datasources.attributes.BooleanAttribute.BooleanAttributeBuilder;

public class AttributeUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(AttributeUtils.class);
	
	/**
	 * Maximum number of categories that will retain the property as enum,
	 * otherwise string attribute will be created
	 */
	public final static int DEFAULT_MAX_SET_CATEGORIES = 10;
	
	/**
	 * Create attribute for provided values accounting for values diversity.
	 * 
	 * Since attribute diversity may be small we could create SetAttribute<T> instead of Attribute<T>.
	 * Default DEFAULT_MAX_SET_CATEGORIES is used to define set of values.
	 * 
	 * @param name name of the attribute
	 * @param description attribute description 
	 * @param values set of possible values, may be null
	 * @param attributeClazz Java class corresponding to the attribute
	 * @return
	 */
	public static <T extends Comparable<T>> Attribute<T> createAttributeForValues(String name, String description, 
			Set<? extends T> values, Class<T> attributeClazz, AttributeRange<T> attributeRange) {
		return createAttributeForValues(name, description, values, attributeClazz, attributeRange, DEFAULT_MAX_SET_CATEGORIES);
	}
	
	/**
	 * Create attribute for provided values accounting for values diversity.
	 * 
	 * Since attribute diversity may be small we could create SetAttribute<T> instead of Attribute<T>.
	 * Default DEFAULT_MAX_SET_CATEGORIES is used to define set of values.
	 * AttributeRange set to null.
	 * 
	 * @param name name of the attribute
	 * @param description attribute description 
	 * @param values set of possible values, may be null
	 * @param attributeClazz Java class corresponding to the attribute
	 * @return
	 */
	public static <T extends Comparable<T>> Attribute<T> createAttributeForValues(String name, String description, 
			Set<? extends T> values, Class<T> attributeClazz) {
		return createAttributeForValues(name, description, values, attributeClazz, null);
	}	
	
	
	/**
	 * Create attribute for provided values accounting for values diversity.
	 * 
	 * Since attribute diversity may be small we could create SetAttribute<T> instead of Attribute<T>.
	 * 
	 * @param name name of the attribute
	 * @param description attribute description 
	 * @param values set of possible values, may be null
	 * @param attributeClazz Java class corresponding to the attribute
	 * @param maxSetCategories maximum number of categories to represent attribute as SetAttribute 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<T>> Attribute<T> createAttributeForValues(String name, String description, 
			Set<? extends T> values, Class<T> attributeClazz, AttributeRange<T> attributeRange, int maxSetCategories) {
		
		logger.debug("Building attribute: {}", name);
		AttributeBuilder<T> attributeBuilder;
		
		if (attributeClazz.equals(Boolean.class)) {
			
			logger.debug("Building boolean attribute");
			attributeBuilder = (AttributeBuilder<T>) new BooleanAttributeBuilder(name);
			
		} else if (values.size() > maxSetCategories) {
			
			logger.debug("Number of values: {} exceeds maximum Emum categories: {}. "
					+ "Creating attribute according to specified class: {}", 
					values.size(), maxSetCategories, attributeClazz);
			
			if (attributeClazz.equals(Byte.class) 
					|| attributeClazz.equals(Integer.class)
					|| attributeClazz.equals(Long.class)) {
				
				logger.debug("Building integer attribute");
				attributeBuilder = (AttributeBuilder<T>) new IntegerAttributeBuilder(name)
						.range((AttributeRange<Integer>) attributeRange);
				
			} else if (attributeClazz.equals(Float.class)
					|| attributeClazz.equals(Double.class)) {
				
				logger.debug("Building double attribute");
				attributeBuilder = (AttributeBuilder<T>) new DoubleAttributeBuilder(name)
						.range((AttributeRange<Double>) attributeRange);

			} else {
				
				logger.debug("Building string attribute");
				attributeBuilder = (AttributeBuilder<T>) new StringAttributeBuilder(name);
			}
			
		} else {

			logger.debug("Creating Set attribute {} with number of categories: {} and values: {}",
					name, values.size(), values);
			attributeBuilder = new SetAttributeBuilder<T>(name, attributeClazz)
					.values(values);
		}
		
		return attributeBuilder.description(description).build();
	}

}
