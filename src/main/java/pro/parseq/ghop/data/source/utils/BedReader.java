package pro.parseq.ghop.data.source.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pro.parseq.ghop.data.source.utils.BedFileEntry.BedFileEntryBuilder;

public class BedReader {

	private BufferedReader bedReader;

	private String line;
	private boolean readingDataLines = false;

	public BedReader(InputStream bedFile) {

		bedReader = new BufferedReader(new InputStreamReader(bedFile));
		try {
			line = bedReader.readLine();
			if (currentIsDataLine()) {
				readingDataLines = true;
			}
		} catch (IOException e) {
			throw new RuntimeException(String
					.format("I/O exception while BED file reading: %s", e.getMessage()));
		}
	}

	public String currentLine() {
		return line;
	}

	public boolean currentIsHeaderLine() {
		return !currentIsDataLine();
	}

	public boolean currentIsDataLine() {
		return BedFileEntry.pattern.matcher(line).matches();
	}

	public boolean isEndOfFile() {
		return line == null;
	}

	public BedFileEntry parseCurrent() {

		if (line == null || !readingDataLines) {
			return null;
		}
		if (!BedFileEntry.pattern.matcher(line).matches()) {
			// TODO: do it in a more gentle way
			throw new RuntimeException(String.format("Malformed BED dataline: %s", line));
		}

		String[] fileds = line.split(BedFileEntry.DELIMITER);
		BedFileEntryBuilder entryBuilder = new BedFileEntryBuilder(fileds[0],
				Long.parseLong(fileds[1]), Long.parseLong(fileds[2]));
		if (fileds.length > 3) {
			entryBuilder.name(fileds[3]);
			// TODO: other optional fields
		}

		return entryBuilder.build();
	}

	public String next() {

		try {

			line = bedReader.readLine();
			if (line != null && currentIsDataLine()) {
				readingDataLines = true;
			}

			return line;
		} catch (IOException e) {
			throw new RuntimeException(String
					.format("I/O exception while BED file reading: %s", e.getMessage()));
		}
	}
}
