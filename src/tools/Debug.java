package tools;

import org.newdawn.slick.Graphics;

import settings.Graphic;

public class Debug {

	public static String[] debugs = new String[10];

	private static final int spaceing = 10;
	private static final int xPos = Graphic.width - 100;

	public static boolean bool;

	public static void draw(Graphics g) {

		int y = 1;
		for (String s : debugs) {
			if (s != null)
				g.drawString(s, xPos, y++ * spaceing);
		}
	}

}
