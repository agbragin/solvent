package pro.parseq.ghop.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferenceServiceTest {

	private static final String TEST_REFERENCE = "TestReference";
	private static final String UNKNOWN_REFERENCE = "TotallyUnknown";
	private static final String SOMEBIGCONTIG = "somebigcontig";

	@Autowired
	private ReferenceService referenceService;

	@Test
	public void testAvailableReferenceGenomes() throws Exception {
		assertThat(referenceService.getReferenceGenomes()).contains(new ReferenceGenome(TEST_REFERENCE));
	}

	@Test
	public void testAvailableTestReferenceGenomeContigs() throws Exception {
		assertThat(referenceService.getContigs(TEST_REFERENCE)).containsExactly(new Contig(TEST_REFERENCE, SOMEBIGCONTIG));
	}

	@Test
	public void testAvailableUnknownReferenceGenomeContigs() throws Exception {

		try {
			referenceService.getContigs(UNKNOWN_REFERENCE);
			fail("Requesting contig list for unknown reference genome should cause an exception!");
		} catch (ReferenceGenomeNotFoundException e) {}
	}
}
