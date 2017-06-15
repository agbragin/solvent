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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import pro.parseq.solvent.datasources.BandBuilder;
import pro.parseq.solvent.entities.BedBand;
import pro.parseq.solvent.exceptions.IllegalBedFileDataLineException;

public final class BedUtils {

	private static final String HEADER_LINE_PATTERN = "(browser|track|#)[^\\n]+";
	private static final Pattern headerLinePattern = Pattern.compile(HEADER_LINE_PATTERN);

	private static final String DATA_LINE_DELIMITER = "\t";
	private static final String DATA_LINE_PATTERN = "\\w+(\\t\\d+){2}(\\t[^\\t\\n]+)*";
	private static final Pattern dataLinePattern = Pattern.compile(DATA_LINE_PATTERN);

	public static final Predicate<String> isHeaderLine = new Predicate<String>() {

		@Override
		public boolean test(String line) {
			return headerLinePattern.matcher(line).matches();
		}
	};

	public static final Predicate<String> isDataLine = new Predicate<String>() {

		@Override
		public boolean test(String line) {
			return dataLinePattern.matcher(line).matches();
		}
	};

	public static final Region parseRegion(String line) {

		if (!isDataLine.test(line)) {
			throw new IllegalBedFileDataLineException(line);
		}

		String[] fields = line.split(DATA_LINE_DELIMITER);
		String chrom = fields[0];
		long chromStart = Long.parseLong(fields[1]);
		long chromEnd = Long.parseLong(fields[2]);
		List<String> opts = new ArrayList<>();
		for (int i = 3; i < fields.length; ++i) {
			opts.add(fields[i]);
		}

		return new Region(chrom, chromStart, chromEnd, opts);
	}
	
	public static List<BedBand> getBands(String referenceGenomeName, InputStream bed,
			BandBuilder<BedBand> bandBuilder) {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(bed))) {

			return reader.lines().filter(isHeaderLine.negate())
					.map(BedUtils::parseRegion)
					.map(bandBuilder::build)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException("I/O exception while BED file reading: "
					+ e.getMessage());
		}
	}

	/**
	 * Represents any valid (tab-delimited) BED file data entry
	 * 
	 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
	 */
	public static final class Region {

		// Mandatory BED fields
		private final String chrom;
		private final long chromStart;	// 0-based inclusive
		private final long chromEnd;	// 0-based exclusive

		// Holds all other specified optional (non-coordinate) field values
		private final List<String> opts;

		public Region(String chrom, long chromStart, long chromEnd, List<String> opts) {

			this.chrom = chrom;
			this.chromStart = chromStart;
			this.chromEnd = chromEnd;
			this.opts = opts;
		}

		public String getChrom() {
			return chrom;
		}

		public long getChromStart() {
			return chromStart;
		}

		public long getChromEnd() {
			return chromEnd;
		}

		public List<String> getOpts() {
			return opts;
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder(chrom)
					.append(DATA_LINE_DELIMITER).append(chromStart)
					.append(DATA_LINE_DELIMITER).append(chromEnd);
			if (opts.size() > 0) {
				sb.append(DATA_LINE_DELIMITER).append(opts.stream()
						.collect(Collectors.joining(DATA_LINE_DELIMITER)));
			}

			return sb.toString();
		}
	}
}
