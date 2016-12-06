package pro.parseq.ghop.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class Band {

	private final String layerId;

	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;

	private JsonNode properties = JsonNodeFactory.instance.objectNode();

	public Band(String layerId,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord) {

		this.layerId = layerId;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
	}

	public Band(String layerId,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			JsonNode properties) {

		this.layerId = layerId;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.properties = properties;
	}

	public String getLayerId() {
		return layerId;
	}

	public GenomicCoordinate getStartCoord() {
		return startCoord;
	}

	public GenomicCoordinate getEndCoord() {
		return endCoord;
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

		return layerId.equals(((Band) obj).layerId)
				&& startCoord.equals(((Band) obj).startCoord)
				&& endCoord.equals(((Band) obj).endCoord);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {

		return new StringBuilder(layerId)
				.append("[")
				.append(startCoord)
				.append(";")
				.append(endCoord)
				.append(")")
				.toString();
	}
}
