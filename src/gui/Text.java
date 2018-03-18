package gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import renderer.Camera;
import renderer.IRenderable;
import util.ButtonAction;

public class Text extends Button {

	public Text(ButtonAction onClick, String content, Color stringColor, Vector2f position, float size) {
		super(onClick, content, stringColor, position, size, null, null);
	}

	public Text(ButtonAction onClick, String content, Color stringColor, Vector2f position) {
		this(onClick, content, stringColor, position, 1f);
	}

	public Text(ButtonAction onClick, String content, Vector2f position) {
		this(onClick, content, Color.white, position, 1f);
	}

	public Text(String content, Color fontColor, Vector2f position) {
		this(new ButtonAction() {
		}, content, fontColor, position, 1f);
	}

	public Text(String content, Vector2f position) {
		this(content, Color.white, position);
	}

	Graphics graphics;

	@Override
	public void render(Camera camera, Graphics g) {
		graphics = g;
		super.render(camera, g);
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return super.font.getWidth(super.content);
		return super.font.getHeight(super.content);

	}

}
