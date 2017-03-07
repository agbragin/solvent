package pro.parseq.ghop.utils;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import pro.parseq.ghop.services.ReferenceService;

@Component
public class GenomicCoordinateComparatorFactoryImpl implements GenomicCoordinateComparatorFactory {

	@Override
	public Comparator<GenomicCoordinate> newComparator(ReferenceService referenceService) {
		return new GenomicCoordinateComparator(referenceService);
	}
}
