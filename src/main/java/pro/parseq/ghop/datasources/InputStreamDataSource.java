package pro.parseq.ghop.datasources;

import java.io.InputStream;

import pro.parseq.ghop.entities.Track;

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
