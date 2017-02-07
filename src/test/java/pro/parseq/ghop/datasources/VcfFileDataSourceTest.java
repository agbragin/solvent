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
package pro.parseq.ghop.datasources;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.attributes.AttributeType;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.VariantBand;
import pro.parseq.ghop.utils.GenomicCoordinate;


@RunWith(SpringRunner.class)
@SpringBootTest
public class VcfFileDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(VcfFileDataSourceTest.class);

	@Autowired
	private Comparator<GenomicCoordinate> comparator;

	private static final String REFERENCE_NAME = "GRCh37.p13";
	private static final String VCF = "/valid_synthetic_with_samples_data.vcf";
	private VcfFileDataSource vcfFileDataSource;

	@Before
	public void createVcfFileDataSource() {

		vcfFileDataSource = new VcfFileDataSource(
				null, getClass().getResourceAsStream(VCF), comparator, REFERENCE_NAME);
	}

	@Test
	public void testCreateVcfFileDataSource() {
		assertNotNull("Data source is created", vcfFileDataSource);
	}

	@Test
	public void testBorders() {

		Contig contig = new Contig(REFERENCE_NAME, "chr20");
		List<GenomicCoordinate> borders = vcfFileDataSource.leftBorders(1,
				new GenomicCoordinate(contig, 14370));
		assertEquals("Check leftmost band", 1, borders.size());
		assertEquals("Check leftmost band", 14370, borders.get(0).getCoord());

		borders = vcfFileDataSource.leftBorders(1, new GenomicCoordinate(contig, 15000));
		assertEquals("Check leftmost band", 1, borders.size());
		assertEquals("Check leftmost band", 14371, borders.get(0).getCoord());

		borders = vcfFileDataSource.rightBorders(10, new GenomicCoordinate(contig, 15000));
		assertEquals("Check right borders", 4, borders.size());

		assertEquals("Check right borders", borders,
			Stream.of(
					new GenomicCoordinate(contig, 17330),
					new GenomicCoordinate(contig, 17331),
					new GenomicCoordinate(contig, 1110696),
					new GenomicCoordinate(contig, 1110697))
		.collect(Collectors.toList()));
	}

	@Test
	public void testBands() {

		Contig contig = new Contig(REFERENCE_NAME, "chr20");
		Set<VariantBand> bands = vcfFileDataSource.leftBordersGenerants(1,
				new GenomicCoordinate(contig, 14370));
		assertEquals("Check leftmost band", 1, bands.size());
		bands = vcfFileDataSource.leftBordersGenerants(1,
				new GenomicCoordinate(contig, 14371));
		assertEquals("Check leftmost band", 1, bands.size());

		bands = vcfFileDataSource.leftBordersGenerants(1,
				new GenomicCoordinate(contig, 1110699));
		assertEquals("Check rightmost band", 2, bands.size());

		bands = vcfFileDataSource.leftBordersGenerants(1,
				new GenomicCoordinate(contig, 1110697));
		assertEquals("Check rightmost band", 2, bands.size());

		bands = vcfFileDataSource.leftBordersGenerants(0,
				new GenomicCoordinate(contig, 1110696));
		assertEquals("Check rightmost band", 2, bands.size());

		bands = vcfFileDataSource.rightBordersGenerants(1,
				new GenomicCoordinate(contig, 1110696));
		assertEquals("Check rightmost band", 2, bands.size());
	}

	@Test
	public void testProperties() {

		long count = vcfFileDataSource.getBands().stream()
			.filter(band -> band.getName().equals("rs6054257"))
			.peek(band -> {

				JsonNode properties = band.getProperties();

				logger.debug("Properties: {}", properties);

				// Obligatory properties
				assertEquals("rs6054257", properties.get("ID").asText());
				assertEquals("G", properties.get("REF").asText());
				assertEquals("A", properties.get("ALT").asText());
				assertEquals(29.0, properties.get("QUAL").asDouble(), Double.MIN_VALUE);

				// Filter properties
				assertEquals(new ArrayList<String>(), 
						StreamSupport.stream(Spliterators.spliteratorUnknownSize(
										properties.get("FILTER").iterator(), Spliterator.ORDERED),
										false)
								.map(node -> node.asText())
								.collect(Collectors.toList()));

				// Info properties
				assertEquals(8, properties.get("LN").asInt());
				assertEquals(1, properties.get("DLN").asInt());
				assertEquals(Stream.of(0).collect(Collectors.toList()), 
						StreamSupport.stream(Spliterators.spliteratorUnknownSize(
										properties.get("AN").iterator(), Spliterator.ORDERED),
										false)
								.map(node -> node.asInt())
								.collect(Collectors.toList()));
				assertTrue(properties.get("VAL").asBoolean());

				// Format properties
				assertEquals("0/1", properties.get("test_sample/GT").asText());
			})
			.count();

		assertEquals("Check variant count", 1, count);

		count = vcfFileDataSource.getBands().stream()
			.filter(band -> band.getName().equals(""))
			.peek(band -> {

				JsonNode properties = band.getProperties();

				logger.info("Properties: {}", properties);

				// Obligatory properties
				assertEquals("", properties.get("ID").asText());
				assertEquals("T", properties.get("REF").asText());
				assertEquals("A", properties.get("ALT").asText());
				assertEquals(3.0, properties.get("QUAL").asDouble(), Double.MIN_VALUE);

				// Filter properties
				assertEquals(Stream.of("dl2", "l10").collect(Collectors.toList()), 
						StreamSupport.stream(Spliterators.spliteratorUnknownSize(
										properties.get("FILTER").iterator(), Spliterator.ORDERED),
										false)
								.map(node -> node.asText())
								.collect(Collectors.toList()));

				// Info properties
				assertEquals(9, properties.get("LN").asInt());
				assertEquals(2, properties.get("DLN").asInt());
				assertEquals(Stream.of(0, 1, 2).collect(Collectors.toList()), 
						StreamSupport.stream(
						Spliterators.spliteratorUnknownSize(
										properties.get("AN").iterator(), Spliterator.ORDERED),
										false)
								.map(node -> node.asInt())
								.collect(Collectors.toList()));
				assertTrue(!properties.get("VAL").asBoolean());

				// Format properties
				assertEquals("1/1", properties.get("test_sample/GT").asText());
			})
			.count();

		assertEquals("Check variant count", 1, count);
	}

	@Test
	public void testFilter() {
		/* TODO: add implementation */
	}

	@Test
	public void testAttributes() {

		Set<Attribute<?>> attributes = vcfFileDataSource.attributes();
		assertNotNull("Attributes are created", attributes);

		for (Attribute<?> attribute : attributes) {
			logger.info("Attribute: {}, type: {}", attribute.getName(), attribute.getType());
		}

		// Check VCF attributes
		assertEquals("Check ID attribute", 1,
			attributes.stream().filter(it -> it.getName().equals("ID")
					&& it.getType().equals(AttributeType.STRING)).count());
		assertEquals("Check REF attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("REF")
						&& it.getType().equals(AttributeType.STRING)).count());
		assertEquals("Check ALT attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("ALT")
						&& it.getType().equals(AttributeType.STRING)).count());
		assertEquals("Check QUAL attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("QUAL")
						&& it.getType().equals(AttributeType.FLOAT)).count());
		assertEquals("Check FILTER attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("FILTER")
						&& it.getType().equals(AttributeType.ENUM)
						&& it.getRange().getValues().size() == 2).count());
		assertEquals("Check AN INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("AN")
						&& it.getType().equals(AttributeType.INTEGER)).count());
		assertEquals("Check VAL INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("VAL")
						&& it.getType().equals(AttributeType.BOOLEAN)).count());
		assertEquals("Check DLN INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("DLN")
						&& it.getType().equals(AttributeType.INTEGER)).count());
		assertEquals("Check LN INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("LN")
						&& it.getType().equals(AttributeType.INTEGER)).count());
		assertEquals("Check GT FORMAT attribute", 1,
				attributes.stream().filter(it -> it.getName().equals("test_sample/GT")
						&& it.getType().equals(AttributeType.ENUM)
						&& new HashSet<>(it.getRange().getValues())
						// NOTE: this Fromat.toString() may not be obvious
							.equals(new HashSet<String>(Arrays.asList("[0/1]", "[1/1]", "[1/2]")))
				).count());
	}

	@Test
	public void testCreateAttribute() {

		String name = "TEST";
		String description = "Test attribute";
		int maxEnumCategories = 5;
		Set<String> values = new HashSet<>(
				Arrays.asList("a", "b", "c", "d"));

		// Test with strings
		Attribute<?> attribute = VcfFileDataSource.createAttributeForValues(name, description, values, maxEnumCategories);
		assertEquals("Enum attribute is created with small number of categories",
				AttributeType.ENUM, attribute.getType());
		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());

		assertNotNull("Check range", attribute.getRange());
		assertEquals("Check range lower bound", "a", attribute.getRange().getLowerBound());
		assertEquals("Check range lower bound", "d", attribute.getRange().getUpperBound());
		assertEquals("Check range values", new ArrayList<String>(values), attribute.getRange().getValues());

		values.add("e");
		attribute = VcfFileDataSource.createAttributeForValues(name, description, values, maxEnumCategories);
		assertEquals("Enum attribute is created when number of categories equals max allowed",
				AttributeType.ENUM, attribute.getType());

		values.add("f");
		attribute = VcfFileDataSource.createAttributeForValues(name, description, values, maxEnumCategories);
		assertEquals("String attribute is created when number of categories exceeds max allowed",
				AttributeType.STRING, attribute.getType());
		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());

		// Test with custom class
		Set<Long> longValues = LongStream.range(0, 5)
				.mapToObj(it -> new Long(it))
				.collect(Collectors.toSet());

		attribute = VcfFileDataSource.createAttributeForValues(name, description, longValues, maxEnumCategories);
		assertNotNull("Check id", attribute.getId());
		assertEquals("Check name", name, attribute.getName());
		assertEquals("Check description", description, attribute.getDescription());

		assertNotNull("Check range", attribute.getRange());
		assertEquals("Check range values", new ArrayList<Long>(longValues), attribute.getRange().getValues());
	}
}
