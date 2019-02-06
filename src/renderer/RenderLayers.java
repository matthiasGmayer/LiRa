package renderer;

import java.util.HashMap;

import effects.Glow;
import entities.Activator;
import entities.ColoredGlass;
import entities.Goal;
import entities.LightEmitter;
import entities.Mirror;
import entities.Wall;
import gui.Button;
import gui.CheckBox;
import gui.ColorField;
import gui.Panel;
import gui.Plane;
import gui.StringWriter;
import gui.Text;
import gui.TextField;

public class RenderLayers {
	public static HashMap<Class<? extends IRenderable>, Integer> renderLayers = new HashMap<Class<? extends IRenderable>, Integer>();

	static {
		add(Camera.class);

		// gameObjects
		add(Activator.class);
		add(Wall.class);
		add(Goal.class);
		add(Mirror.class);
		add(ColoredGlass.class);
		add(LightEmitter.class);
		add(Glow.class);

		// gui
		add(Panel.class);
		add(Plane.class);
		add(Text.class);
		add(CheckBox.class);
		add(ColorField.class);
		add(TextField.class);
		add(Button.class);
		add(StringWriter.class);
	}

	private static int h = 0;

	private static void add(Class<? extends IRenderable> c) {
		renderLayers.put(c, h++);
	}
}
