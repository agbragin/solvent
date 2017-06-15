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

import java.util.Set;

public class UnknownReferenceGenomeException extends RuntimeException {

	private static final long serialVersionUID = 4546260233000370811L;

	private final String referenceGenomeName;
	private final Set<String> availableReferenceGenomeNames;

	public UnknownReferenceGenomeException(String referenceGenomeName,
			Set<String> availableReferenceGenomeNames) {

		super(String.format("Unknown reference genome name '%s'; available are: %s",
				referenceGenomeName, availableReferenceGenomeNames));

		this.referenceGenomeName = referenceGenomeName;
		this.availableReferenceGenomeNames = availableReferenceGenomeNames;
	}

	public String getReferenceGenomeName() {
		return referenceGenomeName;
	}

	public Set<String> getAvailableReferenceGenomeNames() {
		return availableReferenceGenomeNames;
	}
}
