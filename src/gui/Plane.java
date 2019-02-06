package gui;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import entities.IUpdatable;
import renderer.Camera;
import renderer.IRenderable;
import settings.Actions;
import settings.Controls;
import util.ButtonAction;

public class Plane implements IRenderable, IUpdatable {
	private Vector2f position;
	private Color color;
	private Vector2f dimensions;
	private ButtonAction onClick;

	public Plane(ButtonAction onClick, Vector2f position, Vector2f dimensions, Color color) {
		super();
		this.onClick = onClick;
		this.position = position;
		this.dimensions = dimensions;
		this.color = color;
	}

	public Plane(Vector2f position, Vector2f dimensions, Color color) {
		this(new ButtonAction() {
		}, position, dimensions, color);
	}

	private final int doubleClickTime = 250;
	private int time;
	private boolean pressed, mousePressed, entered, leaved;

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {

		time += delta;
		Vector2f v = camera.screenToWorldPoint(Actions.mousePosition);
		boolean onButton = v.x >= Math.min(position.x + dimensions.x, position.x)
				&& v.x <= Math.max(position.x + dimensions.x, position.x)
				&& v.y >= Math.min(position.y + dimensions.y, position.y)
				&& v.y <= Math.max(position.y + dimensions.y, position.y);

		if (Actions.is(Controls.leftMouse)) {
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
	public void render(Camera camera, Graphics g) {
		g.setColor(color);
		g.fillRect(position.x, position.y, dimensions.x, dimensions.y);
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return dimensions.x;
		return dimensions.y;
	}

}
