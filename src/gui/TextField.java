package gui;

import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import entities.IPositionable;
import entities.IScalable;
import entities.IUpdatable;
import renderer.Camera;
import renderer.IRenderable;
import settings.Actions;
import settings.Controls;
import tools.Tools;
import util.ChangeAction;
import util.KeyListener;
import util.UpdateAction;

public class TextField implements IPositionable, IScalable, IRenderable, IUpdatable, KeyListener {

	private static HashMap<String, String> contentMap = new HashMap<String, String>();
	static {
		contentMap.put("period", ".");
		contentMap.put("minus", "-");
		contentMap.put("space", "");
	}

	private Vector2f position;
	private float size;
	private String content;
	private Font font = Fonts.defaultFont;
	private Color fontColor;
	private ChangeAction onChange;
	private UpdateAction onUpdate;

	private boolean selected, hide;

	public TextField(ChangeAction onChange, UpdateAction onUpdate, String content, Color fontColor, Vector2f position,
			int size, boolean hide) {
		super();
		this.onChange = onChange;
		this.onUpdate = onUpdate;
		this.content = content;
		this.fontColor = fontColor;
		this.position = position;
		this.hide = hide;
		this.size = size;
		Actions.subscribe(this);
	}

	public TextField(ChangeAction onChange, UpdateAction onUpdate, String content, Color fontColor, Vector2f position,
			int size) {
		this(onChange, onUpdate, content, fontColor, position, size, false);
	}
	
	public TextField(ChangeAction onChange, String content, Color fontColor, Vector2f position, int size, boolean hide) {
		this(onChange, new UpdateAction() {

			@Override
			public void onUpdate(Object source, int delta) {

			}
		}, content, fontColor, position, size, hide);
	}

	public TextField(String content, Color fontColor, Vector2f position, int size, boolean hide) {
		this(new ChangeAction() {
			@Override
			public void onChange(Object source) {

			}
		}, content, fontColor, position, size, hide);
	}


	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {
		onUpdate.onUpdate(this, delta);

		blinkTime += delta;
		if (blinkTime > maxBlinkTime) {
			blinkTime -= maxBlinkTime;
			blink = !blink;
		}
		Vector2f v = camera.screenToWorldPoint(Actions.mousePosition);
		boolean onButton = Tools.isPointInRectangle(v, position, getApparentSize(IRenderable.Direction.width),
				getApparentSize(IRenderable.Direction.height), 0);

		if (Actions.is(Controls.leftMouse)) {
			if (onButton) {
				selected = true;
			} else {
				selected = false;
			}
		}

	}

	@Override
	public void keyPressed(int iD) {
		if (selected && Actions.getLastKeyPressed() >= 0) {
			String key = Input.getKeyName((Actions.getLastKeyPressed()));

			if (key.equals("RETURN")) {
				selected = false;
			}
			if (key.equals("BACK")) {
				if (content.length() > 0)
					content = content.substring(0, content.length() - 1);
			}

			if (!(Actions.isPressed(Input.KEY_LSHIFT) || Actions.isPressed(Input.KEY_RSHIFT)))
				key = key.toLowerCase();
			if (key.length() == 1)
				content += key;
			else {
				String s = contentMap.get(key);
				if (s != null) {
					content += s;
				}
			}

			onChange.onChange(this);
		}
	}

	int blinkTime;
	final int maxBlinkTime = 500;
	boolean blink;

	@Override
	public void render(Camera camera, Graphics g) {

		float width = getApparentSize(IRenderable.Direction.width);
		float height = getApparentSize(IRenderable.Direction.height);
		g.setColor(fontColor);
		float presize = g.getLineWidth();
		g.setLineWidth(size);
		g.drawRect(position.x - width / 2, position.y - height / 2, width + 10, height);
		g.setFont(font);
		g.setColor(fontColor);
		
		String text = "";
		if(hide)
			for (int i = 0; i < content.length(); i++) {
				text += "*";
			}
		else
			text = content;
		
		g.drawString(text, position.x - width / 2, position.y - height / 2);
		if (selected && blink) {
			g.fillRect(position.x + width / 2 + 5, position.y - height / 2, 5, height);
		}
		g.setLineWidth(presize);
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return Math.max(50, font.getWidth(content) + 2);
		return Math.max(font.getHeight("A") + 2, font.getHeight(content) + 2);

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


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
