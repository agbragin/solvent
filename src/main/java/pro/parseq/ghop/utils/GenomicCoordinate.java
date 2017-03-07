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
package pro.parseq.ghop.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pro.parseq.ghop.entities.Contig;

/**
 * <p>Holds an information about genomic coordinate</p>
 * 
 * <p>{@code contig} field holds an information about reference genome and contig id</p>
 * <p>{@code coord} field holds an information about contig's coordinate (according to <b>zero-based, half-open</b> scheme)</p>
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class GenomicCoordinate {

	private final Contig contig;
	private final long coord;

	public GenomicCoordinate(Contig contig, long coord) {
		this.contig = contig;
		this.coord = coord;
	}

	@JsonCreator
	public GenomicCoordinate(@JsonProperty("genome") String referenceGenomeId,
			@JsonProperty("contig") String contigId, @JsonProperty("coord") long coord) {
		contig = new Contig(referenceGenomeId, contigId);
		this.coord = coord;
	}

	public Contig getContig() {
		return contig;
	}

	public long getCoord() {
		return coord;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GenomicCoordinate)) {
			return false;
		}

		return contig.equals(((GenomicCoordinate) obj).contig)
				&& coord == ((GenomicCoordinate) obj).coord;
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder(29, 2017)
				.append(contig)
				.append(coord)
				.toHashCode();
	}

	@Override
	public String toString() {

		return new StringBuilder()
				.append(contig)
				.append(":")
				.append(coord)
				.toString();
	}
}
