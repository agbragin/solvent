package pro.parseq.ghop.datasources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.NucleotideBand;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.services.LocalReferenceService;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.Nucleotide;

public class ReferenceDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(ReferenceDataSourceTest.class);

	private static final String TEST_TRACK = "TestReferenceTrack";
	private static final Track testTrack = new Track(TEST_TRACK);

	private static final String TEST_REFERENCE = "TestReference";
	private static final String FOO_CONTIG = "foo";
	private static final String BAR_CONTIG = "bar";
	private static final String BAZ_CONTIG = "baz";

	private static final ReferenceGenome testReference = new ReferenceGenome(TEST_REFERENCE);
	private static final long ANY_CONTIG_LENGTH = 8;
	private static final Contig foo = new Contig(testReference, FOO_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig bar = new Contig(testReference, BAR_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig baz = new Contig(testReference, BAZ_CONTIG, ANY_CONTIG_LENGTH);

	private static final GenomicCoordinate fooStart = new GenomicCoordinate(foo, 0);
	private static final GenomicCoordinate fooSecond = new GenomicCoordinate(foo, 1);
	private static final GenomicCoordinate fooThird = new GenomicCoordinate(foo, 2);
	private static final GenomicCoordinate fooFourth = new GenomicCoordinate(foo, 3);
	private static final GenomicCoordinate fooFifth = new GenomicCoordinate(foo, 4);
	private static final GenomicCoordinate fooSixth = new GenomicCoordinate(foo, 5);
	private static final GenomicCoordinate fooSeventh = new GenomicCoordinate(foo, 6);
	private static final GenomicCoordinate fooEighth = new GenomicCoordinate(foo, 7);
	private static final GenomicCoordinate fooEnd   = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate barStart = new GenomicCoordinate(bar, 0);
	private static final GenomicCoordinate barSecond = new GenomicCoordinate(bar, 1);
	private static final GenomicCoordinate barThird = new GenomicCoordinate(bar, 2);
	private static final GenomicCoordinate barEighth = new GenomicCoordinate(bar, 7);
	private static final GenomicCoordinate barEnd   = new GenomicCoordinate(bar, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate bazStart = new GenomicCoordinate(baz, 0);
	private static final GenomicCoordinate bazSecond = new GenomicCoordinate(baz, 1);
	private static final GenomicCoordinate bazEighth = new GenomicCoordinate(baz, 7);
	private static final GenomicCoordinate bazEnd   = new GenomicCoordinate(baz, ANY_CONTIG_LENGTH);

	private static final NucleotideBand fooFirstNucleotide = new NucleotideBand(testTrack, fooStart, fooSecond, Nucleotide.A);
	private static final NucleotideBand fooSecondNucleotide = new NucleotideBand(testTrack, fooSecond, fooThird, Nucleotide.T);
	private static final NucleotideBand fooThirdNucleotide = new NucleotideBand(testTrack, fooThird, fooFourth, Nucleotide.G);
	private static final NucleotideBand fooFourthNucleotide = new NucleotideBand(testTrack, fooFourth, fooFifth, Nucleotide.C);
	private static final NucleotideBand fooFifthNucleotide = new NucleotideBand(testTrack, fooFifth, fooSixth, Nucleotide.N);
	private static final NucleotideBand fooSixthNucleotide = new NucleotideBand(testTrack, fooSixth, fooSeventh, Nucleotide.N);
	private static final NucleotideBand fooSeventhNucleotide = new NucleotideBand(testTrack, fooSeventh, fooEighth, Nucleotide.N);
	private static final NucleotideBand fooLastNucleotide = new NucleotideBand(testTrack, fooEighth, fooEnd, Nucleotide.A);
	private static final NucleotideBand barFirstNucleotide = new NucleotideBand(testTrack, barStart, barSecond, Nucleotide.A);
	private static final NucleotideBand barSecondNucleotide = new NucleotideBand(testTrack, barSecond, barThird, Nucleotide.A);
	private static final NucleotideBand barLastNucleotide = new NucleotideBand(testTrack, barEighth, barEnd, Nucleotide.A);
	private static final NucleotideBand bazFirstNucleotide = new NucleotideBand(testTrack, bazStart, bazSecond, Nucleotide.N);
	private static final NucleotideBand bazLastNucleotide = new NucleotideBand(testTrack, bazEighth, bazEnd, Nucleotide.A);

	private ReferenceDataSource dataSource;
	private LocalReferenceService refservice;

	@Before
	public void setUp() throws Exception {

		refservice = new LocalReferenceService(new ReferenceExplorer(getClass()
				.getResource("/references").getPath()));
		dataSource = new ReferenceDataSource(testTrack, refservice);
	}

	@Test
	public void testReferenceDataSourceBorderGenerants() throws Exception {

		logger.info("Test reference data source border generants");

		Set<NucleotideBand> bands = dataSource.borderGenerants(fooStart);
		logger.info("Generants of {} are: {}", fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide);

		bands = dataSource.borderGenerants(fooSecond);
		logger.info("Generants of {} are: {}", fooSecond, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide);

		bands = dataSource.borderGenerants(fooSecond);
		logger.info("Generants of {} are: {}", fooSecond, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide);

		bands = dataSource.borderGenerants(fooThird);
		logger.info("Generants of {} are: {}", fooThird, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSecondNucleotide, fooThirdNucleotide);

		bands = dataSource.borderGenerants(fooFourth);
		logger.info("Generants of {} are: {}", fooFourth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooThirdNucleotide, fooFourthNucleotide);

		bands = dataSource.borderGenerants(fooFifth);
		logger.info("Generants of {} are: {}", fooFifth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFourthNucleotide, fooFifthNucleotide);

		bands = dataSource.borderGenerants(fooSixth);
		logger.info("Generants of {} are: {}", fooSixth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFifthNucleotide, fooSixthNucleotide);

		bands = dataSource.borderGenerants(fooSeventh);
		logger.info("Generants of {} are: {}", fooSeventh, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSixthNucleotide, fooSeventhNucleotide);

		bands = dataSource.borderGenerants(fooEighth);
		logger.info("Generants of {} are: {}", fooEighth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.borderGenerants(fooEnd);
		logger.info("Generants of {} are: {}", fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooLastNucleotide);

		bands = dataSource.borderGenerants(barStart);
		logger.info("Generants of {} are: {}", barStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(barFirstNucleotide);

		bands = dataSource.borderGenerants(barSecond);
		logger.info("Generants of {} are: {}", barSecond, bands);

		assertThat(bands).containsExactlyInAnyOrder(barFirstNucleotide, barSecondNucleotide);
	}

	@Test
	public void testReferenceDataSourceCoverage() throws Exception {

		logger.info("Test reference data source coverage");

		Set<NucleotideBand> bands = dataSource.coverage(fooStart);
		logger.info("Coverage of {} is: {}", fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide);

		bands = dataSource.coverage(fooSecond);
		logger.info("Coverage of {} is: {}", fooSecond, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide);

		bands = dataSource.coverage(fooSecond);
		logger.info("Coverage of {} is: {}", fooSecond, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide);

		bands = dataSource.coverage(fooThird);
		logger.info("Coverage of {} is: {}", fooThird, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSecondNucleotide, fooThirdNucleotide);

		bands = dataSource.coverage(fooFourth);
		logger.info("Coverage of {} is: {}", fooFourth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooThirdNucleotide, fooFourthNucleotide);

		bands = dataSource.coverage(fooFifth);
		logger.info("Coverage of {} is: {}", fooFifth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFourthNucleotide, fooFifthNucleotide);

		bands = dataSource.coverage(fooSixth);
		logger.info("Coverage of {} is: {}", fooSixth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFifthNucleotide, fooSixthNucleotide);

		bands = dataSource.coverage(fooSeventh);
		logger.info("Coverage of {} is: {}", fooSeventh, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSixthNucleotide, fooSeventhNucleotide);

		bands = dataSource.coverage(fooEighth);
		logger.info("Coverage of {} is: {}", fooEighth, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.coverage(fooEnd);
		logger.info("Coverage of {} is: {}", fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooLastNucleotide);

		bands = dataSource.coverage(bazEnd);
		logger.info("Coverage of {} is: {}", bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazLastNucleotide);
	}

	@Test
	public void testReferenceDataSourceLeftBorders() throws Exception {

		logger.info("Test reference data source left borders");

		List<GenomicCoordinate> coords = dataSource.leftBorders(0, fooStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, fooStart, coords);

		assertThat(coords).containsExactly(fooStart);

		coords = dataSource.leftBorders(1, fooStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, fooStart, coords);

		assertThat(coords).containsExactly(fooStart);

		coords = dataSource.leftBorders(2, fooStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, fooStart, coords);

		assertThat(coords).containsExactly(fooStart);

		coords = dataSource.leftBorders(0, fooThird);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, fooThird, coords);

		assertThat(coords).containsExactly(fooThird);

		coords = dataSource.leftBorders(1, fooThird);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, fooThird, coords);

		assertThat(coords).containsExactly(fooSecond, fooThird);

		coords = dataSource.leftBorders(2, fooThird);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, fooThird, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird);

		coords = dataSource.leftBorders(3, fooThird);
		logger.info("Result of requesting {} borders left from {} is: {}",
				3, fooThird, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird);

		coords = dataSource.leftBorders(4, fooThird);
		logger.info("Result of requesting {} borders left from {} is: {}",
				4, fooThird, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird);

		coords = dataSource.leftBorders(0, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, barStart, coords);

		assertThat(coords).containsExactly(barStart);

		coords = dataSource.leftBorders(1, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, barStart, coords);

		assertThat(coords).containsExactly(fooEnd, barStart);

		coords = dataSource.leftBorders(2, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, barStart, coords);

		assertThat(coords).containsExactly(fooEighth, fooEnd, barStart);

		coords = dataSource.leftBorders(2, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, barStart, coords);

		assertThat(coords).containsExactly(fooEighth, fooEnd, barStart);

		coords = dataSource.leftBorders(8, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				8, barStart, coords);

		assertThat(coords).containsExactly(fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart);

		coords = dataSource.leftBorders(9, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				9, barStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart);

		coords = dataSource.leftBorders(10, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				10, barStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart);

		coords = dataSource.leftBorders(11, barStart);
		logger.info("Result of requesting {} borders left from {} is: {}",
				11, barStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart);

		coords = dataSource.leftBorders(0, bazSecond);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, bazSecond, coords);

		assertThat(coords).containsExactly(bazSecond);

		coords = dataSource.leftBorders(1, bazSecond);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, bazSecond, coords);

		assertThat(coords).containsExactly(bazStart, bazSecond);

		coords = dataSource.leftBorders(2, bazSecond);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, bazSecond, coords);

		assertThat(coords).containsExactly(barEnd, bazStart, bazSecond);

		coords = dataSource.leftBorders(3, bazSecond);
		logger.info("Result of requesting {} borders left from {} is: {}",
				3, bazSecond, coords);

		assertThat(coords).containsExactly(barEighth, barEnd, bazStart, bazSecond);

		coords = dataSource.leftBorders(0, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, bazEnd, coords);

		assertThat(coords).containsExactly(bazEnd);

		coords = dataSource.leftBorders(1, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, bazEnd, coords);

		assertThat(coords).containsExactly(bazEighth, bazEnd);

		coords = dataSource.leftBorders(1, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, bazEnd, coords);

		assertThat(coords).containsExactly(bazEighth, bazEnd);

		coords = dataSource.leftBorders(6, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				6, bazEnd, coords);

		assertThat(coords).containsSubsequence(bazEighth, bazEnd).doesNotContain(bazSecond).size().isEqualTo(7);

		coords = dataSource.leftBorders(7, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				7, bazEnd, coords);

		assertThat(coords).containsSubsequence(bazSecond, bazEighth, bazEnd).doesNotContain(bazStart).size().isEqualTo(8);

		coords = dataSource.leftBorders(8, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				8, bazEnd, coords);

		assertThat(coords).containsSubsequence(bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(barEnd).size().isEqualTo(9);

		coords = dataSource.leftBorders(9, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				9, bazEnd, coords);

		assertThat(coords).containsSubsequence(barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(barEighth).size().isEqualTo(10);

		coords = dataSource.leftBorders(10, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				10, bazEnd, coords);

		assertThat(coords).containsSubsequence(barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(11);

		coords = dataSource.leftBorders(14, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				14, bazEnd, coords);

		assertThat(coords).containsSubsequence(barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(barThird).size().isEqualTo(15);

		coords = dataSource.leftBorders(15, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				15, bazEnd, coords);

		assertThat(coords).containsSubsequence(barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(barSecond).size().isEqualTo(16);

		coords = dataSource.leftBorders(16, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				16, bazEnd, coords);

		assertThat(coords).containsSubsequence(barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(barStart).size().isEqualTo(17);

		coords = dataSource.leftBorders(17, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				17, bazEnd, coords);

		assertThat(coords).containsSubsequence(barStart, barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(fooEnd).size().isEqualTo(18);

		coords = dataSource.leftBorders(18, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				18, bazEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(fooEighth).size().isEqualTo(19);

		coords = dataSource.leftBorders(25, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				25, bazEnd, coords);

		assertThat(coords).containsSubsequence(fooSecond, fooEnd, barStart, barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).doesNotContain(fooStart).size().isEqualTo(26);

		coords = dataSource.leftBorders(26, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				26, bazEnd, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooEnd, barStart, barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(27);

		coords = dataSource.leftBorders(27, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				27, bazEnd, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooEnd, barStart, barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(27);

		coords = dataSource.leftBorders(28, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				28, bazEnd, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooEnd, barStart, barSecond, barThird, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(27);
	}

	@Test
	public void testReferenceDataSourceRightBorders() throws Exception {

		logger.info("Test reference data source right borders");

		List<GenomicCoordinate> coords = dataSource.rightBorders(0, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				0, fooStart, coords);

		assertThat(coords).containsExactly(fooStart);

		coords = dataSource.rightBorders(1, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				1, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond);

		coords = dataSource.rightBorders(2, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				2, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird);

		coords = dataSource.rightBorders(3, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				3, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth);

		coords = dataSource.rightBorders(4, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				4, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth);

		coords = dataSource.rightBorders(5, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				5, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth);

		coords = dataSource.rightBorders(6, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				6, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh);

		coords = dataSource.rightBorders(7, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				7, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth);

		coords = dataSource.rightBorders(8, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				8, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd);

		coords = dataSource.rightBorders(9, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				9, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart);

		coords = dataSource.rightBorders(10, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				10, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond);

		coords = dataSource.rightBorders(16, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				16, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth).doesNotContain(barEnd, bazStart).size().isEqualTo(17);

		coords = dataSource.rightBorders(17, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				17, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd).doesNotContain(bazStart).size().isEqualTo(18);

		coords = dataSource.rightBorders(18, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				18, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd, bazStart).doesNotContain(bazSecond).size().isEqualTo(19);

		coords = dataSource.rightBorders(19, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				19, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd, bazStart, bazSecond).size().isEqualTo(20);

		coords = dataSource.rightBorders(25, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				25, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd, bazStart, bazSecond, bazEighth).doesNotContain(bazEnd).size().isEqualTo(26);

		coords = dataSource.rightBorders(26, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				26, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(27);

		coords = dataSource.rightBorders(27, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				27, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(27);

		coords = dataSource.rightBorders(28, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				28, fooStart, coords);

		assertThat(coords).containsSubsequence(fooStart, fooSecond, fooThird, fooFourth, fooFifth, fooSixth, fooSeventh, fooEighth, fooEnd, barStart, barSecond, barEighth, barEnd, bazStart, bazSecond, bazEighth, bazEnd).size().isEqualTo(27);

		coords = dataSource.rightBorders(0, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				0, fooEnd, coords);

		assertThat(coords).containsExactly(fooEnd);

		coords = dataSource.rightBorders(1, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				1, fooEnd, coords);

		assertThat(coords).containsExactly(fooEnd, barStart);

		coords = dataSource.rightBorders(2, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				2, fooEnd, coords);

		assertThat(coords).containsExactly(fooEnd, barStart, barSecond);

		coords = dataSource.rightBorders(8, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				8, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth).doesNotContain(barEnd).size().isEqualTo(9);

		coords = dataSource.rightBorders(9, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				9, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth, barEnd).doesNotContain(bazStart).size().isEqualTo(10);

		coords = dataSource.rightBorders(10, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				10, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth, barEnd, bazStart).doesNotContain(bazSecond).size().isEqualTo(11);

		coords = dataSource.rightBorders(17, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				17, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth, barEnd, bazStart, bazEighth).doesNotContain(bazEnd).size().isEqualTo(18);

		coords = dataSource.rightBorders(18, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				18, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth, barEnd, bazStart, bazEighth, bazEnd).size().isEqualTo(19);

		coords = dataSource.rightBorders(19, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				19, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth, barEnd, bazStart, bazEighth, bazEnd).size().isEqualTo(19);

		coords = dataSource.rightBorders(20, fooEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				20, fooEnd, coords);

		assertThat(coords).containsSubsequence(fooEnd, barStart, barEighth, barEnd, bazStart, bazEighth, bazEnd).size().isEqualTo(19);
	}

	@Test
	public void testReferenceDataSourceLeftBorderGenerants() throws Exception {

		logger.info("Test reference data source left border generants");

		Set<NucleotideBand> bands = dataSource.leftBordersGenerants(0, fooStart);
		logger.info("Generants of the next {} left borders from {} are: {}",
				0, fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide);

		bands = dataSource.leftBordersGenerants(1, fooStart);
		logger.info("Generants of the next {} left borders from {} are: {}",
				1, fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide);

		bands = dataSource.leftBordersGenerants(2, fooStart);
		logger.info("Generants of the next {} left borders from {} are: {}",
				2, fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide);

		bands = dataSource.leftBordersGenerants(0, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				0, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(1, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				1, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(2, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				2, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(2, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				2, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(3, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				3, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(4, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				4, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(5, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				5, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(6, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				6, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(7, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				7, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(8, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				8, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(9, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				9, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(10, fooEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				10, fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		bands = dataSource.leftBordersGenerants(0, barEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				0, barEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(barLastNucleotide);

		bands = dataSource.leftBordersGenerants(0, bazEnd);
		logger.info("Generants of the next {} left borders from {} are: {}",
				0, bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazLastNucleotide);
	}

	@Test
	public void testReferenceDataSourceRightBorderGenerants() throws Exception {

		logger.info("Test reference data source right border generants");

		Set<NucleotideBand> bands = dataSource.rightBordersGenerants(0, barEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				0, barEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(barLastNucleotide);

		bands = dataSource.rightBordersGenerants(1, barEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				1, barEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(barLastNucleotide, bazFirstNucleotide);

		bands = dataSource.rightBordersGenerants(7, barEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				7, barEnd, bands);

		assertThat(bands).contains(barLastNucleotide, bazFirstNucleotide).doesNotContain(bazLastNucleotide).size().isEqualTo(8);

		bands = dataSource.rightBordersGenerants(8, barEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				8, barEnd, bands);

		assertThat(bands).contains(barLastNucleotide, bazFirstNucleotide, bazLastNucleotide).size().isEqualTo(9);

		bands = dataSource.rightBordersGenerants(9, barEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				9, barEnd, bands);

		assertThat(bands).contains(barLastNucleotide, bazFirstNucleotide, bazLastNucleotide).size().isEqualTo(9);

		bands = dataSource.rightBordersGenerants(10, barEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				10, barEnd, bands);

		assertThat(bands).contains(barLastNucleotide, bazFirstNucleotide, bazLastNucleotide).size().isEqualTo(9);
	}
}
