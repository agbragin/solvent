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

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.databind.JsonNode;

import pro.parseq.solvent.utils.GenomicCoordinate;

@Relation(collectionRelation = "bands")
public class BedBand extends AbstractPropertiesAwareBand {

	private static final long serialVersionUID = 1086744425254066136L;

	public BedBand(Track track, GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			String name, JsonNode properties) {

		super(track, startCoord, endCoord, name, properties);
	}
}
