package pro.parseq.ghop.utils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ContigNotFoundException;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.services.ReferenceService;

@Component
public class GenomicCoordinateComparator implements Comparator<GenomicCoordinate> {

	@Autowired
	private ReferenceService referenceService;

	@Override
	public int compare(GenomicCoordinate o1, GenomicCoordinate o2) {

		int referenceGenomesComparisonResult = referenceGenomesComparison(
				o1.getContig().getReferenceGenome().getId(),
				o2.getContig().getReferenceGenome().getId());
		if (referenceGenomesComparisonResult != 0) {
			return referenceGenomesComparisonResult;
		}
		int contigsComparisonResult = contigsComparison(
				o1.getContig().getId(), o2.getContig().getId(),
				o1.getContig().getReferenceGenome().getId());
		if (contigsComparisonResult != 0) {
			return contigsComparisonResult;
		}

		return coordinatesComparison(o1.getCoord(), o2.getCoord());
	}

	private int coordinatesComparison(long c1, long c2) {

		if (c1 == c2) {
			return 0;
		}
		if (c1 < c2) {
			return -1;
		} else {
			return 1;
		}
	}

	private int referenceGenomesComparison(String r1, String r2) {

		Set<ReferenceGenome> availableReferenceGenomes = referenceService.getReferenceGenomes();
		ReferenceGenome referenceGenome1 = new ReferenceGenome(r1);
		ReferenceGenome referenceGenome2 = new ReferenceGenome(r2);
		if (!availableReferenceGenomes.contains(referenceGenome1)) {
			throw new ReferenceGenomeNotFoundException(referenceGenome1);
		}
		if (!availableReferenceGenomes.contains(referenceGenome2)) {
			throw new ReferenceGenomeNotFoundException(referenceGenome2);
		}

		return stringComparisonNormalizer(r1.compareTo(r2));
	}

	private int contigsComparison(String c1, String c2, String r) {

		List<Contig> availableContigs = referenceService.getContigs(new ReferenceGenome(r));
		ReferenceGenome referenceGenome = new ReferenceGenome(r);
		Contig contig1 = new Contig(referenceGenome, c1, 0);
		Contig contig2 = new Contig(referenceGenome, c2, 0);
		if (!availableContigs.contains(contig1)) {
			throw new ContigNotFoundException(contig1);
		}
		if (!availableContigs.contains(contig2)) {
			throw new ContigNotFoundException(contig2);
		}

		Integer contig1Idx = availableContigs.indexOf(contig1);
		Integer contig2Idx = availableContigs.indexOf(contig2);

		return contig1Idx.compareTo(contig2Idx);
	}

	private static final int stringComparisonNormalizer(int comparisonResult) {

		if (comparisonResult < 0) {
			return -1;
		}
		if (comparisonResult > 0) {
			return 1;
		}

		return 0;
	}
}
