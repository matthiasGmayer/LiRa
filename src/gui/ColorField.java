package gui;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import entities.IColorable;
import entities.IUpdatable;
import renderer.Camera;
import renderer.IRenderable;
import settings.Actions;
import settings.Controls;
import tools.Tools;
import util.SelectAction;
import util.UpdateAction;

public class ColorField implements IRenderable, IUpdatable, IColorable {
	Vector2f position;
	float size;
	boolean pressed, mousePressed, checked;
	SelectAction onSelect;
	UpdateAction onUpdate;
	Color color;

	public ColorField(SelectAction onCheck, UpdateAction onUpdate, Vector2f position, float size, Color color) {
		super();
		this.onUpdate = onUpdate;
		this.onSelect = onCheck;
		this.position = position;
		this.color = color;
		this.size = size;
	}

	public ColorField(SelectAction onCheck, Vector2f position, float size, Color color) {
		this(onCheck, new UpdateAction() {

			@Override
			public void onUpdate(Object source, int delta) {

			}
		}, position, size, color);
	}

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {

		onUpdate.onUpdate(this, delta);

		Vector2f v = camera.screenToWorldPoint(Actions.mousePosition);
		boolean onButton = Tools.isPointInRectangle(v, position, getApparentSize(IRenderable.Direction.width),
				getApparentSize(IRenderable.Direction.height), 0);

		if (Actions.is(Controls.leftMouse)) {
			if (onButton)
				if (!pressed && !mousePressed) {
					pressed = true;

					onSelect.onSelect(this);

				}

			mousePressed = true;
		} else {
			pressed = false;
			mousePressed = false;
		}
	}

	@Override
	public void render(Camera camera, Graphics g) {
		float width = getApparentSize(IRenderable.Direction.width);
		float height = getApparentSize(IRenderable.Direction.height);
		g.setColor(color);
		g.fillRect(position.x - width / 2, position.y - height / 2, width, height);
		g.setColor(Color.black);
		g.drawRect(position.x - width / 2, position.y - height / 2, width, height);
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		return size;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
}
