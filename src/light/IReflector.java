package light;

import org.newdawn.slick.geom.Vector2f;

public interface IReflector extends IIntersectable {
	public float getReflectedAngle(float angle, Vector2f intersection);
}
