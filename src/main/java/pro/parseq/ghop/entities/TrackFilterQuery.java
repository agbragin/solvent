package pro.parseq.ghop.entities;

import java.util.List;

public class TrackFilterQuery {

	private List<AttributeFilterEntity> filters;
	private List<AttributeFilterAggregateEntity> aggregates;

	protected TrackFilterQuery() {}

	public List<AttributeFilterEntity> getFilters() {
		return filters;
	}

	public void setFilters(List<AttributeFilterEntity> filters) {
		this.filters = filters;
	}

	public List<AttributeFilterAggregateEntity> getAggregates() {
		return aggregates;
	}

	public void setAggregates(List<AttributeFilterAggregateEntity> aggregates) {
		this.aggregates = aggregates;
	}
}
