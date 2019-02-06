package tools;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class Tools {

	public static Vector2f copyIfPresent(Vector2f v) {
		if (v == null)
			return null;
		return v.copy();
	}

	public static Float copyIfPresent(Float f) {
		if (f == null)
			return null;
		return Float.valueOf(f);
	}

	// get intersection by mathematical function
	public static Vector2f getIntersectionPoint(Vector2f v1, Vector2f v2, Vector2f v3, Vector2f v4) {

		Vector2f intersection;

		if (v1.x > v2.x) {
			Vector2f temp = v1;
			v1 = v2;
			v2 = temp;
		}
		if (v3.x > v4.x) {
			Vector2f temp = v3;
			v3 = v4;
			v4 = temp;
		}
		if (v1.x == v2.x && v3.y == v4.y) {
			intersection = new Vector2f(v1.x, v3.y);
		} else if (v3.x == v4.x && v1.y == v2.y) {
			intersection = new Vector2f(v3.x, v1.y);
		} else if (v1.x == v2.x) {
			double pitch = (v4.y - v3.y) / (v4.x - v3.x);
			double b = v3.y - pitch * v3.x;
			intersection = new Vector2f(v1.x, (float) (pitch * v1.x + b));
		} else if (v3.x == v4.x) {
			double pitch = (v2.y - v1.y) / (v2.x - v1.x);
			double b = v1.y - pitch * v1.x;
			intersection = new Vector2f(v3.x, (float) (pitch * v3.x + b));

		} else if (v1.y == v2.y) {
			double pitch = (v4.y - v3.y) / (v4.x - v3.x);
			double b = v3.y - pitch * v3.x;
			intersection = new Vector2f((float) ((v1.y - b) / pitch), v1.y);
		} else if (v3.y == v4.y) {
			double pitch = (v2.y - v1.y) / (v2.x - v1.x);
			double b = v1.y - pitch * v1.x;
			intersection = new Vector2f((float) ((v3.y - b) / pitch), v3.y);

		} else {
			double pitch1 = (v2.y - v1.y) / (v2.x - v1.x);
			double b1 = v1.y - pitch1 * v1.x;
			double pitch2 = (v4.y - v3.y) / (v4.x - v3.x);
			double b2 = v3.y - pitch2 * v3.x;
			double x = (b2 - b1) / (pitch1 - pitch2);
			intersection = new Vector2f((float) x, (float) (pitch1 * x + b1));
		}

		float x = intersection.x;
		float y = intersection.y;

		float y1 = Math.min(v1.y, v2.y);
		float y2 = Math.max(v1.y, v2.y);
		float y3 = Math.min(v3.y, v4.y);
		float y4 = Math.max(v3.y, v4.y);

		if (x >= v1.x && x <= v2.x && x >= v3.x && x <= v4.x && y >= y1 && y <= y2 && y >= y3 && y <= y4) {
			return intersection;
		} else
			return null;
	}

	public static Vector2f getIntersectionPoint(float x1, float x2, float x3, float x4, float x5, float x6, float x7,
			float x8) {
		return getIntersectionPoint(new Vector2f(x1, x2), new Vector2f(x3, x4), new Vector2f(x5, x6),
				new Vector2f(x7, x8));
	}

	public static double getDistance(Vector2f point1, Vector2f point2) {
		return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
	}

	public static double getDistance(float x1, float y1, float x2, float y2) {
		return getDistance(new Vector2f(x1, y1), new Vector2f(x2, y2));
	}

	public static Vector2f getDistanceVector(Vector2f point1, Vector2f point2) {
		return new Vector2f(point1.x - point2.x, point1.y - point2.y);
	}

	public static Vector2f getDistanceVector(float x1, float y1, float x2, float y2) {
		return getDistanceVector(new Vector2f(x1, y1), new Vector2f(x2, y2));
	}

	public static Vector2f getLineHalfingPoint(Vector2f point1, Vector2f point2) {
		return new Vector2f((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
	}

	public static Vector2f getLineHalfingPoint(float x1, float y1, float x2, float y2) {
		return getLineHalfingPoint(new Vector2f(x1, y1), new Vector2f(x2, y2));
	}

	public static Vector2f getLineDivision(Vector2f point1, float factor1, Vector2f point2, float factor2) {
		return new Vector2f((point1.x * factor1 + point2.x * factor2) / (factor1 + factor2),
				(point1.y * factor1 + point2.y * factor2) / (factor1 + factor2));
	}

	public static Vector2f getOrthogonalPoint(Vector2f linePoint1, Vector2f linePoint2, Vector2f point) {

		if (linePoint1.x > linePoint2.x) {
			Vector2f temp = linePoint1;
			linePoint1 = linePoint2;
			linePoint2 = temp;
		}
		if (linePoint1.x == linePoint2.x) {
			return new Vector2f(linePoint1.x, point.y);

		}
		if (linePoint1.y == linePoint2.y) {
			return new Vector2f(point.x, linePoint1.y);
		}
		double pitch1 = (linePoint2.y - linePoint1.y) / (linePoint2.x - linePoint1.x);
		double b1 = linePoint1.y - pitch1 * linePoint1.x;
		double pitch2 = -1 / pitch1;
		double b2 = point.y - pitch2 * point.x;
		double x = (b2 - b1) / (pitch1 - pitch2);

		Vector2f orthogonalPoint = new Vector2f((float) x, (float) (b2 + pitch2 * x));

		return orthogonalPoint;

	}

	public static Vector2f getOrthogonalPoint(float x, float y, float x1, float y1, float x2, float y2) {
		return getOrthogonalPoint(new Vector2f(x, y), new Vector2f(x1, y1), new Vector2f(x2, y2));
	}

	public static double getAngle(Vector2f start, Vector2f end) {
		Vector2f distance;
		distance = end.copy().sub(start);
		distance.x = Math.abs(distance.x);
		double angle = Math.atan((distance.y) / (distance.x));
		if (start.x > end.x)
			angle = Math.PI - angle;
		return angle;
	}

	public static double getAngle(Vector2f point) {
		return getAngle(new Vector2f(0, 0), point);
	}

	public static boolean isPointInRectangle(Vector2f point, Rectangle rect, float rotation) {
		if (rotation == 0)
			return rect.contains(point.x, point.y);
		float[] temppoints = rect
				.transform(Transform.createRotateTransform(rotation, rect.getCenterX(), rect.getCenterY())).getPoints();
		Vector2f[] points = new Vector2f[temppoints.length / 2];
		for (int i = 0; i < temppoints.length / 2; i++) {
			points[i] = new Vector2f(temppoints[2 * i], temppoints[2 * i + 1]);
		}

		for (int i = 0; i < points.length; i++) {
			float width;
			if ((i & 1) == 1) {
				width = rect.getWidth();
			} else {
				width = rect.getHeight();
			}
			Vector2f orthPoint;
			orthPoint = getOrthogonalPoint(points[i % points.length], points[(i + 1) % points.length], point);

			double distance = getDistance(orthPoint, point);
			if (distance > width) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPointInRectangle(Vector2f point, Vector2f rPosition, float width, float height,
			float rotation) {
		Rectangle rect = new Rectangle(rPosition.x - width / 2, rPosition.y - height / 2, width, height);
		return isPointInRectangle(point, rect, rotation);
	}
}
