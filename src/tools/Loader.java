package tools;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Loader {
	public static final String defaultPath = "resources/";

	public static final String defaultImagePath = "images/";

	// ! will be replaced by the default path

	public static Image loadImage(String path) {
		path = path.replace("!", defaultPath + defaultImagePath);
		if (!path.contains("."))
			path += ".png";
		try {
			Image image = new Image(path);
			image.setFilter(Image.FILTER_LINEAR);
			System.out.println(path + " loaded");
			return image;
		} catch (SlickException e) {
			e.printStackTrace();
			System.err.println("IMAGE NOT FOUND: " + path);
		}
		return null;
	}
}
