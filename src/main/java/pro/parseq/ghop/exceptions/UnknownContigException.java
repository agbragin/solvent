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

import java.util.List;

public class UnknownContigException extends RuntimeException {

	private static final long serialVersionUID = -2627070873847041797L;

	private final String referenceGenomeName;
	private final String contigName;
	private final List<String> availableContigNames;

	public UnknownContigException(String referenceGenomeName,
			String contigName, List<String> availableContigNames) {

		super(String.format("Unknown contig name '%s' for reference genome '%s'; available are: %s",
				contigName, referenceGenomeName, availableContigNames));

		this.referenceGenomeName = referenceGenomeName;
		this.contigName = contigName;
		this.availableContigNames = availableContigNames;
	}

	public String getReferenceGenomeName() {
		return referenceGenomeName;
	}

	public String getContigName() {
		return contigName;
	}

	public List<String> getAvailableContigNames() {
		return availableContigNames;
	}
}
