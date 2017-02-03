package pro.parseq.ghop.datasources.attributes;

import java.util.Collection;

import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import pro.parseq.ghop.datasources.filters.FilterOperator;
import pro.parseq.ghop.utils.IdGenerationUtils;

@JsonInclude(Include.NON_NULL)
public abstract class AbstractAttribute<T extends Comparable<T>> implements Attribute<T>, Identifiable<Long> {

	private final long id;
	private final String name;
	private final AttributeType type;
	private final String description;
	private final AttributeRange<T> range;

	protected AbstractAttribute(String name, AttributeType type, String description, AttributeRange<T> range) {

		this.id = IdGenerationUtils.generateAttributeId();
		this.name = name;
		this.type = type;
		this.description = description;
		this.range = range;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public AttributeType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public AttributeRange<T> getRange() {
		return range;
	}

	public int compare(T a, T b) {
		return a.compareTo(b);
	}

	public abstract T parseValue(String s);

	@JsonProperty("filterOperators")
	public abstract Collection<FilterOperator> operators();

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Attribute<?>)) {
			return false;
		}

		return id == ((Attribute<?>) obj).getId();
	}

	@Override
	public String toString() {
		return name;
	}
}
