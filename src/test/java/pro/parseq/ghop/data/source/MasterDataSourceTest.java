package pro.parseq.ghop.data.source;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.Query;
import pro.parseq.ghop.data.Track;

public class MasterDataSourceTest {

	private static final String GENOME = "testGenome";

	private static final String CHROMOSOMES_TRACK = "chromosomes";
	private static final String REGIONS_TRACK = "regions";

	private static final Track chromosomes = new Track(CHROMOSOMES_TRACK);
	private static final Track regions = new Track(REGIONS_TRACK);

	private static final String CHROMOSOMES_TRACK_BED = "/chromosomes.bed";
	private static final String REGIONS_TRACK_BED = "/regions.bed";

	/**
	 *    10 --1-- 20       20 --2-- 50 --3-- 70             0 --5-- 10 40 --6-- 50
	 *                                   60 ----4---- 140
	 * 0 ====chr1==== 100 0 ============chr2============ 150 0 ======chr4======= 50
	 */
	private static final GenomicCoordinate chr1_0 = new GenomicCoordinate(GENOME, "chr1", 0);
	private static final GenomicCoordinate chr1_10 = new GenomicCoordinate(GENOME, "chr1", 10);
	private static final GenomicCoordinate chr1_20 = new GenomicCoordinate(GENOME, "chr1", 20);
	private static final GenomicCoordinate chr1_100 = new GenomicCoordinate(GENOME, "chr1", 100);
	private static final GenomicCoordinate chr2_0 = new GenomicCoordinate(GENOME, "chr2", 0);
	private static final GenomicCoordinate chr2_20 = new GenomicCoordinate(GENOME, "chr2", 20);
	private static final GenomicCoordinate chr2_50 = new GenomicCoordinate(GENOME, "chr2", 50);
	private static final GenomicCoordinate chr2_60 = new GenomicCoordinate(GENOME, "chr2", 60);
	private static final GenomicCoordinate chr2_70 = new GenomicCoordinate(GENOME, "chr2", 70);
	private static final GenomicCoordinate chr2_140 = new GenomicCoordinate(GENOME, "chr2", 140);
	private static final GenomicCoordinate chr2_150 = new GenomicCoordinate(GENOME, "chr2", 150);
	private static final GenomicCoordinate chr4_0 = new GenomicCoordinate(GENOME, "chr4", 0);
	private static final GenomicCoordinate chr4_10 = new GenomicCoordinate(GENOME, "chr4", 10);
	private static final GenomicCoordinate chr4_40 = new GenomicCoordinate(GENOME, "chr4", 40);
	private static final GenomicCoordinate chr4_50 = new GenomicCoordinate(GENOME, "chr4", 50);

	private static final Band chr1 = new Band.BandBuilder(chromosomes, chr1_0, chr1_100).build();
	private static final Band chr2 = new Band.BandBuilder(chromosomes, chr2_0, chr2_150).build();
	private static final Band chr4 = new Band.BandBuilder(chromosomes, chr4_0, chr4_50).build();

	private static final Band region1 = new Band.BandBuilder(regions, chr1_10, chr1_20).build();
	private static final Band region2 = new Band.BandBuilder(regions, chr2_20, chr2_50).build();
	private static final Band region3 = new Band.BandBuilder(regions, chr2_50, chr2_70).build();
	private static final Band region4 = new Band.BandBuilder(regions, chr2_60, chr2_140).build();
	private static final Band region5 = new Band.BandBuilder(regions, chr4_0, chr4_10).build();
	private static final Band region6 = new Band.BandBuilder(regions, chr4_40, chr4_50).build();

	private DataSource chromosomesSource = new BedFileDataSource(chromosomes,
			getClass().getResourceAsStream(CHROMOSOMES_TRACK_BED), GENOME);
	private DataSource regionsSource = new BedFileDataSource(regions,
			getClass().getResourceAsStream(REGIONS_TRACK_BED), GENOME);

	private MasterDataSource masterDataSource;

	@Before
	public void setUp() throws Exception {

		masterDataSource = new MasterDataSource();
		masterDataSource.addDataSource(chromosomesSource);
		masterDataSource.addDataSource(regionsSource);
	}

	@Test
	public void testMasterDataSource() throws Exception {

		Set<Track> tracks = new HashSet<>();
		tracks.add(regions);
		GenomicCoordinate coord = new GenomicCoordinate(GENOME, "chr3", 100);
		Query query = new Query(coord, 0, 0, tracks);

		Set<Band> bands = masterDataSource.getBands(query);
		assertThat(bands).isEmpty();

		query = new Query(coord, 0, 1, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5).size().isEqualTo(1);

		query = new Query(coord, 0, 2, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5).size().isEqualTo(1);

		query = new Query(coord, 0, 3, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6).size().isEqualTo(2);

		query = new Query(coord, 0, 4, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6).size().isEqualTo(2);

		query = new Query(coord, 0, 5, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6).size().isEqualTo(2);

		query = new Query(coord, 1, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region4).size().isEqualTo(1);

		query = new Query(coord, 2, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3, region4).size().isEqualTo(2);

		query = new Query(coord, 3, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3, region4).size().isEqualTo(2);

		query = new Query(coord, 4, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 5, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 6, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4).size().isEqualTo(4);

		query = new Query(coord, 7, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4).size().isEqualTo(4);

		query = new Query(coord, 8, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4).size().isEqualTo(4);

		coord = new GenomicCoordinate(GENOME, "chr2", 50);
		query = new Query(coord, 0, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3).size().isEqualTo(2);

		query = new Query(coord, 1, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3).size().isEqualTo(2);

		query = new Query(coord, 2, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3).size().isEqualTo(3);

		query = new Query(coord, 3, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3).size().isEqualTo(3);

		query = new Query(coord, 4, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3).size().isEqualTo(3);

		query = new Query(coord, 0, 1, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 0, 2, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 0, 3, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 0, 4, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5).size().isEqualTo(4);

		query = new Query(coord, 0, 5, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5).size().isEqualTo(4);

		query = new Query(coord, 0, 6, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5, region6).size().isEqualTo(5);

		query = new Query(coord, 0, 7, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5, region6).size().isEqualTo(5);

		query = new Query(coord, 0, 8, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5, region6).size().isEqualTo(5);

		query = new Query(coord, 0, 9, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5, region6).size().isEqualTo(5);

		query = new Query(coord, 1, 9, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, region5, region6).size().isEqualTo(5);

		query = new Query(coord, 2, 9, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, region5, region6).size().isEqualTo(6);

		query = new Query(coord, 3, 9, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, region5, region6).size().isEqualTo(6);

		query = new Query(coord, 4, 9, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, region5, region6).size().isEqualTo(6);

		query = new Query(coord, 5, 9, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, region5, region6).size().isEqualTo(6);

		coord = new GenomicCoordinate(GENOME, "chr2", 55);
		query = new Query(coord, 0, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3).size().isEqualTo(1);

		query = new Query(coord, 1, 1, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 2, 2, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		query = new Query(coord, 2, 3, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4).size().isEqualTo(3);

		coord = new GenomicCoordinate(GENOME, "chr2", 70);
		query = new Query(coord, 0, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3, region4).size().isEqualTo(2);

		query = new Query(coord, 1, 1, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3, region4).size().isEqualTo(2);

		tracks.add(chromosomes);
		coord = new GenomicCoordinate(GENOME, "chr3", 100);
		query = new Query(coord, 0, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).isEmpty();

		query = new Query(coord, 0, 1, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, chr4).size().isEqualTo(2);

		query = new Query(coord, 0, 2, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, chr4).size().isEqualTo(2);

		query = new Query(coord, 0, 3, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6, chr4).size().isEqualTo(3);

		query = new Query(coord, 0, 4, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6, chr4).size().isEqualTo(3);

		query = new Query(coord, 0, 5, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6, chr4).size().isEqualTo(3);

		query = new Query(coord, 0, 6, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region5, region6, chr4).size().isEqualTo(3);

		query = new Query(coord, 1, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(chr2).size().isEqualTo(1);

		query = new Query(coord, 2, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region4, chr2).size().isEqualTo(2);

		query = new Query(coord, 3, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3, region4, chr2).size().isEqualTo(3);

		query = new Query(coord, 4, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region3, region4, chr2).size().isEqualTo(3);

		query = new Query(coord, 5, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, chr2).size().isEqualTo(4);

		query = new Query(coord, 6, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, chr2).size().isEqualTo(4);

		query = new Query(coord, 7, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, chr2).size().isEqualTo(4);

		query = new Query(coord, 8, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region2, region3, region4, chr1, chr2).size().isEqualTo(5);

		query = new Query(coord, 9, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, chr1, chr2).size().isEqualTo(6);

		query = new Query(coord, 10, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, chr1, chr2).size().isEqualTo(6);

		query = new Query(coord, 11, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, chr1, chr2).size().isEqualTo(6);

		query = new Query(coord, 12, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, chr1, chr2).size().isEqualTo(6);

		query = new Query(coord, 13, 0, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, chr1, chr2).size().isEqualTo(6);

		query = new Query(coord, 50, 50, tracks);
		bands = masterDataSource.getBands(query);
		assertThat(bands).contains(region1, region2, region3, region4, region5, region6, chr1, chr2, chr4).size().isEqualTo(9);
	}
}
