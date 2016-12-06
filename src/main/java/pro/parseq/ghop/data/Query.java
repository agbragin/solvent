package pro.parseq.ghop.data;

import java.util.Set;

public class Query {

	private final GenomicCoordinate coord;
	private final int left;
	private final int right;
	private final LayerSettings layerSettings;

	public Query(GenomicCoordinate coord, int left, int right, Set<String> layers) {

		this.coord = coord;
		this.left = left;
		this.right = right;

		layerSettings = new LayerSettings();
		layers.stream().forEach(layer -> layerSettings.addLayer(layer));
	}

	public Query(GenomicCoordinate coord, int left, int right, LayerSettings layerSettings) {

		this.coord = coord;
		this.left = left;
		this.right = right;
		this.layerSettings = layerSettings;
	}

	public LayerSettings getLayerSettings() {
		return layerSettings;
	}

	public GenomicCoordinate getCoord() {
		return coord;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}
}
