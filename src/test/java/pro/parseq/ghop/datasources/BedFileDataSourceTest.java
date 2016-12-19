package pro.parseq.ghop.datasources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.Filters;
import pro.parseq.ghop.utils.GenomicCoordinate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BedFileDataSourceTest {

	@Autowired
	private DataSourceFactory dataSourceFactory;

	private DataSource dataSource;

	private Track track;
	private ReferenceGenome referenceGenome;

	@Before
	public void setUpDataSource() throws Exception {

		track = new Track("testTrack");
		referenceGenome = new ReferenceGenome("GRCh37.p13");
		dataSource = dataSourceFactory.newBedFileDataSourceInstance(track,
				getClass().getResourceAsStream("/contigs.bed"), referenceGenome);
	}

	@Test
	public void testInstantiation() throws Exception {
		assertThat(dataSource).isNotNull();
		assertThat(dataSource.track()).isEqualTo(track);
	}

	@Test
	public void testBorderGenerants() throws Exception {

		Contig chr1 = new Contig(referenceGenome, "chr1");

		GenomicCoordinate coord = new GenomicCoordinate(chr1, 5);
		Set<Band> generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(1);

		coord = new GenomicCoordinate(chr1, 6);
		generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(chr1, 2);
		generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(2);
	}

	@Test
	public void testLeftBorders() throws Exception {

		Contig chr1 = new Contig(referenceGenome, "chr1");
		Contig chr2 = new Contig(referenceGenome, "chr2");

		GenomicCoordinate coord = new GenomicCoordinate(chr1, 5);
		List<GenomicCoordinate> coords = dataSource.leftBorders(3, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(3)).size().isEqualTo(4);

		coord = new GenomicCoordinate(chr1, 6);
		coords = dataSource.leftBorders(3, coord, new Filters());
		assertThat(coords).doesNotContain(coord).size().isEqualTo(3);

		coord = new GenomicCoordinate(chr1, 2);
		coords = dataSource.leftBorders(3, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(1)).size().isEqualTo(2);

		coord = new GenomicCoordinate(chr2, 2);
		coords = dataSource.leftBorders(10, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(8)).size().isEqualTo(9);

		coords = dataSource.leftBorders(0, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);
	}

	@Test
	public void testRightBorders() throws Exception {

		Contig chr1 = new Contig(referenceGenome, "chr1");
		Contig chr2 = new Contig(referenceGenome, "chr2");

		GenomicCoordinate coord = new GenomicCoordinate(chr1, 6);
		List<GenomicCoordinate> coords = dataSource.rightBorders(11, coord, new Filters());
		assertThat(coords).doesNotContain(coord).size().isEqualTo(10);

		coords = dataSource.rightBorders(0, coord, new Filters());
		assertThat(coords).size().isEqualTo(0);

		coords = dataSource.rightBorders(1, coord, new Filters());
		assertThat(coords).doesNotContain(coord).size().isEqualTo(1);

		coord = new GenomicCoordinate(chr1, 7);
		coords = dataSource.rightBorders(11, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(10);

		coord = new GenomicCoordinate(chr1, 7);
		coords = dataSource.rightBorders(4, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(5);

		coord = new GenomicCoordinate(chr2, 3);
		coords = dataSource.rightBorders(0, coord, new Filters());
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);

		coord = new GenomicCoordinate(chr2, 7);
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

		Contig chr1 = new Contig(referenceGenome, "chr1");

		GenomicCoordinate start = new GenomicCoordinate(chr1, 2);
		GenomicCoordinate end = new GenomicCoordinate(chr1, 5);
		Band band = new Band.BandBuilder("testTrack_2", track, start, end).build();

		GenomicCoordinate coord = new GenomicCoordinate(chr1, 6);
		Set<Band> generants = dataSource.leftBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(1);

		generants = dataSource.leftBordersGenerants(0, coord, new Filters());
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(chr1, 5);
		generants = dataSource.leftBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(2);

		coord = new GenomicCoordinate(chr1, 3);
		generants = dataSource.leftBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(3);

		generants = dataSource.leftBordersGenerants(2, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(4);

		generants = dataSource.leftBordersGenerants(10, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(4);
	}

	@Test
	public void testRightGenerants() throws Exception {

		Contig chr1 = new Contig(referenceGenome, "chr1");
		Contig chr2 = new Contig(referenceGenome, "chr2");

		GenomicCoordinate start = new GenomicCoordinate(chr1, 3);
		GenomicCoordinate end = new GenomicCoordinate(chr1, 7);
		Band band = new Band.BandBuilder("testTrack_3", track, start, end).build();

		GenomicCoordinate coord = new GenomicCoordinate(chr1, 6);
		Set<Band> generants = dataSource.rightBordersGenerants(0, coord, new Filters());
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(chr1, 6);
		generants = dataSource.rightBordersGenerants(1, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(1);

		generants = dataSource.rightBordersGenerants(2, coord, new Filters());
		assertThat(generants).contains(band).size().isEqualTo(2);

		coord = new GenomicCoordinate(chr2, 3);
		generants = dataSource.rightBordersGenerants(0, coord, new Filters());
		assertThat(generants).size().isEqualTo(2);
	}

	@Test
	public void testCoverage() throws Exception {

		Contig chr1 = new Contig(referenceGenome, "chr1");
		Contig chr2 = new Contig(referenceGenome, "chr2");

		GenomicCoordinate start = new GenomicCoordinate(chr1, 3);
		GenomicCoordinate end = new GenomicCoordinate(chr1, 7);
		Band band = new Band.BandBuilder("testTrack_3", track, start, end).build();

		GenomicCoordinate coord = new GenomicCoordinate(chr1, 6);
		Set<Band> coverage = dataSource.coverage(coord, null);
		assertThat(coverage).contains(band).size().isEqualTo(1);

		coord = new GenomicCoordinate(chr1, 3);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).contains(band).size().isEqualTo(4);

		coord = new GenomicCoordinate(chr1, 10);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).size().isEqualTo(0);

		coord = new GenomicCoordinate(chr2, 3);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).size().isEqualTo(2);

		coord = new GenomicCoordinate(chr2, 7);
		coverage = dataSource.coverage(coord, null);
		assertThat(coverage).size().isEqualTo(0);
	}
}
