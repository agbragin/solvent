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

import java.util.Arrays;

import pro.parseq.ghop.datasources.DataSourceType;

public class IllegalDataSourceTypeException extends RuntimeException {

	private static final long serialVersionUID = -7716363815707968776L;

	private final String type;

	public IllegalDataSourceTypeException(String type) {

		super(String.format("Illegal data source type: %s; available are: %s",
				type, Arrays.asList(DataSourceType.values())));

		this.type = type;
	}

	public String getType() {
		return type;
	}
}
