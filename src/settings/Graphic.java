package settings;

import java.awt.Dimension;
import java.awt.Toolkit;

import tools.CSV;

public class Graphic {
	
	private static CSV g;
	
	static {
		g = new CSV("!graphics");
		String firstStart = g.get("firststart", 0, 1);
		if (firstStart == null || Boolean.parseBoolean(firstStart)) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			width = (int) screenSize.getWidth();
			height = (int) screenSize.getHeight();
			fullscreen = true;
			vSync = true;
			antialiasing = true;
			multiSamples = 16;	
			save();

		} else {

			width = Integer.parseInt(g.get("resolution", 0, 1));
			height = Integer.parseInt(g.get("resolution", 0, 2));
			fullscreen = Boolean.parseBoolean(g.get("fullscreen", 0, 1));
			vSync = Boolean.parseBoolean(g.get("vsync", 0, 1));
			antialiasing = Boolean.parseBoolean(g.get("antialiasing", 0, 1));
			multiSamples = Integer.parseInt(g.get("multisamples", 0, 1));
		}

	}
	
	public static int width, height, multiSamples;
	public static boolean fullscreen, vSync, antialiasing;
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	
	
	
	public static void save() {
		g = new CSV();
		g.setPath("!graphics");
		g.add("fullscreen", Boolean.toString(fullscreen));
		g.add("vsync", Boolean.toString(vSync));
		g.add("antialiasing", Boolean.toString(antialiasing));
		g.add("resolution", Integer.toString(width), Integer.toString(height));
		g.add("multisamples", Integer.toString(multiSamples));
		g.add("firststart", Boolean.toString(false));
		g.write();
	}

}
