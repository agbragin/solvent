package pro.parseq.ghop.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.attributes.AttributeType;

public class AttributeUtilsTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AttributeUtilsTest.class);
	
	@Test
	public void testCreateAttribute() {

		String name = "TEST";
		String description = "Test attribute";
		int maxSetCategories = 5;
		
		
		logger.info("Creating String attribute");
		Set<String> values = new HashSet<>(
				Arrays.asList("a", "b", "c", "d", "e", "f"));
		Attribute<?> attribute = AttributeUtils.createAttributeForValues(name, description, 
				values, String.class, null, maxSetCategories);
		assertEquals("String attribute is created properly",
				AttributeType.STRING, attribute.getType());
		
		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());

		
		logger.info("Creating Long attribute");
		Set<Long> longValues = LongStream.range(0, 10)
				.mapToObj(it -> new Long(it))
				.collect(Collectors.toSet());
		attribute = AttributeUtils.createAttributeForValues(name, description, 
				longValues, Long.class, null, maxSetCategories);
		assertEquals("Integer attribute is created properly",
				AttributeType.INTEGER, attribute.getType());
		assertNull("Null range value for non set type", attribute.getRange());
		
		
		logger.info("Creating Integer attribute");
		Set<Integer> integerValues = IntStream.range(0, 10)
				.mapToObj(it -> new Integer(it))
				.collect(Collectors.toSet());
		attribute = AttributeUtils.createAttributeForValues(name, description, 
				integerValues, Integer.class, null, maxSetCategories);
		assertEquals("Integer attribute is created properly",
				AttributeType.INTEGER, attribute.getType());
		assertNull("Null range value for non set type", attribute.getRange());
		
		
		logger.info("Creating set attribute for Double");
		Set<Double> doubleValues = DoubleStream.iterate(0.5, n -> n + 1)
				.limit(10)
				.boxed()
				.collect(Collectors.toSet());
		attribute = AttributeUtils.createAttributeForValues(name, description, doubleValues, Double.class, null, maxSetCategories);
		assertEquals("Float attribute is created properly",
				AttributeType.FLOAT, attribute.getType());
		
		
		logger.info("Creating Boolean attribute");
		Set<Boolean> booleanValues = new HashSet<>(Arrays.asList(true, false));
		attribute = AttributeUtils.createAttributeForValues(name, description, 
				booleanValues, Boolean.class, null, maxSetCategories);
		assertEquals("Boolean attribute is created properly",
				AttributeType.BOOLEAN, attribute.getType());
	
	}
	
	@Test
	public void testCreateSetAttribute() {
		
		String name = "SET TEST";
		String description = "Set attrinute test";
		int maxEnumCategories = 5;
		Set<String> values = new HashSet<>(
				Arrays.asList("a", "b", "c", "d"));
		
		
		logger.info("Creating set attribute for String");
		Attribute<?> attribute = AttributeUtils.createAttributeForValues(name, description, 
				values, String.class, null, maxEnumCategories);
		assertEquals("Set attribute is created with small number of categories",
				AttributeType.SET, attribute.getType());
		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());

		assertNotNull("Check range", attribute.getRange());
		assertEquals("Check range lower bound", "a", attribute.getRange().getLowerBound());
		assertEquals("Check range lower bound", "d", attribute.getRange().getUpperBound());
		assertEquals("Check range values", new ArrayList<String>(values), attribute.getRange().getValues());

		values.add("e");
		attribute = AttributeUtils.createAttributeForValues(name, description, values, String.class, null, maxEnumCategories);
		assertEquals("Set attribute is created when number of categories equals max allowed",
				AttributeType.SET, attribute.getType());
		
		
		logger.info("Creating set attribute for Long");
		Set<Long> longValues = LongStream.range(0, 5)
				.mapToObj(it -> new Long(it))
				.collect(Collectors.toSet());

		attribute = AttributeUtils.createAttributeForValues(name, description, 
				longValues, Long.class, null, maxEnumCategories);
		assertEquals("Set attribute is created when number of categories equals max allowed",
				AttributeType.SET, attribute.getType());

		assertNotNull("Check range", attribute.getRange());
		assertEquals("Check lower bound", 0L, attribute.getRange().getLowerBound());
		assertEquals("Check lower bound", 4L, attribute.getRange().getUpperBound());
		assertEquals("Check values", 
				longValues.stream()
						.sorted()
						.collect(Collectors.toList()), 
				attribute.getRange().getValues());
		
		
		logger.info("Creating set attribute for Integer");
		Set<Integer> integerValues = IntStream.range(0, 5)
				.mapToObj(it -> new Integer(it))
				.collect(Collectors.toSet());
		attribute = AttributeUtils.createAttributeForValues(name, description, 
				integerValues, Integer.class, null, maxEnumCategories);
		assertEquals("Set attribute is created when number of categories equals max allowed",
				AttributeType.SET, attribute.getType());
		
		assertNotNull("Check range", attribute.getRange());
		assertEquals("Check lower bound", 0, attribute.getRange().getLowerBound());
		assertEquals("Check lower bound", 4, attribute.getRange().getUpperBound());
		assertEquals("Check values", 
				integerValues.stream()
						.sorted()
						.collect(Collectors.toList()), 
				attribute.getRange().getValues());
		
		
		logger.info("Creating set attribute for Double");
		Set<Double> doubleValues = DoubleStream.iterate(0.5, n -> n + 1)
				.limit(5)
				.boxed()
				.collect(Collectors.toSet());
		attribute = AttributeUtils.createAttributeForValues(name, description, doubleValues, Double.class, null, maxEnumCategories);
		assertEquals("Set attribute is created when number of categories equals max allowed",
				AttributeType.SET, attribute.getType());
		
		assertNotNull("Check range", attribute.getRange());
		assertEquals("Check lower bound", 0.5, (Double) attribute.getRange().getLowerBound(), Double.MIN_VALUE);
		assertEquals("Check upper bound", 4.5, (Double) attribute.getRange().getUpperBound(), Double.MIN_VALUE);
		assertEquals("Check values", 
				doubleValues.stream()
						.sorted()
						.collect(Collectors.toList()), 
				attribute.getRange().getValues());
		
	}

}
