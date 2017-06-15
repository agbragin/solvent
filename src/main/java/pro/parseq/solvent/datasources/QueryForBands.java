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

import pro.parseq.solvent.utils.GenomicCoordinate;

public class QueryForBands {

	// Bearing genomic coordinate
	private final GenomicCoordinate coord;
	// Left borders count (positive, 0-based)
	private final int left;
	// Right borders count (positive, 0-based)
	private final int right;
	// Target data sources to retrieve bands from
	private final Set<DataSource<?>> dataSources;

	// TODO: add tracks correlation

	public QueryForBands(GenomicCoordinate coord, int left, int right, Set<DataSource<?>> dataSources) {

		this.coord = coord;
		this.left = left;
		this.right = right;
		this.dataSources = dataSources;
	}

	public GenomicCoordinate getCoord() {
		return coord;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public Set<DataSource<?>> getDataSources() {
		return dataSources;
	}
}
