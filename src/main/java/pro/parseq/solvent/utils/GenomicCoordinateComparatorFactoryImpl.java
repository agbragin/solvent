package pro.parseq.solvent.utils;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import pro.parseq.solvent.services.ReferenceService;

@Component
public class GenomicCoordinateComparatorFactoryImpl implements GenomicCoordinateComparatorFactory {

	@Override
	public Comparator<GenomicCoordinate> newComparator(ReferenceService referenceService) {
		return new GenomicCoordinateComparator(referenceService);
	}
}
