package light;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

public interface IIntersectable {
	public void addIntersections(Vector2f emittingPoint, Vector2f rayPoint, ArrayList<Intersection> intersections);
}
