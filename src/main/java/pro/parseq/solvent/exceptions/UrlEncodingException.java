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

import java.io.UnsupportedEncodingException;

public class UrlEncodingException extends IllegalArgumentException {

	private static final long serialVersionUID = 6403922954543982861L;

	private static final String MESSAGE_TEMPLATE = "Encoding exception with %s url using %s character enctription: %s";

	private final UnsupportedEncodingException exception;
	private final String url;
	private final String enc;

	public UrlEncodingException(UnsupportedEncodingException exception, String url, String enc) {

		super(String.format(MESSAGE_TEMPLATE, url, enc, exception.getMessage()));

		this.exception = exception;
		this.url = url;
		this.enc = enc;
	}

	public UnsupportedEncodingException getException() {
		return exception;
	}

	public String getUrl() {
		return url;
	}

	public String getEnc() {
		return enc;
	}

	@Override
	public String toString() {
		return String.format(MESSAGE_TEMPLATE, url, enc, exception.getMessage());
	}
}
