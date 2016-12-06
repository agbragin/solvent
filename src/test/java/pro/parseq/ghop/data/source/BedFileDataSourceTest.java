package pro.parseq.ghop.data.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.Filters;
import pro.parseq.ghop.data.GenomicCoordinate;

public class BedFileDataSourceTest {

	private static final String GENOME = "test";
	private static final String LAYER = "seqs";
	private static final String CHR1 = "chr1";
	private static final String CHR2 = "chr2";

	private DataSource dataSource = new BedFileDataSource(LAYER,
			getClass().getResourceAsStream("/contigs.bed"), GENOME);

	@Test
	public void testInstantiation() throws Exception {
		assertThat(dataSource).isNotNull();
		assertThat(dataSource.layer()).isEqualTo(LAYER);
	}

	@Test
	public void testBorderGenerants() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(GENOME, CHR1, 5);
		Set<Band> generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(1);

		coord = new GenomicCoordinate(GENOME, CHR1, 6);
		generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(GENOME, CHR1, 2);
		generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(2);
	}

	@Test
	public void testLeftBorders() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(GENOME, CHR1, 5);
		List<GenomicCoordinate> coords = dataSource.leftBorders(3, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(3)).size().isEqualTo(4);

		coord = new GenomicCoordinate(GENOME, CHR1, 6);
		coords = dataSource.leftBorders(3, coord, new Filters());
		assertThat(coords).doesNotContain(coord).size().isEqualTo(3);

		coord = new GenomicCoordinate(GENOME, CHR1, 2);
		coords = dataSource.leftBorders(3, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(1)).size().isEqualTo(2);

		coord = new GenomicCoordinate(GENOME, CHR2, 2);
		coords = dataSource.leftBorders(10, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(8)).size().isEqualTo(9);

		coords = dataSource.leftBorders(0, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);
	}

	@Test
	public void testRightBorders() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(GENOME, CHR1, 6);
		List<GenomicCoordinate> coords = dataSource.rightBorders(11, coord, new Filters());
		assertThat(coords).doesNotContain(coord).size().isEqualTo(10);

		coords = dataSource.rightBorders(0, coord, new Filters());
		assertThat(coords).size().isEqualTo(0);

		coords = dataSource.rightBorders(1, coord, new Filters());
		assertThat(coords).doesNotContain(coord).size().isEqualTo(1);

		coord = new GenomicCoordinate(GENOME, CHR1, 7);
		coords = dataSource.rightBorders(11, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(10);

		coord = new GenomicCoordinate(GENOME, CHR1, 7);
		coords = dataSource.rightBorders(4, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(5);

		coord = new GenomicCoordinate(GENOME, CHR2, 3);
		coords = dataSource.rightBorders(0, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);

		coord = new GenomicCoordinate(GENOME, CHR2, 7);
		coords = dataSource.rightBorders(0, coord, new Filters());
		assertThat(coords).size().isEqualTo(0);

		coords = dataSource.rightBorders(1, coord, new Filters());
		assertThat(coords).size().isEqualTo(1);

		coords = dataSource.rightBorders(4, coord, new Filters());
		assertThat(coords).size().isEqualTo(2);

		coords = dataSource.rightBorders(2, coord, new Filters());
		assertThat(coords).size().isEqualTo(2);
	}

	@Test
	public void testLeftGenerants() throws Exception {

		GenomicCoordinate start = new GenomicCoordinate(GENOME, CHR1, 2);
		GenomicCoordinate end = new GenomicCoordinate(GENOME, CHR1, 5);
		Band band = new Band(LAYER, start, end);

		GenomicCoordinate coord = new GenomicCoordinate(GENOME, CHR1, 6);
		Set<Band> generants = dataSource.leftBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(1);

		generants = dataSource.leftBordersGenerants(0, coord, new Filters());
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(GENOME, CHR1, 5);
		generants = dataSource.leftBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(2);

		coord = new GenomicCoordinate(GENOME, CHR1, 3);
		generants = dataSource.leftBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(3);

		generants = dataSource.leftBordersGenerants(2, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(4);

		generants = dataSource.leftBordersGenerants(10, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(4);
	}

	@Test
	public void testRightGenerants() throws Exception {

		GenomicCoordinate start = new GenomicCoordinate(GENOME, CHR1, 3);
		GenomicCoordinate end = new GenomicCoordinate(GENOME, CHR1, 7);
		Band band = new Band(LAYER, start, end);

		GenomicCoordinate coord = new GenomicCoordinate(GENOME, CHR1, 6);
		Set<Band> generants = dataSource.rightBordersGenerants(0, coord, new Filters());
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(GENOME, CHR1, 6);
		generants = dataSource.rightBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(1);

		generants = dataSource.rightBordersGenerants(2, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(2);

		coord = new GenomicCoordinate(GENOME, CHR2, 3);
		generants = dataSource.rightBordersGenerants(0, coord, new Filters());
		assertThat(generants).size().isEqualTo(2);
	}

	@Test
	public void testCoverage() throws Exception {

		GenomicCoordinate start = new GenomicCoordinate(GENOME, CHR1, 3);
		GenomicCoordinate end = new GenomicCoordinate(GENOME, CHR1, 7);
		Band band = new Band(LAYER, start, end);

		GenomicCoordinate coord = new GenomicCoordinate(GENOME, CHR1, 6);
		Set<Band> coverage = dataSource.coverage(coord, null);
		assertThat(coverage).contains(band).size().isEqualTo(1);

		coord = new GenomicCoordinate(GENOME, CHR1, 3);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).contains(band).size().isEqualTo(4);

		coord = new GenomicCoordinate(GENOME, CHR1, 10);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).size().isEqualTo(0);

		coord = new GenomicCoordinate(GENOME, CHR2, 3);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).size().isEqualTo(2);

		coord = new GenomicCoordinate(GENOME, CHR2, 7);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).size().isEqualTo(0);
	}
}
