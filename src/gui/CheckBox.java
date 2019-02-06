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
import tools.Tools;
import util.CheckAction;
import util.UpdateAction;

public class CheckBox implements IUpdatable, IRenderable {

	Vector2f position;
	float size;
	boolean pressed, mousePressed, checked;
	CheckAction onCheck;
	UpdateAction onUpdate;

	public CheckBox(CheckAction onCheck, UpdateAction onUpdate, Vector2f position, float size, boolean checked) {
		super();
		this.onUpdate = onUpdate;
		this.onCheck = onCheck;
		this.position = position;
		this.size = size;
		this.checked = checked;
	}

	public CheckBox(CheckAction onCheck, UpdateAction onUpdate, Vector2f position, float size) {
		this(onCheck, onUpdate, position, size, false);
	}

	public CheckBox(CheckAction onCheck, UpdateAction onUpdate, Vector2f position, boolean checked) {
		this(onCheck, onUpdate, position, 30, checked);
	}

	public CheckBox(CheckAction onCheck, UpdateAction onUpdate, Vector2f position) {
		this(onCheck, onUpdate, position, false);
	}

	public CheckBox(CheckAction onCheck, Vector2f position) {
		this(onCheck, new UpdateAction() {	
			@Override
			public void onUpdate(Object source, int delta) {
				
			}
		}, position);
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
					checked = !checked;
					onCheck.onCheck(checked);
				}

			mousePressed = true;
		} else {
			pressed = false;
			mousePressed = false;
		}
	}

	@Override
	public void render(Camera camera, Graphics g) {
		g.setColor(Color.black);
		float width = getApparentSize(IRenderable.Direction.width);
		float height = getApparentSize(IRenderable.Direction.height);
		g.drawRect(position.x - width / 2, position.y - height / 2, width, height);
		if (checked) {
			g.drawLine(position.x - width / 2, position.y - height / 2, position.x + width / 2,
					position.y + height / 2);
			g.drawLine(position.x - width / 2, position.y + height / 2, position.x + width / 2,
					position.y - height / 2);
		}
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

}
