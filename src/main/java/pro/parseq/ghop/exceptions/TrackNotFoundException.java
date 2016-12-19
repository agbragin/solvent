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
