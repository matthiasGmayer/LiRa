package gui;

import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import entites.IPositionable;
import entites.IScalable;
import entites.IUpdatable;
import renderer.Camera;
import renderer.IRenderable;

public class Panel implements IRenderable, IUpdatable, IPositionable, IScalable {

	public ArrayList<IRenderable> objects = new ArrayList<IRenderable>();

	protected Camera camera = new Camera() {
		@Override
		public void update(List<Object> gameObjects, Camera camera, int delta) {
			getPosition().x = getTargetPosition().x
					- (getTargetPosition().x - getPosition().x) * (float) pow(super.positionInterpolationFactor, delta);
			getPosition().y = getTargetPosition().y
					- (getTargetPosition().y - getPosition().y) * (float) pow(super.positionInterpolationFactor, delta);
		}

	};

	public Panel(ArrayList<IRenderable> objects) {
		super();
		this.objects = objects;
	}

	public Panel(IRenderable... objects) {
		super();
		for (IRenderable iRenderable : objects) {
			add(iRenderable);
		}
	}

	public synchronized void add(Iterable<? extends IRenderable> objects) {
		for (IRenderable iRenderable : objects) {
			add(iRenderable);
		}
	}

	public synchronized void add(IRenderable... objects) {
		for (IRenderable iRenderable : objects) {
			add(iRenderable);
		}
	}

	public List<IRenderable> toAdd = new LinkedList<IRenderable>();

	private void add(IRenderable r) {
		toAdd.add(r);
	}

	List<Object> toRemove = new LinkedList<Object>();

	public synchronized <T> T remove(T object) {
		toRemove.add(object);
		return object;
	}

	public synchronized <T, V extends Iterable<T>> V remove(V l) {
		for (T t : l)
			remove(t);
		return l;
	}

	public synchronized void remove(Object... os) {
		for (Object o : os)
			remove(o);
	}

	@Override
	public void render(Camera camera, Graphics g) {
		g.pushTransform();
		this.camera.render(camera, g);

		for (IRenderable iRenderable : objects) {
			iRenderable.render(this.camera, g);
		}

		g.popTransform();
	}

	@Override
	public synchronized void update(List<Object> gameObjects, Camera camera, int delta) {
		for (IRenderable r : toAdd) {
			int i = 0;
			for (; i < objects.size(); i++)
				if (r.getRenderLayer() <= objects.get(i).getRenderLayer())
					break;
			objects.add(i, r);
		}
		toAdd.clear();
		for (Object o : toRemove) {
			objects.remove(o);
		}
		toRemove.clear();

		for (IRenderable r : objects) {
			if (r instanceof IUpdatable) {
				((IUpdatable) r).update(gameObjects, this.camera, delta);
			}
		}
		this.camera.update(gameObjects, this.camera, delta);
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		return 0;
	}

	@Override
	public Vector2f getPosition() {
		return camera.getPosition().negate();
	}

	@Override
	public void setPosition(Vector2f position) {
		position = position.negate();
		camera.setPosition(position);
		camera.getTargetPosition().set(position);
	}

	public Vector2f getTargetPosition() {
		return camera.getTargetPosition().negate();
	}

	public void setTargetPosition(Vector2f TargetPosition) {
		camera.setTargetPosition(TargetPosition.negate());
	}

	@Override
	public float getScale() {
		return camera.getSize();
	}

	@Override
	public void setScale(float size) {
		camera.setSize(size);
	}

	public void clear() {
		objects.clear();
	}

}
