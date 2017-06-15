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
package pro.parseq.solvent.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.Test;

import pro.parseq.solvent.exceptions.IllegalBedFileDataLineException;
import pro.parseq.solvent.utils.BedUtils;
import pro.parseq.solvent.utils.BedUtils.Region;

public class BedUtilsTest {

	private static final String TAB = "\t";
	private static final String SPACE = " ";

	private static final String CHR1 = "chr1";
	private static final String CHR2 = "chr2";
	private static final long TEN = 10;
	private static final long TWENTY = 20;
	private static final long THIRTY = 30;
	private static final long FOURTY = 40;
	private static final String NAME = "A";
	private static final String OPT1 = "opt1";
	private static final String OPT2 = "opt2";

	private static final String VALID_DATA_LINE_WO_OPTS = String.join(TAB, CHR1, String.valueOf(TEN), String.valueOf(TWENTY));
	private static final String VALID_DATA_LINE_WITH_NAME = String.join(TAB, CHR1, String.valueOf(TEN), String.valueOf(TWENTY), NAME);
	private static final String VALID_DATA_LINE_WITH_OPT = String.join(TAB, CHR2, String.valueOf(THIRTY), String.valueOf(FOURTY), String.join(SPACE, OPT1, OPT2));
	private static final String VALID_DATA_LINE_WITH_OPTS = String.join(TAB, CHR2, String.valueOf(THIRTY), String.valueOf(FOURTY), OPT1, OPT2);
	private static final String ILLEGAL_DELIMITED_DATA_LINE = String.join(SPACE, CHR1, String.valueOf(TEN), String.valueOf(TWENTY));
	private static final String ILLEGAL_COORD_VALUE_DATA_LINE = String.join(TAB, CHR2, "thirty", "fourty");
	private static final String MISSING_MANDATORY_FIELD_DATA_LINE = String.join(TAB, CHR1, String.valueOf(TEN));

	private static final Consumer<Region> chr1Region = region -> {

		assertThat(region.getChrom()).isEqualTo(CHR1);
		assertThat(region.getChromStart()).isEqualTo(TEN);
		assertThat(region.getChromEnd()).isEqualTo(TWENTY);
	};

	private static final Consumer<Region> namedRegion = region -> {

		assertThat(region.getChrom()).isEqualTo(CHR1);
		assertThat(region.getChromStart()).isEqualTo(TEN);
		assertThat(region.getChromEnd()).isEqualTo(TWENTY);
		assertThat(region.getOpts()).containsExactly(NAME);
	};

	private static final Consumer<Region> chr2Region = region -> {

		assertThat(region.getChrom()).isEqualTo(CHR2);
		assertThat(region.getChromStart()).isEqualTo(THIRTY);
		assertThat(region.getChromEnd()).isEqualTo(FOURTY);
	};

	private static final Consumer<Region> hasTestOpts = region -> {
		assertThat(region.getOpts()).containsExactly(OPT1, OPT2).size().isEqualTo(2);
	};

	private static final Consumer<Region> hasCollapsedTestOpts = region -> {
		assertThat(region.getOpts()).containsExactly(String.join(SPACE, OPT1, OPT2)).size().isEqualTo(1);
	};

	@Test
	public void testDataLinePredicate() throws Exception {

		assertThat(BedUtils.isDataLine).acceptsAll(
				Arrays.asList(VALID_DATA_LINE_WO_OPTS,
						VALID_DATA_LINE_WITH_OPTS));
		assertThat(BedUtils.isDataLine).rejectsAll(
				Arrays.asList(ILLEGAL_DELIMITED_DATA_LINE,
						ILLEGAL_COORD_VALUE_DATA_LINE,
						MISSING_MANDATORY_FIELD_DATA_LINE));
	}

	@Test
	public void testValidDataLineWOOptsParsing() throws Exception {

		try {

			assertThat(BedUtils.parseRegion(VALID_DATA_LINE_WO_OPTS)).satisfies(chr1Region);
			assertThat(BedUtils.parseRegion(VALID_DATA_LINE_WITH_OPTS)).satisfies(chr2Region).satisfies(hasTestOpts);
			assertThat(BedUtils.parseRegion(VALID_DATA_LINE_WITH_NAME)).satisfies(namedRegion);
			assertThat(BedUtils.parseRegion(VALID_DATA_LINE_WITH_OPT)).satisfies(chr2Region).satisfies(hasCollapsedTestOpts);
		} catch (IllegalBedFileDataLineException e) {
			fail("Valid BED datalines should not cause an exception!");
		}

		try {
			BedUtils.parseRegion(ILLEGAL_DELIMITED_DATA_LINE);
			fail("Invalid delimited BED dataline should cause an exception!");
		} catch (IllegalBedFileDataLineException e) {}

		try {
			BedUtils.parseRegion(ILLEGAL_COORD_VALUE_DATA_LINE);
			fail("BED dataline with invalid coordinate value should cause an exception!");
		} catch (IllegalBedFileDataLineException e) {}

		try {
			BedUtils.parseRegion(MISSING_MANDATORY_FIELD_DATA_LINE);
			fail("BED dataline with missing mandatory field should cause an exception!");
		} catch (IllegalBedFileDataLineException e) {}
	}
}
