package pro.parseq.ghop.data.source;

import java.io.InputStream;

public abstract class InputStreamDataSource extends DataSource {

	private final String layer;

	protected final InputStream is;

	public InputStreamDataSource(String layer, InputStream is) {
		this.layer = layer;
		this.is = is;
	}

	@Override
	public String layer() {
		return layer;
	}
}
