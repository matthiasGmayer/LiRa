package gameStates;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import gui.Button;
import gui.CheckBox;
import gui.Panel;
import gui.Text;
import main.App;
import settings.Graphic;
import tools.Loader;
import util.ButtonAction;

public class GraphicSettings extends BasicState {

	Image image;

	int textX = -100;
	int x = 100;
	int y = -4;
	int spaceing = 50;

	private boolean vsync, antialiasing, fullscreen;
	private int width, height, multiSamples;

	private Image[] arrowButtons = { Loader.loadImage("!Menu/arrowButtonPressedLeft"),
			Loader.loadImage("!Menu/arrowButtonReleasedLeft"), Loader.loadImage("!Menu/arrowButtonPressedRight"),
			Loader.loadImage("!Menu/arrowButtonReleasedRight") };

	private List<String> resolutions = Arrays.asList("1080x720", "1280x800", "1280x1024", "1366x768", "1440x900",
			"1600x900", "1920x1080");

	Text resolution;
	Text multisample;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		create(new Panel(

				new Text("VSync", Color.black, new Vector2f(textX, y * spaceing)),
				new CheckBox(b -> vsync = b, (source, delta) -> {
				}, new Vector2f(x, y++ * spaceing), Graphic.vSync),

				new Text("AntiAliasing", Color.black, new Vector2f(textX, y * spaceing)),
				new CheckBox(b -> antialiasing = b, (source, delta) -> {
				}, new Vector2f(x, y++ * spaceing), Graphic.antialiasing),

				new Text(getLanguage().fullcreen, Color.black, new Vector2f(textX, y * spaceing)),
				new CheckBox(b -> fullscreen = b, (source, delta) -> {
				}, new Vector2f(x, y++ * spaceing), Graphic.fullscreen),

				new Text(getLanguage().resolution, Color.black, new Vector2f(textX, y * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						resolution.setContent(nextResolution(i -> --i));
						ButtonAction.super.onRelease(source);
					}
				}, "", Color.white, new Vector2f(x - 100, y * spaceing), 0.5f, arrowButtons[0], arrowButtons[1]),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						resolution.setContent(nextResolution(i -> ++i));
						ButtonAction.super.onRelease(source);
					}
				}, "", Color.white, new Vector2f(x + 100, y * spaceing), 0.5f, arrowButtons[2], arrowButtons[3]),

				resolution = new Text(nextResolution(i -> i), Color.black, new Vector2f(x, y++ * spaceing)),

				new Text("Multi Samples", Color.black, new Vector2f(textX, ++y * spaceing)),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						multisample.setContent(Integer.toString(nextMultiSample(i -> i - 2)));
						ButtonAction.super.onRelease(source);
					}
				}, "", Color.white, new Vector2f(x - 100, y * spaceing), 0.5f, arrowButtons[0], arrowButtons[1]),

				new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						multisample.setContent(Integer.toString(nextMultiSample(i -> i + 2)));
						ButtonAction.super.onRelease(source);
					}
				}, "", Color.white, new Vector2f(x + 100, y * spaceing), 0.5f, arrowButtons[2], arrowButtons[3]),

				multisample = new Text(Integer.toString(Graphic.multiSamples), Color.black,
						new Vector2f(x, y++ * spaceing)),

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
				}, getLanguage().apply, Color.black, new Vector2f(100, 250))));
		super.init(gc, sbg);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		antialiasing = Graphic.antialiasing;
		fullscreen = Graphic.fullscreen;
		vsync = Graphic.vSync;
		width = Graphic.width;
		height = Graphic.height;
		multiSamples = Graphic.multiSamples;

		resolution.setContent(resolutions.stream()
				.filter(s -> width == Integer.parseInt(s.split("x")[0]) && height == Integer.parseInt(s.split("x")[1]))
				.findAny().orElse("0"));

		super.enter(container, game);
	}

	private void save() {
		String[] s = resolution.getContent().split("x");
		width = Integer.parseInt(s[0]);
		height = Integer.parseInt(s[1]);
		Graphic.antialiasing = antialiasing;
		Graphic.fullscreen = fullscreen;
		Graphic.vSync = vsync;
		Graphic.width = width;
		Graphic.height = height;
		Graphic.multiSamples = multiSamples;

		Graphic.save();

		App.app.setMultiSample(multiSamples);
		App.app.setVSync(vsync);
		try {
			App.app.setDisplayMode(width, height, fullscreen);
			App.app.reinit();
		} catch (SlickException e) {

			e.printStackTrace();
		}
	}

	int h;

	private String nextResolution(Function<Integer, Integer> f) {
		h = ((f.apply(h) % resolutions.size()) + resolutions.size()) % resolutions.size();

		String s = resolutions.get(h);
		String[] s2 = s.split("x");
		if (Integer.parseInt(s2[0]) > Graphic.screenSize.getWidth()
				|| Integer.parseInt(s2[1]) > Graphic.screenSize.getHeight())
			return nextResolution(f);
		return resolutions.get(h);
	}

	private int nextMultiSample(Function<Integer, Integer> f) {
		return multiSamples = ((f.apply(multiSamples) % 18) + 18) % 18;
	}
}
