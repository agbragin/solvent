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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.solvent.datasources.DataSource;
import pro.parseq.solvent.datasources.MasterDataSource;
import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.exceptions.DataSourceNotFoundException;
import pro.parseq.solvent.utils.HateoasUtils;

@RestController
@RequestMapping("/dataSources")
public class DataSourceController {

	@Autowired
	public MasterDataSource masterDataSource;

	@GetMapping
	public Resources<Resource<DataSource<?>>> getDataSources() {
		return HateoasUtils.dataSourceResources(masterDataSource.getDataSources());
	}

	@RequestMapping("/{id}")
	public Resource<DataSource<?>> getDataSource(@PathVariable Long id) {

		DataSource<?> dataSource = masterDataSource.getDataSource(id);
		if (dataSource == null) {
			throw new DataSourceNotFoundException(id);
		}

		return HateoasUtils.dataSourceResource(dataSource);
	}

	@RequestMapping("/{id}/attributes")
	public Resources<Resource<Attribute<?>>> getDataSourceAttributes(@PathVariable Long id) {

		DataSource<?> dataSource = masterDataSource.getDataSource(id);
		if (dataSource == null) {
			throw new DataSourceNotFoundException(id);
		}

		return HateoasUtils.dataSourceAttributeResources(dataSource,
				dataSource.attributes());
	}
}
