package light;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class LightPoint extends Vector2f {
	private static final long serialVersionUID = 1L;
	private Color color = Color.white;

	public LightPoint() {
	}

	public LightPoint(double theta) {
		super(theta);
	}

	public LightPoint(float x, float y) {
		super(x, y);
	}

	public LightPoint(float[] coords) {
		super(coords);
	}

	public LightPoint(Vector2f other) {
		super(other);
	}

	public LightPoint(Color color) {
		this.color = color;
	}

	public LightPoint(double theta, Color color) {
		super(theta);
		this.color = color;
	}

	public LightPoint(float x, float y, Color color) {
		super(x, y);
		this.color = color;
	}

	public LightPoint(float[] coords, Color color) {
		super(coords);
		this.color = color;
	}

	public LightPoint(Vector2f other, Color color) {
		super(other);
		this.color = color;
	}

	public LightPoint(LightPoint other) {
		this(other, other.color);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public LightPoint copy() {
		return new LightPoint(this);
	}

}
