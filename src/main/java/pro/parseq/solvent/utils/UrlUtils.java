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
package pro.parseq.solvent.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import pro.parseq.solvent.exceptions.UrlEncodingException;

public class UrlUtils {

	public static final String DEFAULT_ENC = "UTF-8";

	public static final String encode(String url, String enc) {

		try {
			return URLEncoder.encode(url, enc);
		} catch (UnsupportedEncodingException e) {
			throw new UrlEncodingException(e, url, enc);
		}
	}

	public static final String encode(String url) {
		return encode(url, DEFAULT_ENC);
	}

	public static final String decode(String url, String enc) {

		try {
			return URLDecoder.decode(url, enc);
		} catch (UnsupportedEncodingException e) {
			throw new UrlEncodingException(e, url, enc);
		}
	}

	public static final String decode(String url) {
		return decode(url, DEFAULT_ENC);
	}
}
