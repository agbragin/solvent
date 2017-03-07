package pro.parseq.ghop.utils;

import java.util.Comparator;

import pro.parseq.ghop.services.ReferenceService;

public interface GenomicCoordinateComparatorFactory {

	/**
	 * Returns a new instance of comparator for presented reference service
	 * 
	 * @param referenceService {@link ReferenceService}
	 * @return {@link GenomicCoordinate} {@link Comparator}
	 */
	Comparator<GenomicCoordinate> newComparator(ReferenceService referenceService);
}
