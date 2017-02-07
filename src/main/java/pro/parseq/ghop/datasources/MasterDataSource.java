/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package pro.parseq.ghop.datasources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.utils.GenomicCoordinate;

@Component
public class MasterDataSource {

	private Map<String, Track> tracks = new HashMap<>();

	@Autowired
	private Comparator<GenomicCoordinate> comparator;

	public Set<Track> getTracks() {
		return tracks.keySet().stream().map(tracks::get).collect(Collectors.toSet());
	}

	public Track getTrack(String trackName) {
		return tracks.get(trackName);
	}

	public Track addTrack(Track track) {
		return tracks.put(track.getName(), track);
	}

	public Track removeTrack(Track track) {
		return tracks.remove(track);
	}

	public Set<DataSource<? extends Band>> getDataSources() {

		return Stream.concat(
						tracks.values().stream().map(Track::getDataSource),
						tracks.values().stream().map(Track::getFilters)
								.map(Map::values).flatMap(Collection::stream))
				.collect(Collectors.toSet());
	}

	public DataSource<? extends Band> getDataSource(long id) {

		return getDataSources().stream()
				.filter(ds -> ds.getId().equals(id))
				.findAny().orElse(null);
	}

	public Set<Attribute<?>> getAttributes() {

		return getDataSources().stream()
				.map(DataSource::attributes)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	public Attribute<?> getAttribute(long id) {

		return getAttributes().stream()
				.filter(a -> a.getId().equals(id))
				.findAny().orElse(null);
	}

	/**
	 * Note, that invocation of this method will evict cache associated with reference genomes information
	 * 
	 * TODO: it's wrong to do this here, change cache eviction logic
	 */
	@CacheEvict("referenceGenomes")
	public void removeAll() {
		tracks = new HashMap<>();
	}

	/**
	 * This is straightforward dummy implementation
	 * 
	 * TODO: do it in a more optimized way
	 */
	/**
	 * Query for data sources' objects
	 * 
	 * @param query {@link QueryForBands} (encapsulates information about bearing coordinate, tracks correlation etc.)
	 * @return {@link Band} {@link Set} for present query
	 */
	public Set<? extends Band> getBands(QueryForBands query) {

		// Band collection from each source for the present query
		Set<Band> bands = new HashSet<>();
		// Band collection from each source that is covering present coordinate
		Set<Band> coverage = new HashSet<>();
		// Coordinate collection of all bands defined above
		Set<GenomicCoordinate> coords = new HashSet<>();
		for (DataSource<? extends Band> dataSource: query.getDataSources()) {

			// Retrieve track's coordinate coverage
			Set<? extends Band> trackCoverage = dataSource.coverage(query.getCoord());
			trackCoverage.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
			// Retrieve track's next bands' coordinates
			Set<? extends Band> trackNextBands = dataSource
					.rightBordersGenerants(query.getRight(), query.getCoord());
			trackNextBands.stream().forEach(band -> {
				coords.add(band.getStartCoord());
				coords.add(band.getEndCoord());
			});
			// Retrieve track's previous bands' coordinates
			Set<? extends Band> trackPrevBands = dataSource
					.leftBordersGenerants(query.getLeft(), query.getCoord());
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
}
