package effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import entities.IControllable;
import entities.IEntity;
import entities.IScalable;
import renderer.Camera;
import renderer.IRenderable;
import tools.Loader;

public class Glow implements IRenderable, IEntity, IScalable {

	private static Image standardImage;
	private static float standardSize = 1f;
	private static Color standardColor = new Color(0.1f, 0.1f, 1f);
	static {
		standardImage = Loader.loadImage("!standardParticle");
	}
	private IControllable parent;
	private Image image;
	private Color color;
	private float size;

	public Glow(IControllable parent, float size, Color color, Image image) {
		super();
		this.parent = parent;
		this.image = image;
		this.color = color;
		this.size = size;
	}

	public Glow(IControllable parent, float size, Color color) {
		this(parent, size, color, standardImage);
	}

	public Glow(IControllable parent, float size) {
		this(parent, size, standardColor, standardImage);
	}

	public Glow(IControllable parent, Color color) {
		this(parent, standardSize, color, standardImage);
	}

	public Glow(IControllable parent) {
		this(parent, standardColor);
	}

	@Override
	public void render(Camera camera, Graphics g) {
		Image image;
		image = this.image.getScaledCopy((int) (2.5f * size * parent.getApparentSize(Direction.width)),
				(int) (2.5f * size * parent.getApparentSize(Direction.height)));
		image.setImageColor(color.r, color.g, color.b, color.a);
		image.rotate((float) (Math.toDegrees(parent.getRotation())));
		image.drawCentered(parent.getPosition().x, parent.getPosition().y);
	}

	@Override
	public float getRenderLayer() {
		return parent.getRenderLayer() - 0.1f;

	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return image.getWidth() * size;
		return image.getHeight() * size;
	}

	@Override
	public Vector2f getPosition() {
		return parent.getPosition();
	}

	@Override
	public void setPosition(Vector2f v) {
		parent.setPosition(v);
	}

	@Override
	public float getScale() {
		return size;
	}

	@Override
	public void setScale(float s) {
		size = s;
	}

	@Override
	public float getRotation() {
		return 0;
	}

	@Override
	public void setRotation(float r) {
	}

}
