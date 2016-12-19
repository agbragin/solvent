package pro.parseq.ghop.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

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

	@Autowired
	private ReferenceService referenceService;

	@Test
	public void testReferenceGenomes() throws Exception {

		Set<ReferenceGenome> referenceGenomes = referenceService.getReferenceGenomes();

		ReferenceGenome testReference = new ReferenceGenome("TestReference");
		assertThat(referenceGenomes).contains(testReference);
	}

	@Test
	public void testReferenceGenomeContigs() throws Exception {

		ReferenceGenome testReference = new ReferenceGenome("TestReference");
		Contig somebigcontig = new Contig(testReference, "somebigcontig", 0);
		List<Contig> contigs = referenceService.getContigs(testReference);

		assertThat(contigs).containsExactly(somebigcontig);
	}

	@Test
	public void testUnknownReferenceGenomeContigs() throws Exception {

		ReferenceGenome unknownReference = new ReferenceGenome("unknown");
		try {
			referenceService.getContigs(unknownReference);
			fail("Should throw ReferenceGenomeNotFoundException on unknown genome");
		} catch (ReferenceGenomeNotFoundException e) {}
	}
}
