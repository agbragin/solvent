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
package pro.parseq.solvent.entities;

import java.util.Collection;

import pro.parseq.solvent.datasources.filters.AggregateOperator;

public class AttributeFilterAggregateEntity {

	private long id;
	private Collection<Long> filters;
	private AggregateOperator operator;

	protected AttributeFilterAggregateEntity() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Collection<Long> getFilters() {
		return filters;
	}

	public void setFilters(Collection<Long> filters) {
		this.filters = filters;
	}

	public AggregateOperator getOperator() {
		return operator;
	}

	public void setOperator(AggregateOperator operator) {
		this.operator = operator;
	}
}
