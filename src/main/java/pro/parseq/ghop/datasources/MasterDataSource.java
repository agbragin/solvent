package pro.parseq.ghop.datasources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.Query;

@Component
public class MasterDataSource {

	private Map<Track, DataSource> dataSources = new HashMap<>();

	@Autowired
	private Comparator<GenomicCoordinate> comparator;

	/**
	 * This is straightforward dummy implementation
	 * 
	 * TODO: do it in a more optimized way
	 */
	public Set<Band> getBands(Query query) {

		// Band collection from each source for the present query
		Set<Band> bands = new HashSet<>();
		// Band collection from each source that is covering present coordinate
		Set<Band> coverage = new HashSet<>();
		// Coordinate collection of all bands defined above
		Set<GenomicCoordinate> coords = new HashSet<>();
		for (Track track: query.getTrackSettings().getTracks()) {

			// Retrieve track's coordinate coverage
			Set<Band> trackCoverage = dataSources.get(track).coverage(
					query.getCoord(), query.getTrackSettings().getTrackFilters(track));
			trackCoverage.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
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

			coverage.addAll(trackCoverage);
			bands.addAll(trackCoverage);
			bands.addAll(trackNextBands);
			bands.addAll(trackPrevBands);
		}

		// Arrange retrieved coordinates in an ascending order
		List<GenomicCoordinate> sortedCoords = new ArrayList<>(coords);
		Collections.sort(sortedCoords, comparator);
		/**
		 * Try to find an index of requested coordinate in the retrieved coordinate collection,
		 */
		int vOri = Collections.binarySearch(sortedCoords, query.getCoord(), comparator);
		/**
		 * If requested coordinate is found in the collection,
		 * we should take coordinate next to it,
		 * as we've already accounted it in the coverage
		 */
		int corr = 1;
		if (vOri < 0) {
			vOri = -(vOri + 1);
			/**
			 * But if not, than vOri is already the next coordinate
			 * (see Collections.binarySearch method documentation)
			 */
			corr = 0;
		}
		// Bands to output
		Set<Band> output = new HashSet<>(coverage);
		/**
		 * Take right borders generants
		 */
		for (int i = vOri + corr; i < sortedCoords.size() && (i - vOri - corr) < query.getRight(); ++i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(band -> band.getStartCoord().equals(sortedCoords.get(idx))
							|| band.getEndCoord().equals(sortedCoords.get(idx)))
					.collect(Collectors.toSet()));
		}
		/**
		 * Take left border generants
		 * (due to Collections.binarySearch mechanism we start iterating from (vOri-1) by default,
		 * so no need in any corrections as we done in right borders generants)
		 */
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
