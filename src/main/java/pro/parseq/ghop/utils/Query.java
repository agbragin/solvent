package pro.parseq.ghop.utils;

import java.util.Set;

import pro.parseq.ghop.entities.Track;

/**
 * Data source query for objects class
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class Query {

	// Bearing genomic coordinate
	private final GenomicCoordinate coord;
	// Left borders count (positive, 0-based)
	private final int left;
	// Right borders count (positive, 0-based)
	private final int right;
	// Encapsulates track filters and correlations
	private final TrackSettings trackSettings;

	public Query(GenomicCoordinate coord, int left, int right, Set<Track> tracks) {

		this.coord = coord;
		this.left = left;
		this.right = right;

		trackSettings = new TrackSettings();
		tracks.stream().forEach(track -> trackSettings.addTrack(track));
	}

	public Query(GenomicCoordinate coord, int left, int right, TrackSettings trackSettings) {

		this.coord = coord;
		this.left = left;
		this.right = right;
		this.trackSettings = trackSettings;
	}

	public TrackSettings getTrackSettings() {
		return trackSettings;
	}

	public GenomicCoordinate getCoord() {
		return coord;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}
}
