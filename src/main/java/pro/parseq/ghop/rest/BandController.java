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

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.QueryForBands;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.utils.DataSourceUtils;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.HateoasUtils;

@RestController
@RequestMapping("/bands")
public class BandController {

	@Autowired
	private MasterDataSource masterDataSource;

	@Autowired
	private DataSourceUtils dataSourceUtils;

	@GetMapping
	public Resources<? extends Band> getBands(@RequestParam("genome") String referenceGenomeName,
			@RequestParam("contig") String contigName, @RequestParam long coord,
			@RequestParam int left, @RequestParam int right,
			@RequestParam("dataSources") Set<String> dataSourceUris) {

		QueryForBands query = new QueryForBands(
				new GenomicCoordinate(referenceGenomeName, contigName, coord),
				left, right, dataSourceUris.stream()
						.map(dataSourceUtils::retrieveDataSourceByUri)
						.collect(Collectors.toSet()));

		return HateoasUtils.bandResources(masterDataSource.getBands(query), query);
	}
}
