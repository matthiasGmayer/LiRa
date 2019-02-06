package entities;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import gameStates.StateHandler;
import gameStates.States;
import levels.ILoadable;
import light.IIntersectable;
import light.IReflector;
import light.Intersection;
import renderer.Camera;
import renderer.IRenderable;
import tools.CSV;
import tools.Loader;
import tools.Parser;
import tools.Tools;

public class Mirror
		implements IEntity, IScalable, IRenderable, IReflector, IIntersectable, ILoadable, IControllable, IActivatable {

	public static final Image standardImage;

	static {
		standardImage = Loader.loadImage("!standardMirror");
	}

	private Vector2f position;
	private float size, rotation;
	private Image image;
	private boolean selected, movable, rotatable;

	private Vector2f rPosition;
	private Float rDistance, rAngle, rSpectrum = (float) Math.PI * 2;

	private boolean activated, state;
	private Mirror inActive, active;

	public Mirror(Vector2f position, float size, float rotation, Image image, boolean movable, boolean rotatable,
			Vector2f rPosition, Float rDistance, Float rAngle, Float rSpectrum) {
		super();
		this.position = position;
		this.size = size;
		this.rotation = rotation;
		this.image = image;
		this.movable = movable;
		this.rotatable = rotatable;
		this.rPosition = rPosition;
		this.rDistance = rDistance;
		this.rAngle = rAngle;
		this.rSpectrum = rSpectrum;
	}

	public Mirror() {
		this(new Vector2f(), 1, 0, standardImage, false, false, null, null, null, null);
	}

	@Override
	public void render(Camera camera, Graphics g) {
		if (!isState()) {
			Image image = this.image.getScaledCopy(size);
			image.rotate((float) Math.toDegrees(rotation));
			image.drawCentered(position.x, position.y);
		} else {
			if (StateHandler.isInEdit()) {
				Image image = this.image.getScaledCopy(size);
				image.setImageColor(1, 1, 1, 0.5f);
				image.rotate((float) Math.toDegrees(rotation));
				image.drawCentered(position.x, position.y);
			}
		}
	}

	@Override
	public void addIntersections(Vector2f emittingPoint, Vector2f rayPoint, ArrayList<Intersection> intersections) {
		Vector2f[] v = getCorners();
		Vector2f intersection = Tools.getIntersectionPoint(emittingPoint, rayPoint, v[0], v[1]);
		if (intersection != null) {
			Intersection i = new Intersection();
			i.set(intersection);
			i.intersectedObject = this;
			intersections.add(i);
		}
	}

	@Override
	public float getReflectedAngle(float angle, Vector2f intersection) {

		// on which side is the ray. Prevents bug of double reflections by setting the
		// intersection away from the mirror
		float rot = rotation;
		float reflectedAngle = angle - 2 * (angle - rot);
		intersection.add(new Vector2f(1, 0).add(Math.toDegrees(angle) + 180));

		return reflectedAngle;

	}

	public Vector2f[] getCorners() {
		Vector2f[] v = new Vector2f[2];
		v[0] = new Vector2f(image.getWidth() / 2 * size, 0);
		v[0].add(Math.toDegrees(rotation) + 180).add(position);
		v[1] = new Vector2f(image.getWidth() / 2 * size, 0);
		v[1].add(Math.toDegrees(rotation)).add(position);
		return v;
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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void save(CSV csv, String s) {
		csv.add(s, Parser.toString(position.x), Parser.toString(position.y), Parser.toString(size),
				Parser.toString(rotation), Parser.toString(rPosition, 0), Parser.toString(rPosition, 1),
				Parser.toString(rDistance), Parser.toString(rAngle), Parser.toString(rSpectrum),
				image == standardImage ? "!" : Parser.imageToPath(image), Parser.toString(movable),
				Parser.toString(rotatable));
	}

	@Override
	public void load(ArrayList<String> l) {
		int h = 1;
		position = Parser.toVector2f(l.get(h++), l.get(h++));
		size = Parser.toFloat(l.get(h++));
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
	public void setMovable(boolean controllable) {
		this.movable = controllable;
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
		return new Mirror(Tools.copyIfPresent(position), size, rotation, image, movable, rotatable,
				Tools.copyIfPresent(rPosition), Tools.copyIfPresent(rDistance), Tools.copyIfPresent(rAngle),
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
	public Mirror getActiveState() {
		return active;
	}

	@Override
	public Mirror getInActiveState() {
		return inActive;
	}

	@Override
	public void setActiveState(IActivatable a) {
		active = (Mirror) a;
	}

	@Override
	public void setInActiveState(
	// IActivatable a
	) {
		inActive = (Mirror) clone();
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
		if (object instanceof Mirror) {
			Mirror c = (Mirror) object;
			if (isState() || c.isState() || this == c)
				return;
			unlink();
			c.setState(true);
			States.game.idList.remove(active);
			active = c;
			inActive = (Mirror) clone();
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
