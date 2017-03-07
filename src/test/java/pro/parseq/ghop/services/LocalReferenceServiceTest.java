package pro.parseq.ghop.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.utils.GenomicCoordinate;

public class LocalReferenceServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(LocalReferenceServiceTest.class);

	private static final String UNKNOWN_REFERENCE = "YetiGenome";
	private static final String TEST_REFERENCE = "TestReference";
	private static final String FOO_CONTIG = "foo";
	private static final String BAR_CONTIG = "bar";
	private static final String BAZ_CONTIG = "baz";
	private static final String UNKNOWN_CONTIG = "qux";

	private static final ReferenceGenome testReference = new ReferenceGenome(TEST_REFERENCE);
	private static final long ANY_CONTIG_LENGTH = 8;
	private static final Contig foo = new Contig(testReference, FOO_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig bar = new Contig(testReference, BAR_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig baz = new Contig(testReference, BAZ_CONTIG, ANY_CONTIG_LENGTH);

	private static final GenomicCoordinate fooStart = new GenomicCoordinate(foo, 0);
	private static final GenomicCoordinate fooSecond = new GenomicCoordinate(foo, 1);
	private static final GenomicCoordinate fooEighth = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH - 1);
	private static final GenomicCoordinate fooEnd   = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate barStart = new GenomicCoordinate(bar, 0);
	private static final GenomicCoordinate barSecond = new GenomicCoordinate(bar, 1);
	private static final GenomicCoordinate barEighth   = new GenomicCoordinate(bar, ANY_CONTIG_LENGTH - 1);
	private static final GenomicCoordinate barEnd   = new GenomicCoordinate(bar, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate bazStart = new GenomicCoordinate(baz, 0);
	private static final GenomicCoordinate bazSecond = new GenomicCoordinate(baz, 1);
	private static final GenomicCoordinate bazEnd   = new GenomicCoordinate(baz, ANY_CONTIG_LENGTH);

	private LocalReferenceService refservice;

	@Before
	public void setUp() throws Exception {
		refservice = new LocalReferenceService(new ReferenceExplorer(getClass()
				.getResource("/references").getPath()));
	}

	@Test
	public void testAvailableReferenceGenomes() throws Exception {

		logger.info("Test available reference genome set");

		Set<ReferenceGenome> references = refservice.getReferenceGenomes();
		logger.info("{} reference genomes are available: {}", references.size(), references);

		assertThat(references).containsExactly(testReference);
	}

	@Test
	public void testContigsDiscovery() throws Exception {

		logger.info("Test contigs discovery method");

		List<Contig> contigs = refservice.getContigs(TEST_REFERENCE);
		logger.info("Found {} contigs for {}: {}", contigs.size(), TEST_REFERENCE, contigs);

		assertThat(contigs).containsExactly(foo, bar, baz);

		try {

			logger.info("Trying to obtain contigs list for unknown reference: {}",
					UNKNOWN_REFERENCE);
			refservice.getContigs(UNKNOWN_REFERENCE);

			fail("Contigs data must not be available for any unknown reference genome!");
		} catch (ReferenceGenomeNotFoundException e) {

			logger.info("Failed to obtain contigs list for reference: {}",
					UNKNOWN_REFERENCE);
			assertThat(e.getReferenceGenomeName()).isEqualTo(UNKNOWN_REFERENCE);
		}
	}

	@Test
	public void testContigLength() throws Exception {

		logger.info("Test contig length discovery method");

		long fooLength = refservice.getContigLength(TEST_REFERENCE, FOO_CONTIG);
		logger.info("Got {}'s length: {}", FOO_CONTIG, fooLength);

		assertThat(fooLength).isEqualTo(ANY_CONTIG_LENGTH);

		long barLength = refservice.getContigLength(TEST_REFERENCE, BAR_CONTIG);
		logger.info("Got {}'s length: {}", BAR_CONTIG, barLength);

		assertThat(barLength).isEqualTo(ANY_CONTIG_LENGTH);

		long bazLength = refservice.getContigLength(TEST_REFERENCE, BAZ_CONTIG);
		logger.info("Got {}'s length: {}", BAZ_CONTIG, bazLength);

		assertThat(bazLength).isEqualTo(ANY_CONTIG_LENGTH);

		try {

			logger.info("Trying to obtain contig length for unknown reference: {}",
					UNKNOWN_REFERENCE);
			refservice.getContigLength(UNKNOWN_REFERENCE, FOO_CONTIG);

			fail("Contig length data must not be available for contig of unknown reference");
		} catch (ReferenceGenomeNotFoundException e) {

			logger.info("Failed to obtain contig's length for reference: {}",
					UNKNOWN_REFERENCE);
			assertThat(e.getReferenceGenomeName()).isEqualTo(UNKNOWN_REFERENCE);
		}

		try {

			logger.info("Trying to obtain length of unknown contig: {}",
					UNKNOWN_CONTIG);
			refservice.getContigLength(TEST_REFERENCE, UNKNOWN_CONTIG);

			fail("Contig length data must not be available for any unknown contig");
		} catch (UnknownContigException e) {

			logger.info("Failed to obtain length for contig: {}",
					UNKNOWN_CONTIG);

			assertThat(e.getReferenceGenomeName()).isEqualTo(TEST_REFERENCE);
			assertThat(e.getContigName()).isEqualTo(UNKNOWN_CONTIG);
			assertThat(e.getAvailableContigNames()).containsExactly(FOO_CONTIG, BAR_CONTIG, BAZ_CONTIG);
		}
	}

	@Test
	public void testReferenceSequences() throws Exception {

		logger.info("Test sequence obtaining method");

		DispersedSequence fooFirstNucleotide = refservice.getSequence(fooStart, 0, 0);
		logger.info("Retrieved {}'s first nucleotide: {}", FOO_CONTIG, fooFirstNucleotide);

		assertThat(fooFirstNucleotide.startCoord()).isEqualTo(fooStart);
		assertThat(fooFirstNucleotide.endCoord()).isEqualTo(fooSecond);
		assertThat(fooFirstNucleotide.sequence()).isEqualTo("A");
		assertThat(fooFirstNucleotide.getFragments()).size().isEqualTo(1);

		DispersedSequence fooSequence = refservice.getSequence(fooStart, 0, (int) (ANY_CONTIG_LENGTH - 1));
		logger.info("Retrieved {}'s sequence: {}", FOO_CONTIG, fooSequence);

		assertThat(fooSequence.startCoord()).isEqualTo(fooStart);
		assertThat(fooSequence.endCoord()).isEqualTo(fooEnd);
		assertThat(fooSequence.sequence()).isEqualTo("ATGCNNNA");
		assertThat(fooSequence.getFragments()).size().isEqualTo(1);

		DispersedSequence barFirstNucleotide = refservice.getSequence(barStart, 0, 0);
		logger.info("Retrieved {}'s first nucleotide: {}", BAR_CONTIG, barFirstNucleotide);

		assertThat(barFirstNucleotide.startCoord()).isEqualTo(barStart);
		assertThat(barFirstNucleotide.endCoord()).isEqualTo(barSecond);
		assertThat(barFirstNucleotide.sequence()).isEqualTo("A");
		assertThat(barFirstNucleotide.getFragments()).size().isEqualTo(1);

		DispersedSequence wholeGenome = refservice.getSequence(barStart,
				(int) ANY_CONTIG_LENGTH, (int) (2 * ANY_CONTIG_LENGTH - 1));
		logger.info("Retrieved whole genome sequence: {}", wholeGenome);

		assertThat(wholeGenome.startCoord()).isEqualTo(fooStart);
		assertThat(wholeGenome.endCoord()).isEqualTo(bazEnd);
		assertThat(wholeGenome.sequence()).isEqualTo("ATGCNNNAAATTGGCANNNNNNNA");
		assertThat(wholeGenome.getFragments()).size().isEqualTo(3);

		DispersedSequence sequence = refservice.getSequence(barStart, 1, (int) ANY_CONTIG_LENGTH);
		logger.info("Retrieved genome sequence: {}", sequence);

		assertThat(sequence.startCoord()).isEqualTo(fooEighth);
		assertThat(sequence.endCoord()).isEqualTo(bazSecond);
		assertThat(sequence.sequence()).isEqualTo("AAATTGGCAN");
		assertThat(wholeGenome.getFragments()).size().isEqualTo(3);
	}

	@Test
	public void testCoordinateShifting() throws Exception {

		logger.info("Test coordinate shifting method");

		assertThat(refservice.shiftCoordinate(fooStart, 0)).isEqualTo(fooStart);
		assertThat(refservice.shiftCoordinate(fooStart, -1)).isEqualTo(fooStart);
		assertThat(refservice.shiftCoordinate(fooStart, (int) -ANY_CONTIG_LENGTH)).isEqualTo(fooStart);

		assertThat(refservice.shiftCoordinate(fooStart, 1)).isEqualTo(fooSecond);
		assertThat(refservice.shiftCoordinate(fooStart, (int) ANY_CONTIG_LENGTH)).isEqualTo(fooEnd);
		assertThat(refservice.shiftCoordinate(fooStart, (int) (ANY_CONTIG_LENGTH + 1))).isEqualTo(barStart);
		assertThat(refservice.shiftCoordinate(fooStart, (int) (2 * ANY_CONTIG_LENGTH + 1))).isEqualTo(barEnd);
		assertThat(refservice.shiftCoordinate(fooStart, (int) (2 * ANY_CONTIG_LENGTH + 2))).isEqualTo(bazStart);
		assertThat(refservice.shiftCoordinate(fooStart, (int) (3 * ANY_CONTIG_LENGTH + 2))).isEqualTo(bazEnd);
		assertThat(refservice.shiftCoordinate(fooStart, (int) (3 * ANY_CONTIG_LENGTH + 3))).isEqualTo(bazEnd);
		assertThat(refservice.shiftCoordinate(fooStart, (int) (4 * ANY_CONTIG_LENGTH))).isEqualTo(bazEnd);

		assertThat(refservice.shiftCoordinate(barStart, 0)).isEqualTo(barStart);
		assertThat(refservice.shiftCoordinate(barStart, -1)).isEqualTo(fooEnd);
		assertThat(refservice.shiftCoordinate(barStart, (int) -ANY_CONTIG_LENGTH)).isEqualTo(fooSecond);
		assertThat(refservice.shiftCoordinate(barStart, (int) -(ANY_CONTIG_LENGTH + 1))).isEqualTo(fooStart);
		assertThat(refservice.shiftCoordinate(barStart, (int) ANY_CONTIG_LENGTH)).isEqualTo(barEnd);
		assertThat(refservice.shiftCoordinate(barStart, (int) (ANY_CONTIG_LENGTH + 1))).isEqualTo(bazStart);

		assertThat(refservice.shiftCoordinate(barEnd, 0)).isEqualTo(barEnd);
		assertThat(refservice.shiftCoordinate(barEnd, 1)).isEqualTo(bazStart);
	}

	@Test
	public void testGapAwareCoordinateShifting() throws Exception {

		logger.info("Test gap aware coordinate shifting methods");

		assertThat(refservice.leftGapAwareShift(barStart, 0)).isEqualTo(barStart);
		assertThat(refservice.leftGapAwareShift(barStart, 1)).isEqualTo(fooEighth);
		assertThat(refservice.leftGapAwareShift(barStart, (int) (ANY_CONTIG_LENGTH - 1))).isEqualTo(fooSecond);
		assertThat(refservice.leftGapAwareShift(barStart, (int) ANY_CONTIG_LENGTH)).isEqualTo(fooStart);
		assertThat(refservice.leftGapAwareShift(barStart, (int) (ANY_CONTIG_LENGTH + 1))).isEqualTo(fooStart);
		assertThat(refservice.leftGapAwareShift(barStart, (int) (2 * ANY_CONTIG_LENGTH))).isEqualTo(fooStart);

		assertThat(refservice.leftGapAwareShift(barEnd, 0)).isEqualTo(barEnd);
		assertThat(refservice.leftGapAwareShift(barEnd, 1)).isEqualTo(barEighth);
		assertThat(refservice.leftGapAwareShift(barEnd, (int) (ANY_CONTIG_LENGTH - 1))).isEqualTo(barSecond);
		assertThat(refservice.leftGapAwareShift(barEnd, (int) ANY_CONTIG_LENGTH)).isEqualTo(barStart);
		assertThat(refservice.leftGapAwareShift(barEnd, (int) (ANY_CONTIG_LENGTH + 1))).isEqualTo(fooEighth);

		assertThat(refservice.rightGapAwareShift(fooEighth, 1)).isEqualTo(fooEnd);
		assertThat(refservice.rightGapAwareShift(fooEighth, 2)).isEqualTo(barSecond);
		assertThat(refservice.rightGapAwareShift(fooEighth, 8)).isEqualTo(barEighth);
		assertThat(refservice.rightGapAwareShift(fooEighth, 9)).isEqualTo(barEnd);
		assertThat(refservice.rightGapAwareShift(fooEighth, 10)).isEqualTo(bazSecond);
	}
}
