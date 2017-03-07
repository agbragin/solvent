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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.exceptions.AttributeNotFoundException;
import pro.parseq.ghop.utils.HateoasUtils;

@RestController
@RequestMapping("/attributes")
public class AttributeController {

	@Autowired
	private MasterDataSource masterDataSource;

	@GetMapping
	public Resources<Resource<Attribute<?>>> getAttributes() {
		return HateoasUtils.attributeResources(masterDataSource.getAttributes());
	}

	@RequestMapping("/{id}")
	public Resource<Attribute<?>> getAttribute(@PathVariable Long id) {

		Attribute<?> attribute = masterDataSource.getAttribute(id);
		if (attribute == null) {
			throw new AttributeNotFoundException(id);
		}

		return HateoasUtils.attributeResource(attribute);
	}
}
