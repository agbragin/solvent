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
package pro.parseq.ghop.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.datasources.filters.AttributeFilter;
import pro.parseq.ghop.entities.AttributeFilterEntity;
import pro.parseq.ghop.exceptions.UnknownAttributeException;

@Component
public class AttributeFilterUtils {

	@Autowired
	private MasterDataSource masterDataSource;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AttributeFilter<?> buildAttributeFilter(AttributeFilterEntity entity) {

		Attribute<?> attribute = masterDataSource.getAttribute(entity.getAttributeId());
		if (attribute == null) {
			throw new UnknownAttributeException(entity.getAttributeId());
		}

		Collection<?> values = entity.getValues().stream()
				.map(attribute::parseValue).collect(Collectors.toList());

		return new AttributeFilter(entity.getId(), attribute,
				entity.getOperator(), values, entity.isIncludeNulls());
	}
}
