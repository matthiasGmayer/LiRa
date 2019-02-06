package entities;

import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import gameStates.BasicState;
import gameStates.Game;
import gameStates.StateHandler;
import gameStates.States;
import levels.ILoadable;
import light.IColorGetter;
import light.ISolid;
import light.Intersection;
import renderer.Camera;
import tools.CSV;
import tools.Loader;
import tools.Parser;
import tools.Tools;

public class Activator
		implements IControllable, IScalable, IColorable, IUpdatable, ILoadable, IColorGetter, ISolid, ILinkable {

	public static Image standardImage;

	static {
		standardImage = Loader.loadImage("!standardActivator");
	}

	private Vector2f position;
	private float rotation, scale;
	private Image image;
	private Color color;
	private boolean movable, rotatable, selected, activating;

	private Vector2f rPosition;
	private Float rDistance, rAngle, rSpectrum = (float) Math.PI * 2;

	ArrayList<IActivatable> linkedObjects = new ArrayList<IActivatable>();

	private ArrayList<Integer> ids = new ArrayList<Integer>();

	public Activator(Vector2f position, float rotation, float scale, Image image, Color color, boolean movable,
			boolean rotatable, Vector2f rPosition, Float rDistance, Float rAngle, Float rSpectrum) {
		super();
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.image = image;
		this.color = color;
		this.movable = movable;
		this.rotatable = rotatable;
		this.rPosition = rPosition;
		this.rDistance = rDistance;
		this.rAngle = rAngle;
		this.rSpectrum = rSpectrum;
	}

	public Activator() {
		this(new Vector2f(), 0, 1, standardImage, Color.white, false, false, null, null, null, null);
	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector2f position) {
		this.position = position;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void render(Camera camera, Graphics g) {
		float width = getApparentSize(Direction.width);
		float height = getApparentSize(Direction.height);
		g.setColor(new Color(1 - color.r, 1 - color.g, 1 - color.b));
		g.fillRect(position.x - width / 4, position.y - height / 4, width / 2, height / 2);
		g.setColor(Color.black);
		g.fillOval(position.x - width / 6, position.y - height / 6, width / 3, height / 3);
		g.setColor(activating ? Color.green : Color.red);
		g.fillOval(position.x - width / 8, position.y - height / 8, width / 4, height / 4);

		Image image = this.image.getScaledCopy(scale);
		image.setImageColor(color.r, color.g, color.b);
		image.rotate((float) Math.toDegrees(rotation));
		image.drawCentered(position.x, position.y);

	}

	int h;

	@Override
	public float getApparentSize(Direction dir) {
		if (dir == Direction.width)
			return scale * image.getWidth();
		return scale * image.getHeight();
	}

	int time, maxTime = 1000;
	boolean done;
	float tolerance = 0.2f;
	boolean last;

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {
		boolean colorsMatch = isColorMatching();

		activating = colorsMatch;
		if (last != activating) {
			done = false;
			time = 0;
		}
		last = activating;
		if (time > maxTime) {
			done = true;
			time = 0;
		}

		if (!done) {

			States.game.changed();
			States.edit.changed();

			time += delta;
			if (activating) {
				for (IActivatable a : linkedObjects) {
					a.setActivated(true);
					approach(a, a.getActiveState(), delta);
				}
			} else {
				for (IActivatable a : linkedObjects) {
					a.setActivated(false);
					approach(a, a.getInActiveState(), delta);
				}
			}
		}
		if (h < ids.size()) {
			BasicState currentState = StateHandler.getCurrentState();
			Object o = null;
			if (currentState instanceof Game) {
				o = ((Game) currentState).getObjectById(ids.get(h));
			}
			if (o != null && o instanceof IActivatable) {
				h++;
				linkedObjects.add((IActivatable) o);
			}
		}
	}

	public boolean isColorMatching() {
		for (Color c : colorMap.values()) {
			if (c != null && Math.abs(c.r - color.r) < tolerance && Math.abs(c.g - color.g) < tolerance
					&& Math.abs(c.b - color.b) < tolerance) {
				return true;
			}
		}
		return false;
	}

	public void approach(IActivatable a, IActivatable b, int delta) {
		if (a == null || b == null)
			return;
		if (a instanceof IPositionable) {
			Vector2f v = ((IPositionable) a).getPosition();
			Vector2f v2 = ((IPositionable) b).getPosition();
			v.x = approach(v.x, v2.x, delta);
			v.y = approach(v.y, v2.y, delta);
		}
		if (a instanceof IRotatable) {
			IRotatable r = (IRotatable) a;
			IRotatable r2 = (IRotatable) b;
			r.setRotation(approach(r.getRotation(), r2.getRotation(), delta));
		}
		if (a instanceof IScalable) {
			IScalable r = (IScalable) a;
			IScalable r2 = (IScalable) b;
			r.setScale(approach(r.getScale(), r2.getScale(), delta));
		}
		if (a instanceof IResizable) {
			Vector2f v = ((IResizable) a).getDimensions();
			Vector2f v2 = ((IResizable) b).getDimensions();
			v.x = approach(v.x, v2.x, delta);
			v.y = approach(v.y, v2.y, delta);
		}

		if (a instanceof IColorable) {
			IColorable r = (IColorable) a;
			IColorable r2 = (IColorable) b;
			Color c = r.getColor();
			Color c2 = r2.getColor();
			r.setColor(new Color(approach(c.r, c2.r, delta), approach(c.g, c2.g, delta), approach(c.b, c2.b, delta),
					approach(c.a, c2.a, delta)));
		}
		if (a instanceof IControllable) {
			IControllable c = (IControllable) a;
			IControllable c2 = ((IControllable) b);
			c.setMovable(c2.isMovable());
			if (c2.getRPosition() == null)
				c.setRPosition(null);
			else {
				Vector2f v = c.getRPosition();
				Vector2f v2 = c2.getRPosition();
				v.x = approach(v.x, v2.x, delta);
				v.y = approach(v.y, v2.y, delta);
			}
			if (c2.getRAngle() == null)
				c.setRAngle(null);
			else
				c.setRAngle(approach(c.getRAngle(), c2.getRAngle(), delta));
			if (c2.getRDistance() == null)
				c.setRDistance(null);
			else
				c.setRDistance(approach(c.getRDistance(), c2.getRDistance(), delta));
			if (c2.getRSpectrum() == null)
				c.setRSpectrum(null);
			else
				c.setRSpectrum(approach(c.getRSpectrum(), c2.getRSpectrum(), delta));

		}
	}

	public float approach(float approaching, float toApproach, int delta) {
		return (float) (toApproach - (toApproach - approaching) * pow(0.995f, delta));
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void addIntersections(Vector2f emittingPoint, Vector2f rayPoint, ArrayList<Intersection> intersections) {

		Vector2f[] corners = getCorners();

		addIntersection(emittingPoint, rayPoint, corners[0], corners[1], intersections);
		addIntersection(emittingPoint, rayPoint, corners[1], corners[2], intersections);
		addIntersection(emittingPoint, rayPoint, corners[2], corners[3], intersections);
		addIntersection(emittingPoint, rayPoint, corners[3], corners[0], intersections);
	}

	private Vector2f[] getCorners() {
		Rectangle rect = new Rectangle(position.x - image.getWidth() * scale / 2,
				position.y - image.getHeight() * scale / 2, image.getWidth() * scale, image.getHeight() * scale);
		float[] temppoints = rect
				.transform(Transform.createRotateTransform(rotation, rect.getCenterX(), rect.getCenterY())).getPoints();
		Vector2f[] points = new Vector2f[temppoints.length / 2];
		for (int i = 0; i < temppoints.length / 2; i++) {
			points[i] = new Vector2f(temppoints[2 * i], temppoints[2 * i + 1]);
		}
		return points;
	}

	private void addIntersection(Vector2f emittingPoint, Vector2f rayPoint, Vector2f v1, Vector2f v2,
			ArrayList<Intersection> intersections) {

		Vector2f intersection = Tools.getIntersectionPoint(emittingPoint, rayPoint, v1, v2);
		if (intersection != null) {
			Intersection i = new Intersection();
			i.set(intersection);
			i.intersectedObject = this;

			// bug fix
			i.add(new Vector2f(3, 0).add(Math.toDegrees(Tools.getAngle(emittingPoint, rayPoint))));

			intersections.add(i);
		}
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
		return new Activator(Tools.copyIfPresent(position), rotation, scale, image, color, movable, rotatable,
				Tools.copyIfPresent(rPosition), Tools.copyIfPresent(rDistance), Tools.copyIfPresent(rAngle),
				Tools.copyIfPresent(rSpectrum));
	}

	@Override
	public void save(CSV csv, String s) {
		int i = csv.add(s, Parser.toString(position.x), Parser.toString(position.y), Parser.toString(scale),
				Parser.toString(rotation), Parser.toString(rPosition, 0), Parser.toString(rPosition, 1),
				Parser.toString(rDistance), Parser.toString(rAngle), Parser.toString(rSpectrum),
				image == standardImage ? "!" : Parser.imageToPath(image), Parser.toString(color.r),
				Parser.toString(color.g), Parser.toString(color.b), Parser.toString(movable),
				Parser.toString(rotatable));
		String[] l = new String[linkedObjects.size()];
		for (int j = 0; j < l.length; j++) {
			l[j] = Integer.toString(States.edit.getId(linkedObjects.get(j)));
		}
		csv.add(i, l);

	}

	@Override
	public void load(ArrayList<String> l) {
		int h = 1;
		position.x = Parser.toFloat(l.get(h++));
		position.y = Parser.toFloat(l.get(h++));
		scale = Parser.toFloat(l.get(h++));
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
		while (h < l.size()) {
			ids.add(Integer.parseInt(l.get(h++)));
		}

	}

	HashMap<Object, Color> colorMap = new HashMap<Object, Color>();

	@Override
	public synchronized void onColorGet(Color color, Object source) {
		if (color == null)
			colorMap.remove(source);
		else
			colorMap.put(source, color);
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public void unlink() {
		linkedObjects.clear();
	}

	@Override
	public void link(Object a) {
		if (a instanceof IActivatable && !((IActivatable) a).isState())
			linkedObjects.add((IActivatable) a);
	}

	public ArrayList<IActivatable> getLinkedObjects() {
		return linkedObjects;
	}

}
