package gameStates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import gui.Picture;
import main.App;
import renderer.IRenderable.Direction;
import settings.Graphic;
import tools.Loader;
import util.ButtonAction;

public class Menu extends BasicState {

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

		States.menu = this;

		Image lira = Loader.loadImage("!Menu/LiRa");

		int buttonHeight = (int) new Button(null, null, null).getApparentSize(Direction.height);
		int spaceing = buttonHeight + 10;
		float y = ((lira.getHeight() - Graphic.height / 2) + 75f) / spaceing;
		System.out.println(lira.getHeight() + " " + y);
		create(new Panel(

				new Picture(new Vector2f(0, -Graphic.height / 2 + lira.getHeight() / 2), lira),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						enterState(sbg, Select.class);
					}
				}, getLanguage().play, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						enterState(sbg, Edit.class);
					}
				}, getLanguage().levelEditor, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						ButtonAction.super.onRelease(source);
						enterState(sbg, Settings.class);
					}
				}, getLanguage().settings, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						App.exit();
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().quit, Color.black, new Vector2f(0, y++ * spaceing))
//				,
				
//				new StringWriter("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", new Vector2f(), 10, Color.black)

		));
	}
}
