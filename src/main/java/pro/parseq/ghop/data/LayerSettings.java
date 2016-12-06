package pro.parseq.ghop.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LayerSettings {

	private Map<String, Filters> layerFilters = new HashMap<>();

	// TODO: add layer correlations

	public LayerSettings() {}

	public Set<String> getLayers() {
		return layerFilters.keySet();
	}

	public Filters getLayerFilters(String layer) {
		return layerFilters.get(layer);
	}

	public Filters setLayerFilters(String layer, Filters filters) {
		return layerFilters.put(layer, filters);
	}

	public Filters addLayer(String layer) {
		return setLayerFilters(layer, null);
	}

	public Filters removeLayer(String layer) {
		return layerFilters.remove(layer);
	}

	public Filters clearLayerFilters(String layer) {
		return setLayerFilters(layer, null);
	}
}
