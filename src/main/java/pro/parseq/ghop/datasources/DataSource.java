package pro.parseq.ghop.datasources;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.Filters;
import pro.parseq.ghop.utils.GenomicCoordinate;

/**
 * Data source adapter class (all performance responsibilities lay on it's realization)
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public abstract class DataSource {

	/**
	 * @return {@link Track} data source belongs to
	 */
	public abstract Track track();

	/**
	 * Returns ascending ordered list of data source objects' borders lying to the left of the given coordinate
	 * 
	 * @param count Borders count to return
	 * @param coord Given genomic coordinate
	 * @param filters {@link Track} filters to define target objects to look borders of
	 * @return {@link GenomicCoordinate} object borders list lying to the left of the given for specified parameters
	 */
	public abstract List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord, Filters filters);

	/**
	 * Returns ascending ordered list of data source objects' borders lying to the right of the given coordinate
	 * 
	 * @param count Borders count to return
	 * @param coord Given genomic coordinate
	 * @param filters {@link Track} filters to define target objects to look borders of
	 * @return {@link GenomicCoordinate} object borders list lying to the right of the given for specified parameters
	 */
	public abstract List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord, Filters filters);

	/**
	 * Obtain data source objects "generating" the given border (i.e. data source objects whose one of the borders equals to the given coordinate)
	 * 
	 * @param coord Genomic coordinate representing the border
	 * @param filters {@link Track} filters to define target objects
	 * @return {@link Band} {@link Set} "generating" the given border
	 */
	public abstract Set<Band> borderGenerants(GenomicCoordinate coord, Filters filters);

	/**
	 * Obtain data source objects "covering" the given coordinate
	 * 
	 * @param coord Genomic coordinate to look "coverage" for
	 * @param filters {@link Track} filters to define target objects
	 * @return {@link Band} {@link Set} "covering" the given coordinate
	 */
	public abstract Set<Band> coverage(GenomicCoordinate coord, Filters filters);

	/**
	 * Obtain data source objects "generating" left borders
	 * 
	 * @param count Left borders count
	 * @param coord Given genomic coordinate
	 * @param filters {@link Track} filters to define target objects
	 * @return {@link Band} {@link Set} "generating" left borders (i.e. data source objects whose one of the borders equals to the given coordinate)
	 */
	public Set<Band> leftBordersGenerants(int count, GenomicCoordinate coord, Filters filters) {

		Set<Band> generants = new HashSet<>();
		for (GenomicCoordinate leftBorder: leftBorders(count, coord, filters)) {
			generants.addAll(borderGenerants(leftBorder, filters));
		}

		return generants;
	}

	/**
	 * Obtain data source objects "generating" right borders
	 * 
	 * @param count Right borders count
	 * @param coord Given genomic coordinate
	 * @param filters {@link Track} filters to define target objects
	 * @return {@link Band} {@link Set} "generating" right borders (i.e. data source objects whose one of the borders equals to the given coordinate)
	 */
	public Set<Band> rightBordersGenerants(int count, GenomicCoordinate coord, Filters filters) {

		Set<Band> generants = new HashSet<>();
		for (GenomicCoordinate rightBorder: rightBorders(count, coord, filters)) {
			generants.addAll(borderGenerants(rightBorder, filters));
		}

		return generants;
	}
}
