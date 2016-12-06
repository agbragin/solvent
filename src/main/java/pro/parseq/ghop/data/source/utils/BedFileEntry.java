package pro.parseq.ghop.data.source.utils;

import java.util.regex.Pattern;

public class BedFileEntry {

	public static final String DELIMITER = "\t";
	public static final String PATTERN = "\\w+(\\t\\d+){2}(\\t[\\S]+)*";
	public static final Pattern pattern = Pattern.compile(PATTERN);

	// Mandatory BED fields
	private final String chrom;
	private final long chromStart;	// 0-based inclusive
	private final long chromEnd;	// 0-based exclusive

	// Optional BED fields
	private final String name;
	// TODO: more optional BED fields?

	public static class BedFileEntryBuilder {

		private final String chrom;
		private final long chromStart;
		private final long chromEnd;

		private String name = null;

		public BedFileEntryBuilder(String chrom, long chromStart, long chromEnd) {

			this.chrom = chrom;
			this.chromStart = chromStart;
			this.chromEnd = chromEnd;
		}

		public BedFileEntryBuilder name(String name) {
			this.name = name;
			return this;
		}

		public BedFileEntry build() {
			return new BedFileEntry(chrom, chromStart, chromEnd, name);
		}
	}

	private BedFileEntry(String chrom, long chromStart, long chromEnd, String name) {

		this.chrom = chrom;
		this.chromStart = chromStart;
		this.chromEnd = chromEnd;
		this.name = name;
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

	public String getName() {
		return name;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(chrom)
				.append(DELIMITER).append(chromStart)
				.append(DELIMITER).append(chromEnd);
		if (name != null) {
			sb.append(DELIMITER).append(name);
		}

		return sb.toString();
	}
}
