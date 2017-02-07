/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package pro.parseq.ghop.datasources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.Arrays;
import java.util.Comparator;
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

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.AttributeFilter;
import pro.parseq.ghop.datasources.filters.FilterOperator;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.BedBand;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.GenomicCoordinate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicBedFileDataSourceTest {

	private static final Logger logger = LoggerFactory.getLogger(BasicBedFileDataSourceTest.class);

	private static final String HUMAN_REFERENCE = "GRCh37.p13";
	private static final String CHR1 = "chr1";
	private static final String CHR2 = "chr2";
	private static final String TEST_TRACK = "regions";
	private static final String BED = "/basic_regions.bed";

	@Autowired
	private Comparator<GenomicCoordinate> comparator;

	private BasicBedFileDataSource dataSource, filteredDataSource;
	private Track track;

	@Before
	public void setUpDataSource() throws Exception {

		track = new Track(TEST_TRACK);
		logger.info("Creating new data source for a file: {}", BED);
		dataSource = new BasicBedFileDataSource(track,
				getClass().getResourceAsStream(BED),
				comparator, HUMAN_REFERENCE);

		@SuppressWarnings("unchecked")
		AttributeFilter<String> nameAttributeFilter = new AttributeFilter<String>(0,
				(Attribute<String>) dataSource.attributes().iterator().next(),
				FilterOperator.LIKE, Arrays.asList(CHR2), false);
		FilterQuery filterQuery = new FilterQuery(Arrays.asList(nameAttributeFilter), null);
		logger.info("Creating data source for filter: {}", filterQuery);
		filteredDataSource = (BasicBedFileDataSource) dataSource.filter(filterQuery);
	}

	// TODO: test this source method in a more 'robust' way
	@Test
	public void testBasicDataSourceBorderGenerants() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 5);
		Set<BedBand> generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(1);

		generants = filteredDataSource.borderGenerants(coord);
		assertThat(generants).isEmpty();

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		generants = dataSource.borderGenerants(coord);
		assertThat(generants).isEmpty();

		generants = filteredDataSource.borderGenerants(coord);
		assertThat(generants).isEmpty();

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 2);
		generants = dataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(2);

		generants = filteredDataSource.borderGenerants(coord);
		assertThat(generants).isEmpty();

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 3);
		generants = filteredDataSource.borderGenerants(coord);
		assertThat(generants).size().isEqualTo(2);
	}

	// TODO: test this source method in a more 'robust' way
	@Test
	public void testBasicDataSourceLeftBorders() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 5);
		List<GenomicCoordinate> coords = dataSource.leftBorders(3, coord);
		assertThat(coords).contains(coord, atIndex(3)).size().isEqualTo(4);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		coords = dataSource.leftBorders(3, coord);
		assertThat(coords).doesNotContain(coord).size().isEqualTo(3);

		coords = filteredDataSource.leftBorders(10, coord);
		assertThat(coords).isEmpty();

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 2);
		coords = dataSource.leftBorders(3, coord);
		assertThat(coords).contains(coord, atIndex(1)).size().isEqualTo(2);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 2);
		coords = dataSource.leftBorders(10, coord);
		assertThat(coords).contains(coord, atIndex(8)).size().isEqualTo(9);

		coords = filteredDataSource.leftBorders(10, coord);
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);

		coords = dataSource.leftBorders(0, coord);
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);
	}

	// TODO: test this source method in a more 'robust' way
	@Test
	public void testBasicDataSourceRightGenerants() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		List<GenomicCoordinate> coords = dataSource.rightBorders(11, coord);
		assertThat(coords).doesNotContain(coord).size().isEqualTo(10);

		coords = filteredDataSource.rightBorders(11, coord);
		assertThat(coords).doesNotContain(coord).size().isEqualTo(7);

		coords = dataSource.rightBorders(0, coord);
		assertThat(coords).size().isEqualTo(0);

		coords = dataSource.rightBorders(1, coord);
		assertThat(coords).doesNotContain(coord).size().isEqualTo(1);

		coords = filteredDataSource.rightBorders(1, coord);
		assertThat(coords).size().isEqualTo(1);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 7);
		coords = dataSource.rightBorders(11, coord);
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(10);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 7);
		coords = dataSource.rightBorders(4, coord);
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(5);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 3);
		coords = dataSource.rightBorders(0, coord);
		assertThat(coords).contains(coord, atIndex(0)).size().isEqualTo(1);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 7);
		coords = dataSource.rightBorders(0, coord);
		assertThat(coords).size().isEqualTo(0);

		coords = dataSource.rightBorders(1, coord);
		assertThat(coords).size().isEqualTo(1);

		coords = dataSource.rightBorders(4, coord);
		assertThat(coords).size().isEqualTo(2);

		coords = dataSource.rightBorders(2, coord);
		assertThat(coords).size().isEqualTo(2);
	}

	// TODO: test this source method in a more 'robust' way
	@Test
	public void testBasicDataSourceLeftBorderGenerants() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		Set<BedBand> generants = dataSource.leftBordersGenerants(1, coord);
		assertThat(generants).size().isEqualTo(1);

		generants = dataSource.leftBordersGenerants(0, coord);
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 5);
		generants = dataSource.leftBordersGenerants(1, coord);
		assertThat(generants).size().isEqualTo(2);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 3);
		generants = dataSource.leftBordersGenerants(1, coord);
		assertThat(generants).size().isEqualTo(3);

		generants = dataSource.leftBordersGenerants(2, coord);
		assertThat(generants).size().isEqualTo(4);

		generants = dataSource.leftBordersGenerants(10, coord);
		assertThat(generants).size().isEqualTo(4);
	}

	// TODO: test this source method in a more 'robust' way
	@Test
	public void testBasicDataSourceRightBorderGenerants() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		Set<BedBand> generants = dataSource.rightBordersGenerants(0, coord);
		assertThat(generants).size().isEqualTo(0);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		generants = dataSource.rightBordersGenerants(1, coord);
		assertThat(generants).size().isEqualTo(1);

		generants = dataSource.rightBordersGenerants(2, coord);
		assertThat(generants).size().isEqualTo(2);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 3);
		generants = dataSource.rightBordersGenerants(0, coord);
		assertThat(generants).size().isEqualTo(2);
	}

	// TODO: test this source method in a more 'robust' way
	@Test
	public void testBasicDataSourceCoverage() throws Exception {

		GenomicCoordinate coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 6);
		Set<BedBand> coverage = dataSource.coverage(coord);
		assertThat(coverage).size().isEqualTo(1);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 3);
		coverage = dataSource.coverage(coord);
		assertThat(coverage).size().isEqualTo(4);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR1, 10);
		coverage = dataSource.coverage(coord);
		assertThat(coverage).size().isEqualTo(0);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 3);
		coverage = dataSource.coverage(coord);
		assertThat(coverage).size().isEqualTo(2);

		coord = new GenomicCoordinate(HUMAN_REFERENCE, CHR2, 7);
		coverage = dataSource.coverage(coord);
		assertThat(coverage).size().isEqualTo(0);
	}
}
