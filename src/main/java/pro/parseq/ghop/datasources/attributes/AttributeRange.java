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
package pro.parseq.ghop.datasources.attributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AttributeRange<T extends Comparable<T>> {

	private final T lowerBound;
	private final T upperBound;

	private final InclusionType inclusionType;

	private final List<T> values;

	public AttributeRange(T lowerBound, T upperBound, InclusionType inclusionType) {

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.inclusionType = inclusionType;
		this.values = null;
	}

	public AttributeRange(T[] values) {
		this(Arrays.stream(values)
				.collect(Collectors.toList()));
	}

	/**
	 * Create attribute range for a list of values.
	 * 
	 * Since values are comparable lower and upper bound can be naturally defined.
	 * 
	 * @param values
	 */
	public AttributeRange(List<T> values) {

		Collections.sort(values);

		this.lowerBound = values.get(0);
		this.upperBound = values.get(values.size() - 1);
		this.inclusionType = InclusionType.CLOSED;
		this.values = values;
	}

	public T getLowerBound() {
		return lowerBound;
	}

	public T getUpperBound() {
		return upperBound;
	}

	public InclusionType getInclusionType() {
		return inclusionType;
	}

	public List<T> getValues() {
		return values;
	}
}
