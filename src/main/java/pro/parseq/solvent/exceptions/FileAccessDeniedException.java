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

import java.io.File;
import java.nio.file.AccessMode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Access denied")
public class FileAccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = -2838495267107651435L;

	private final File file;
	private final AccessMode mode;

	public FileAccessDeniedException(File file, AccessMode mode) {

		super(String.format("Access denied exception: can not %s the file: %s", mode, file.getAbsolutePath()));

		this.file = file;
		this.mode = mode;
	}

	public File getFile() {
		return file;
	}

	public AccessMode getMode() {
		return mode;
	}
}
