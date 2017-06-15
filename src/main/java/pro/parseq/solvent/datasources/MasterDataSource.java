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
package pro.parseq.solvent.datasources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.entities.Band;
import pro.parseq.solvent.entities.ReferenceGenome;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.services.ReferenceService;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.GenomicCoordinateComparatorFactory;
import pro.parseq.solvent.utils.PredicateUtils;

@Component
public class MasterDataSource {

	@Autowired
	private GenomicCoordinateComparatorFactory genomicCoordinateComparatorFactory;

	private Map<String, Track> tracks = new HashMap<>();

	private ReferenceService referenceService;
	private Comparator<GenomicCoordinate> comparator;
	private ReferenceGenome referenceGenome;

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
		this.comparator = genomicCoordinateComparatorFactory.newComparator(referenceService);
	}

	public ReferenceService getReferenceService() {

		if (referenceService == null) {
			throw new IllegalStateException("No reference service was selected: select your reference service first");
		}

		return referenceService;
	}

	public Comparator<GenomicCoordinate> getComparator() {

		if (referenceService == null) {
			throw new IllegalStateException("No reference service was selected: select your reference service first");
		}

		return comparator;
	}

	public void setReferenceGenome(ReferenceGenome referenceGenome) {
		this.referenceGenome = referenceGenome;
	}

	public ReferenceGenome getReferenceGenome() {

		if (referenceGenome == null) {
			throw new IllegalStateException("No reference genome was selected: select reference genome first");
		}

		return referenceGenome;
	}

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
		return tracks.remove(track.getName());
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
	public DataSourceBands getBands(QueryForBands query) {

		// Band collection from each source for the present query
		Set<Band> bands = new HashSet<>();
		// Coordinate collection of all bands defined above
		Set<GenomicCoordinate> coords = new HashSet<>();

		query.getDataSources().stream().forEach(dataSource -> {

			// Retrieve data source's bands
			Set <? extends Band> trackBands = dataSource
					.getBands(query.getCoord(), query.getLeft(), query.getRight());

			// Retrieve bands' borders
			coords.addAll(trackBands.stream()
					.map(band -> Stream.concat(Stream.of(band.getStartCoord()), Stream.of(band.getEndCoord())))
					.flatMap(Function.identity())
					.collect(Collectors.toSet()));

			bands.addAll(trackBands);
		});

		if (coords.size() == 0) {
			return new DataSourceBands(new HashSet<>(), true, true);
		}

		// Arrange retrieved coordinates in an ascending order
		List<GenomicCoordinate> sortedCoords = new ArrayList<>(coords);
		Collections.sort(sortedCoords, comparator);
		/**
		 * Try to find an index of requested coordinate in the retrieved coordinate collection,
		 */
		int vOri = Collections.binarySearch(sortedCoords, query.getCoord(), comparator);
		if (vOri < 0) {
			/**
			 * See Collections.binarySearch method documentation
			 */
			vOri = -(vOri + 1);
		}

		// Bands to output
		Set<Band> output = new HashSet<>();
		/**
		 * Take right borders generants
		 */
		for (int i = vOri; i < sortedCoords.size() && (i - vOri) <= query.getRight(); ++i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(PredicateUtils.isCovering(sortedCoords.get(idx), comparator))
					.collect(Collectors.toSet()));
		}
		/**
		 * Retrieve an information about whether the requested coordinates contain data sources' right outermost point
		 * (in other words, do they hold more bands laying beyond the rightmost coordinate)
		 */
		int rightmostIdx = ((vOri + query.getRight()) < sortedCoords.size()) ? (vOri + query.getRight()) : (sortedCoords.size() - 1);
		GenomicCoordinate rightmost = sortedCoords.get(rightmostIdx);
		boolean isRightmost = query.getDataSources().stream().allMatch(it -> it.getBands(rightmost, 0, 2).equals(it.getBands(rightmost, 0, 0)));

		/**
		 * Take left border generants
		 * (due to Collections.binarySearch mechanism we start iterating from (vOri-1) by default,
		 * so no need in any corrections as we done in right borders generants)
		 */
		for (int i = vOri - 1; i > -1 && (vOri - 1 - i) <= query.getLeft(); --i) {
			final int idx = i;
			output.addAll(bands.stream()
					.filter(PredicateUtils.isCovering(sortedCoords.get(idx), comparator))
					.collect(Collectors.toSet()));
		}
		/**
		 * Retrieve an information about whether the requested coordinates contain data sources' left outermost point
		 * (in other words, do they hold more bands laying beyond the leftmost coordinate)
		 */
		int leftmostIdx = ((vOri - query.getLeft()) < 0) ? 0 : (vOri - query.getLeft());
		GenomicCoordinate leftmost = sortedCoords.get(leftmostIdx);
		boolean isLeftmost = query.getDataSources().stream().allMatch(it -> it.getBands(leftmost, 2, 0).equals(it.getBands(leftmost, 0, 0)));

		return new DataSourceBands(output, isLeftmost, isRightmost);
	}
}
