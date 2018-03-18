package entites;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import gameStates.StateHandler;
import gameStates.States;
import light.IColorGetter;
import light.IColorer;
import light.IIntersectable;
import light.IReflector;
import light.ISolid;
import light.Intersection;
import light.LightPoint;
import light.LightRay;
import renderer.Camera;
import renderer.IRenderable;
import tools.CSV;
import tools.Loader;
import tools.Parser;
import tools.Tools;

public class LightEmitter
		implements IRenderable, IUpdatable, ILoadable, IControllable, IScalable, IColorable, IActivatable {
	public static final Image standardImage;

	static {
		standardImage = Loader.loadImage("!standardEmitter");
	}

	private LightRay lightRay;

	private Vector2f position;
	private float size, rotation;
	private boolean selected, movable, rotatable;
	private Color color;

	private Vector2f rPosition;
	private Float rDistance, rAngle, rSpectrum = (float) Math.PI * 2;

	private boolean activated, state;
	private LightEmitter inActive, active;

	private Image image;

	

	public LightEmitter(Vector2f position, float size, float rotation, boolean movable,
			boolean rotatable, Color color, Vector2f rPosition, Float rDistance, Float rAngle, Float rSpectrum,
			Image image) {
		super();
		this.position = position;
		this.size = size;
		this.rotation = rotation;
		this.movable = movable;
		this.rotatable = rotatable;
		this.color = color;
		this.rPosition = rPosition;
		this.rDistance = rDistance;
		this.rAngle = rAngle;
		this.rSpectrum = rSpectrum;
		this.image = image;
		
		lightRay = new LightRay();
		lightRay.setScale(size);
		lightRay.positions.add(new LightPoint(position, color));
	}

	public LightEmitter() {
		this(new Vector2f(0, 0), 1, 0, false, false, Color.white,null,null,null,null , standardImage);
	}

	boolean first;

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {
		// if (first) {
		// changed(gameObjects);
		// first = false;
		// }
		updateLightRay(gameObjects);
	}

	@Override
	public void changed(List<Object> gameObjects) {
		// updateLightRay(gameObjects);
	}

	private int max = 100;

	public void updateLightRay(List<Object> gameObjects) {
		ArrayList<Intersection> pos = new ArrayList<Intersection>();
		pos.clear();
		Intersection myIntersection = new Intersection();
		myIntersection.set(position);
		myIntersection.intersectedObject = this;
		pos.add(myIntersection);

		float angle = rotation;
		Color color = this.color;

		int h = 0;
		while (h++ < max) {
			// System.out.println(h);
			Vector2f position = pos.get(pos.size() - 1);
			Vector2f rayPoint = new Vector2f(1, 0).add(Math.toDegrees(angle)).scale(100000).add(position);

			ArrayList<Intersection> intersections = new ArrayList<Intersection>();

			for (Object o : gameObjects) {
				if (o instanceof IIntersectable) {
					if (o instanceof IActivatable && ((IActivatable) o).isState())
						continue;
					((IIntersectable) o).addIntersections(position, rayPoint, intersections);
				}
			}

			Intersection nearestIntersection;
			try {
				nearestIntersection = intersections.get(0);
			} catch (Exception e) {
				return;
			}

			float distance = nearestIntersection.distance(position);
			for (int i = 1; i < intersections.size(); i++) {
				Intersection intersection = intersections.get(i);
				float dist = intersection.distance(position);
				if (distance > dist) {
					distance = dist;
					nearestIntersection = intersection;
				}
			}
			Object object = nearestIntersection.intersectedObject;

			if (object instanceof IReflector)
				angle = ((IReflector) object).getReflectedAngle(angle, nearestIntersection);
			pos.add(nearestIntersection);
			if (object == null)
				break;
			if (object instanceof ISolid)
				if (!((ISolid) object).isTransparent())
					break;

		}

		ArrayList<LightPoint> lPoints = new ArrayList<LightPoint>();
		ArrayList<LightPoint> currentLightPoints = new ArrayList<LightPoint>();
		IColorer lastColorer = null;

		LightPoint p;

		ArrayList<IColorGetter> getters = new ArrayList<IColorGetter>();

		for (int i = 0; i < pos.size(); i++) {
			Object o = pos.get(i).intersectedObject;

			if (o == lastColorer) {
				currentLightPoints.add(p = new LightPoint(pos.get(i), color));

			} else {
				if (lastColorer != null) {
					color = lastColorer.setColors(currentLightPoints);
					currentLightPoints.clear();
				}
				if (o instanceof IColorer) {
					currentLightPoints.add(p = new LightPoint(pos.get(i), color));
					lastColorer = (IColorer) o;
				} else {
					if (o instanceof IColorGetter) {
						getters.add(((IColorGetter) o));
						((IColorGetter) o).onColorGet(color, this);
					}
					lastColorer = null;
				}
				p = new LightPoint(pos.get(i), color);
			}
			lPoints.add(p);

		}

		for (Object o : gameObjects) {
			if (o instanceof IColorGetter && !getters.contains(o))
				((IColorGetter) o).onColorGet(null, this);
		}

		// for(Vector2f v : pos) {
		// lPoints.add(new LightPoint(v, color));
		// }

		lightRay.positions = lPoints;
	}

	@Override
	public void render(Camera camera, Graphics g) {

		if (!isState()) {
			Image image = this.image.getScaledCopy(size);
			image.setImageColor(color.r, color.g, color.b);
			image.rotate((float) Math.toDegrees(rotation));
			image.drawCentered(position.x, position.y);

			lightRay.render(camera, g);
		} else {
			if (StateHandler.isInEdit()) {
				Image image = this.image.getScaledCopy(size);
				image.setImageColor(color.r, color.g, color.b, 0.5f);
				image.rotate((float) Math.toDegrees(rotation));
				image.drawCentered(position.x, position.y);
			}
		}

	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector2f v) {
		position = v;
	}

	@Override
	public float getScale() {
		return size;
	}

	@Override
	public void setScale(float s) {
		size = s;
		lightRay.setScale(s);
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(float r) {
		rotation = r;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void save(CSV csv, String s) {
		csv.add(s, Parser.toString(position.x), Parser.toString(position.y), Parser.toString(size),
				Parser.toString(rotation), Parser.toString(rPosition, 0), Parser.toString(rPosition, 1),
				Parser.toString(rDistance), Parser.toString(rAngle), Parser.toString(rSpectrum),
				image == standardImage ? "!" : Parser.imageToPath(image), Parser.toString(color.r),
				Parser.toString(color.g), Parser.toString(color.b), Parser.toString(movable),
				Parser.toString(rotatable));
	}

	@Override
	public void load(ArrayList<String> l) {
		int h = 1;
		position.x = Parser.toFloat(l.get(h++));
		position.y = Parser.toFloat(l.get(h++));
		size = Parser.toFloat(l.get(h++));
		lightRay.setScale(size);
		rotation = Parser.toFloat(l.get(h++));
		rPosition = Parser.toVector2f(l.get(h++), l.get(h++));
		rDistance = Parser.toFloat(l.get(h++));
		rAngle = Parser.toFloat(l.get(h++));
		rSpectrum = Parser.toFloat(l.get(h++));

		if (l.get(h++).equals("!"))
			image = standardImage;
		else
			image = Loader.loadImage(l.get(h - 1));
		color = Parser.toColor(l.get(h++), l.get(h++), l.get(h++));
		movable = Parser.toBoolean(l.get(h++));
		rotatable = Parser.toBoolean(l.get(h++));
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return image.getWidth() * size;
		return image.getHeight() * size;
	}

	@Override
	public void setSelected(boolean b) {
		selected = b;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean isMovable() {
		return movable;
	}

	@Override
	public void setMovable(boolean b) {
		movable = b;
	}

	@Override
	public boolean isRotatable() {
		return rotatable;
	}

	@Override
	public void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}

	@Override
	public IControllable clone() {
		return new LightEmitter(Tools.copyIfPresent(position), size, rotation, movable, rotatable, color, Tools.copyIfPresent(rPosition), Tools.copyIfPresent(rDistance),
				Tools.copyIfPresent(rAngle),Tools.copyIfPresent(rSpectrum), image);
	}

	@Override
	public String toString() {
		return position + " " + movable + " " + rotatable + " " + super.toString();
	}

	@Override
	public Vector2f getRPosition() {
		return rPosition;
	}

	@Override
	public Float getRDistance() {
		return rDistance;
	}

	@Override
	public Float getRAngle() {
		return rAngle;
	}

	@Override
	public Float getRSpectrum() {
		return rSpectrum;
	}

	@Override
	public void setRPosition(Vector2f rPosition) {
		this.rPosition = rPosition;
	}

	@Override
	public void setRDistance(Float rDistance) {
		this.rDistance = rDistance;
	}

	@Override
	public void setRAngle(Float rAngle) {
		this.rAngle = rAngle;
	}

	@Override
	public void setRSpectrum(Float rSpectrum) {
		this.rSpectrum = rSpectrum;
	}

	@Override
	public LightEmitter getActiveState() {
		return active;
	}

	@Override
	public LightEmitter getInActiveState() {
		return inActive;
	}

	@Override
	public void setActiveState(IActivatable a) {
		active = (LightEmitter) a;
	}

	@Override
	public void setInActiveState(
	// IActivatable a
	) {
		inActive = (LightEmitter) clone();
	}

	@Override
	public boolean isActivated() {
		return activated;
	}

	@Override
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public boolean isState() {
		return state;
	}

	@Override
	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public void link(Object object) {
		if (object instanceof LightEmitter) {
			LightEmitter c = (LightEmitter) object;
			if (isState() || c.isState() || this == c)
				return;
			unlink();
			c.setState(true);
			States.game.idList.remove(active);
			active = c;
			inActive = (LightEmitter) clone();
		}
	}

	@Override
	public void unlink() {
		if (active != null) {
			active.setState(false);
			States.game.idList.add(active);
		}
		active = null;
	}
}
