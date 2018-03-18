package renderer;

import org.newdawn.slick.Graphics;

public interface IRenderable {
	public void render(Camera camera, Graphics g);

	public enum Direction {
		width, height
	}

	public float getApparentSize(IRenderable.Direction dir);

	default public float getRenderLayer() {
		Integer i = RenderLayers.renderLayers.get(getClass());
		try {
			if (i != null) {
				return RenderLayers.renderLayers.get(getClass());
			} else {
				return RenderLayers.renderLayers.get(getClass().getSuperclass());
			}
		} catch (Exception e) {
			System.err.println("No RenderLayer set to " + getClass());
			e.printStackTrace();
			return 0;
		}
	}
}
