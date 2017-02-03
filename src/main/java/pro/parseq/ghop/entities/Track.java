package pro.parseq.ghop.entities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pro.parseq.ghop.datasources.DataSource;

@Relation(collectionRelation = "tracks")
public class Track {

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
