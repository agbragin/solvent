package pro.parseq.ghop.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pro.parseq.ghop.data.GenomicCoordinate.CoordinateComparator;

public class GenomicCoordinateTest {

	private static final String referenceGenomeA = "A";
	private static final String referenceGenomeB = "B";
	private static final String contigA = "a";
	private static final String contigB = "b";

	private static final CoordinateComparator comparator = new GenomicCoordinate.CoordinateComparator();

	@Test
	public void testGenomicCoordinateComparison() throws Exception {

		GenomicCoordinate coordA = new GenomicCoordinate(referenceGenomeA, contigA, 0);
		GenomicCoordinate coordB = new GenomicCoordinate(referenceGenomeB, contigA, 0);
		assertThat(comparator.compare(coordA, coordB)).isLessThan(0);
		assertThat(comparator.compare(coordB, coordA)).isGreaterThan(0);

		coordB = new GenomicCoordinate(referenceGenomeA, contigB, 0);
		assertThat(comparator.compare(coordA, coordB)).isLessThan(0);
		assertThat(comparator.compare(coordB, coordA)).isGreaterThan(0);

		coordB = new GenomicCoordinate(referenceGenomeA, contigA, 0);
		assertThat(comparator.compare(coordA, coordB)).isEqualTo(0);
		assertThat(comparator.compare(coordB, coordA)).isEqualTo(0);

		coordB = new GenomicCoordinate(referenceGenomeA, contigA, 1);
		assertThat(comparator.compare(coordA, coordB)).isLessThan(0);
		assertThat(comparator.compare(coordB, coordA)).isGreaterThan(0);
	}

	@Test
	public void testGenomicCoordinateSorting() throws Exception {

		GenomicCoordinate coordA = new GenomicCoordinate(referenceGenomeA, contigA, 5);
		GenomicCoordinate coordB = new GenomicCoordinate(referenceGenomeA, contigA, 3);
		GenomicCoordinate coordC = new GenomicCoordinate(referenceGenomeA, contigA, 1);
		GenomicCoordinate coordD = new GenomicCoordinate(referenceGenomeA, contigB, 0);
		GenomicCoordinate coordE = new GenomicCoordinate(referenceGenomeB, contigA, 0);

		List<GenomicCoordinate> coords = new ArrayList<>();
		coords.add(coordE);
		coords.add(coordD);
		coords.add(coordA);
		coords.add(coordC);
		coords.add(coordB);
		coords.add(coordC);
		Collections.sort(coords, comparator);

		assertThat(coords.get(0)).isEqualTo(coordC);
		assertThat(coords.get(1)).isEqualTo(coordC);
		assertThat(coords.get(2)).isEqualTo(coordB);
		assertThat(coords.get(3)).isEqualTo(coordA);
		assertThat(coords.get(4)).isEqualTo(coordD);
		assertThat(coords.get(5)).isEqualTo(coordE);
	}
}
