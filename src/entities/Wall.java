package entities;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import gameStates.StateHandler;
import gameStates.States;
import levels.ILoadable;
import light.IIntersectable;
import light.ISolid;
import light.Intersection;
import renderer.Camera;
import renderer.IRenderable;
import tools.CSV;
import tools.Loader;
import tools.Parser;
import tools.Tools;

public class Wall implements IControllable, IResizable, ILoadable, IIntersectable, ISolid, IActivatable {

	public static Image standardImage;
	static {
		standardImage = Loader.loadImage("!standardWall");
	}

	private Vector2f position, dimensions;
	private float rotation;

	private Vector2f rPosition;
	private Float rDistance, rAngle, rSpectrum = (float) Math.PI * 2;

	private boolean movable, rotatable, selected;

	Image image;

	private boolean activated, state;
	private Wall inActive, active;

	public Wall(Vector2f position, Vector2f dimensions, float rotation, Vector2f rPosition, Float rDistance,
			Float rAngle, Float rSpectrum, boolean movable, boolean rotatable, Image image) {
		super();
		this.position = position;
		this.dimensions = dimensions;
		this.rotation = rotation;
		this.rPosition = rPosition;
		this.rDistance = rDistance;
		this.rAngle = rAngle;
		this.rSpectrum = rSpectrum;
		this.movable = movable;
		this.rotatable = rotatable;
		this.image = image;
	}

	public Wall() {
		this(new Vector2f(), new Vector2f(1, 1), 0, null, null, null, null, false, false, standardImage);
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
	public Vector2f getDimensions() {
		return dimensions;
	}

	@Override
	public void setDimensions(Vector2f v) {
		dimensions = v;
	}

	@Override
	public void save(CSV csv, String s) {
		csv.add(s, Parser.toString(position.x), Parser.toString(position.y), Parser.toString(dimensions, 0),
				Parser.toString(dimensions, 1), Parser.toString(rotation), Parser.toString(rPosition, 0),
				Parser.toString(rPosition, 1), Parser.toString(rDistance), Parser.toString(rAngle),
				Parser.toString(rSpectrum), image == standardImage ? "!" : Parser.imageToPath(image),
				Parser.toString(movable), Parser.toString(rotatable));
	}

	@Override
	public void load(ArrayList<String> l) {
		int h = 1;
		position.x = Parser.toFloat(l.get(h++));
		position.y = Parser.toFloat(l.get(h++));
		dimensions = Parser.toVector2f(l.get(h++), l.get(h++));
		rotation = Parser.toFloat(l.get(h++));
		rPosition = Parser.toVector2f(l.get(h++), l.get(h++));
		rDistance = Parser.toFloat(l.get(h++));
		rAngle = Parser.toFloat(l.get(h++));
		rSpectrum = Parser.toFloat(l.get(h++));

		if (l.get(h++).equals("!"))
			image = standardImage;
		else
			image = Loader.loadImage(l.get(h - 1));
		movable = Parser.toBoolean(l.get(h++));
		rotatable = Parser.toBoolean(l.get(h++));
	}

	@Override
	public void render(Camera camera, Graphics g) {
		if (!isState()) {
			g.setColor(Color.white);
			g.texture(getShape(), image);
			if (StateHandler.isInEdit()) {
				g.setColor(Color.black);
				g.draw(getShape());
			}
		} else if (StateHandler.isInEdit()) {
			g.setColor(new Color(1, 1, 1, 0.5f));
			g.texture(getShape(), image);
			g.setColor(new Color(0, 0, 0, 0.5f));
			g.draw(getShape());
		}
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return image.getWidth() * dimensions.x;
		return image.getHeight() * dimensions.y;
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

		return new Wall(Tools.copyIfPresent(position), Tools.copyIfPresent(dimensions), rotation,
				Tools.copyIfPresent(rPosition), Tools.copyIfPresent(rDistance), Tools.copyIfPresent(rAngle),
				Tools.copyIfPresent(rSpectrum), movable, rotatable, image);
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
	public Wall getActiveState() {
		return active;
	}

	@Override
	public Wall getInActiveState() {
		return inActive;
	}

	@Override
	public void setActiveState(IActivatable a) {
		active = (Wall) a;
	}

	@Override
	public void setInActiveState(
	// IActivatable a
	) {
		inActive = (Wall) clone();
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
		if (object instanceof Wall) {
			Wall c = (Wall) object;
			if (isState() || c.isState() || this == c)
				return;
			unlink();
			c.setState(true);
			States.game.idList.remove(active);
			active = c;
			inActive = (Wall) clone();
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
