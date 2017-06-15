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
package pro.parseq.solvent.datasources;

import java.util.Set;

import org.springframework.hateoas.Identifiable;

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.Band;
import pro.parseq.solvent.utils.GenomicCoordinate;

/**
 * Genomic bands source contract
 * 
 * @param <T> refers to band type
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 * @author Anton Bragin <a href="mailto:abragin@parseq.pro">abragin@parseq.pro</a>
 */
public interface DataSource<T extends Band> extends Identifiable<Long> {

	/**
	 * Returns data source's type
	 * 
	 * @return {@link DataSourceType}
	 */
	DataSourceType getType();

	/**
	 * Returns a data source with filter applied
	 * 
	 * @param query {@link FilterQuery} for target bands to build a data source over them
	 * @return {@link DataSourceImpl} over the filtered {@link Band}s
	 */
	DataSource<T> filter(FilterQuery query);

	/**
	 * Returns a set of available data source attributes to filter bands by
	 * 
	 * @return {@link Set} of available {@link Attribute}s to filter {@link Band}s by
	 */
	Set<Attribute<?>> attributes();

	/**
	 * <p>Retrieve bands for specified parameters</p>
	 * 
	 * <p>You can interpret retrieving logic as follows:</p>
	 * <ol>
	 *   <li>
	 *     Collect <i>coordinates</i> (i.e. bands' borders):
	 *     <ul>
	 *       <li>Define an order on the set of coordinates</li>
	 *       <li>Pick a specified coordinate</li>
	 *       <li>Pick <i>next</i> {@code left} coordinates left to it</li>
	 *       <li>Pick <i>next</i> {@code right} coordinates right to it</li>
	 *     </ul>
	 *   </li>
	 *   <li>Collect bands covering any of this coordinates or use any of them as a border (no matter inclusive or exclusive it is)</li>
	 * </ol>
	 * 
	 * @param coord Bearing {@link GenomicCoordinate}, which is <b>zero-based, half-open</b>
	 * @param left Not negative number of next coordinates (i.e. bands' borders) left to the bearing
	 * @param right Not negative number of next coordinates (i.e. bands' borders) right to the bearing
	 * @return {@link Set} of {@link Band}s "covering" or "generating" specified coordinates (borders)
	 */
	Set<T> getBands(GenomicCoordinate coord, int left, int right);
}
