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
package pro.parseq.ghop.exceptions;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.FilterOperator;

public class UnsupportedAttributeFilterOperatorException extends RuntimeException {

	private static final long serialVersionUID = 8373445986263311854L;

	private final Attribute<?> attribute;
	private final FilterOperator operator;

	public UnsupportedAttributeFilterOperatorException(Attribute<?> attribute, FilterOperator operator) {

		super(String.format("Attribute '%s' does not support filter operator: %s, only: %s",
				attribute.getName(), operator, attribute.operators()));

		this.attribute = attribute;
		this.operator = operator;
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}

	public FilterOperator getOperator() {
		return operator;
	}
}
