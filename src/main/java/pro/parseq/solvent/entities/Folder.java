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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Folder {

	/**
	 * Folder's absolute path
	 */
	private final String path;

	/**
	 * Absolute paths of files contained in the folder
	 */
	private final List<String> files;

	/**
	 * Folder names contained in the folder
	 */
	private final List<String> folders;

	/**
	 * Most common instantiation scenario based on the {@link File},
	 * so this field distinguishes physical quality of it
	 * (whether it represented by file or folder on the disk)
	 */
	private final boolean isFile;

	public Folder(String path, List<String> files, List<String> folders, boolean isFile) {

		this.path = path;
		this.files = files;
		this.folders = folders;
		this.isFile = isFile;
	}

	public Folder(String path, List<String> files, List<String> folders) {
		this(path, files, folders, false);
	}

	public Folder(File file) {

		this.path = file.getAbsolutePath();
		this.files = new ArrayList<>();
		this.folders = new ArrayList<>();
		this.isFile = true;
	}

	public String getPath() {
		return path;
	}

	public List<String> getFiles() {
		return files;
	}

	public List<String> getFolders() {
		return folders;
	}

	public boolean isFile() {
		return isFile;
	}

	public boolean addFolder(String folderName) {
		return this.folders.add(folderName);
	}
}
