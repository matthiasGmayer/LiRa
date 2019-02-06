package gameStates;

import java.io.File;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import main.App;
import renderer.IRenderable.Direction;
import settings.Actions;
import settings.Language;
import tools.CSV;
import util.ButtonAction;

public class Languages extends BasicState {
	Language currentLanguage = App.currentLanguage;
	int buttonHeight = (int) new Button(null, null, null).getApparentSize(Direction.height);
	int y = -1;
	int spacing = buttonHeight + 10;
	Panel p;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

		create(p = new Panel());
		File file = new File("language");
		File[] file2 = file.listFiles();

		for (File file3 : file2) {
			Language l = new Language(new CSV(file3));
			p.add(new Button(new ButtonAction() {
				@Override
				public void onRelease(Object source) {
					currentLanguage = l;
					ButtonAction.super.onRelease(source);
				}
			}, l.languageName, Color.black, new Vector2f(0, y++ * spacing)));
		}

		p.add(new Button(new ButtonAction() {
			@Override
			public void onRelease(Object source) {
				enterState(sbg, Menu.class);
				ButtonAction.super.onRelease(source);
			}
		}, getLanguage().cancel, Color.black, new Vector2f(200, --y * spacing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						save();
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().apply, Color.black, new Vector2f(200, --y * spacing)));

		super.init(gc, sbg);
	}

	float offset;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		float wheel = Actions.getWheelAmount();
		float panelY = (float) (Math.round(wheel / spacing) * spacing);

		if (offset + panelY > 0) {
			offset = -panelY;
		}
		if (offset + panelY < (-y) * spacing) {
			offset = (-y) * spacing - panelY;
		}

		p.setTargetPosition(new Vector2f(0, offset + panelY));
		super.update(gc, sbg, delta);
	}

	private void save() {
		App.currentLanguage = currentLanguage;
		CSV c = new CSV("!language");
		c.set(0, 0, currentLanguage.languageName.toLowerCase());
		c.write();
		App.reinit();
	}
}
