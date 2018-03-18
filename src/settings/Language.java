package settings;

import tools.CSV;

public class Language {

	public static String standardDirectory = "language/";

	public final String play, levelEditor, settings, quit, campaign, customlevels, menu, controls, graphics, resolution,
			fullcreen, apply, cancel, open, close, language, movable, rotatable, link, unlink, strength, rotation,
			position, size, colors, delete, load, save, override, yes, no, languageName;

	public Language(String dir) {
		this(new CSV(dir.replace("!", standardDirectory)));
	}

	public Language(CSV csv) {
		play = csv.get("play", 0, 1);
		levelEditor = csv.get("levelEditor", 0, 1);
		settings = csv.get("settings", 0, 1);
		quit = csv.get("quit", 0, 1);
		campaign = csv.get("campaign", 0, 1);
		customlevels = csv.get("customlevels", 0, 1);
		menu = csv.get("menu", 0, 1);
		controls = csv.get("controls", 0, 1);
		graphics = csv.get("graphics", 0, 1);
		resolution = csv.get("resolution", 0, 1);
		fullcreen = csv.get("fullcreen", 0, 1);
		apply = csv.get("apply", 0, 1);
		cancel = csv.get("cancel", 0, 1);
		open = csv.get("open", 0, 1);
		close = csv.get("close", 0, 1);
		language = csv.get("language", 0, 1);
		movable = csv.get("movable", 0, 1);
		rotatable = csv.get("rotatable", 0, 1);
		link = csv.get("link", 0, 1);
		unlink = csv.get("unlink", 0, 1);
		languageName = csv.get("languageName", 0, 1);
		colors = csv.get("colors", 0, 1);
		delete = csv.get("delete", 0, 1);
		load = csv.get("load", 0, 1);
		yes = csv.get("yes", 0, 1);
		no = csv.get("no", 0, 1);
		override = csv.get("override", 0, 1);
		position = csv.get("position", 0, 1);
		rotation = csv.get("rotation", 0, 1);
		save = csv.get("save", 0, 1);
		size = csv.get("size", 0, 1);
		strength = csv.get("strength", 0, 1);

	}

}
