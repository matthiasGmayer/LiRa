package gameStates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import renderer.IRenderable.Direction;
import util.ButtonAction;

public class Settings extends BasicState {

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		int buttonHeight = (int) new Button(null, null, null).getApparentSize(Direction.height);
		int y = -1;
		int spaceing = buttonHeight + 10;
		create(new Panel(

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, Languages.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().language, Color.black, new Vector2f(0, y++ * spaceing)),
				
				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, Controls.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().controls, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, GraphicSettings.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().graphics, Color.black, new Vector2f(0, y++ * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().menu, Color.black, new Vector2f(0, y++ * spaceing))

		));
		super.init(gc, sbg);
	}

}
