package main;

import java.awt.Dimension;
import java.awt.Toolkit;
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
import gameStates.Controls;
import gameStates.CustomLevels;
import gameStates.Edit;
import gameStates.Game;
import gameStates.GraphicSettings;
import gameStates.Languages;
import gameStates.Menu;
import gameStates.Select;
import gameStates.Settings;
import gameStates.StateHandler;
import settings.Actions;
import settings.Graphic;
import settings.Language;
import tools.CSV;

public class App extends StateBasedGame {

	public static AppGameContainer app;

	public final static Language english = new Language("!english"), deutsch = new Language("!deutsch");
	public static Language currentLanguage = new Language("!"+new CSV("!language").get(0, 0));

	public static List<Class<? extends BasicState>> stateList = Arrays.asList(Campaign.class, Controls.class,
			CustomLevels.class, Edit.class, Game.class, GraphicSettings.class, Menu.class, Select.class,
			Settings.class, Languages.class);

	public App(String name) {
		super(name);
	}

	public static void main(String[] args) {

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
		System.out.println("started");
		CSV graphicSettings = new CSV("!graphics");
		String firstStart = graphicSettings.get("firststart", 0, 1);
		if (firstStart == null || Boolean.parseBoolean(firstStart)) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Graphic.width = (int) screenSize.getWidth();
			Graphic.height = (int) screenSize.getHeight();
			Graphic.fullscreen = true;

			graphicSettings.set("resolution", 0, "resolution", Integer.toString(Graphic.width),
					Integer.toString(Graphic.height));
			graphicSettings.set("firststart", 0, 1, "false");
			graphicSettings.set("fullscreen", 0, 1, "true");
			graphicSettings.set("vSync", 0, 1, "true");
			graphicSettings.set("multisamples", 0, 1, "16");
			graphicSettings.set("antialiasing", 0, 1, "true");
			graphicSettings.write();

		} else {

			Graphic.width = Integer.parseInt(graphicSettings.get("resolution", 0, 1));
			Graphic.height = Integer.parseInt(graphicSettings.get("resolution", 0, 2));
			Graphic.fullscreen = Boolean.parseBoolean(graphicSettings.get("fullscreen", 0, 1));
		}

		Graphic.vSync = Boolean.parseBoolean(graphicSettings.get("vsync", 0, 1));
		Graphic.antialiasing = Boolean.parseBoolean(graphicSettings.get("antialiasing", 0, 1));
		Graphic.multiSamples = Integer.parseInt(graphicSettings.get("multisamples", 0, 1));

		try {
			app = new AppGameContainer(new App("LiRa"));
			System.out.println("app set");
			try {
				app.setDisplayMode(Graphic.width, Graphic.height, Graphic.fullscreen);
			} catch (SlickException e) {
				graphicSettings.set("firststart", 0, 1, "true");
				graphicSettings.write();
				return true;
			}

			app.setVSync(Graphic.vSync);
			app.setMultiSample(Graphic.multiSamples);

			System.out.println("about to start app");
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
	}

	public static void exit() {
		app.exit();
	}
}
