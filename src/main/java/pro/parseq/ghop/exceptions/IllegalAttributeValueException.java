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

public class IllegalAttributeValueException extends RuntimeException {

	private static final long serialVersionUID = 5342888998701253034L;

	private final Attribute<?> attribute;
	private final String value;

	public IllegalAttributeValueException(Attribute<?> attribute, String value) {

		super(String.format("Attribute '%s' is of type: %s; can't parse value: %s",
				attribute.getName(), attribute.getType(), value));

		this.attribute = attribute;
		this.value = value;
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}
}
