package pro.parseq.ghop.rest;

public class TrackNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4629829984465669002L;

	private final String track;

	public TrackNotFoundException(String track) {
		super(String.format("Track of name '%s' is not found. Did you add this track?", track));
		this.track = track;
	}

	public String getTrack() {
		return track;
	}
}
