package pro.parseq.ghop.data.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pro.parseq.ghop.data.UnknownReferenceGenomeException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferenceWebServiceTest {

	private static final String HG = "GRCh37.p13";
	private static final String TG = "TestReference";
	private static final String UG = "Unknown";
	private static final ReferenceGenome hg = new ReferenceGenome(HG);
	private static final ReferenceGenome tg = new ReferenceGenome(TG);
	private static final ReferenceGenome ug = new ReferenceGenome(UG);

	private static final Contig somebigcontig = new Contig("somebigcontig", 12);

	private static final Contig chr1 = new Contig("chr1", 0);
	private static final Contig chr5 = new Contig("chr5", 0);
	private static final Contig chr10 = new Contig("chr10", 0);
	private static final Contig chrM = new Contig("chrM", 0);

	@Autowired
	private ReferenceWebService referenceWebService;

	@Test
	public void testReferences() throws Exception {

		Set<ReferenceGenome> referenceGenomes = referenceWebService.getReferenceGenomes();

		assertThat(referenceGenomes).contains(hg, tg).doesNotContain(ug);
	}

	@Test
	public void testTestReference() throws Exception {

		List<Contig> contigs = referenceWebService.getContigs(tg);

		assertThat(contigs).contains(somebigcontig).size().isEqualTo(1);
	}

	@Test
	public void testUnknownReference() throws Exception {

		try {
			referenceWebService.getContigs(ug);
			fail("Should throw UnknownReferenceGenomeException");
		} catch (UnknownReferenceGenomeException e) {}
	}

	@Test
	public void testHumanReferenceGenome() throws Exception {

		List<Contig> contigs = referenceWebService.getContigs(hg);

		assertThat(contigs).contains(chr1, atIndex(0))
				.contains(chr5, atIndex(4))
				.contains(chr10, atIndex(9))
				.contains(chrM, atIndex(24));
	}
}
