package settings;

import java.awt.Dimension;
import java.awt.Toolkit;

import tools.CSV;

public class Graphic {
	public static int width, height, multiSamples;
	public static boolean fullscreen, vSync, antialiasing;

	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static void save() {
		CSV g = new CSV();
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
