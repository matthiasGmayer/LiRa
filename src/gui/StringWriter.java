package gui;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import entites.IPositionable;
import entites.IUpdatable;
import renderer.Camera;
import renderer.IRenderable;

public class StringWriter implements IUpdatable, IRenderable, IPositionable{
	
	
	private String content;
	private Vector2f position;
	private Color color;
	int width;
	

	
	public StringWriter(String content, Vector2f position, int width,  Color color) {
		super();
		this.content = content;
		this.position = position;
		this.color = color;
		this.width = width;
	}
	int time;
	@Override
	public void update(List<Object> gameObjects, Camera camera, int delta) {
		time += delta;
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
	public void render(Camera camera, Graphics g) {
		g.setColor(color);
		renderO(g, 0);
	}
	public void renderO(Graphics g, int d) {
		System.out.println(d +" "+ width*d);
		g.drawString(content.substring(width*d, (int)Math.min(time/30f, Math.min((d+1)*width-1, content.length()))), position.x, position.y + d*10);
		System.out.println(time);
		if(time/30f > width * (d+1) && content.length() > width * (d+1))
			renderO(g, d+1);
	}
	@Override
	public float getApparentSize(Direction dir) {
		// TODO Auto-generated method stub
		return 0;
	}
}
