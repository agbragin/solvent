package pro.parseq.ghop.data.source.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

public class BedReaderTest {

	private static final String VALID_WO_HEADER = "/valid_wo_header.bed";
	private static final String VALID_SINGLELINE_HEADER = "/valid_singleline_header.bed";
	private static final String VALID_MULTILINE_HEADER = "/valid_multiline_header.bed";
	private static final String INVALID_DATALINE_FORMAT = "/invalid_dataline_format.bed";
	private static final String INVALID_HEADER_PLACEMENT = "/invalid_header_placement.bed";

	private InputStream validWoHeader = getClass().getResourceAsStream(VALID_WO_HEADER);
	private InputStream validSinglelineHeader = getClass().getResourceAsStream(VALID_SINGLELINE_HEADER);
	private InputStream validMultilineHeader = getClass().getResourceAsStream(VALID_MULTILINE_HEADER);
	private InputStream invalidDatalineFormat = getClass().getResourceAsStream(INVALID_DATALINE_FORMAT);
	private InputStream invalidHeaderPlacement = getClass().getResourceAsStream(INVALID_HEADER_PLACEMENT);

	private BedReader bedReader;

	@Test
	public void testValidWithoutHeader() throws Exception {

		bedReader = new BedReader(validWoHeader);
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNull();
		assertThat(bedReader.isEndOfFile());
	}

	@Test
	public void testValidSinglelineHeader() throws Exception {

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

		bedReader = new BedReader(invalidDatalineFormat);
		assertThat(bedReader.currentIsHeaderLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(!bedReader.currentIsDataLine());
		try {
			bedReader.parseCurrent();
			fail("Expecting exception while parsing malformed dataline");
		} catch (RuntimeException e) {}
	}

	@Test
	public void testInvalidHeaderPlacement() throws Exception {

		bedReader = new BedReader(invalidHeaderPlacement);
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsDataLine());
		assertThat(bedReader.next()).isNotEmpty();
		assertThat(bedReader.currentIsHeaderLine());
		try {
			bedReader.parseCurrent();
			fail("Expecting exception while encountering header after datalines");
		} catch (RuntimeException e) {}
	}
}
