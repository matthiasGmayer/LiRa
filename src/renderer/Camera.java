package renderer;

import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import entites.IUpdatable;
import settings.Actions;
import settings.Controlls;
import settings.Graphic;

public class Camera implements IUpdatable, IRenderable {

	private Vector2f position = new Vector2f();
	private Vector2f targetPosition = new Vector2f();
	private float size = 1;
	private float targetSize;

	private float offset;

	protected float sizeInterpolationFactor = 0.995f;
	protected float positionInterpolationFactor = 0.995f;

	boolean middleMouseClicked;
	Vector2f mouseClickPosition;

	Vector2f previousMousePosition = new Vector2f();
	float previousWheelAmount;
	int scrollTime;

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {

		float factor = (float) (delta / 2f / sqrt(size));
		if (Actions.is(Controlls.cameraLeft)) {
			targetPosition.x -= factor;
		}
		if (Actions.is(Controlls.cameraRight)) {
			targetPosition.x += factor;
		}
		if (Actions.is(Controlls.cameraUp)) {
			targetPosition.y -= factor;
		}
		if (Actions.is(Controlls.cameraDown)) {
			targetPosition.y += factor;
		}

		float x = Actions.getWheelAmount();

		targetSize = (float) (1 / (1 + exp(-(x + offset) / 500f))) + 0.1f;

		if (x + offset < -1000) {
			offset = -1000 - x;
			targetSize = (float) (1 / (1 + exp(-(-1000) / 500f))) + 0.1f;
		} else if (x + offset > 1000) {
			offset = 1000 - x;
			targetSize = (float) (1 / (1 + exp(-(1000) / 500f))) + 0.1f;

		}
		size = (float) (targetSize - (targetSize - size) * pow(sizeInterpolationFactor, delta));
		position.x = targetPosition.x
				- (targetPosition.x - position.x) * (float) pow(positionInterpolationFactor, delta);
		position.y = targetPosition.y
				- (targetPosition.y - position.y) * (float) pow(positionInterpolationFactor, delta);

		if (previousWheelAmount != Actions.getWheelAmount()) {
			scrollTime = 3;
		}
		if (scrollTime-- > 0) {
			targetPosition.add(previousMousePosition.copy().sub(targetScreenToWorldPoint(Actions.mousePosition)));
		}

		if (Actions.is(Controlls.middleMouse)) {
			if (middleMouseClicked) {
				middleMouseClicked = false;
				mouseClickPosition = targetScreenToWorldPoint(Actions.mousePosition);
				positionInterpolationFactor = 0.9f;
			} else {
				targetPosition.add(mouseClickPosition.copy().sub(targetScreenToWorldPoint(Actions.mousePosition)));
				// targetPosition.set(position);
			}
		} else {
			middleMouseClicked = true;
			positionInterpolationFactor = 0.995f;

		}

		previousMousePosition.set(targetScreenToWorldPoint(Actions.mousePosition));
		previousWheelAmount = Actions.getWheelAmount();
	}

	@Override
	public void render(Camera camera, Graphics g) {
		g.resetTransform();
		g.translate(Graphic.width / 2, Graphic.height / 2);
		g.scale(size, size);
		g.translate(-position.x, -position.y);
	}

	public Vector2f screenToWorldPoint(Vector2f v) {
		float x = (v.x - Graphic.width / 2) / size + position.x;
		float y = (v.y - Graphic.height / 2) / size + position.y;
		return new Vector2f(x, y);
	}

	public Vector2f worldToScreenPoint(Vector2f v) {
		float x = (v.x - position.x) * size + Graphic.width / 2;
		float y = (v.y - position.y) * size + Graphic.height / 2;
		return new Vector2f(x, y);
	}

	private Vector2f targetScreenToWorldPoint(Vector2f v) {
		float x = (v.x - Graphic.width / 2) / targetSize + targetPosition.x;
		float y = (v.y - Graphic.height / 2) / targetSize + targetPosition.y;
		return new Vector2f(x, y);
	}

	@SuppressWarnings("unused")
	private Vector2f targetWorldToScreenPoint(Vector2f v) {
		float x = (v.x - targetPosition.x) * targetSize + Graphic.width / 2;
		float y = (v.y - targetPosition.y) * targetSize + Graphic.height / 2;
		return new Vector2f(x, y);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		return 0;
	}

	public Vector2f getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Vector2f v) {
		targetPosition = v;
	}
}
