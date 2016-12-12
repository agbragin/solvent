package pro.parseq.ghop.data;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@Relation(collectionRelation = "bands")
public class Band {

	@JsonUnwrapped
	private final Track track;

	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;

	private String name;
	private JsonNode properties = JsonNodeFactory.instance.objectNode();

	private Band(Track track,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			String name, JsonNode properties) {

		this.track = track;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.name = name;
		this.properties = properties;
	}

	public Track getTrack() {
		return track;
	}

	public GenomicCoordinate getStartCoord() {
		return startCoord;
	}

	public GenomicCoordinate getEndCoord() {
		return endCoord;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JsonNode getProperties() {
		return properties;
	}

	public void setProperties(JsonNode properties) {
		this.properties = properties;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Band)) {
			return false;
		}

		return track.equals(((Band) obj).track)
				&& startCoord.equals(((Band) obj).startCoord)
				&& endCoord.equals(((Band) obj).endCoord);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {

		return new StringBuilder()
				.append(track)
				.append("[")
				.append(startCoord)
				.append(";")
				.append(endCoord)
				.append(")")
				.toString();
	}

	public static class BandBuilder {

		private final Track track;
		private final GenomicCoordinate startCoord;
		private final GenomicCoordinate endCoord;

		private String name;
		private JsonNode properties = JsonNodeFactory.instance.objectNode();

		public BandBuilder(Track track, GenomicCoordinate startCoord, GenomicCoordinate endCoord) {

			this.track = track;
			this.startCoord = startCoord;
			this.endCoord = endCoord;
		}

		public BandBuilder name(String name) {
			this.name = name;
			return this;
		}

		public BandBuilder properties(JsonNode properties) {
			this.properties = properties;
			return this;
		}

		public Band build() {
			return new Band(track, startCoord, endCoord, name, properties);
		}
	}
}
