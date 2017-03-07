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
package pro.parseq.ghop.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contig {

	/**
	 * Can't use here something like this: @JsonUnwrapped(prefix = "referenceGenome")
	 * due to: https://github.com/FasterXML/jackson-databind/issues/1467
	 * 
	 * TODO: change if it would be fixed
	 */
	private ReferenceGenome referenceGenome;

	private String id;
	private Long length;

	protected Contig() {}

	public Contig(ReferenceGenome referenceGenome, String id) {
		this.referenceGenome = referenceGenome;
		this.id = id;
	}

	public Contig(ReferenceGenome referenceGenome, String id, long length) {

		this.referenceGenome = referenceGenome;
		this.id = id;
		this.length = length;
	}

	@JsonCreator
	public Contig(@JsonProperty("referenceGenomeId") String referenceGenomeId,
			@JsonProperty("id") String id, @JsonProperty("length") long length) {

		referenceGenome = new ReferenceGenome(referenceGenomeId);
		this.id = id;
		this.length = length;
	}

	public Contig(String referenceGenomeId, String id) {
		referenceGenome = new ReferenceGenome(referenceGenomeId);
		this.id = id;
	}

	public ReferenceGenome getReferenceGenome() {
		return referenceGenome;
	}

	public void setReferenceGenome(ReferenceGenome referenceGenome) {
		this.referenceGenome = referenceGenome;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Contig)) {
			return false;
		}

		return referenceGenome.equals(((Contig) obj).referenceGenome)
				&& id.equals(((Contig) obj).id);
	}

	@Override
	public String toString() {

		return new StringBuilder()
				.append(referenceGenome)
				.append(":")
				.append(id)
				.toString();
	}
}
