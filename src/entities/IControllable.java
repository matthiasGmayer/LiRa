package entities;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import renderer.IRenderable;
import tools.Tools;

public interface IControllable extends IEntity, IRenderable {

	public void setSelected(boolean b);

	public boolean isSelected();

	default public void changed(List<Object> gameObjects) {
	}

	public boolean isMovable();

	public void setMovable(boolean b);

	public boolean isRotatable();

	public void setRotatable(boolean b);

	public IControllable clone();

	public Vector2f getRPosition();

	public Float getRAngle();

	public Float getRSpectrum();

	public Float getRDistance();

	public void setRPosition(Vector2f v);

	public void setRAngle(Float f);

	public void setRSpectrum(Float f);

	public void setRDistance(Float f);

	public static void restrict(IControllable c) {

		Vector2f position = c.getRPosition();
		Float angle = c.getRAngle();
		Float spectrum = c.getRSpectrum();
		Float distance = c.getRDistance();

		if (position != null && distance != null) {
			Vector2f distVec = Tools.getDistanceVector(c.getPosition(), position);
			if (distVec.length() > distance) {
				distVec.normalise().scale(distance);
				c.getPosition().sub(Tools.getDistanceVector(c.getPosition(), distVec.add(position)));
			}
		}

		if (angle != null && spectrum != null) {

			if (spectrum < 0) {
				angle += spectrum;
				spectrum = -spectrum;
			}

			float dis = (float) ((c.getRotation() - angle) % (Math.PI * 2));
			if (dis > spectrum)
				c.setRotation(spectrum);
			else if (dis < angle)
				c.setRotation(angle);

		}
	}
}
