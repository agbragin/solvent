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
import pro.parseq.ghop.data.Track;

@Component
public class MasterDataSource {

	private Map<Track, DataSource> dataSources = new HashMap<>();

	/**
	 * This is straightforward dummy implementation
	 * 
	 * TODO: do it in a more optimized way
	 */
	public Set<Band> getBands(Query query) {

		Set<Band> bands = new HashSet<>();
		Set<Band> coverage = new HashSet<>();
		Set<GenomicCoordinate> coords = new HashSet<>();
		for (Track track: query.getTrackSettings().getTracks()) {

			// Retrieve track's coordinate coverage
			Set<Band> trackCoverage = dataSources.get(track).coverage(
					query.getCoord(), query.getTrackSettings().getTrackFilters(track));
			trackCoverage.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
			coverage.addAll(trackCoverage);
			// Retrieve track's next bands' coordinates
			Set<Band> trackNextBands = dataSources.get(track).rightBordersGenerants(
					query.getRight(), query.getCoord(),
					query.getTrackSettings().getTrackFilters(track));
			trackNextBands.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
			// Retrieve track's previous bands' coordinates
			Set<Band> trackPrevBands = dataSources.get(track).leftBordersGenerants(
					query.getLeft(), query.getCoord(),
					query.getTrackSettings().getTrackFilters(track));
			trackPrevBands.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});

			bands.addAll(trackCoverage);
			bands.addAll(trackNextBands);
			bands.addAll(trackPrevBands);
		}

		List<GenomicCoordinate> sortedCoords = new ArrayList<>(coords);
		Collections.sort(sortedCoords, new GenomicCoordinate.CoordinateComparator());
		int vOri = Collections.binarySearch(sortedCoords, query.getCoord(),
				new GenomicCoordinate.CoordinateComparator());
		if (vOri < 0) {
			vOri = -(vOri + 1);
		}
		Set<Band> output = new HashSet<>(coverage);
		for (int i = vOri; i < sortedCoords.size() && (i - vOri) < query.getRight(); ++i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(band -> band.getStartCoord().equals(sortedCoords.get(idx))
							|| band.getEndCoord().equals(sortedCoords.get(idx)))
					.collect(Collectors.toSet()));
		}
		for (int i = vOri - 1; i > -1 && (vOri - 1 - i) < query.getLeft(); --i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(band -> band.getStartCoord().equals(sortedCoords.get(idx))
							|| band.getEndCoord().equals(sortedCoords.get(idx)))
					.collect(Collectors.toSet()));
		}

		return output;
	}

	public DataSource addDataSource(DataSource dataSource) {
		return dataSources.put(dataSource.track(), dataSource);
	}

	public Track removeDataSource(Track track) {
		return dataSources.remove(track).track();
	}

	public Set<Track> removeAll() {

		Set<Track> removedTracks = dataSources.keySet();
		dataSources = new HashMap<>();

		return removedTracks;
	}

	public Set<Track> getTracks() {
		return dataSources.keySet();
	}
}
