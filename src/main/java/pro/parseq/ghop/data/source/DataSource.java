package pro.parseq.ghop.data.source;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.Filters;
import pro.parseq.ghop.data.GenomicCoordinate;

public abstract class DataSource {

	public abstract String layer();
	public abstract List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord, Filters filters);
	public abstract List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord, Filters filters);
	public abstract Set<Band> borderGenerants(GenomicCoordinate coord);
	public abstract Set<Band> coverage(GenomicCoordinate coord, Filters filters);

	public Set<Band> leftBordersGenerants(int count, GenomicCoordinate coord, Filters filters) {

		Set<Band> generants = new HashSet<>();
		for (GenomicCoordinate leftBorder: leftBorders(count, coord, filters)) {
			generants.addAll(borderGenerants(leftBorder));
		}

		return generants;
	}

	public Set<Band> rightBordersGenerants(int count, GenomicCoordinate coord, Filters filters) {

		Set<Band> generants = new HashSet<>();
		for (GenomicCoordinate rightBorder: rightBorders(count, coord, filters)) {
			generants.addAll(borderGenerants(rightBorder));
		}

		return generants;
	}
}
