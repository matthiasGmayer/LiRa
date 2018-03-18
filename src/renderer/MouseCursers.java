package renderer;

import org.newdawn.slick.Image;

import tools.Loader;

public class MouseCursers {
	public static Image move;
	public static Image turn;
	public static Image standard;

	static {
		move = Loader.loadImage("!mouseCursors/move");
		// turn = Loader.loadImage("!mouseCursors/turn");
		// standard = Loader.loadImage("!mouseCursors/standard");
	}
}
