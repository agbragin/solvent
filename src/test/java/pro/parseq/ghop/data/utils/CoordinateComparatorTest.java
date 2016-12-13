package pro.parseq.ghop.data.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.UnknownContigException;
import pro.parseq.ghop.data.UnknownReferenceGenomeException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoordinateComparatorTest {

	private static final String UNKNOWN_REFERENCE = "unknown";
	private static final String GRCh37_p13 = "GRCh37.p13";
	private static final String TEST_REFERENCE = "TestReference";

	private static final String UNKNOWN_CONTIG = "unknown";
	private static final String CHR1 = "chr1";
	private static final String CHR2 = "chr2";
	private static final String SIMEBIGCONTIG = "somebigcontig";

	@Autowired
	private CoordinateComparator comparator;

	@Test
	public void testUnknownReferenceGenome() throws Exception {

		GenomicCoordinate unknown = new GenomicCoordinate(UNKNOWN_REFERENCE, CHR1, 0);
		try {
			comparator.compare(unknown, unknown);
			fail("UnknownReferenceGenomeException should be thrown");
		} catch (UnknownReferenceGenomeException e) {
			assertThat(e.getReferenceGenome()).isEqualTo(UNKNOWN_REFERENCE);
		}
	}

	@Test
	public void testUnknownContig() throws Exception {

		GenomicCoordinate unknown = new GenomicCoordinate(TEST_REFERENCE, UNKNOWN_CONTIG, 0);
		try {
			comparator.compare(unknown, unknown);
			fail("UnknownContigException should be thrown");
		} catch (UnknownContigException e) {
			assertThat(e.getReferenceGenome()).isEqualTo(TEST_REFERENCE);
			assertThat(e.getContig()).isEqualTo(UNKNOWN_CONTIG);
		}
	}

	@Test
	public void testDifferentReferenceGenomes() throws Exception {

		GenomicCoordinate testReferenceCoord = new GenomicCoordinate(TEST_REFERENCE, SIMEBIGCONTIG, 0);
		GenomicCoordinate grch37p13Coord = new GenomicCoordinate(GRCh37_p13, CHR1, 0);

		assertThat(comparator.compare(testReferenceCoord, grch37p13Coord)).isEqualTo(1);
		assertThat(comparator.compare(grch37p13Coord, testReferenceCoord)).isEqualTo(-1);
	}

	@Test
	public void testDifferentContigs() throws Exception {

		GenomicCoordinate chr1Coord = new GenomicCoordinate(GRCh37_p13, CHR1, 100);
		GenomicCoordinate chr2Coord = new GenomicCoordinate(GRCh37_p13, CHR2, 0);

		assertThat(comparator.compare(chr1Coord, chr2Coord)).isEqualTo(-1);
		assertThat(comparator.compare(chr2Coord, chr1Coord)).isEqualTo(1);

		assertThat(comparator.compare(chr1Coord, chr1Coord)).isEqualTo(0);
		assertThat(comparator.compare(chr1Coord, new GenomicCoordinate(GRCh37_p13, CHR1, 100))).isEqualTo(0);
		assertThat(comparator.compare(new GenomicCoordinate(GRCh37_p13, CHR1, 100), chr1Coord)).isEqualTo(0);

		assertThat(comparator.compare(chr2Coord, chr2Coord)).isEqualTo(0);
		assertThat(comparator.compare(chr2Coord, new GenomicCoordinate(GRCh37_p13, CHR2, 0))).isEqualTo(0);
		assertThat(comparator.compare(new GenomicCoordinate(GRCh37_p13, CHR2, 0), chr2Coord)).isEqualTo(0);
	}

	@Test
	public void testSameContigs() throws Exception {

		GenomicCoordinate coord1 = new GenomicCoordinate(GRCh37_p13, CHR2, 100);
		GenomicCoordinate coord2 = new GenomicCoordinate(GRCh37_p13, CHR2, 50);

		assertThat(comparator.compare(coord1, coord2)).isEqualTo(1);
		assertThat(comparator.compare(coord2, coord1)).isEqualTo(-1);
	}
}
