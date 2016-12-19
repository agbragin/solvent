package pro.parseq.ghop.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ContigNotFoundException;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GenomicCoordinateComparatorTest {

	@Autowired
	private GenomicCoordinateComparator comparator;

	@Test
	public void testGenomicCoordinateComparator() throws Exception {

		ReferenceGenome testReference = new ReferenceGenome("TestReference");
		ReferenceGenome humanReference = new ReferenceGenome("GRCh37.p13");
		ReferenceGenome unknownReference = new ReferenceGenome("unknown");

		Contig somebigcontig = new Contig(testReference, "somebigcontig");
		Contig chr1 = new Contig(humanReference, "chr1");
		Contig chr2 = new Contig(humanReference, "chr2");
		Contig chrM = new Contig(humanReference, "chrM");
		Contig unknownContig1 = new Contig(unknownReference, "chr1");
		Contig unknownContig2 = new Contig(humanReference, "chr23");

		GenomicCoordinate somebigcontig_1 = new GenomicCoordinate(somebigcontig, 1);
		GenomicCoordinate somebigcontig_3 = new GenomicCoordinate(somebigcontig, 3);
		GenomicCoordinate somebigcontig_12 = new GenomicCoordinate(somebigcontig, 12);

		GenomicCoordinate chr1_10 = new GenomicCoordinate(chr1, 10);
		GenomicCoordinate chr1_20 = new GenomicCoordinate(chr1, 20);
		GenomicCoordinate chr2_1 = new GenomicCoordinate(chr2, 1);
		GenomicCoordinate chr2_2 = new GenomicCoordinate(chr2, 2);
		GenomicCoordinate chrM_5 = new GenomicCoordinate(chrM, 5);
		GenomicCoordinate chrM_6 = new GenomicCoordinate(chrM, 6);

		GenomicCoordinate illegal1 = new GenomicCoordinate(unknownContig1, 10);
		GenomicCoordinate illegal2 = new GenomicCoordinate(unknownContig2, 10);

		List<GenomicCoordinate> coords = Arrays.asList(
				somebigcontig_3, chrM_6, chrM_5, somebigcontig_1, chr1_20,
				chr2_2, chr1_10, chrM_5, somebigcontig_12, chr1_10, chr2_1,
				somebigcontig_3);
		Collections.sort(coords, comparator);

		assertThat(coords).containsExactly(chr1_10, chr1_10, chr1_20, chr2_1,
				chr2_2, chrM_5, chrM_5, chrM_6, somebigcontig_1,
				somebigcontig_3, somebigcontig_3, somebigcontig_12);

		coords = Arrays.asList(illegal1, chr1_10, chrM_5);
		try {
			Collections.sort(coords, comparator);
			fail("Should throw ReferenceGenomeNofFoundException on coordinate with unknown reference genome");
		} catch (ReferenceGenomeNotFoundException e) {}

		coords = Arrays.asList(chr1_10, chrM_5, illegal2);
		try {
			Collections.sort(coords, comparator);
			fail("Should throw ContigNofFoundException on coordinate with unknown contig");
		} catch (ContigNotFoundException e) {}
	}
}
