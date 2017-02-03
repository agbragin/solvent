package pro.parseq.ghop.datasources.attributes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SetAttributeTest {

	@Test
	public void testSetAttributeBuilder() {

		String name = "TEST";
		String description = "Test description";
		Set<Long> values = LongStream.range(0, 5)
				.mapToObj(it -> new Long(it))
				.collect(Collectors.toSet());

		Attribute<Long> attribute = new SetAttribute.SetAttributeBuilder<Long>(name)
				.setDescription(description)
				.setValues(values)
				.build();

		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());
		assertEquals("Check type", AttributeType.ENUM, attribute.getType());
		assertNotNull("Check range", attribute.getRange());

		assertEquals("Check range lower bound", new Long(0), attribute.getRange().getLowerBound());
		assertEquals("Check range lower bound", new Long(4), attribute.getRange().getUpperBound());
		assertEquals("Check range values", new ArrayList<Long>(values), attribute.getRange().getValues());
	}
}
