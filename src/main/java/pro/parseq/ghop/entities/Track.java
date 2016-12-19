package pro.parseq.ghop.entities;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Relation(collectionRelation = "tracks")
public class Track {

	@JsonProperty("track")
	private final String name;

	@JsonCreator
	public Track(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
