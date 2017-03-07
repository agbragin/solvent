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
package pro.parseq.ghop.rest;

import java.io.IOException;
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

import pro.parseq.ghop.datasources.ChromosomeDataSource;
import pro.parseq.ghop.datasources.DataSource;
import pro.parseq.ghop.datasources.DataSourceFactory;
import pro.parseq.ghop.datasources.DataSourceType;
import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.AttributeFilter;
import pro.parseq.ghop.datasources.filters.FilterQuery;
import pro.parseq.ghop.entities.ChromosomeBand;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.entities.TrackFilterQuery;
import pro.parseq.ghop.exceptions.IllegalDataSourceTypeException;
import pro.parseq.ghop.exceptions.TrackNotFoundException;
import pro.parseq.ghop.utils.AttributeFilterUtils;
import pro.parseq.ghop.utils.HateoasUtils;

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
	public Resources<Resource<Track>> getTracks() {
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
			@RequestParam("type") String dataSourceType, @RequestParam MultipartFile file) {

		try {

			/**
			 * String to Enum conversion out-of-the-box doesn't work,
			 * so now we need to pass data source type as a String
			 * 
			 * TODO: investigate this
			 */
			DataSourceType type = DataSourceType.getEnum(dataSourceType);
			DataSource<?> dataSource;
			switch (type) {
			case VCF:
				dataSource = dataSourceFactory.vcfFileDataSourceInstance(track, file.getInputStream());
				break;

			case VARIANTS_BED:
				dataSource = dataSourceFactory.variantsBedFileDataSourceInstance(track, file.getInputStream());
				break;

			case BASIC_BED:
				dataSource = dataSourceFactory.basicBedFileDataSourceInstance(track, file.getInputStream());
				break;

			default:
				throw new RuntimeException("Yet unsupported data source type: " + type);
			}

			track.setDataSource(dataSource);
			masterDataSource.addTrack(track);

			return getTrack(track);
		} catch (IllegalArgumentException e) {
			throw new IllegalDataSourceTypeException(dataSourceType);
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"I/O exception while BED file manipultaions: %s", e.getMessage()));
		}
	}

	@PostMapping(params = { "genome" })
	public Resource<Track> chooseGenome(@RequestParam("genome") ReferenceGenome referenceGenome) {

		masterDataSource.setReferenceGenome(referenceGenome);

		Track chromosomeTrack = new Track(CHROMOSOME_TRACK);
		DataSource<ChromosomeBand> chromosomeDataSource = new ChromosomeDataSource(chromosomeTrack,
				masterDataSource.getReferenceService().getContigs(referenceGenome.getId()),
				masterDataSource.getComparator());

		chromosomeTrack.setDataSource(chromosomeDataSource);
		masterDataSource.addTrack(chromosomeTrack);

		return getTrack(chromosomeTrack);
	}
}
