package tools;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Parser {
	public static String imageToPath(Image image) {
		return image.toString().split(" ")[1];
	}

	public static String colorToHex(Color color) {

		String s = "";
		s += toHex(color.getRed());
		s += toHex(color.getGreen());
		s += toHex(color.getBlue());
		return s;
	}

	private static String toHex(int i) {
		String s = Integer.toHexString(i);
		if (s.length() < 2)
			return "0" + s;
		return s;
	}

	public static Color hexToColor(String hex) {
		if (hex.startsWith("#"))
			hex = hex.substring(1);
		return new Color(Integer.parseInt(hex.substring(0, 1), 16), Integer.parseInt(hex.substring(2, 3), 16),
				Integer.parseInt(hex.substring(4, 5), 16));
	}

	public static <T extends Number> String toString(T n) {
		if (n == null)
			return "n";
		return Float.toString(n.floatValue());
	}

	public static String toString(Boolean b) {
		if (b == null)
			return null;
		if (b)
			return "t";
		return "f";
	}

	public static String toString(Vector2f v, int p) {
		if (v == null)
			return "n";
		if (p == 0)
			return toString(v.x);
		return toString(v.y);
	}

	public static Float toFloat(String s) {
		s = s.toLowerCase();
		if (nullcheck(s))
			return null;
		return Float.parseFloat(s);
	}

	public static Boolean toBoolean(String s) {
		s = s.toLowerCase();
		switch (s) {
		case "t":
		case "true":
			return Boolean.valueOf(true);
		case "f":
		case "false":
			return Boolean.valueOf(false);
		default:
			return null;
		}
	}

	public static Vector2f toVector2f(String s, String s2) {
		if (nullcheck(s, s2))
			return null;
		return new Vector2f(toFloat(s), toFloat(s2));
	}

	public static Color toColor(String s, String s2, String s3, String s4) {
		if (nullcheck(s, s2, s3, s4))
			return null;
		return new Color(toFloat(s), toFloat(s2), toFloat(s3), toFloat(s4));
	}

	public static Color toColor(String s, String s2, String s3) {
		return toColor(s, s2, s3, "1");
	}

	public static String[] toStrings(Float... floats) {
		String[] s = new String[floats.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = toString(floats[i]);
		}
		return s;
	}

	private static boolean nullcheck(String s) {
		return s.equals("null") || s.equals("n");
	}

	private static boolean nullcheck(String... strings) {
		for (String string : strings) {
			if (nullcheck(string))
				return true;
		}
		return false;
	}

}
