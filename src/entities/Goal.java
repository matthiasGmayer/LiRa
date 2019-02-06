package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import gameStates.BasicState;
import gameStates.Game;
import gameStates.StateHandler;
import levels.ILoadable;
import light.IColorGetter;
import light.ISolid;
import light.Intersection;
import renderer.Camera;
import renderer.IRenderable;
import tools.CSV;
import tools.Loader;
import tools.Parser;
import tools.Tools;

public class Goal
		implements IControllable, IScalable, IRenderable, IColorGetter, ILoadable, ISolid, IColorable, IUpdatable {

	public static Image standardImage;
	static {
		standardImage = Loader.loadImage("!standardGoal");
	}

	private Vector2f position;
	private float rotation, scale;

	private Vector2f rPosition;
	private Float rDistance, rAngle, rSpectrum = (float) Math.PI * 2;

	private boolean movable, rotatable, selected;

	private Image image;
	private Color color;

	public Goal(Vector2f position, float rotation, float scale, Vector2f rPosition, Float rDistance, Float rAngle,
			Float rSpectrum, boolean movable, boolean rotatable, Image image, Color color) {
		super();
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.rPosition = rPosition;
		this.rDistance = rDistance;
		this.rAngle = rAngle;
		this.rSpectrum = rSpectrum;
		this.movable = movable;
		this.rotatable = rotatable;
		this.image = image;
		this.color = color;
	}

	public Goal() {
		this(new Vector2f(), 0, 1, null, null, null, null, false, false, standardImage, Color.white);
	}

	private Shape getShape() {
		float width = getApparentSize(IRenderable.Direction.width);
		float height = getApparentSize(IRenderable.Direction.height);
		Rectangle r = new Rectangle(0, 0, width, height);
		Shape p = r.transform(Transform.createRotateTransform(getRotation()));
		p.setCenterX(position.x);
		p.setCenterY(position.y);
		return p;
	}

	@Override
	public void addIntersections(Vector2f emittingPoint, Vector2f rayPoint, ArrayList<Intersection> intersections) {

		float[] po = getShape().getPoints();

		int h = 0;
		Vector2f v1 = new Vector2f(po[h++], po[h++]);
		Vector2f v2 = new Vector2f(po[h++], po[h++]);
		Vector2f v3 = new Vector2f(po[h++], po[h++]);
		Vector2f v4 = new Vector2f(po[h++], po[h++]);

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

	@Override
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector2f v) {
		position = v;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(float r) {
		rotation = r;
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
	public void save(CSV csv, String s) {
		csv.add(s, Parser.toString(position.x), Parser.toString(position.y), Parser.toString(scale),
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
	}

	@Override
	public void render(Camera camera, Graphics g) {
		float factor =  Math.min(winTime/3000f,1);
		float width = getApparentSize(Direction.width)*factor;
		float height = getApparentSize(Direction.height)*factor;
		g.setColor(color);
		g.fillOval(position.x-width/2, position.y-height/2, width, height);
		
		Image image = this.image.getScaledCopy(scale);
		image.setImageColor(color.r, color.g, color.b);
		image.rotate((float) (Math.toDegrees(rotation)));
		image.drawCentered(position.x, position.y);
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return image.getWidth() * scale;
		return image.getHeight() * scale;
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
		return new Goal(Tools.copyIfPresent(position), rotation, scale, Tools.copyIfPresent(rPosition),
				Tools.copyIfPresent(rDistance), Tools.copyIfPresent(rAngle), Tools.copyIfPresent(rSpectrum), movable,
				rotatable, image, color);
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
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	float tolerance = 0.2f;

	HashMap<Object, Color> colorMap = new HashMap<Object, Color>();
	private boolean colorMatch;

	@Override
	public void onColorGet(Color color, Object source) {
		colorMap.put(source, color);
		colorMatch = isColorMatching();

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

	private int winTime;

	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {
		if (colorMatch)
			winTime += delta;
		else
			winTime -= delta;
		if (winTime <= 0)
			winTime = 0;
		if (winTime >= 3000) {
			BasicState s = StateHandler.getCurrentState();
			if (s instanceof Game) {
				((Game) s).win();
			}
			winTime = 3000;
		}
		rotation+=winTime/100000f*delta;
	}

}
