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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.attributes.AttributeType;
import pro.parseq.solvent.datasources.attributes.SetAttribute;

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

		Attribute<Long> attribute = new SetAttribute.SetAttributeBuilder<Long>(name, Long.class)
				.description(description)
				.values(values)
				.build();

		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());
		assertEquals("Check type", AttributeType.SET, attribute.getType());
		assertNotNull("Check range", attribute.getRange());

		assertEquals("Check range lower bound", new Long(0), attribute.getRange().getLowerBound());
		assertEquals("Check range lower bound", new Long(4), attribute.getRange().getUpperBound());
		assertEquals("Check range values", new ArrayList<Long>(values), attribute.getRange().getValues());
		
		values.stream()
				.forEach(it -> assertTrue("Check value retrieval", it.equals(attribute.parseValue(it.toString()))));

	}
}
