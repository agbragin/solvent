package pro.parseq.ghop.datasources;

import java.io.InputStream;

import pro.parseq.ghop.entities.Track;

/**
 * {@link DataSource} represented by {@link InputStream}
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
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
