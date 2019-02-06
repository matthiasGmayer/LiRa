package gameStates;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import gui.Text;
import settings.Actions;
import settings.Controls;
import settings.Key;
import util.ButtonAction;

public class ControlSettings extends BasicState {

	int spacing = 110;
	int y = -1;
	int buttonX1 = 0;
	int buttonX2 = 200;
	int textX = -200;

	boolean linking;
	Button b;
	Panel p;
	Controls controls;
	int place;
	HashMap<Controls, Key[]> keyMap = new HashMap<>();

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		create(p = new Panel());
		p.add(new Text(getLanguage().camera, Color.black, new Vector2f(buttonX1, y++ * spacing)));
		createKeys(getLanguage().cameraUp, Controls.cameraUp);
		createKeys(getLanguage().cameraDown, Controls.cameraDown);
		createKeys(getLanguage().cameraLeft, Controls.cameraLeft);
		createKeys(getLanguage().cameraRight, Controls.cameraRight);
		p.add(new Text(getLanguage().levelEditor, Color.black, new Vector2f(buttonX1, y++ * spacing)));
		createKeys(getLanguage().copy, Controls.copy);
		createKeys(getLanguage().limitMovement, Controls.limitMovement);
		createKeys(getLanguage().delete, Controls.delete);
		createKeys(getLanguage().link, Controls.link);

		create(new Panel(new Button(new ButtonAction() {
			@Override
			public void onRelease(Object source) {
				enterState(sbg, Menu.class);
				ButtonAction.super.onRelease(source);
			}
		}, getLanguage().cancel, Color.black, new Vector2f(400, spacing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						save();
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().apply, Color.black, new Vector2f(400, 2 * spacing))

		));
		super.init(gc, sbg);

	}

	private void createKeys(String s, Controls c) {
		Key[] k = Actions.getIds(c);
		p.add(new Text(s, Color.black, new Vector2f(textX, y * spacing)),
				createKeyButton(s, buttonX1, c, Actions.getName(k[0]), 0),
				createKeyButton(s, buttonX2, c, Actions.getName(k[1]), 1));
		y++;
	}

	private Button createKeyButton(String s, int x, Controls c, String name, int p) {
		return new Button(new ButtonAction() {
			@Override
			public void onRelease(Object source) {
				linking = true;
				b = (Button) source;
				controls = c;
				place = p;
				b.setContent(". . .");
				ButtonAction.super.onRelease(source);
			}
		}, name, Color.black, new Vector2f(x, y * spacing));
	}

	float offset;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		float wheel = Actions.getWheelAmount();
		float panelY = (float) (Math.round(wheel / spacing) * spacing);

		if (offset + panelY > 0) {
			offset = -panelY;
		}
		if (offset + panelY < (-y + 3) * spacing) {
			offset = (-y + 3) * spacing - panelY;
		}

		p.setTargetPosition(new Vector2f(0, offset + panelY));
		super.update(gc, sbg, delta);
	}

	public void save() {
		Actions.save();
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		Actions.reload();
		super.leave(container, game);
	}

	@Override
	public void keyPressed(int key, char c) {
		if (linking) {
			linking = false;
			String s = Input.getKeyName(key);
			boolean none = s.equals("ESCAPE");
			if (none)
				b.setContent("NONE");
			else {
				b.setContent(s);
			}
			;
			Key k = Actions.getIds(controls)[place];
			k.setPressed(false);
			if (none)
				k.setID(-4);
			else
				k.setID(key);

		}
		super.keyPressed(key, c);
	}
}
