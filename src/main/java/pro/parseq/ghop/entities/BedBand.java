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

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.IdGenerationUtils;
import pro.parseq.ghop.utils.PropertiesAware;

@Relation(collectionRelation = "bands")
public class BedBand implements Band, PropertiesAware {

	// Object's identifier (should be unique across all data sources)
	transient private long id;

	@JsonUnwrapped
	private final Track track;

	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;

	private String name;
	private JsonNode properties = JsonNodeFactory.instance.objectNode();

	public BedBand(Track track,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			String name, JsonNode properties) {

		this.id = IdGenerationUtils.generateBandId();
		this.track = track;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.name = name;
		this.properties = properties;
	}

	public Track getTrack() {
		return track;
	}

	public GenomicCoordinate getStartCoord() {
		return startCoord;
	}

	public GenomicCoordinate getEndCoord() {
		return endCoord;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public JsonNode getProperties() {
		return properties;
	}

	public void setProperties(JsonNode properties) {
		this.properties = properties;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Band)) {
			return false;
		}

		return id == ((BedBand) obj).id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public String toString() {

		return new StringBuilder()
				.append(track)
				.append("[")
				.append(startCoord)
				.append(";")
				.append(endCoord)
				.append(")")
				.toString();
	}
}
