package pro.parseq.ghop.data.utils;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.UnknownContigException;
import pro.parseq.ghop.data.service.Contig;
import pro.parseq.ghop.data.service.ReferenceGenome;
import pro.parseq.ghop.data.service.ReferenceService;

@Component
public class CoordinateComparator implements Comparator<GenomicCoordinate> {

	@Autowired
	private ReferenceService referenceService;

	@Override
	public int compare(GenomicCoordinate o1, GenomicCoordinate o2) {

		ReferenceGenome o1Reference = new ReferenceGenome(o1.getReferenceGenome());
		ReferenceGenome o2Reference = new ReferenceGenome(o2.getReferenceGenome());
		Contig o1Contig = new Contig(o1.getContig(), 0);
		Contig o2Contig = new Contig(o2.getContig(), 0);
		List<Contig> o1ReferenceContigs = referenceService.getContigs(o1Reference);
		List<Contig> o2ReferenceContigs = referenceService.getContigs(o2Reference);
		if (!o1ReferenceContigs.contains(o1Contig)) {
			throw new UnknownContigException(o1.getReferenceGenome(), o1.getContig());
		}
		if (!o2ReferenceContigs.contains(o2Contig)) {
			throw new UnknownContigException(o2.getReferenceGenome(), o2.getContig());
		}

		if (!o1.getReferenceGenome().equals(o2.getReferenceGenome())) {
			return stringComparisonNormalizer(o1.getReferenceGenome().compareTo(o2.getReferenceGenome()));
		}
		if (!o1.getContig().equals(o2.getContig())) {

			Integer idx1 = o1ReferenceContigs.indexOf(o1Contig);
			Integer idx2 = o2ReferenceContigs.indexOf(o2Contig);

			return idx1.compareTo(idx2);
		}

		if (o1.getCoord() == o2.getCoord()) {
			return 0;
		}
		if (o1.getCoord() < o2.getCoord()) {
			return -1;
		} else {
			return 1;
		}
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
