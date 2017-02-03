package pro.parseq.ghop.datasources.filters;

import java.util.List;

import pro.parseq.ghop.entities.AttributeFilterAggregateEntity;

public class FilterQuery {

	private final List<AttributeFilter<?>> filters;
	private final List<AttributeFilterAggregateEntity> aggregates;

	public FilterQuery(List<AttributeFilter<?>> filters,
			List<AttributeFilterAggregateEntity> aggregates) {

		this.filters = filters;
		this.aggregates = aggregates;
	}

	public List<AttributeFilter<?>> getFilters() {
		return filters;
	}

	public List<AttributeFilterAggregateEntity> getAggregates() {
		return aggregates;
	}
}
