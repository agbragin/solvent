package pro.parseq.ghop.datasources.filters;

import java.util.Collection;

import pro.parseq.ghop.datasources.attributes.Attribute;

public class AttributeFilter<T extends Comparable<T>> {

	private final long id;
	private final Attribute<T> attribute;
	private final FilterOperator operator;
	private final Collection<T> values;
	private final boolean includeNulls;

	public AttributeFilter(long id, Attribute<T> attribute,
			FilterOperator operator, Collection<T> values, boolean includeNulls) {

		this.id = id;
		this.attribute = attribute;
		this.operator = operator;
		this.values = values;
		this.includeNulls = includeNulls;
	}

	public long getId() {
		return id;
	}

	public Attribute<T> getAttribute() {
		return attribute;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public Collection<T> getValues() {
		return values;
	}

	public boolean isIncludeNulls() {
		return includeNulls;
	}
}
