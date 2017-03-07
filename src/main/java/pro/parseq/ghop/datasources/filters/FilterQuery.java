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
package pro.parseq.ghop.datasources.filters;

import java.util.List;

import pro.parseq.ghop.entities.AttributeFilterAggregateEntity;

public class FilterQuery {

	private final List<AttributeFilter<?>> filters;
	private final List<AttributeFilterAggregateEntity> aggregates;

	public FilterQuery(List<AttributeFilter<?>> filters,
			List<AttributeFilterAggregateEntity> aggregates) {

		this.filters = filters;
		this.aggregates = aggregates;
	}

	public List<AttributeFilter<?>> getFilters() {
		return filters;
	}

	public List<AttributeFilterAggregateEntity> getAggregates() {
		return aggregates;
	}
}
