package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import gameStates.BasicState;
import gameStates.Campaign;
import gameStates.ControlSettings;
import gameStates.CustomLevels;
import gameStates.Edit;
import gameStates.Game;
import gameStates.GraphicSettings;
import gameStates.Languages;
import gameStates.Menu;
import gameStates.OnlineLevels;
import gameStates.Select;
import gameStates.Settings;
import gameStates.StateHandler;
import settings.Actions;
import settings.Graphic;
import settings.Language;
import tools.CSV;

public class App extends StateBasedGame {

	public static AppGameContainer app;

	public static Language currentLanguage = new Language("!" + new CSV("!language").get(0, 0));

	public static List<Class<? extends BasicState>> stateList = Arrays.asList(Campaign.class, ControlSettings.class,
			CustomLevels.class, Edit.class, Game.class, GraphicSettings.class, Menu.class, Select.class, Settings.class,
			Languages.class, OnlineLevels.class);

	public App(String name) {
		super(name);
	}

	public static void main(String[] args) {

		//if not in eclipse, print to file
		if (System.getenv("inEclipse") == null) {
			new File("logs").mkdirs();
			File f = new File("logs/log.txt");
			File f2 = new File("logs/logErr.txt");
			try {
				f.createNewFile();
				f2.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				System.setOut(new PrintStream(f));
				System.setErr(new PrintStream(f2));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		while (start()) {
			continue;
		}
		app.exit();
	}

	public final Class<? extends BasicState> startingClass = Menu.class;

	public static boolean start() {
		try {
			app = new AppGameContainer(new App("LiRa"));
			String[] icons = { "resources/images/Menu/icon64.png", "resources/images/Menu/icon32.png" };
			app.setIcons(icons);
			try {
				app.setDisplayMode(Graphic.width, Graphic.height, Graphic.fullscreen);
			} catch (SlickException e) {
				return true;
			}

			app.setVSync(Graphic.vSync);
			app.setMultiSample(Graphic.multiSamples);
			app.setShowFPS(false);
			app.start();

		} catch (SlickException e) {
			e.printStackTrace();
			return false;
		}
		return false;

	}

	@Override
	public void keyPressed(int key, char c) {
		Actions.setPressed(key, true);
		super.keyPressed(key, c);
	}

	@Override
	public void keyReleased(int key, char c) {
		Actions.setPressed(key, false);
		super.keyReleased(key, c);
	}

	@Override
	public void mouseWheelMoved(int wheelAmount) {
		Actions.addWheelAmount(wheelAmount);
		super.mouseWheelMoved(wheelAmount);
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int x, int y) {
		Actions.mousePosition.set(x, y);
		super.mouseMoved(oldx, oldy, x, y);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		Actions.mousePosition.set(x, y);
		Actions.setPressed(button - 3, true);
		super.mousePressed(button, x, y);
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		Actions.mousePosition.set(x, y);
		Actions.setPressed(button - 3, false);
		super.mouseReleased(button, x, y);
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int x, int y) {
		Actions.mousePosition.set(x, y);
		super.mouseDragged(oldx, oldy, x, x);
	}

	@Override
	public void initStatesList(GameContainer g) throws SlickException {

		for (Class<? extends BasicState> class1 : stateList) {
			try {
				addState(((BasicState) class1.newInstance()));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		StateHandler.enterState(this, startingClass);
		System.out.println("states inited");
		System.out.println(StateHandler.getCurrentState());
	}

	public static void exit() {
		app.exit();
	}

	public static void reinit() {
		try {
			StateHandler.clear();
			app.reinit();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
