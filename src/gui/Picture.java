package gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import util.ButtonAction;

public class Picture extends Button {

	public Picture(ButtonAction onClick, Vector2f position, float size, Image image) {
		super(onClick, "", Color.white, position, size, image, image);
	}

	public Picture(ButtonAction onClick, Vector2f position, Image image) {
		this(onClick, position, 1, image);
	}

	public Picture(Vector2f position, float size, Image image) {
		this(new ButtonAction() {
		}, position, size, image);
	}

	public Picture(Vector2f position, Image image) {
		this(position, 1, image);
	}

}
