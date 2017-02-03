package pro.parseq.ghop.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.datasources.DataSource;
import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.exceptions.DataSourceNotFoundException;
import pro.parseq.ghop.utils.HateoasUtils;

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
