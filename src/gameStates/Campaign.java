package gameStates;

import java.io.File;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.Panel;
import levels.Level;
import levels.LevelProgress;
import tools.CSV;
import tools.Loader;
import util.ButtonAction;

public class Campaign extends BasicState {

	Panel p;
	int i = 0;
	int spacing = 68;
	int width = 10, height = 5;

	Image[] field = {

			Loader.loadImage("!Menu/campaignButtonPressedUndone"),
			Loader.loadImage("!Menu/campaignButtonReleasedUndone"), Loader.loadImage("!Menu/campaignButtonPressedDone"),
			Loader.loadImage("!Menu/campaignButtonReleasedDone") };

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		create(p = new Panel(new Button(new ButtonAction() {
			@Override
			public void onRelease(Object source) {
				enterState(sbg, Menu.class);
				ButtonAction.super.onRelease(source);
			}
		}, getLanguage().menu, Color.black, new Vector2f(400, 250))

		));

		File f = new File("campaign");
		File[] files = f.listFiles();
		for (File file : files) {
			Level l = new Level(new CSV(file));
			boolean b = LevelProgress.isLevelDone(l);
			Image image = b ? field[2] : field[0];
			Image image1 = b ? field[3] : field[1];
			p.add(new Button(new ButtonAction() {
				@Override
				public void onRelease(Object source) {
					StateHandler.getState(Game.class).setLevel(l);
					StateHandler.enterState(sbg, Game.class);
					ButtonAction.super.onRelease(source);
				}
			}, Integer.toString(i + 1), Color.black, new Vector2f((i % width) * spacing - spacing * width / 2,
					i++ / width * spacing - spacing * height / 2), 1f, image, image1));
		}
		super.init(gc, sbg);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		i = 0;
		remove(p);
		init(container, game);
		super.enter(container, game);
	}
}
