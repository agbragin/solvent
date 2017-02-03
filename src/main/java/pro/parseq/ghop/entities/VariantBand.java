package pro.parseq.ghop.entities;

import com.fasterxml.jackson.databind.JsonNode;

import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.PropertiesAware;

public class VariantBand implements Band, PropertiesAware {

	private final Track track;
	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;
	private final String name;
	private final JsonNode properties;

	public VariantBand(Track track, GenomicCoordinate startCoord, 
			GenomicCoordinate endCoord, String name, JsonNode properties) {

		this.track = track;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.name = name;
		this.properties = properties;
	}

	@Override
	public Track getTrack() {
		return track;
	}

	@Override
	public GenomicCoordinate getStartCoord() {
		return startCoord;
	}

	@Override
	public GenomicCoordinate getEndCoord() {
		return endCoord;
	}

	@Override
	public String getName() {
		return name;
	}

	public JsonNode getProperties() {
		return properties;
	}

	@Override
	public String toString() {

		return String.format("%s:%s-%s", this.getName(),
				this.getStartCoord(), this.getEndCoord());
	}
}
