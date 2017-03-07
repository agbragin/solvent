package pro.parseq.ghop.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.services.configs.RefserviceConfig;
import pro.parseq.ghop.utils.GenomicCoordinate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BufferedReferenceServiceClientTest {

	private static final Logger logger = LoggerFactory.getLogger(BufferedReferenceServiceClientTest.class);

	private static final String UNKNOWN_REFERENCE = "YetiGenome";
	private static final String HUMAN_REFERENCE = "GRCh37.p13";
	private static final String TEST_REFERENCE = "TestReference";
	private static final String TEST_CONTIG = "somebigcontig";
	private static final String CHR1 = "chr1";
	private static final String CHR2 = "chr2";
	private static final String UNKNOWN_CONTIG = "qux";
	private static final long TEST_CONTIG_LENGTH = 12;
	private static final long CHR1_LENGTH = 249250621;
	private static final long CHR2_LENGTH = 243199373;

	private static final ReferenceGenome testReference = new ReferenceGenome(TEST_REFERENCE);
	private static final ReferenceGenome humanReference = new ReferenceGenome(HUMAN_REFERENCE);
	private static final Contig testContig = new Contig(testReference, TEST_CONTIG, TEST_CONTIG_LENGTH);
	private static final Contig chr1 = new Contig(humanReference, CHR1, CHR1_LENGTH);
	private static final Contig chr2 = new Contig(humanReference, CHR2, CHR2_LENGTH);

	private static final GenomicCoordinate testContigStart = new GenomicCoordinate(testContig, 0);
	private static final GenomicCoordinate testContigSecond = new GenomicCoordinate(testContig, 1);
	private static final GenomicCoordinate testContigEleventh = new GenomicCoordinate(testContig, 10);
	private static final GenomicCoordinate testContigTwelfth = new GenomicCoordinate(testContig, 11);
	private static final GenomicCoordinate testContigEnd = new GenomicCoordinate(testContig, TEST_CONTIG_LENGTH);

	private static final GenomicCoordinate chr1EndPrev = new GenomicCoordinate(chr1, CHR1_LENGTH - 1);
	private static final GenomicCoordinate chr1End = new GenomicCoordinate(chr1, CHR1_LENGTH);
	private static final GenomicCoordinate chr2Start = new GenomicCoordinate(chr2, 0);
	private static final GenomicCoordinate chr2StartNext = new GenomicCoordinate(chr2, 1);

	@Autowired
	private RefserviceConfig config;

	private BufferedReferenceServiceClient refservice;

	@Before
	public void setUp() throws Exception {
		refservice = new BufferedReferenceServiceClient(new RemoteReferenceService(config));
	}

	@Test
	public void testAvailableReferenceGenomes() throws Exception {

		logger.info("Test available reference genome set");

		Set<ReferenceGenome> references = refservice.getReferenceGenomes();
		logger.info("{} reference genomes are available: {}", references.size(), references);

		assertThat(references).contains(testReference, humanReference);
	}

	@Test
	public void testContigsDiscovery() throws Exception {

		logger.info("Test contigs discovery method");

		List<Contig> contigs = refservice.getContigs(TEST_REFERENCE);
		logger.info("Found {} contigs for {}: {}", contigs.size(), TEST_REFERENCE, contigs);

		assertThat(contigs).containsExactly(testContig);

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

		long contigLength = refservice.getContigLength(TEST_REFERENCE, TEST_CONTIG);
		logger.info("Got {}'s length: {}", TEST_CONTIG, contigLength);

		assertThat(contigLength).isEqualTo(TEST_CONTIG_LENGTH);

		try {

			logger.info("Trying to obtain contig length for unknown reference: {}",
					UNKNOWN_REFERENCE);
			refservice.getContigLength(UNKNOWN_REFERENCE, TEST_CONTIG);

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
			assertThat(e.getAvailableContigNames()).containsExactly(TEST_CONTIG);
		}
	}

	@Test
	public void testReferenceSequences() throws Exception {

		logger.info("Test sequence obtaining method");

		DispersedSequence testContigFirstNucleotide = refservice.getSequence(testContigStart, 0, 0);
		logger.info("Retrieved {}'s first nucleotide: {}", TEST_CONTIG, testContigFirstNucleotide);

		assertThat(testContigFirstNucleotide.startCoord()).isEqualTo(testContigStart);
		assertThat(testContigFirstNucleotide.endCoord()).isEqualTo(testContigSecond);
		assertThat(testContigFirstNucleotide.sequence()).isEqualTo("A");
		assertThat(testContigFirstNucleotide.getFragments()).size().isEqualTo(1);

		DispersedSequence testContigWholeSequence = refservice.getSequence(testContigSecond, 1, (int) (TEST_CONTIG_LENGTH - 1));
		logger.info("Retrieved {}'s sequence: {}", TEST_CONTIG, testContigWholeSequence);

		assertThat(testContigWholeSequence.startCoord()).isEqualTo(testContigStart);
		assertThat(testContigWholeSequence.endCoord()).isEqualTo(testContigEnd);
		assertThat(testContigWholeSequence.sequence()).isEqualTo("AAATAATAATAA");
		assertThat(testContigWholeSequence.getFragments()).size().isEqualTo(1);

		DispersedSequence testContigSequence = refservice.getSequence(testContigEleventh, 9, 0);
		logger.info("Retrieved {}'s sequence: {}", TEST_CONTIG, testContigSequence);

		assertThat(testContigSequence.startCoord()).isEqualTo(testContigSecond);
		assertThat(testContigSequence.endCoord()).isEqualTo(testContigTwelfth);
		assertThat(testContigSequence.sequence()).isEqualTo("AATAATAATA");
		assertThat(testContigSequence.getFragments()).size().isEqualTo(1);

		DispersedSequence humanGenomeSequence = refservice.getSequence(chr1EndPrev, 0, 1);
		assertThat(humanGenomeSequence.startCoord()).isEqualTo(chr1EndPrev);
		assertThat(humanGenomeSequence.endCoord()).isEqualTo(chr2StartNext);
		assertThat(humanGenomeSequence.sequence()).isEqualTo("NN");
		assertThat(humanGenomeSequence.getFragments()).size().isEqualTo(2);

		humanGenomeSequence = refservice.getSequence(chr1End, 1, 1);
		assertThat(humanGenomeSequence.startCoord()).isEqualTo(chr1EndPrev);
		assertThat(humanGenomeSequence.endCoord()).isEqualTo(chr2StartNext);
		assertThat(humanGenomeSequence.sequence()).isEqualTo("NN");
		assertThat(humanGenomeSequence.getFragments()).size().isEqualTo(2);

		humanGenomeSequence = refservice.getSequence(chr2Start, 1, 0);
		assertThat(humanGenomeSequence.startCoord()).isEqualTo(chr1EndPrev);
		assertThat(humanGenomeSequence.endCoord()).isEqualTo(chr2StartNext);
		assertThat(humanGenomeSequence.sequence()).isEqualTo("NN");
		assertThat(humanGenomeSequence.getFragments()).size().isEqualTo(2);
	}

	@Test
	public void testCoordinateShifting() throws Exception {

		logger.info("Test coordinate shifting method");

		assertThat(refservice.shiftCoordinate(testContigStart, 0)).isEqualTo(testContigStart);
		assertThat(refservice.shiftCoordinate(testContigStart, -1)).isEqualTo(testContigStart);
		assertThat(refservice.shiftCoordinate(testContigStart, -10)).isEqualTo(testContigStart);

		assertThat(refservice.shiftCoordinate(testContigStart, 1)).isEqualTo(testContigSecond);
		assertThat(refservice.shiftCoordinate(testContigStart, (int) (TEST_CONTIG_LENGTH - 2))).isEqualTo(testContigEleventh);
		assertThat(refservice.shiftCoordinate(testContigStart, (int) (TEST_CONTIG_LENGTH - 1))).isEqualTo(testContigTwelfth);
		assertThat(refservice.shiftCoordinate(testContigStart, (int) TEST_CONTIG_LENGTH)).isEqualTo(testContigEnd);
		assertThat(refservice.shiftCoordinate(testContigStart, (int) (TEST_CONTIG_LENGTH + 1))).isEqualTo(testContigEnd);
		assertThat(refservice.shiftCoordinate(testContigStart, (int) (2 * TEST_CONTIG_LENGTH))).isEqualTo(testContigEnd);

		assertThat(refservice.shiftCoordinate(testContigEnd, 1)).isEqualTo(testContigEnd);
		assertThat(refservice.shiftCoordinate(testContigEnd, 0)).isEqualTo(testContigEnd);
		assertThat(refservice.shiftCoordinate(testContigEnd, -1)).isEqualTo(testContigTwelfth);
		assertThat(refservice.shiftCoordinate(testContigEnd, -2)).isEqualTo(testContigEleventh);
		assertThat(refservice.shiftCoordinate(testContigEnd, (int) (-TEST_CONTIG_LENGTH + 1))).isEqualTo(testContigSecond);
		assertThat(refservice.shiftCoordinate(testContigEnd, (int) -TEST_CONTIG_LENGTH)).isEqualTo(testContigStart);

		assertThat(refservice.shiftCoordinate(chr1EndPrev, 0)).isEqualTo(chr1EndPrev);
		assertThat(refservice.shiftCoordinate(chr1EndPrev, 1)).isEqualTo(chr1End);
		assertThat(refservice.shiftCoordinate(chr1EndPrev, 2)).isEqualTo(chr2Start);
		assertThat(refservice.shiftCoordinate(chr1EndPrev, 3)).isEqualTo(chr2StartNext);

		assertThat(refservice.shiftCoordinate(chr2StartNext, 0)).isEqualTo(chr2StartNext);
		assertThat(refservice.shiftCoordinate(chr2StartNext, -1)).isEqualTo(chr2Start);
		assertThat(refservice.shiftCoordinate(chr2StartNext, -2)).isEqualTo(chr1End);
		assertThat(refservice.shiftCoordinate(chr2StartNext, -3)).isEqualTo(chr1EndPrev);
	}
}
