package gui;

import java.awt.Font;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

@SuppressWarnings("unchecked")
public class Fonts {
	static UnicodeFont defaultFont;
	static {
		defaultFont = new UnicodeFont(new Font("Arial", 0, 20));
		defaultFont.getEffects().add(new ColorEffect(java.awt.Color.white));
		defaultFont.addAsciiGlyphs();
		try {
			defaultFont.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
