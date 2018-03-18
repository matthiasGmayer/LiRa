package light;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

import tools.Tools;

public class Frame extends Vector2f implements ISolid, IIntersectable {

	private static final long serialVersionUID = 1L;

	float x;
	float y;
	float width;
	float height;

	public Frame(float x, float y, float width, float height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void addIntersections(Vector2f emittingPoint, Vector2f rayPoint, ArrayList<Intersection> intersections) {

		Vector2f v1 = new Vector2f(x, y);
		Vector2f v2 = new Vector2f(x + width, y);
		Vector2f v3 = new Vector2f(x + width, y + height);
		Vector2f v4 = new Vector2f(x, y + height);

		addIntersection(emittingPoint, rayPoint, v1, v2, intersections);
		addIntersection(emittingPoint, rayPoint, v2, v3, intersections);
		addIntersection(emittingPoint, rayPoint, v3, v4, intersections);
		addIntersection(emittingPoint, rayPoint, v4, v1, intersections);
	}

	private void addIntersection(Vector2f emittingPoint, Vector2f rayPoint, Vector2f v1, Vector2f v2,
			ArrayList<Intersection> intersections) {

		Vector2f intersection = Tools.getIntersectionPoint(emittingPoint, rayPoint, v1, v2);
		if (intersection != null) {
			Intersection i = new Intersection();
			i.set(intersection);
			i.intersectedObject = this;
			intersections.add(i);
		}
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

}
