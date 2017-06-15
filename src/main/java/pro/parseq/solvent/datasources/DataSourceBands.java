package pro.parseq.solvent.datasources;

import java.util.Set;

import pro.parseq.solvent.entities.Band;

public class DataSourceBands {

	private final Set<? extends Band> bands;
	private final boolean leftmost;
	private final boolean rightmost;

	public DataSourceBands(Set<? extends Band> bands, boolean lefmost, boolean rightmost) {

		this.bands = bands;
		this.leftmost = lefmost;
		this.rightmost = rightmost;
	}

	public Set<? extends Band> getBands() {
		return bands;
	}

	public boolean isLeftmost() {
		return leftmost;
	}

	public boolean isRightmost() {
		return rightmost;
	}
}
