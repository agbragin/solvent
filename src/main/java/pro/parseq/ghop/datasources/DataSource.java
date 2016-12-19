package pro.parseq.ghop.datasources;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.Filters;
import pro.parseq.ghop.utils.GenomicCoordinate;

public abstract class DataSource {

	public abstract Track track();
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
