package pro.parseq.ghop.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pro.parseq.ghop.entities.Track;

public class TrackSettings {

	private Map<Track, Filters> trackFilters = new HashMap<>();

	// TODO: add layer correlations

	public TrackSettings() {}

	public Set<Track> getTracks() {
		return trackFilters.keySet();
	}

	public Filters getTrackFilters(Track track) {
		return trackFilters.get(track);
	}

	public Filters setTrackFilters(Track track, Filters filters) {
		return trackFilters.put(track, filters);
	}

	public Filters addTrack(Track track) {
		return setTrackFilters(track, null);
	}

	public Filters removeLayer(Track track) {
		return trackFilters.remove(track);
	}

	public Filters clearTrackFilters(Track track) {
		return setTrackFilters(track, null);
	}
}
