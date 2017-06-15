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
package pro.parseq.solvent.exceptions;

import pro.parseq.solvent.datasources.DataSourceType;

public class TrackCreationException extends RuntimeException {

	private static final long serialVersionUID = -7245999806004044834L;

	private static final String MESSAGE_TEMPLATE = "Exception while %s[%s] track creation: %s";

	private final RuntimeException exception;
	private final String trackName;
	private final DataSourceType type;

	public TrackCreationException(RuntimeException exception, String trackName, DataSourceType type) {

		super(String.format(MESSAGE_TEMPLATE, trackName, type, exception.getMessage()));

		this.exception = exception;
		this.trackName = trackName;
		this.type = type;
	}

	public RuntimeException getException() {
		return exception;
	}

	public String getTrackName() {
		return trackName;
	}

	public DataSourceType getType() {
		return type;
	}
}
