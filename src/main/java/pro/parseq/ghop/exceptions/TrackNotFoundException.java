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

import pro.parseq.ghop.entities.Track;

public class TrackNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4739675986100017789L;

	private final Track track;

	public TrackNotFoundException(Track track) {

		super(String.format("Track of name '%s' is not found. "
				+ "Did you add this track?", track));

		this.track = track;
	}

	public Track getTrack() {
		return track;
	}
}
