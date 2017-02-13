/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package pro.parseq.ghop.datasources;

import java.util.List;
import java.util.Set;

import javax.sound.midi.Track;

import org.springframework.hateoas.Identifiable;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.utils.GenomicCoordinate;

/**
 * Source of genomic bands and their coordinates.  
 * 
 * @author aafanasyev, abragin
 *
 */
public interface DataSource<T extends Band> extends Identifiable<Long> {

	/**
	 * Returns data source's type
	 * 
	 * @return {@link DataSourceType}
	 */
	DataSourceType getType();

	/**
	 * Returns a data source with filter applied.
	 * 
	 * @param query {@link FilterQuery} for target bands to build a data source over them
	 * @return {@link DataSourceImpl} over the filtered {@link Band}s
	 */
	DataSource<T> filter(FilterQuery query);

	/**
	 * Returns a set of available data source attributes to filter bands by.
	 * 
	 * @return {@link Set} of available {@link Attribute}s to filter {@link Band}s by
	 */
	Set<Attribute<?>> attributes();

	/**
	 * Returns list of data source object borders to the left of the given coordinate in ascending order.
	 * 
	 * The original coordinate is not included into the count.
	 * 
	 * @param count Borders count to return
	 * @param coord Given genomic coordinate
	 * @return {@link GenomicCoordinate} object borders list lying to the left of the given for specified parameters
	 */
	List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord);

	/**
	 * Returns list of data source objects borders lying to the right of the given coordinate in ascending order. 
	 * 
	 * The original coordinate is not included into the count.
	 * 
	 * @param count Borders count to return
	 * @param coord Given genomic coordinate
	 * @return {@link GenomicCoordinate} object borders list lying to the right of the given for specified parameters
	 */
	List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord);

	/**
	 * Obtain data source objects generating the border provided.
	 * 
	 *  Include i.e. data source objects whose one of the borders equals to the given coordinate.
	 * 
	 * @param coord Genomic coordinate representing the border
	 * @param filters {@link Track} filters to define target objects
	 * @return {@link Band} {@link Set} "generating" the given border
	 */
	Set<T> borderGenerants(GenomicCoordinate coord);

	/**
	 * Obtain data source objects intersecting the given coordinate.
	 * 
	 * @param coord Genomic coordinate to look "coverage" for
	 * @return {@link Band} {@link Set} "covering" the given coordinate
	 */
	Set<T> coverage(GenomicCoordinate coord);

	/**
	 * Obtain data source objects generating left borders.
	 * 
	 * Bands forming the original coordinate are included.
	 * 
	 * @param count Count of left borders to retrieve generating bands to
	 * @param coord Genomic coordinate
	 * @return {@link Band} {@link Set} "generating" left borders (i.e. data source objects whose one of the borders equals to the given coordinate)
	 */
	Set<T> leftBordersGenerants(int borderCount, GenomicCoordinate coord);

	/**
	 * Obtain data source objects generating right borders.
	 * 
	 * Bands forming the original coordinate are included.
	 * 
	 * @param count Right borders count
	 * @param coord Given genomic coordinate
	 * @return {@link Band} {@link Set} "generating" right borders (i.e. data source objects whose one of the borders equals to the given coordinate)
	 */
	Set<T> rightBordersGenerants(int borderCount, GenomicCoordinate coord);

}
