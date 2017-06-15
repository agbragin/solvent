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
package pro.parseq.solvent.datasources.filters;

import java.util.Collection;

import pro.parseq.solvent.datasources.attributes.Attribute;

public class AttributeFilter<T extends Comparable<T>> {

	private final long id;
	private final Attribute<T> attribute;
	private final FilterOperator operator;
	private final Collection<T> values;
	private final boolean includeNulls;

	public AttributeFilter(long id, Attribute<T> attribute,
			FilterOperator operator, Collection<T> values, boolean includeNulls) {

		this.id = id;
		this.attribute = attribute;
		this.operator = operator;
		this.values = values;
		this.includeNulls = includeNulls;
	}

	public long getId() {
		return id;
	}

	public Attribute<T> getAttribute() {
		return attribute;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public Collection<T> getValues() {
		return values;
	}

	public boolean isIncludeNulls() {
		return includeNulls;
	}
}
