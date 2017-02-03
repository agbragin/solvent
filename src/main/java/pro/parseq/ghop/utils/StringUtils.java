package pro.parseq.ghop.utils;

public class StringUtils {

	public static final String underscored(String s) {
		return s.replaceAll(" ", "_");
	}

	public static final String enumValueString(String s) {
		return underscored(s).toUpperCase();
	}
}
