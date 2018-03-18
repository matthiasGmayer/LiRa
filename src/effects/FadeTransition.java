package effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;

import settings.Graphic;

public class FadeTransition implements Transition {

	int time = 0, maxTime;
	Color color;
	float alpha;
	boolean fadeIn;

	GameState a, b;

	@Override
	public void init(GameState a, GameState b) {
		this.a = a;
		this.b = b;
	}

	public FadeTransition(int duration, Color color, boolean fadeIn) {
		super();
		this.maxTime = duration;
		this.color = color;
		this.fadeIn = fadeIn;
	}

	@Override
	public boolean isComplete() {
		return time > maxTime;
	}

	@Override
	public void postRender(StateBasedGame sbg, GameContainer gc, Graphics g) throws SlickException {

		g.setColor(new Color(color.r, color.g, color.b, alpha));
		g.fillRect(0, 0, Graphic.width, Graphic.height);
	}

	@Override
	public void preRender(StateBasedGame sbg, GameContainer gc, Graphics g) throws SlickException {

	}

	@Override
	public void update(StateBasedGame arg0, GameContainer arg1, int delta) throws SlickException {
		time += delta;
		if (fadeIn)
			alpha = 1 - (float) time / (float) maxTime;
		else
			alpha = (float) time / (float) maxTime;
	}

}
