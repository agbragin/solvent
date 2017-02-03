package pro.parseq.ghop.utils;

/**
 * Extremely bad stab
 * 
 * TODO: annihilate it as fast as possible!
 */
public class IdGenerationUtils {

	private static long lastAttributeId = 0;
	private static long lastBandId = 0;
	private static long lastDataSourceId = 0;

	public static long generateAttributeId() {
		return lastAttributeId++;
	}

	public static long generateBandId() {
		return lastBandId++;
	}

	public static long generateDataSourceId() {
		return lastDataSourceId++;
	}
}
