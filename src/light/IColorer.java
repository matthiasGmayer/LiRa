package light;

import java.util.ArrayList;

import org.newdawn.slick.Color;

public interface IColorer extends IIntersectable {
	public Color setColors(ArrayList<LightPoint> lightPoints);
}
