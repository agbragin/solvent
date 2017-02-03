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
				.map(v -> attribute.parseValue(v)).collect(Collectors.toList());

		return new AttributeFilter(entity.getId(), attribute,
				entity.getOperator(), values, entity.isIncludeNulls());
	}
}
