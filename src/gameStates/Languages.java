package gameStates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import main.App;
import renderer.IRenderable.Direction;
import settings.Language;
import tools.CSV;
import util.ButtonAction;

public class Languages extends BasicState {
	Language currentLanguage = App.currentLanguage;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

		int buttonHeight = (int) new Button(null, null, null).getApparentSize(Direction.height);
		int y = -1;
		int spaceing = buttonHeight + 10;

		create(new Panel(

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						currentLanguage = App.english;
						ButtonAction.super.onRelease(source);
					}
				}, "English", Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						currentLanguage = App.deutsch;
						ButtonAction.super.onRelease(source);
					}
				}, "Deutsch", Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().cancel, Color.black, new Vector2f(-100, 250)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						save();
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().apply, Color.black, new Vector2f(100, 250))

		));

		super.init(gc, sbg);
	}

	private void save() {
		App.currentLanguage = currentLanguage;
		CSV c = new CSV();
		c.add(currentLanguage.languageName);
		try {
			App.app.reinit();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
