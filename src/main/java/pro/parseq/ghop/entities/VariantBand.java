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

import com.fasterxml.jackson.databind.JsonNode;

import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.PropertiesAware;

public class VariantBand implements Band, PropertiesAware {

	private final Track track;
	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;
	private final String name;
	private final JsonNode properties;

	public VariantBand(Track track, GenomicCoordinate startCoord, 
			GenomicCoordinate endCoord, String name, JsonNode properties) {

		this.track = track;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.name = name;
		this.properties = properties;
	}

	@Override
	public Track getTrack() {
		return track;
	}

	@Override
	public GenomicCoordinate getStartCoord() {
		return startCoord;
	}

	@Override
	public GenomicCoordinate getEndCoord() {
		return endCoord;
	}

	@Override
	public String getName() {
		return name;
	}

	public JsonNode getProperties() {
		return properties;
	}

	@Override
	public String toString() {

		return String.format("%s:%s-%s", this.getName(),
				this.getStartCoord(), this.getEndCoord());
	}
}
