package pro.parseq.ghop.entities;

import java.util.Collection;

import pro.parseq.ghop.datasources.filters.AggregateOperator;

public class AttributeFilterAggregateEntity {

	private long id;
	private Collection<Long> filters;
	private AggregateOperator operator;

	protected AttributeFilterAggregateEntity() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Collection<Long> getFilters() {
		return filters;
	}

	public void setFilters(Collection<Long> filters) {
		this.filters = filters;
	}

	public AggregateOperator getOperator() {
		return operator;
	}

	public void setOperator(AggregateOperator operator) {
		this.operator = operator;
	}
}
