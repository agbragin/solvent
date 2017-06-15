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
package pro.parseq.solvent.utils;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import pro.parseq.solvent.datasources.DataSource;
import pro.parseq.solvent.datasources.DataSourceBands;
import pro.parseq.solvent.datasources.DataSourceType;
import pro.parseq.solvent.datasources.QueryForBands;
import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.entities.ReferenceGenome;
import pro.parseq.solvent.entities.ReferenceGenomeContigs;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.rest.*;

public class HateoasUtils {

	public static final Resources<Resource<Track>> trackResources(Set<Track> tracks) {

		Link selfLink = linkTo(methodOn(TrackController.class).getTracks())
				.withSelfRel();
		Set<Resource<Track>> trackResources = tracks.stream()
				.map(HateoasUtils::trackResource).collect(Collectors.toSet());

		return new Resources<>(trackResources, selfLink);
	}

	public static final Resource<Track> trackResource(Track track) {

		Link selfLink = linkTo(methodOn(TrackController.class)
				.getTrack(track)).withSelfRel();
		Link attributesLink = linkTo(methodOn(TrackController.class)
				.getTrackAttributes(track)).withRel("attributes");
		Link dataSourceLink = linkTo(methodOn(TrackController.class)
				.getTrackDataSource(track)).withRel("dataSource");
		Link filtersLink = linkTo(methodOn(TrackController.class)
				.getTrackFilters(track)).withRel("filters");

		return new Resource<>(track, selfLink, attributesLink, dataSourceLink, filtersLink);
	}

	public static final Resources<Resource<Attribute<?>>> trackAttributeResources(Track track,
			Set<Attribute<?>> attributes) {

		Link selfLink = linkTo(methodOn(TrackController.class)
				.getTrackAttributes(track)).withSelfRel();
		Set<Resource<Attribute<?>>> attributeResources = attributes.stream()
				.map(HateoasUtils::attributeResource).collect(Collectors.toSet());

		return new Resources<>(attributeResources, selfLink);
	}

	public static final Resources<Resource<DataSource<?>>> trackDataSourceResources(Track track,
			Set<DataSource<?>> dataSources) {

		Link selfLink = linkTo(methodOn(TrackController.class)
				.getTrackDataSource(track)).withSelfRel();
		Set<Resource<DataSource<?>>> dataSourceResources = dataSources.stream()
				.map(HateoasUtils::dataSourceResource).collect(Collectors.toSet());

		return new Resources<>(dataSourceResources, selfLink);
	}

	public static final Resources<Resource<DataSource<?>>> trackFilterResources(Track track,
			Set<DataSource<?>> dataSources) {

		Link selfLink = linkTo(methodOn(TrackController.class)
				.getTrackFilters(track)).withSelfRel();
		Set<Resource<DataSource<?>>> dataSourceResources = dataSources.stream()
				.map(HateoasUtils::dataSourceResource).collect(Collectors.toSet());

		return new Resources<>(dataSourceResources, selfLink);
	}

	public static final Resources<Resource<Attribute<?>>> attributeResources(Set<Attribute<?>> attributes) {

		Link selfLink = linkTo(methodOn(AttributeController.class)
				.getAttributes()).withSelfRel();
		Set<Resource<Attribute<?>>> attributeResources = attributes.stream()
				.map(HateoasUtils::attributeResource).collect(Collectors.toSet());

		return new Resources<>(attributeResources, selfLink);
	}

	public static final Resource<Attribute<?>> attributeResource(Attribute<?> attribute) {

		Link selfLink = linkTo(methodOn(AttributeController.class)
				.getAttribute(attribute.getId())).withSelfRel();

		return new Resource<>(attribute, selfLink);
	}

	public static final Resource<ReferenceGenome> referenceGenomeResource(
			ReferenceGenome referenceGenome) {

		Link selfLink = linkTo(methodOn(ReferenceController.class)
				.getReferenceGenome(referenceGenome)).withSelfRel();

		return new Resource<>(referenceGenome, selfLink);
	}

	public static final Resources<Resource<ReferenceGenome>> referenceGenomeResources(
			Set<ReferenceGenome> referenceGenomes) {

		Link selfLink = linkTo(methodOn(ReferenceController.class)
				.getReferenceGenomes()).withSelfRel();
		Set<Resource<ReferenceGenome>> referenceGenomeResources = referenceGenomes.stream()
				.map(HateoasUtils::referenceGenomeResource)
				.collect(Collectors.toSet());

		return new Resources<>(referenceGenomeResources, selfLink);
	}

	public static final Resource<ReferenceGenomeContigs> referenceGenomeContigsResource(
			ReferenceGenome referenceGenome, List<String> contigs) {

		Link selfLink = linkTo(methodOn(ReferenceController.class)
				.getReferenceGenome(referenceGenome)).withSelfRel();

		return new Resource<>(new ReferenceGenomeContigs(contigs), selfLink);
	}

	public static final Resources<Resource<DataSource<?>>> dataSourceResources(Set<DataSource<?>> dataSources) {

		Link selfLink = linkTo(methodOn(DataSourceController.class)
				.getDataSources()).withSelfRel();
		Set<Resource<DataSource<?>>> dataSourceResources = dataSources.stream()
				.map(HateoasUtils::dataSourceResource)
				.collect(Collectors.toSet());

		return new Resources<>(dataSourceResources, selfLink);
	}

	public static final Resource<DataSource<?>> dataSourceResource(DataSource<?> dataSource) {

		Link selfLink = linkTo(methodOn(DataSourceController.class)
				.getDataSource(dataSource.getId())).withSelfRel();
		Link attributesLink = linkTo(methodOn(DataSourceController.class)
				.getDataSourceAttributes(dataSource.getId())).withRel("attributes");

		return new Resource<>(dataSource, selfLink, attributesLink);
	}

	public static final Resources<Resource<Attribute<?>>> dataSourceAttributeResources(
			DataSource<?> dataSource, Set<Attribute<?>> attributes) {

		Link selfLink = linkTo(methodOn(DataSourceController.class)
				.getDataSourceAttributes(dataSource.getId())).withSelfRel();
		Set<Resource<Attribute<?>>> attributeResources = attributes.stream()
				.map(HateoasUtils::attributeResource)
				.collect(Collectors.toSet());

		return new Resources<>(attributeResources, selfLink);
	}

	public static final Resource<DataSourceBands> bandResources(DataSourceBands bands, QueryForBands query) {

		Link selfLink = linkTo(methodOn(BandController.class)
				.getBands(query.getCoord().getContig().getId(), query.getCoord().getCoord(),
						query.getLeft(), query.getRight(),
						query.getDataSources().stream()
								.map(DataSource::getId)
								.map(String::valueOf)
								.collect(Collectors.toSet())))
				.withSelfRel();

		return new Resource<>(bands, selfLink);
	}

	public static final Resources<DataSourceType> dataSourceTypeResources() {

		Link selfLink = linkTo(methodOn(DataSourceTypeController.class)
				.getDataSourceTypes()).withSelfRel();

		return new Resources<>(Arrays.asList(DataSourceType.values()), selfLink);
	}
}
