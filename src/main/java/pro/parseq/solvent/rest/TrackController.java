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
package pro.parseq.solvent.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pro.parseq.solvent.datasources.ChromosomeDataSource;
import pro.parseq.solvent.datasources.DataSource;
import pro.parseq.solvent.datasources.DataSourceFactory;
import pro.parseq.solvent.datasources.DataSourceType;
import pro.parseq.solvent.datasources.MasterDataSource;
import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.filters.AttributeFilter;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.ChromosomeBand;
import pro.parseq.solvent.entities.ReferenceGenome;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.entities.TrackFilterQuery;
import pro.parseq.solvent.exceptions.TrackCreationException;
import pro.parseq.solvent.exceptions.TrackNotFoundException;
import pro.parseq.solvent.utils.AttributeFilterUtils;
import pro.parseq.solvent.utils.HateoasUtils;

@RestController
@RequestMapping("/tracks")
public class TrackController {

	private static final String CHROMOSOME_TRACK = "Chromosome";

	@Autowired
	private MasterDataSource masterDataSource;

	@Autowired
	private DataSourceFactory dataSourceFactory;

	@Autowired
	private AttributeFilterUtils attributeFilterUtils;

	@GetMapping
	public Resources<?> getTracks() {
		return HateoasUtils.trackResources(masterDataSource.getTracks());
	}

	@DeleteMapping
	public void removeAll() {
		masterDataSource.removeAll();
	}

	/**
	 * Using @RequestMapping instead of @GetMapping as while
	 * there is no support for the latter in ControllerLinkBuilder,
	 * so resources end up with inappropriate self links
	 * (/tracks, but not /tracks/{track_name})
	 * 
	 * https://github.com/spring-projects/spring-hateoas/issues/471
	 */
	@RequestMapping("/{track}")
	public Resource<Track> getTrack(@PathVariable Track track) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			// TODO: the controller should return NOT_FOUND status, not throw an exception
			throw new TrackNotFoundException(track);
		}

		return HateoasUtils.trackResource(storedTrack);
	}

	@DeleteMapping("/{track}")
	public Resource<Track> removeTrack(@PathVariable Track track) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			throw new TrackNotFoundException(track);
		}

		return HateoasUtils.trackResource(masterDataSource.removeTrack(storedTrack));
	}

	/**
	 * At the controller level attributes are 'attached' to tracks
	 * 
	 * TODO: mb we should do it in a more natural way (as attributes come from data sources)?
	 */
	@RequestMapping("/{track}/attributes")
	public Resources<Resource<Attribute<?>>> getTrackAttributes(@PathVariable Track track) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			throw new TrackNotFoundException(track);
		}

		return HateoasUtils.trackAttributeResources(storedTrack,
				storedTrack.getDataSource().attributes());
	}

	@RequestMapping("/{track}/dataSource")
	public Resource<DataSource<?>> getTrackDataSource(@PathVariable Track track) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			throw new TrackNotFoundException(track);
		}

		return HateoasUtils.dataSourceResource(storedTrack.getDataSource());
	}

	@RequestMapping("/{track}/filters")
	public Resources<Resource<DataSource<?>>> getTrackFilters(@PathVariable Track track) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			throw new TrackNotFoundException(track);
		}

		return HateoasUtils.trackDataSourceResources(storedTrack,
				storedTrack.getFilters().values().stream()
						.collect(Collectors.toSet()));
	}

	// TODO: make it possible to pass attribute URI instead of it's id
	@PostMapping("/{track}/filters")
	public Resource<DataSource<?>> createTrackFilter(@PathVariable Track track,
			@RequestBody TrackFilterQuery query) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			throw new TrackNotFoundException(track);
		}

		List<AttributeFilter<?>> filters = query.getFilters().stream()
				.map(attributeFilterUtils::buildAttributeFilter)
				.collect(Collectors.toList());
		FilterQuery filterQuery = new FilterQuery(filters, query.getAggregates());

		DataSource<?> filteredDataSource = storedTrack.getDataSource().filter(filterQuery);
		storedTrack.putFilter(filteredDataSource);

		return HateoasUtils.dataSourceResource(filteredDataSource);
	}

	@DeleteMapping("/{track}/filters")
	public Resources<Resource<DataSource<?>>> removeTrackFilters(@PathVariable Track track) {

		Track storedTrack = masterDataSource.getTrack(track.getName());
		if (storedTrack == null) {
			throw new TrackNotFoundException(track);
		}

		Set<DataSource<?>> filters = storedTrack.getFilters().values()
				.stream().collect(Collectors.toSet());
		storedTrack.setFilters(new HashMap<>());

		return HateoasUtils.trackFilterResources(storedTrack, filters);
	}

	@PostMapping(params = { "track", "type" })
	public Resource<Track> createTrack(@RequestParam Track track,
			@RequestParam("type") DataSourceType dataSourceType, @RequestParam MultipartFile file) {

		try {
			return createTrack(track, dataSourceType, file.getInputStream());
		} catch (RuntimeException e) {
			throw new TrackCreationException(e, track.getName(), dataSourceType);
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"I/O exception while input file manipultaions: %s", e.getMessage()));
		}
	}

	@PostMapping(params = { "track", "type", "path" })
	public Resource<Track> createTrackFromLocalFile(@RequestParam Track track,
			@RequestParam("type") DataSourceType dataSourceType, @RequestParam String path) {

		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("Invalid data-file path: check it for file existence and availablity");
		}

		try (InputStream fis = new FileInputStream(file)) {
			return createTrack(track, dataSourceType, fis);
		} catch (RuntimeException e) {
			throw new TrackCreationException(e, track.getName(), dataSourceType);
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"I/O exception while input file manipultaions: %s", e.getMessage()));
		}
	}

	@PostMapping(params = { "genome" })
	public Resource<Track> chooseGenome(@RequestParam("genome") ReferenceGenome referenceGenome) {

		try {

			masterDataSource.setReferenceGenome(referenceGenome);

			Track chromosomeTrack = new Track(CHROMOSOME_TRACK);
			DataSource<ChromosomeBand> chromosomeDataSource = new ChromosomeDataSource(chromosomeTrack,
					masterDataSource.getReferenceService().getContigs(referenceGenome.getId()),
					masterDataSource.getComparator());

			chromosomeTrack.setDataSource(chromosomeDataSource);
			masterDataSource.addTrack(chromosomeTrack);

			return getTrack(chromosomeTrack);
		} catch (RuntimeException e) {
			throw new TrackCreationException(e, CHROMOSOME_TRACK, DataSourceType.CHROMOSOME);
		}
	}

	private Resource<Track> createTrack(Track track, DataSourceType type, InputStream file) {

		DataSource<?> dataSource;
		switch (type) {
		case VCF:
			dataSource = dataSourceFactory.vcfFileDataSourceInstance(track, file);
			break;

		case BASIC_BED:
			dataSource = dataSourceFactory.basicBedFileDataSourceInstance(track, file);
			break;

		default:
			throw new RuntimeException("Yet unsupported data source type: " + type);
		}

		track.setDataSource(dataSource);
		masterDataSource.addTrack(track);

		return getTrack(track);
	}
}
