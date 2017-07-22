package pro.parseq.solvent.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pro.parseq.solvent.entities.Band;
import pro.parseq.solvent.entities.BedBand;
import pro.parseq.solvent.entities.ChromosomeBand;
import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.entities.ReferenceGenome;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.utils.GenomicCoordinate;

public class BandTest {

	private final static Logger logger = LoggerFactory.getLogger(BandTest.class);

	private static final ReferenceGenome TEST_REFERENCE = new ReferenceGenome("TestReference");
	private static final Contig FOO_CONTIG = new Contig(TEST_REFERENCE, "foo", 8);
	private static final Contig BAR_CONTIG = new Contig(TEST_REFERENCE, "bar", 8);

	private static final GenomicCoordinate FOO_START = new GenomicCoordinate(FOO_CONTIG, 1);
	private static final GenomicCoordinate FOO_INTERNAL = new GenomicCoordinate(FOO_CONTIG, 4);
	private static final GenomicCoordinate FOO_END = new GenomicCoordinate(FOO_CONTIG, 9);

	private static final Track BED_TRACK = new Track("Some test BED track");
	private static final Track CHROMOSOMES_TRACK = new Track("Some chromosomes' track");
	private static final String FOO_REGION = "Some test BED region";

	private static final JsonNode PROPERTIES_SET;
	static {

		ObjectNode props = JsonNodeFactory.instance.objectNode();

		props.put("some_integer_field", 1);
		props.put("some_string_field", "some string value");
		props.putArray("some_array_field").add(1).add(2).add(3);

		PROPERTIES_SET = props;
	}

	private static final JsonNode PROPERTIES_SET_DUPLICATE;
	static {

		ObjectNode props = JsonNodeFactory.instance.objectNode();

		props.putArray("some_array_field").add(1).add(2).add(3);
		props.put("some_string_field", "some string value");
		props.put("some_integer_field", 1);

		PROPERTIES_SET_DUPLICATE = props;
	}

	private static final JsonNode DIFFERENT_PROPERTIES_SET;
	static {

		ObjectNode props = JsonNodeFactory.instance.objectNode();

		props.put("some_string_field", "some string value");
		props.put("some_integer_field", 1);
		// This is the only different thing from previous properties
		props.putArray("some_array_field").add(2).add(3).add(3);

		DIFFERENT_PROPERTIES_SET = props;
	}

	@Test
	public void testBands() throws Exception {

		logger.info("Test different types band instantiation and differentiation");

		Band fooFirstHalfBedBand = new BedBand(BED_TRACK, FOO_START, FOO_INTERNAL, FOO_REGION, PROPERTIES_SET);
		logger.info("Got contig's first half BED band: {}, it's hash equals is: {}",
				fooFirstHalfBedBand, fooFirstHalfBedBand.hashCode());

		Band fooFirstHalfBedBandDuplicate = new BedBand(BED_TRACK, FOO_START, FOO_INTERNAL, FOO_REGION, PROPERTIES_SET_DUPLICATE);
		logger.info("Got duplicate contig's first half BED band: {}, it's hash is: {}",
				fooFirstHalfBedBandDuplicate, fooFirstHalfBedBandDuplicate.hashCode());

		assertThat(fooFirstHalfBedBand.hashCode()).isEqualTo(fooFirstHalfBedBandDuplicate.hashCode());
		assertThat(fooFirstHalfBedBand).isEqualTo(fooFirstHalfBedBandDuplicate);

		Band differentFooFirstHalfBedBand = new BedBand(BED_TRACK, FOO_START, FOO_INTERNAL, FOO_REGION, DIFFERENT_PROPERTIES_SET);
		logger.info("Got different contig's first half BED band: {}, it's hash is equals to: {}",
				differentFooFirstHalfBedBand, differentFooFirstHalfBedBand.hashCode());

		assertThat(fooFirstHalfBedBand.hashCode()).isNotEqualTo(differentFooFirstHalfBedBand.hashCode());
		assertThat(fooFirstHalfBedBand).isNotEqualTo(differentFooFirstHalfBedBand);

		Band fooBand = new ChromosomeBand(CHROMOSOMES_TRACK, FOO_CONTIG);
		logger.info("Got chromosome band: {}, it's hash is equals to: {}",
				fooBand, fooBand.hashCode());

		Band fooBandDuplicate = new ChromosomeBand(CHROMOSOMES_TRACK, FOO_CONTIG);
		logger.info("Got duplicate chromosome band: {}, it's hash is equals to: {}",
				fooBandDuplicate, fooBandDuplicate.hashCode());

		assertThat(fooBand.hashCode()).isEqualTo(fooBandDuplicate.hashCode());
		assertThat(fooBand).isEqualTo(fooBandDuplicate);

		Band barBand = new ChromosomeBand(CHROMOSOMES_TRACK, BAR_CONTIG);
		logger.info("Got different chromosome band: {}, it's hash is equals to: {}",
				barBand, barBand.hashCode());

		assertThat(fooBand.hashCode()).isNotEqualTo(barBand.hashCode());
		assertThat(fooBand).isNotEqualTo(barBand);

		Band fooBedBand = new BedBand(BED_TRACK, FOO_START, FOO_END, FOO_CONTIG.getId(), null);
		logger.info("Got contig's BED band: {}, it's hash is equals to: {}",
				fooBedBand, fooBedBand.hashCode());

		assertThat(fooBand.hashCode()).isNotEqualTo(fooBedBand.hashCode());
		assertThat(fooBand).isNotEqualTo(fooBedBand);
	}
}
