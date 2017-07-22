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
package pro.parseq.solvent.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pro.parseq.solvent.datasources.DataSource;

@Relation(collectionRelation = "tracks")
public class Track implements Serializable {

	private static final long serialVersionUID = 1504446145142194646L;

	@JsonProperty("track")
	private final String name;

	@JsonIgnore
	private DataSource<?> dataSource;

	@JsonIgnore
	private Map<String, DataSource<?>> filters = new HashMap<>();

	@JsonCreator
	public Track(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DataSource<?> getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource<?> dataSource) {
		this.dataSource = dataSource;
	}

	public Map<String, DataSource<?>> getFilters() {
		return filters;
	}

	public DataSource<?> putFilter(DataSource<?> filteredDataSource) {
		return filters.put(filteredDataSource.getId().toString(), filteredDataSource);
	}

	public void setFilters(Map<String, DataSource<?>> filters) {
		this.filters = filters;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Track)) {
			return false;
		}

		return name.equals(((Track) obj).name);
	}

	@Override
	public String toString() {
		return name;
	}
}
