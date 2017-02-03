package pro.parseq.ghop.entities;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import pro.parseq.ghop.datasources.filters.FilterOperator;

public class AttributeFilterEntity {

	private long id;

	@JsonProperty("attribute")
	private long attributeId;

	private FilterOperator operator;
	private Collection<String> values;
	private boolean includeNulls;

	protected AttributeFilterEntity() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(long attributeId) {
		this.attributeId = attributeId;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public void setOperator(FilterOperator operator) {
		this.operator = operator;
	}

	public Collection<String> getValues() {
		return values;
	}

	public void setValues(Collection<String> values) {
		this.values = values;
	}

	public boolean isIncludeNulls() {
		return includeNulls;
	}

	public void setIncludeNulls(boolean includeNulls) {
		this.includeNulls = includeNulls;
	}
}
