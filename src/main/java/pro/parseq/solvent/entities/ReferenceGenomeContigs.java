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

import java.util.List;

/**
 * This is a service class to proper API JSON response serialization 
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class ReferenceGenomeContigs {

	// This would serialize a list of strings into 'contigs' array instead of default 'stringList'
	private final List<String> contigs;

	public ReferenceGenomeContigs(List<String> contigs) {
		this.contigs = contigs;
	}

	public List<String> getContigs() {
		return contigs;
	}
}
