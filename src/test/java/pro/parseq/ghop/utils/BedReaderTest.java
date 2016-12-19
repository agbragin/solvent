package pro.parseq.ghop.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import pro.parseq.ghop.exceptions.IllegalBedFileEntryException;

public class BedReaderTest {

	private BedReader bedReader;

	@Test
	public void testValidWithoutHeader() throws Exception {

		InputStream validWoHeader = getClass().getResourceAsStream("/valid_wo_header.bed");
		bedReader = new BedReader(validWoHeader);
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNull();
		assertThat(bedReader.isEndOfFile());
	}

	@Test
	public void testValidSinglelineHeader() throws Exception {

		InputStream validSinglelineHeader = getClass().getResourceAsStream("/valid_singleline_header.bed");
		bedReader = new BedReader(validSinglelineHeader);
		assertThat(bedReader.currentIsHeaderLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNull();
		assertThat(bedReader.isEndOfFile());
	}

	@Test
	public void testValidMultilineHeader() throws Exception {

		InputStream validMultilineHeader = getClass().getResourceAsStream("/valid_multiline_header.bed");
		bedReader = new BedReader(validMultilineHeader);
		assertThat(bedReader.currentIsHeaderLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsHeaderLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNull();
		assertThat(bedReader.isEndOfFile());
	}

	@Test
	public void testInvalidDatalineFormat() throws Exception {

		InputStream invalidDatalineFormat = getClass().getResourceAsStream("/invalid_dataline_format.bed");
		bedReader = new BedReader(invalidDatalineFormat);
		assertThat(bedReader.currentIsHeaderLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(!bedReader.currentIsDataLine());
		try {
			bedReader.parseCurrent();
			fail("Expecting exception while parsing malformed dataline");
		} catch (IllegalBedFileEntryException e) {}
	}

	@Test
	public void testInvalidHeaderPlacement() throws Exception {

		InputStream invalidHeaderPlacement = getClass().getResourceAsStream("/invalid_header_placement.bed");
		bedReader = new BedReader(invalidHeaderPlacement);
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsHeaderLine());
		try {
			bedReader.parseCurrent();
			fail("Expecting exception while encountering header after datalines");
		} catch (IllegalBedFileEntryException e) {}
	}
}
