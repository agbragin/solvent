package pro.parseq.ghop.datasources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.ChromosomeBand;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.NucleotideBand;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.services.LocalReferenceService;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.Nucleotide;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MasterDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(MasterDataSourceTest.class);

	private static final String REFERENCE_TRACK = "Reference";
	private static final String CHROMOSOME_TRACK = "Chromosome";
	private static final String TEST_REFERENCE = "TestReference";
	private static final String FOO_CONTIG = "foo";
	private static final String BAR_CONTIG = "bar";

	private static final Track referenceTrack = new Track(REFERENCE_TRACK);
	private static final Track chromosomeTrack = new Track(CHROMOSOME_TRACK);
	private static final ReferenceGenome testReference = new ReferenceGenome(TEST_REFERENCE);

	private static final long ANY_CONTIG_LENGTH = 8;
	private static final Contig foo = new Contig(testReference, FOO_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig bar = new Contig(testReference, BAR_CONTIG, ANY_CONTIG_LENGTH);

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

	private static final Band fooBand = new ChromosomeBand(chromosomeTrack, foo);
	private static final Band barBand = new ChromosomeBand(chromosomeTrack, bar);

	private static final Band fooFirstNucleotide = new NucleotideBand(referenceTrack, fooStart, fooSecond, Nucleotide.A);
	private static final Band fooSecondNucleotide = new NucleotideBand(referenceTrack, fooSecond, fooThird, Nucleotide.T);
	private static final Band fooThirdNucleotide = new NucleotideBand(referenceTrack, fooThird, fooFourth, Nucleotide.G);
	private static final Band fooFourthNucleotide = new NucleotideBand(referenceTrack, fooFourth, fooFifth, Nucleotide.C);
	private static final Band fooFifthNucleotide = new NucleotideBand(referenceTrack, fooFifth, fooSixth, Nucleotide.N);
	private static final Band fooSixthNucleotide = new NucleotideBand(referenceTrack, fooSixth, fooSeventh, Nucleotide.N);
	private static final Band fooSeventhNucleotide = new NucleotideBand(referenceTrack, fooSeventh, fooEighth, Nucleotide.N);
	private static final Band fooLastNucleotide = new NucleotideBand(referenceTrack, fooEighth, fooEnd, Nucleotide.A);
	private static final Band barFirstNucleotide = new NucleotideBand(referenceTrack, barStart, barSecond, Nucleotide.A);
	private static final Band barSecondNucleotide = new NucleotideBand(referenceTrack, barSecond, barThird, Nucleotide.A);

	private Set<DataSource<?>> dataSources;

	@Autowired
	private MasterDataSource masterDataSource;

	@Before
	public void setUp() throws Exception {

		dataSources = new HashSet<>();

		ReferenceExplorer referenceExplorer = new ReferenceExplorer(getClass()
				.getResource("/references").getPath());
		LocalReferenceService refservice = new LocalReferenceService(referenceExplorer);

		masterDataSource.setReferenceService(refservice);
		masterDataSource.setReferenceGenome(testReference);

		DataSource<NucleotideBand> referenceDataSource = new ReferenceDataSource(referenceTrack, refservice);
		referenceTrack.setDataSource(referenceDataSource);

		masterDataSource.addTrack(referenceTrack);

		DataSource<ChromosomeBand> chromosomeDataSource = new ChromosomeDataSource(chromosomeTrack,
				masterDataSource.getReferenceService().getContigs(testReference.getId()),
				masterDataSource.getComparator());

		chromosomeTrack.setDataSource(chromosomeDataSource);
		masterDataSource.addTrack(chromosomeTrack);

		dataSources.add(referenceDataSource);
		dataSources.add(chromosomeDataSource);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDefaultTracks() throws Exception {

		QueryForBands query = new QueryForBands(fooStart, 0, 0,  dataSources);
		Set<Band> bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 0, 0, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide);

		query = new QueryForBands(fooStart, 1, 0,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 1, 0, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide);

		query = new QueryForBands(fooStart, 10, 0,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 10, 0, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide);

		query = new QueryForBands(fooStart, 0, 1,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 0, 1, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide, fooSecondNucleotide);

		query = new QueryForBands(fooStart, 0, 2,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 0, 2, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide);

		query = new QueryForBands(fooStart, 0, 7,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 0, 7, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		query = new QueryForBands(fooStart, 0, 8,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				fooStart, 0, 8, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, fooFirstNucleotide, fooSecondNucleotide, fooThirdNucleotide, fooFourthNucleotide, fooFifthNucleotide, fooSixthNucleotide, fooSeventhNucleotide, fooLastNucleotide);

		query = new QueryForBands(barSecond, 0, 0,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				barSecond, 0, 0, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand, barFirstNucleotide, barSecondNucleotide);

		query = new QueryForBands(barSecond, 1, 0,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				barSecond, 1, 0, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand, barFirstNucleotide, barSecondNucleotide);

		query = new QueryForBands(barSecond, 2, 0,  dataSources);
		bands = (Set<Band>) masterDataSource.getBands(query);
		logger.info("Retrieved bands for request {}:{}:{} are: {}",
				barSecond, 2, 0, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, barBand, fooLastNucleotide, barFirstNucleotide, barSecondNucleotide);
	}
}
