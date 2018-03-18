package gameStates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entites.IControllable;
import entites.IUpdatable;
import main.App;
import renderer.Camera;
import renderer.IRenderable;
import settings.Graphic;
import settings.Language;
import tools.Debug;
import tools.Lists;

public abstract class BasicState extends BasicGameState {

	public final ArrayList<Object> gameObjects = new ArrayList<Object>();

	public UnicodeFont font;

	Camera camera;

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

		System.out.println(getClass() + " inited");
		camera = create(new Camera());
	}

	public List<Object> toAdd = new LinkedList<Object>();

	public synchronized <T> T create(T object) {
		toAdd.add(object);
		if (object instanceof IControllable)
			((IControllable) object).changed(gameObjects);
		return object;
	}

	public synchronized <T, V extends Iterable<T>> V create(V l) {
		for (T t : l)
			create(t);
		return l;
	}

	public synchronized void create(Object... os) {
		for (Object o : os)
			create(o);
	}

	List<Object> toRemove = new LinkedList<Object>();

	public synchronized <T> T remove(T object) {
		toRemove.add(object);
		return object;
	}

	public synchronized <T, V extends Iterable<T>> V remove(V l) {
		for (T t : l)
			remove(t);
		return l;
	}

	public synchronized void remove(Object... os) {
		for (Object o : os)
			remove(o);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {

		// System.out.println(getClass() + " rendered");

		g.setAntiAlias(Graphic.antialiasing);
		g.setLineWidth(3);
		g.setFont(font);

		renderObjects(gc, sbg, g);
		g.resetTransform();
		Debug.draw(g);

		g.setColor(new Color(Color.black.r, Color.black.g, Color.black.b,
				1 - (float) time / (float) StateHandler.transitionTime));
		g.fillRect(0, 0, Graphic.width, Graphic.height);
	}

	protected void renderObjects(GameContainer gc, StateBasedGame sbg, Graphics g) {
		g.setBackground(Color.white);
		Lists.forEach(gameObjects, o -> o instanceof IRenderable, o -> ((IRenderable) o).render(camera, g));
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		time = 0;
		super.enter(container, game);
	}

	int time;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

		// System.out.println(getClass() + " updated");

		time += delta;
		manageObjects();
		Lists.forEach(gameObjects, o -> o instanceof IUpdatable,
				o -> ((IUpdatable) o).update(gameObjects, camera, delta));
	}

	protected void manageObjects() {
		manageAdd();
		manageRemove();

	}

	protected void manageAdd() {
		for (Object o : toAdd) {

			if (o instanceof IRenderable) {
				IRenderable io = (IRenderable) o;
				int i = 0;
				for (; i < gameObjects.size(); i++)
					if (gameObjects.get(i) instanceof IRenderable)
						if (io.getRenderLayer() <= ((IRenderable) gameObjects.get(i)).getRenderLayer())
							break;
				gameObjects.add(i, o);

			} else
				gameObjects.add(o);
		}
		toAdd.clear();
	}

	protected void manageRemove() {
		for (Object o : toRemove) {
			gameObjects.remove(o);
		}
		toRemove.clear();
	}

	protected final Language getLanguage() {
		return App.currentLanguage;
	}
	
	protected final void enterState(StateBasedGame sbg, Class<? extends BasicState> c) {
		StateHandler.enterState(sbg, c);
	}

	@Override
	public final int getID() {
		return StateHandler.getID(this);
	}

}
