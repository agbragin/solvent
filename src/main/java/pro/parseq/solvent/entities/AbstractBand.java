package pro.parseq.solvent.entities;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import pro.parseq.solvent.utils.GenomicCoordinate;

public abstract class AbstractBand implements Band {

	private static final long serialVersionUID = -481387878947254082L;

	@JsonUnwrapped
	private final Track track;

	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;
	private final String name;

	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	public AbstractBand(Track track, GenomicCoordinate startCoord, GenomicCoordinate endCoord, String name) {

		this.track = track;
		this.startCoord = startCoord;
		this.endCoord = endCoord;
		this.name = name;
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
}
