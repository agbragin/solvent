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
package pro.parseq.solvent.datasources;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import com.fasterxml.jackson.databind.node.ArrayNode;

import pro.parseq.solvent.datasources.DataSource;
import pro.parseq.solvent.datasources.VcfFileDataSource;
import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.attributes.AttributeType;
import pro.parseq.solvent.datasources.attributes.SetAttribute;
import pro.parseq.solvent.datasources.filters.AttributeFilter;
import pro.parseq.solvent.datasources.filters.FilterOperator;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.entities.VariantBand;
import pro.parseq.solvent.services.BufferedReferenceServiceClient;
import pro.parseq.solvent.services.ReferenceService;
import pro.parseq.solvent.services.RemoteReferenceService;
import pro.parseq.solvent.services.configs.RefserviceConfig;
import pro.parseq.solvent.utils.AttributeUtils;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.GenomicCoordinateComparator;


@RunWith(SpringRunner.class)
@SpringBootTest
public class VcfFileDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(VcfFileDataSourceTest.class);

	@Autowired
	private RefserviceConfig config;

	private ReferenceService refservice;
	private Comparator<GenomicCoordinate> comparator;

	private static final String REFERENCE_NAME = "GRCh37.p13";
	private static final String VCF = "/valid_synthetic_with_samples_data.vcf";
	private VcfFileDataSource vcfFileDataSource;

	@Before
	public void createVcfFileDataSource() throws IOException {

		refservice = new BufferedReferenceServiceClient(new RemoteReferenceService(config));
		comparator = new GenomicCoordinateComparator(refservice);

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
		assertEquals("Check leftmost band", 2, borders.size());
		assertEquals("Check leftmost band", 14369, borders.get(0).getCoord());

		borders = vcfFileDataSource.leftBorders(1, new GenomicCoordinate(contig, 15000));
		assertEquals("Check leftmost band", 1, borders.size());
		assertEquals("Check leftmost band", 14370, borders.get(0).getCoord());

		borders = vcfFileDataSource.rightBorders(10, new GenomicCoordinate(contig, 15000));
		assertEquals("Check right borders", 4, borders.size());

		assertEquals("Check right borders", borders,
			Stream.of(
					new GenomicCoordinate(contig, 17329),
					new GenomicCoordinate(contig, 17330),
					new GenomicCoordinate(contig, 1110695),
					new GenomicCoordinate(contig, 1110696))
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
				logger.debug("Checking properties: {}", properties);
				assertEquals(8, properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "LN").asInt());
				assertEquals(1, properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "DLN").asInt());
				assertEquals(Stream.of(0).collect(Collectors.toList()), 
						StreamSupport.stream(Spliterators.spliteratorUnknownSize(
										properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX  + "AN").iterator(),
											Spliterator.ORDERED),
										false)
								.map(node -> node.asInt())
								.collect(Collectors.toList()));
				assertTrue(properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "VAL").asBoolean());

				// Format properties
				assertEquals("0/1", properties.get(
						String.format(VcfFileDataSource.FORMAT_ATTRIBUTE_PREFIX + "test_sample%sGT",
								VcfFileDataSource.SAMPLE_ATTRIBUTE_DELIMETER)).asText());
			})
			.count();

		assertEquals("Check variant count", 1, count);

		count = vcfFileDataSource.getBands().stream()
			.filter(band -> band.getName().equals("T>A"))
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
				assertEquals(9, properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "LN").asInt());
				assertEquals(2, properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "DLN").asInt());
				assertEquals(Stream.of(0, 1, 2).collect(Collectors.toList()), 
						StreamSupport.stream(
						Spliterators.spliteratorUnknownSize(
										properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "AN").iterator(),
											Spliterator.ORDERED),
										false)
								.map(node -> node.asInt())
								.collect(Collectors.toList()));
				assertTrue(!properties.get(VcfFileDataSource.INFO_ATTRIBUTE_PREFIX + "VAL").asBoolean());

				// Format properties
				assertEquals("1/1", properties.get(
						String.format(VcfFileDataSource.FORMAT_ATTRIBUTE_PREFIX + "test_sample%sGT", VcfFileDataSource.SAMPLE_ATTRIBUTE_DELIMETER)).asText());
			})
			.count();

		assertEquals("Check variant count", 1, count);
	}

	@Test
	public void testFilter() throws IOException {
		
		Contig chr1 = new Contig("GRCh37.p13", "chr1");
		GenomicCoordinate coordinateStart = new GenomicCoordinate(chr1, 1);
		
		VcfFileDataSource gatkVcf = new VcfFileDataSource(
				null, getClass().getResourceAsStream("/gatk.vcf"), comparator, REFERENCE_NAME);
		
		gatkVcf.attributes().stream().forEach(attribute -> {
			logger.debug("Attribute: {} of type: {}", attribute, attribute.getType());
		});
		
		@SuppressWarnings("unchecked")
		Attribute<String> refAttribute = (Attribute<String>) gatkVcf.attributes().stream()
			.filter(attrib -> attrib.getName().equals("REF"))
			.findFirst()
			.get();
		
		AttributeFilter<String> refFilter = new AttributeFilter<String>(0, refAttribute, FilterOperator.EQUALS, 
				Arrays.asList("A"), false);
		
		FilterQuery query = new FilterQuery(Arrays.asList(refFilter), null);
		DataSource<VariantBand> filtered = gatkVcf.filter(query);
		
		assertEquals(5, filtered.getBands(coordinateStart, 0, 100).size());
		
		// Test enum filtering
		VcfFileDataSource tvcVcf = new VcfFileDataSource(
				null, getClass().getResourceAsStream("/tvc.vcf"), comparator, REFERENCE_NAME);
		
		// Test variant type filtering
		@SuppressWarnings("unchecked")
		Attribute<?> typeAttribute = (Attribute<String>) tvcVcf.attributes().stream()
			.filter(attrib -> attrib.getName().equals(
					VcfFileDataSource.createInfoAttributeName("TYPE")))
			.findFirst()
			.get();
		
		logger.debug("Attribute class: {}", typeAttribute.getClass());
		
		assertNotNull("Check attribute presence", typeAttribute);
		assertEquals("Check attribute type", AttributeType.SET, typeAttribute.getType());
		assertEquals("Check attribute possible values", 
				new HashSet<>(Arrays.asList("snp", "ins", "del", "mnp")), 
				new HashSet<>(typeAttribute.getRange().getValues()));
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		AttributeFilter indelFilter = new AttributeFilter(0, typeAttribute, FilterOperator.IN, 
				Arrays.asList("del", "ins"), false);
		
		query = new FilterQuery(Arrays.asList(indelFilter), null);
		filtered = tvcVcf.filter(query);
		
		filtered.getBands(coordinateStart, 0, 100).stream()
			.forEach(band -> logger.debug("Band: {}", band));
		
		assertEquals(4, filtered.getBands(coordinateStart, 0, 100).size());
		
		// Test zygosity filtering
		@SuppressWarnings("unchecked")
		Attribute<?> zygosityAttribute = (Attribute<String>) tvcVcf.attributes().stream()
			.filter(attrib -> attrib.getName().equals(
					VcfFileDataSource.createFormatAttributeName("GenSeq-AIP-1", "GT")))
			.findFirst()
			.get();
		
		logger.debug("Attribute class: {}", zygosityAttribute.getClass());
		
		assertNotNull("Check attribute presence", zygosityAttribute);
		assertEquals("Check attribute type", AttributeType.SET, zygosityAttribute.getType());
		
		String heterozygousState = "0/1";
		String homozygousState = "1/1";
		
		assertEquals("Check attribute possible values", 
				new HashSet<>(Arrays.asList(homozygousState, heterozygousState)), 
				new HashSet<>(zygosityAttribute.getRange().getValues()));
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		AttributeFilter homozygousFilter = new AttributeFilter(0, zygosityAttribute, FilterOperator.IN, 
				Arrays.asList(homozygousState), false);
		
		tvcVcf.getBands(coordinateStart, 0, 100).stream().forEach(band -> {
			logger.debug("Variant: {}, properties: {}", band, band.getProperties());
		});
		
		query = new FilterQuery(Arrays.asList(homozygousFilter), null);
		filtered = tvcVcf.filter(query);
		
		filtered.getBands(coordinateStart, 0, 100).stream()
			.forEach(band -> logger.debug("Band: {}", band));
		
		assertEquals(38, filtered.getBands(coordinateStart, 0, 100).size());
		
		// Test non-string set Info filtering
		@SuppressWarnings("unchecked")
		Attribute<Double> pbAttribute = (Attribute<Double>) tvcVcf.attributes().stream()
			.filter(attrib -> attrib.getName().equals(
					VcfFileDataSource.createInfoAttributeName("PB")))
			.findFirst()
			.get();
		
		AttributeFilter<Double> pbFilter = new AttributeFilter<>(0, pbAttribute, FilterOperator.IN, 
				Arrays.asList(0.5), false);
		
		query = new FilterQuery(Arrays.asList(pbFilter), null);
		filtered = tvcVcf.filter(query);
		
		assertEquals(143, filtered.getBands(coordinateStart, 0, 500).size());
		
		// Test non-string set Format filtering
		@SuppressWarnings("unchecked")
		Attribute<Integer> gqAttribute = (Attribute<Integer>) gatkVcf.attributes().stream()
				.peek(it -> logger.debug("Attribute: {}", it))
				.filter(attrib -> attrib.getName().equals(
						VcfFileDataSource.createFormatAttributeName("20", "GQ")))
				.findFirst()
				.get();
		
		assertEquals("Check attribute type", AttributeType.SET, gqAttribute.getType());
		
		AttributeFilter<Integer> gqFilter = new AttributeFilter<>(0, gqAttribute, FilterOperator.IN, 
				Arrays.asList(99), false);
		
		query = new FilterQuery(Arrays.asList(gqFilter), null);
		filtered = gatkVcf.filter(query);
		
		assertEquals(35, filtered.getBands(coordinateStart, 0, 500).size());
		
	}

	@Test
	public void testAttributes() {

		List<Attribute<?>> attributes = vcfFileDataSource.attributes();
		assertNotNull("Attributes are created", attributes);
		
		this.testAttributeDiversityAndType(attributes, AttributeUtils.DEFAULT_MAX_SET_CATEGORIES);

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
						&& it.getType().equals(AttributeType.SET)
						&& it.getRange().getValues().size() == 2).count());
		assertEquals("Check VAL INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals(
							VcfFileDataSource.createInfoAttributeName("VAL"))
						&& it.getType().equals(AttributeType.BOOLEAN)).count());
		
		// Note that due to low value diversity these attributes has type SET
		assertEquals("Check AN INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals(
							VcfFileDataSource.createInfoAttributeName("AN"))
						&& it.getType().equals(AttributeType.SET)).count());
		assertEquals("Check DLN INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals(
							VcfFileDataSource.createInfoAttributeName("DLN"))
						&& it.getType().equals(AttributeType.SET)).count());
		assertEquals("Check LN INFO attribute", 1,
				attributes.stream().filter(it -> it.getName().equals(
							VcfFileDataSource.createInfoAttributeName("LN"))
						&& it.getType().equals(AttributeType.SET)).count());
		assertEquals("Check GT FORMAT attribute", 1,
				attributes.stream()
					.filter(it -> it.getName().equals(
							VcfFileDataSource.createFormatAttributeName("test_sample", "GT"))
						&& it.getType().equals(AttributeType.SET)
						&& new HashSet<>(it.getRange().getValues())
							.equals(new HashSet<String>(Arrays.asList("0/1", "1/1", "1/2")))
				).count());

	}
	
	/**
	 * Test that attribute type is determined using attribute values diversity.
	 * 
	 * @param attributes attributes to test
	 * @param maxNumberOfSetCategories how many values can have attribute to be SET attribute
	 */
	private void testAttributeDiversityAndType(List<Attribute<?>> attributes, int maxNumberOfSetCategories) {
		
		// Check that attributes with low diversity has SET format
		attributes.stream()
			// Boolean attribute always has BOOLEAN type
			.filter(attribute -> !attribute.getType().equals(AttributeType.BOOLEAN))
			.filter(attribute -> attribute.getRange() != null && attribute.getRange().getValues() != null)
			.filter(attribute -> attribute.getRange().getValues().size() <= maxNumberOfSetCategories)
			.peek(attribute -> logger.debug("Checking attribute: {} with number of values: {} and type: {}",
					attribute, attribute.getRange().getValues().size(), attribute.getType()))
			.forEach(attribute -> {
				
				assertEquals("Attributes with low diversity has SET format", AttributeType.SET, attribute.getType());
				
			});
		
		// Check that attributes with broad diversity has non SET format
		attributes.stream()
			// Boolean attribute always has BOOLEAN type
			.filter(attribute -> !attribute.getType().equals(AttributeType.BOOLEAN))
			.filter(attribute -> attribute.getRange() == null 
				|| attribute.getRange().getValues() == null
				|| attribute.getRange().getValues().size() > maxNumberOfSetCategories
			)
			.peek(attribute -> logger.debug("Checking attribute: {} with type: {}",
					attribute, attribute.getType()))
			.forEach(attribute -> {
				
				assertFalse("Attributes with low diversity has SET format", attribute.getType().equals(AttributeType.SET));
				
			});
	}
	
	@Test
	public void testDatabaseVcfFiles() throws IOException {
		
		VcfFileDataSource clinvar = new VcfFileDataSource(
				null, getClass().getResourceAsStream("/clinvar.vcf"), comparator, REFERENCE_NAME);
		this.testAttributeDiversityAndType(clinvar.attributes(), AttributeUtils.DEFAULT_MAX_SET_CATEGORIES);
		
		// Collect attributes to id map to check by attribute name
		Map<String, Attribute<?>> attributeMap = clinvar.attributes().stream()
			.peek(it -> logger.debug("Attribute: {}, name: {}, type: {}",
					it, it.getName(), it.getType()))
			.collect(Collectors.toMap(Attribute::getName, Function.identity()));
		
		// Test work with attributes with Number=. and non Flag types
		Attribute<?> clnalle = attributeMap.get(VcfFileDataSource.createInfoAttributeName("CLNALLE"));
		assertNotNull(clnalle);
		assertEquals(AttributeType.SET, clnalle.getType());
		assertEquals(Integer.class, ((SetAttribute<?>) clnalle).getValueClass());
		
		Contig chr1 = new Contig("GRCh37.p13", "chr1");
		GenomicCoordinate coordinateStart = new GenomicCoordinate(chr1, 1);
		Set<VariantBand> bands = clinvar.getBands(coordinateStart, 0, 1);
		VariantBand band = bands.iterator().next();
		assertNotNull(band);
		
		// Check property
		JsonNode attribute = band.getProperties().get(VcfFileDataSource.createInfoAttributeName("CLNALLE"));
		assertNotNull(attribute);
		assertTrue(attribute.isArray());
		assertEquals("Check property value", 1, ((ArrayNode) attribute).get(0).asInt());
		
		
		Attribute<?> clnsig = attributeMap.get(VcfFileDataSource.createInfoAttributeName("CLNSIG"));
		assertNotNull(clnsig);
		assertEquals(AttributeType.STRING, clnsig.getType());
		
		attribute = band.getProperties().get(VcfFileDataSource.createInfoAttributeName("CLNSIG"));
		assertNotNull(attribute);
		assertTrue(attribute.isArray());
		assertEquals("Check property value", "0|5", ((ArrayNode) attribute).get(0).asText());
	}
	
	@Test
	public void testVariantCallersVcfFiles() throws IOException {
		
		Contig chr1 = new Contig("GRCh37.p13", "chr1");
		GenomicCoordinate coordinateStart = new GenomicCoordinate(chr1, 1);
		
		// GATK
		VcfFileDataSource gatkVcf = new VcfFileDataSource(
				null, getClass().getResourceAsStream("/gatk.vcf"), comparator, REFERENCE_NAME);
		
		this.testAttributeDiversityAndType(gatkVcf.attributes(), AttributeUtils.DEFAULT_MAX_SET_CATEGORIES);
		
		assertEquals("Check total nuber of bands", 35, gatkVcf.getBands(coordinateStart, 0, 100).size());
		
		GenomicCoordinate firstBandStart = new GenomicCoordinate(chr1, 1);
		Set<VariantBand> bands = gatkVcf.getBands(firstBandStart, 0, 2);
		
		assertEquals("First band retrieved", 1, bands.size());
		
		VariantBand band = bands.iterator().next();
		
		// Check first band
		assertEquals(new GenomicCoordinate(chr1, 899), band.getStartCoord());
		assertEquals(new GenomicCoordinate(chr1, 900), band.getEndCoord());
		assertEquals("A>ATTTT", band.getName());
		
		JsonNode bandProperties = band.getProperties();
		assertNotNull(bandProperties);
		
		// Check individual properties
		assertEquals("", bandProperties.get("ID").asText());
		assertEquals("A", bandProperties.get("REF").asText());
		assertEquals("ATTTT", bandProperties.get("ALT").asText());
		assertEquals(341.73, bandProperties.get("QUAL").asDouble(), Double.MIN_NORMAL);
		assertTrue(bandProperties.get("FILTER").isArray());
		
		ArrayNode filters = (ArrayNode) bandProperties.get("FILTER");
		assertEquals(0, filters.size());
		
		logger.debug("Properties: {}", bandProperties);
		
		// TVC
		VcfFileDataSource tvcVcf = new VcfFileDataSource(
				null, getClass().getResourceAsStream("/tvc.vcf"), comparator, REFERENCE_NAME);
		
		this.testAttributeDiversityAndType(tvcVcf.attributes(), AttributeUtils.DEFAULT_MAX_SET_CATEGORIES);

		// TVC with VariFind properties
		VcfFileDataSource tvcVarifindVcf = new VcfFileDataSource(
				null, getClass().getResourceAsStream("/tvc-varifind.vcf"), comparator, REFERENCE_NAME);
		
		this.testAttributeDiversityAndType(tvcVarifindVcf.attributes(), AttributeUtils.DEFAULT_MAX_SET_CATEGORIES);
		
	}
 
}
