package pro.parseq.ghop.entities;

import pro.parseq.ghop.utils.GenomicCoordinate;

public interface Band {

	Track getTrack();

	GenomicCoordinate getStartCoord();

	GenomicCoordinate getEndCoord();

	String getName();
}
