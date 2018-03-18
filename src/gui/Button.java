package gui;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Vector2f;

import entites.IPositionable;
import entites.IScalable;
import entites.IUpdatable;
import renderer.Camera;
import renderer.IRenderable;
import settings.Actions;
import settings.Controlls;
import tools.Loader;
import tools.Tools;
import util.ButtonAction;

public class Button implements IPositionable, IScalable, IUpdatable, IRenderable {

	private static Image standardPressedImage, standardReleasedImage;
	static {
		standardPressedImage = Loader.loadImage("!standardButtonPressed");
		standardReleasedImage = Loader.loadImage("!standardButtonReleased");
	}

	private ButtonAction onClick;
	private Vector2f position;
	private float size;
	private Image pressedImage, releasedImage;
	private boolean pressed;
	protected String content;
	protected UnicodeFont font = Fonts.defaultFont;
	protected Color fontColor;

	public Button(ButtonAction onClick, String content, Color stringColor, Vector2f position, float size,
			Image pressedImage, Image releasedImage) {
		super();
		this.onClick = onClick;
		this.position = position;
		this.size = size;
		this.pressedImage = pressedImage;
		this.releasedImage = releasedImage;
		this.content = content;
		this.fontColor = stringColor;

	}

	public Button(ButtonAction onClick, String content, Color stringColor, Vector2f position, float size) {
		this(onClick, content, stringColor, position, size, standardPressedImage, standardReleasedImage);
	}

	public Button(ButtonAction onClick, String content, Color stringColor, Vector2f position) {
		this(onClick, content, stringColor, position, 1);
	}

	public Button(ButtonAction onClick, String content, Color stringColor) {
		this(onClick, content, stringColor, new Vector2f());
	}

	public Button(ButtonAction onClick, String content) {
		this(onClick, content, Color.white, new Vector2f());
	}

	@Override
	public void render(Camera camera, Graphics g) {
		g.setFont(font);
		if (!(pressedImage == null || releasedImage == null)) {

			Image image;
			if (pressed)
				image = pressedImage.getScaledCopy(size);
			else
				image = releasedImage.getScaledCopy(size);
			image.drawCentered(position.x, position.y);
		}
		g.setColor(fontColor);
		int width = font.getWidth(content);
		int height = font.getHeight(content);
		g.drawString(content, position.x - width / 2, position.y - height / 2);
	}

	private final int doubleClickTime = 250;
	private int time;
	private boolean mousePressed, entered, leaved;

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {

		time += delta;
		Vector2f v = camera.screenToWorldPoint(Actions.mousePosition);
		boolean onButton = Tools.isPointInRectangle(v, position, getApparentSize(IRenderable.Direction.width),
				getApparentSize(IRenderable.Direction.height), 0);

		if (Actions.is(Controlls.leftMouse)) {
			if (onButton)
				if (!pressed && !mousePressed) {
					pressed = true;

					if (time < doubleClickTime)
						onClick.onDoublePress(this);
					else {
						onClick.onPress(this);
						time = 0;
					}

				} else {
					onClick.onDeselect(this);
				}

			mousePressed = true;
		} else {
			if (onButton && pressed) {
				onClick.onRelease(this);
			}
			pressed = false;
			mousePressed = false;
		}
		if (onButton) {
			if (!entered) {
				onClick.onEnter(this);
				entered = true;
			}
			leaved = false;
		} else {
			if (!leaved) {
				onClick.onLeave(this);
				leaved = true;
			}
			entered = false;
		}
	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector2f position) {
		this.position = position;
	}

	@Override
	public float getScale() {
		return size;
	}

	@Override
	public void setScale(float size) {
		this.size = size;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isPressed() {
		return pressed;
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return pressedImage.getWidth() * size;
		return pressedImage.getHeight() * size;
	}

}
