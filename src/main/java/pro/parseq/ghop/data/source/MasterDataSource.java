package pro.parseq.ghop.data.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.Query;

@Component
public class MasterDataSource {

	private Map<String, DataSource> dataSources = new HashMap<>();

	/**
	 * This is straightforward dummy implementation
	 * 
	 * TODO: do it in a more optimized way
	 */
	public Set<Band> getBands(Query query) {

		Set<Band> bands = new HashSet<>();
		Set<GenomicCoordinate> coords = new HashSet<>();
		for (String layer: query.getLayerSettings().getLayers()) {

			// Retrieve layer's coordinate coverage
			Set<Band> layerCoverage = dataSources.get(layer).coverage(
					query.getCoord(), query.getLayerSettings().getLayerFilters(layer));
			layerCoverage.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
			// Retrieve layer's next bands' coordinates
			Set<Band> layerNextBands = dataSources.get(layer).rightBordersGenerants(
					query.getRight(), query.getCoord(),
					query.getLayerSettings().getLayerFilters(layer));
			layerNextBands.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
			// Retrieve layer's previous bands' coordinates
			Set<Band> layerPrevBands = dataSources.get(layer).leftBordersGenerants(
					query.getLeft(), query.getCoord(),
					query.getLayerSettings().getLayerFilters(layer));
			layerPrevBands.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});

			bands.addAll(layerCoverage);
			bands.addAll(layerNextBands);
			bands.addAll(layerPrevBands);
		}

		List<GenomicCoordinate> sortedCoords = new ArrayList<>(coords);
		Collections.sort(sortedCoords, new GenomicCoordinate.CoordinateComparator());
		int vOri = Collections.binarySearch(sortedCoords, query.getCoord(),
				new GenomicCoordinate.CoordinateComparator());
		if (vOri < 0) {
			vOri = -(vOri + 1);
		}
		Set<Band> output = new HashSet<>();
		for (int i = vOri; i < sortedCoords.size(); ++i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(band -> band.getStartCoord().equals(sortedCoords.get(idx))
							|| band.getEndCoord().equals(sortedCoords.get(idx)))
					.collect(Collectors.toSet()));
		}
		for (int i = vOri - 1; i > -1; --i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(band -> band.getStartCoord().equals(sortedCoords.get(idx))
							|| band.getEndCoord().equals(sortedCoords.get(idx)))
					.collect(Collectors.toSet()));
		}

		return output;
	}

	public DataSource addDataSource(DataSource dataSource) {
		return dataSources.put(dataSource.layer(), dataSource);
	}

	public Set<String> getLayers() {
		return dataSources.keySet();
	}
}
