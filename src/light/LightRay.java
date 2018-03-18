package light;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import entites.IScalable;
import renderer.Camera;
import renderer.IRenderable;
import settings.Graphic;
import tools.Loader;
import tools.Tools;

public class LightRay implements IRenderable, IScalable {

	private static final float spaceing = 20f;
	private static final Image standardImage;
	private static final float standardSize = 10;

	static {
		standardImage = Loader.loadImage("!standardParticle");
	}

	public LightRay(Image image, float size, LightPoint... positions) {
		super();
		this.positions = new ArrayList<LightPoint>();
		for (LightPoint v : positions)
			this.positions.add(v);
		this.size = size;
		this.image = image;
	}

	public LightRay(LightPoint... positions) {
		this(standardImage, standardSize, positions);
	}

	public LightRay(float size, LightPoint... positions) {
		this(standardImage, size, positions);
	}

	public ArrayList<LightPoint> positions;
	private float size;
	private Image image;

	@Override
	public void render(Camera camera, Graphics g) {
		Image image = this.image.getScaledCopy(size);
		float spaceing = LightRay.spaceing * size;
		// initialize
		if (positions.size() > 1) {
			LightPoint position = positions.get(0).copy();
			LightPoint fromPosition = position.copy();
			LightPoint toPosition = positions.get(1).copy();
			int j = 2;

			float covered = 0;
			// draw until no Points are left
			while (true) {
				// interpolation of colors
				Color fromColor = fromPosition.getColor();
				Color toColor = toPosition.getColor();

				// linear interpolation
				float fromDistance = position.distance(fromPosition);
				float toDistance = position.distance(toPosition);

				Color color = new Color(
						(fromColor.r * toDistance + toColor.r * fromDistance) / (fromDistance + toDistance),
						(fromColor.g * toDistance + toColor.g * fromDistance) / (fromDistance + toDistance),
						(fromColor.b * toDistance + toColor.b * fromDistance) / (fromDistance + toDistance),
						(fromColor.a * toDistance + toColor.a * fromDistance) / (fromDistance + toDistance));
				// Only render on screen
				if (camera.worldToScreenPoint(position)
						.distance(new Vector2f(Graphic.width / 2, Graphic.height / 2)) < Graphic.width) {
					image.draw(position.x - image.getWidth() / 2, position.y - image.getHeight() / 2, color);
				}

				covered += 0.8f * spaceing + 0.2f * spaceing * (float) Math.random();

				float distance = fromPosition.distance(toPosition);
				float factor1 = Math.min(Math.min(covered / distance, distance), 1);
				float factor2 = 1 - factor1;
				position.set(Tools.getLineDivision(fromPosition, factor2, toPosition, factor1));

				if (position.distance(toPosition) < spaceing + 0.001f) {

					covered -= distance;
					fromPosition = toPosition;

					if (positions.size() > j) {
						toPosition = positions.get(j++).copy();
					} else {
						position = toPosition.copy();
						image.draw(position.x - image.getWidth() / 2, position.y - image.getHeight() / 2, color);
						break;
					}
				}
			}
		}
	}

	public float getScale() {
		return size;
	}

	public void setScale(float size) {
		this.size = size;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public float getApparentSize(IRenderable.Direction dir) {
		if (dir == IRenderable.Direction.width)
			return image.getWidth() * size;
		return image.getHeight() * size;
	}
}
