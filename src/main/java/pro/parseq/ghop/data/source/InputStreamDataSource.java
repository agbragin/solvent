package pro.parseq.ghop.data.source;

import java.io.InputStream;

import pro.parseq.ghop.data.Track;

public abstract class InputStreamDataSource extends DataSource {

	private final Track track;

	protected final InputStream is;

	public InputStreamDataSource(Track track, InputStream is) {
		this.track = track;
		this.is = is;
	}

	@Override
	public Track track() {
		return track;
	}
}
