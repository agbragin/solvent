package pro.parseq.solvent.entities;

import com.fasterxml.jackson.databind.JsonNode;

import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.PropertiesAware;

public abstract class AbstractPropertiesAwareBand extends AbstractBand implements PropertiesAware {

	private JsonNode properties;

	public AbstractPropertiesAwareBand(Track track,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			String name, JsonNode properties) {

		super(track, startCoord, endCoord, name);

		this.properties = properties;
	}


	@Override
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
		if (!(obj instanceof AbstractPropertiesAwareBand)) {
			return false;
		}

		return getTrack().equals(((BedBand) obj).getTrack())
				&& getStartCoord().equals(((BedBand) obj).getStartCoord())
				&& getEndCoord().equals(((BedBand) obj).getEndCoord())
				&& getName().equals(((BedBand) obj).getName())
				&& getProperties().equals(((BedBand) obj).getProperties());
	}
}
