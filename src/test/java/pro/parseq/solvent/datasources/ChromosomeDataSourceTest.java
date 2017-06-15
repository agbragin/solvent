package pro.parseq.solvent.datasources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.solvent.datasources.ChromosomeDataSource;
import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.filters.AttributeFilter;
import pro.parseq.solvent.datasources.filters.FilterOperator;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.ChromosomeBand;
import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.entities.ReferenceGenome;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.services.LocalReferenceService;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.GenomicCoordinateComparator;

public class ChromosomeDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(ChromosomeDataSourceTest.class);

	private static final Track TEST_TRACK = new Track("chr");

	private static final String TEST_REFERENCE = "TestReference";
	private static final String FOO_CONTIG = "foo";
	private static final String BAR_CONTIG = "bar";
	private static final String BAZ_CONTIG = "baz";

	private static final ReferenceGenome testReference = new ReferenceGenome(TEST_REFERENCE);
	private static final long ANY_CONTIG_LENGTH = 8;
	private static final long ANY_CONTIG_INTERNAL = 4;
	private static final Contig foo = new Contig(testReference, FOO_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig bar = new Contig(testReference, BAR_CONTIG, ANY_CONTIG_LENGTH);
	private static final Contig baz = new Contig(testReference, BAZ_CONTIG, ANY_CONTIG_LENGTH);

	private static final GenomicCoordinate fooStart = new GenomicCoordinate(foo, 0);
	private static final GenomicCoordinate fooInternal = new GenomicCoordinate(foo, ANY_CONTIG_INTERNAL); 
	private static final GenomicCoordinate fooEnd   = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate outOfFoo   = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH + 1);
	private static final GenomicCoordinate barStart = new GenomicCoordinate(bar, 0);
	private static final GenomicCoordinate barInternal = new GenomicCoordinate(bar, ANY_CONTIG_INTERNAL);
	private static final GenomicCoordinate barEnd   = new GenomicCoordinate(bar, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate outOfBar   = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH + 1);
	private static final GenomicCoordinate bazStart = new GenomicCoordinate(baz, 0);
	private static final GenomicCoordinate bazInternal = new GenomicCoordinate(baz, ANY_CONTIG_INTERNAL);
	private static final GenomicCoordinate bazEnd   = new GenomicCoordinate(baz, ANY_CONTIG_LENGTH);
	private static final GenomicCoordinate outOfBaz   = new GenomicCoordinate(foo, ANY_CONTIG_LENGTH + 1);

	private static final ChromosomeBand fooBand = new ChromosomeBand(TEST_TRACK, foo);
	private static final ChromosomeBand barBand = new ChromosomeBand(TEST_TRACK, bar);
	private static final ChromosomeBand bazBand = new ChromosomeBand(TEST_TRACK, baz);

	private ChromosomeDataSource dataSource, filteredDataSource;

	private Comparator<GenomicCoordinate> comparator;


	@Before
	public void setUp() throws Exception {

		ReferenceExplorer referenceExplorer = new ReferenceExplorer(getClass()
				.getResource("/references").getPath());
		LocalReferenceService refservice = new LocalReferenceService(referenceExplorer);

		comparator = new GenomicCoordinateComparator(refservice);
		dataSource = new ChromosomeDataSource(TEST_TRACK, refservice.getContigs(TEST_REFERENCE), comparator);

		@SuppressWarnings("unchecked")
		AttributeFilter<String> nameAttributeFilter = new AttributeFilter<>(0,
				(Attribute<String>) dataSource.attributes().iterator().next(),
				FilterOperator.ILIKE, Arrays.asList("BA"), false);
		FilterQuery query = new FilterQuery(Arrays.asList(nameAttributeFilter), null);

		filteredDataSource = new ChromosomeDataSource(dataSource, query);
	}

	@Test
	public void testChromosomeDataSourceBands() throws Exception {

		logger.info("Test chromosome data source bands");

		List<ChromosomeBand> chrs = dataSource.getBands();
		logger.info("Data source contains {} bands: {}", chrs.size(), chrs);

		assertThat(chrs).containsExactly(fooBand, barBand, bazBand);
	}

	@Test
	public void testFilteredChromosomeDataSourceBands() throws Exception {

		logger.info("Test filtered chromosome data source bands");

		List<ChromosomeBand> chrs = filteredDataSource.getBands();
		logger.info("Data source contains {} bands: {}", chrs.size(), chrs);

		assertThat(chrs).containsExactly(barBand, bazBand);
	}

	@Test
	public void testChromosomeDataSourceBorderGenerants() throws Exception {

		logger.info("Test chromosome data source border generants");

		Set<ChromosomeBand> bands = dataSource.borderGenerants(fooStart);
		logger.info("Generants of {} are: {}", fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.borderGenerants(fooInternal);
		logger.info("Generants of {} are: {}", fooInternal, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.borderGenerants(fooEnd);
		logger.info("Generants of {} are: {}", fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.borderGenerants(outOfFoo);
		logger.info("Generants of {} are: {}", outOfFoo, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.borderGenerants(barStart);
		logger.info("Generants of {} are: {}", barStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand);

		bands = dataSource.borderGenerants(barInternal);
		logger.info("Generants of {} are: {}", barInternal, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.borderGenerants(barEnd);
		logger.info("Generants of {} are: {}", barEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand);

		bands = dataSource.borderGenerants(outOfBar);
		logger.info("Generants of {} are: {}", outOfBar, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.borderGenerants(bazStart);
		logger.info("Generants of {} are: {}", bazStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.borderGenerants(bazInternal);
		logger.info("Generants of {} are: {}", bazInternal, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.borderGenerants(bazEnd);
		logger.info("Generants of {} are: {}", bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.borderGenerants(outOfBaz);
		logger.info("Generants of {} are: {}", outOfBaz, bands);

		assertThat(bands).isEmpty();
	}

	@Test
	public void testChromosomeDataSourceCoverage() throws Exception {

		logger.info("Test chromosome data source coverage");

		Set<ChromosomeBand> bands = dataSource.coverage(fooStart);
		logger.info("Coverage of {} is: {}", fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.coverage(fooInternal);
		logger.info("Coverage of {} is: {}", fooInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.coverage(fooEnd);
		logger.info("Coverage of {} is: {}", fooEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.coverage(outOfFoo);
		logger.info("Generants of {} are: {}", outOfFoo, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.coverage(barStart);
		logger.info("Coverage of {} is: {}", barStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand);

		bands = dataSource.coverage(barInternal);
		logger.info("Coverage of {} is: {}", barInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand);

		bands = dataSource.coverage(barEnd);
		logger.info("Coverage of {} is: {}", barEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand);

		bands = dataSource.coverage(outOfBar);
		logger.info("Generants of {} are: {}", outOfBar, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.coverage(bazStart);
		logger.info("Coverage of {} is: {}", bazStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.coverage(bazInternal);
		logger.info("Coverage of {} is: {}", bazInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.coverage(bazEnd);
		logger.info("Coverage of {} is: {}", bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.coverage(outOfBaz);
		logger.info("Generants of {} are: {}", outOfBaz, bands);

		assertThat(bands).isEmpty();
	}

	@Test
	public void testChromosomeDataSourceLeftBorders() throws Exception {

		logger.info("Test chromosome data source left borders");

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

		coords = dataSource.leftBorders(0, fooEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, fooEnd, coords);

		assertThat(coords).containsExactly(fooEnd);

		coords = dataSource.leftBorders(1, fooEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, fooEnd, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd);

		coords = dataSource.leftBorders(2, fooEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, fooEnd, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd);

		coords = dataSource.leftBorders(3, fooEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				3, fooEnd, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd);

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

		assertThat(coords).containsExactly(fooStart, fooEnd, barStart);

		coords = dataSource.leftBorders(4, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				4, bazEnd, coords);

		assertThat(coords).containsExactly(fooEnd, barStart, barEnd, bazStart, bazEnd);

		coords = dataSource.leftBorders(5, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				5, bazEnd, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd, barStart, barEnd, bazStart, bazEnd);

		coords = dataSource.leftBorders(6, bazEnd);
		logger.info("Result of requesting {} borders left from {} is: {}",
				6, bazEnd, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd, barStart, barEnd, bazStart, bazEnd);

		coords = dataSource.leftBorders(0, bazInternal);
		logger.info("Result of requesting {} borders left from {} is: {}",
				0, bazInternal, coords);

		assertThat(coords).isEmpty();

		coords = dataSource.leftBorders(1, bazInternal);
		logger.info("Result of requesting {} borders left from {} is: {}",
				1, bazInternal, coords);

		assertThat(coords).containsExactly(bazStart);

		coords = dataSource.leftBorders(2, bazInternal);
		logger.info("Result of requesting {} borders left from {} is: {}",
				2, bazInternal, coords);

		assertThat(coords).containsExactly(barEnd, bazStart);

		coords = dataSource.leftBorders(3, bazInternal);
		logger.info("Result of requesting {} borders left from {} is: {}",
				3, bazInternal, coords);

		assertThat(coords).containsExactly(barStart, barEnd, bazStart);

		coords = dataSource.leftBorders(4, bazInternal);
		logger.info("Result of requesting {} borders left from {} is: {}",
				4, bazInternal, coords);

		assertThat(coords).containsExactly(fooEnd, barStart, barEnd, bazStart);
	}

	@Test
	public void testChromosomeDataSourceRightBorders() throws Exception {

		logger.info("Test chromosome data source right borders");

		List<GenomicCoordinate> coords = dataSource.rightBorders(0, bazEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				0, bazEnd, coords);

		assertThat(coords).containsExactly(bazEnd);

		coords = dataSource.rightBorders(1, bazEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				1, bazEnd, coords);

		assertThat(coords).containsExactly(bazEnd);

		coords = dataSource.rightBorders(2, bazEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				2, bazEnd, coords);

		assertThat(coords).containsExactly(bazEnd);

		coords = dataSource.rightBorders(0, barEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				0, barEnd, coords);

		assertThat(coords).containsExactly(barEnd);

		coords = dataSource.rightBorders(1, barEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				1, barEnd, coords);

		assertThat(coords).containsExactly(barEnd, bazStart);

		coords = dataSource.rightBorders(2, barEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				2, barEnd, coords);

		assertThat(coords).containsExactly(barEnd, bazStart, bazEnd);

		coords = dataSource.rightBorders(3, barEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				3, barEnd, coords);

		assertThat(coords).containsExactly(barEnd, bazStart, bazEnd);

		coords = dataSource.rightBorders(4, barEnd);
		logger.info("Result of requesting {} borders right from {} is: {}",
				4, barEnd, coords);

		assertThat(coords).containsExactly(barEnd, bazStart, bazEnd);

		coords = dataSource.rightBorders(4, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				4, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd, barStart, barEnd, bazStart);

		coords = dataSource.rightBorders(5, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				5, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd, barStart, barEnd, bazStart, bazEnd);

		coords = dataSource.rightBorders(6, fooStart);
		logger.info("Result of requesting {} borders right from {} is: {}",
				6, fooStart, coords);

		assertThat(coords).containsExactly(fooStart, fooEnd, barStart, barEnd, bazStart, bazEnd);

		coords = dataSource.rightBorders(0, fooInternal);
		logger.info("Result of requesting {} borders right from {} is: {}",
				0, fooInternal, coords);

		assertThat(coords).isEmpty();

		coords = dataSource.rightBorders(1, fooInternal);
		logger.info("Result of requesting {} borders right from {} is: {}",
				1, fooInternal, coords);

		assertThat(coords).containsExactly(fooEnd);

		coords = dataSource.rightBorders(2, fooInternal);
		logger.info("Result of requesting {} borders right from {} is: {}",
				2, fooInternal, coords);

		assertThat(coords).containsExactly(fooEnd, barStart);
	}

	@Test
	public void testChromosomeDataSourceLeftBorderGenerants() throws Exception {

		logger.info("Test chromosome data source left border generants");

		Set<ChromosomeBand> bands = dataSource.leftBordersGenerants(0, fooStart);
		logger.info("Generants of the next {} left borders from {} are: {}",
				0, fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.leftBordersGenerants(1, fooStart);
		logger.info("Generants of the next {} left borders from {} are: {}",
				1, fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.leftBordersGenerants(2, fooStart);
		logger.info("Generants of the next {} left borders from {} are: {}",
				2, fooStart, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.leftBordersGenerants(0, bazInternal);
		logger.info("Generants of the next {} left borders from {} are: {}",
				0, bazInternal, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.leftBordersGenerants(1, bazInternal);
		logger.info("Generants of the next {} left borders from {} are: {}",
				1, bazInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.leftBordersGenerants(2, bazInternal);
		logger.info("Generants of the next {} left borders from {} are: {}",
				2, bazInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand, bazBand);

		bands = dataSource.leftBordersGenerants(3, bazInternal);
		logger.info("Generants of the next {} left borders from {} are: {}",
				3, bazInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(barBand, bazBand);

		bands = dataSource.leftBordersGenerants(4, bazInternal);
		logger.info("Generants of the next {} left borders from {} are: {}",
				4, bazInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, barBand, bazBand);
	}

	@Test
	public void testChromosomeDataSourceRightBorderGenerants() throws Exception {

		logger.info("Test chromosome data source right border generants");

		Set<ChromosomeBand> bands = dataSource.rightBordersGenerants(0, bazEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				0, bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.rightBordersGenerants(1, bazEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				1, bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.rightBordersGenerants(2, bazEnd);
		logger.info("Generants of the next {} right borders from {} are: {}",
				2, bazEnd, bands);

		assertThat(bands).containsExactlyInAnyOrder(bazBand);

		bands = dataSource.rightBordersGenerants(0, fooInternal);
		logger.info("Generants of the next {} right borders from {} are: {}",
				0, fooInternal, bands);

		assertThat(bands).isEmpty();

		bands = dataSource.rightBordersGenerants(1, fooInternal);
		logger.info("Generants of the next {} right borders from {} are: {}",
				1, fooInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand);

		bands = dataSource.rightBordersGenerants(2, fooInternal);
		logger.info("Generants of the next {} right borders from {} are: {}",
				2, fooInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, barBand);

		bands = dataSource.rightBordersGenerants(3, fooInternal);
		logger.info("Generants of the next {} right borders from {} are: {}",
				3, fooInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, barBand);

		bands = dataSource.rightBordersGenerants(4, fooInternal);
		logger.info("Generants of the next {} right borders from {} are: {}",
				4, fooInternal, bands);

		assertThat(bands).containsExactlyInAnyOrder(fooBand, barBand, bazBand);
	}
}
