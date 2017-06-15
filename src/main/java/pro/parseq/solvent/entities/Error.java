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

import java.util.Date;

/**
 * Represents error response entity body when exception raises
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class Error {

	// Occurrence timestamp
	private final Date timestamp;
	// Integer http status
	private final int status;
	// Error name or title
	private final String error;
	// Exception class
	private final Class<? extends Exception> exception;
	// Error message
	private final String message;
	// URL caused the exception
	private final String path;

	public Error(Date timestamp, int status, String error, Class<? extends Exception> exception, String message, String path) {

		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.exception = exception;
		this.message = message;
		this.path = path;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public Class<? extends Exception> getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}
}
