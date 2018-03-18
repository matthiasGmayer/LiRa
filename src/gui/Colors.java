package gui;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public class Colors {
	public static final ArrayList<Color> colors = new ArrayList<Color>();
	static {
		colors.add(Color.blue);
		colors.add(Color.cyan);
		colors.add(Color.green);
		// orange
		colors.add(new Color(1, 0.75f, 0));
		colors.add(Color.yellow);
		colors.add(Color.red);
		colors.add(Color.magenta);
		// pink
		colors.add(new Color(1, 0.75f, 0.75f));
		colors.add(Color.gray);
		colors.add(Color.white);
	}

	public static Color getNearestColor(Color c) {
		float lowest = 1000f;
		Color lowestC = null;
		for (Color c2 : colors) {
			float f = getColorDif(c, c2);
			if (lowest > f) {
				lowest = f;
				lowestC = c2;
			}
		}
		return lowestC;
	}

	private static float getColorDif(Color c, Color c2) {
		return getColorSum(c) - getColorSum(c2);
	}

	private static float getColorSum(Color c) {
		return c.r + c.g + c.b;
	}
}
