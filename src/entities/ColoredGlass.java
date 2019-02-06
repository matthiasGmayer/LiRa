package entities;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import gameStates.StateHandler;
import gameStates.States;
import levels.ILoadable;
import light.IColorer;
import light.Intersection;
import light.LightPoint;
import renderer.Camera;
import renderer.IRenderable;
import tools.CSV;
import tools.Loader;
import tools.Parser;
import tools.Tools;

public class ColoredGlass
		implements IScalable, IRenderable, IColorer, ILoadable, IControllable, IColorable, IActivatable {

	public static final Image standardImage;

	static {
		standardImage = Loader.loadImage("!standardColoredGlass");
	}

	private Vector2f position = new Vector2f();
	private float size, rotation, strength;
	private boolean selected, movable, rotatable;
	private Image image;
	private Color color;

	private Vector2f rPosition;
	private Float rDistance, rAngle, rSpectrum = (float) Math.PI * 2;

	private boolean activated, state;
	private ColoredGlass inActive, active;

	public ColoredGlass(Vector2f position, float size, float rotation, float strength, boolean movable,
			boolean rotatable, Image image, Color color, Vector2f rPosition, Float rDistance, Float rAngle,
			Float rSpectrum) {
		super();
		this.position = position;
		this.size = size;
		this.rotation = rotation;
		this.strength = strength;
		this.movable = movable;
		this.rotatable = rotatable;
		this.image = image;
		this.color = color;
		this.rPosition = rPosition;
		this.rDistance = rDistance;
		this.rAngle = rAngle;
		this.rSpectrum = rSpectrum;
	}

	public ColoredGlass() {
		this(new Vector2f(), 1, 0, 1, false, false, standardImage, Color.white, null, null, null, null);
	}

	@Override
	public Color setColors(ArrayList<LightPoint> p) {
		if (p.size() != 2)
			return p.get(0).getColor();

		Color fromColor = p.get(0).getColor();
		Color toColor = getColor();

		// linear interpolation
		float factor = Math.min(p.get(0).distance(p.get(1)) / 120f * strength, 1);

		float factor2 = 1 - factor;

		Color color = new Color((fromColor.r * factor2 + toColor.r * factor),
				(fromColor.g * factor2 + toColor.g * factor), (fromColor.b * factor2 + toColor.b * factor),
				(fromColor.a * factor2 + toColor.a * factor));

		p.get(1).setColor(color);

		return color;
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
		Rectangle rect = new Rectangle(position.x - image.getWidth() * size / 2,
				position.y - image.getHeight() * size / 2, image.getWidth() * size, image.getHeight() * size);
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
	public void render(Camera camera, Graphics g) {
		if (!isState()) {
			Image image = this.image.getScaledCopy(size);
			image.setImageColor(color.r, color.g, color.b);
			image.rotate((float) Math.toDegrees(rotation));
			image.drawCentered(position.x, position.y);
		} else if (StateHandler.isInEdit()) {
			Image image = this.image.getScaledCopy(size);
			image.setImageColor(color.r, color.g, color.b, 0.3f);
			image.rotate((float) Math.toDegrees(rotation));
			image.drawCentered(position.x, position.y);
		}
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
	public float getScale() {
		return size;
	}

	@Override
	public void setScale(float size) {
		this.size = size;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
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
				Parser.toString(rotation), Parser.toString(strength), Parser.toString(rPosition, 0),
				Parser.toString(rPosition, 1), Parser.toString(rDistance), Parser.toString(rAngle),
				Parser.toString(rSpectrum), image == standardImage ? "!" : Parser.imageToPath(image),
				Parser.toString(color.r), Parser.toString(color.g), Parser.toString(color.b), Parser.toString(movable),
				Parser.toString(rotatable));
	}

	@Override
	public void load(ArrayList<String> l) {
		int h = 1;
		position.x = Parser.toFloat(l.get(h++));
		position.y = Parser.toFloat(l.get(h++));
		size = Parser.toFloat(l.get(h++));
		rotation = Parser.toFloat(l.get(h++));
		strength = Parser.toFloat(l.get(h++));
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

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	@Override
	public IControllable clone() {
		return new ColoredGlass(Tools.copyIfPresent(position), size, rotation, strength, movable, rotatable, image,
				color, Tools.copyIfPresent(rPosition), Tools.copyIfPresent(rDistance), Tools.copyIfPresent(rAngle),
				Tools.copyIfPresent(rSpectrum));
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
	public ColoredGlass getActiveState() {
		return active;
	}

	@Override
	public ColoredGlass getInActiveState() {
		return inActive;
	}

	@Override
	public void setActiveState(IActivatable a) {
		active = (ColoredGlass) a;
	}

	@Override
	public void setInActiveState(
	// IActivatable a
	) {
		inActive = (ColoredGlass) clone();
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
		if (object instanceof ColoredGlass) {
			ColoredGlass c = (ColoredGlass) object;
			if (isState() || c.isState() || this == c)
				return;
			unlink();
			c.setState(true);
			States.game.idList.remove(active);
			active = c;
			inActive = (ColoredGlass) clone();
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
