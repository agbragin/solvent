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
package pro.parseq.solvent.datasources.attributes;

import java.util.Collection;

import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import pro.parseq.solvent.datasources.filters.FilterOperator;
import pro.parseq.solvent.utils.IdGenerationUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

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
