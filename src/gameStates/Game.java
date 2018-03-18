package gameStates;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import effects.Glow;
import entites.IActivatable;
import entites.IControllable;
import entites.ILoadable;
import entites.IPositionable;
import gui.Button;
import gui.Panel;
import levels.Level;
import light.Frame;
import renderer.IRenderable;
import renderer.IRenderable.Direction;
import settings.Actions;
import settings.Controlls;
import settings.Graphic;
import tools.Lists;
import tools.Loader;
import tools.Tools;
import util.ButtonAction;

public class Game extends BasicState {

	Image arrow = Loader.loadImage("!arrow");
	Frame screen = new Frame(Graphic.width / 2, Graphic.height / 2, Graphic.width, Graphic.height);
	Panel p;

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		if (!(this instanceof Edit))
			States.game = this;
		create(new Frame(-50000, -50000, 100000, 100000));

		Button b;
		create(p = new Panel(

				b = new Button(new ButtonAction() {
					@Override
					public void onRelease(Object source) {
						StateHandler.enterState(sbg, Menu.class);
						ButtonAction.super.onRelease(source);
					}
				}, getLanguage().menu, Color.black, new Vector2f(0, 0), 0.3f)));
		b.setPosition(new Vector2f(0, b.getApparentSize(Direction.height) / 2 - Graphic.height / 2));

		super.init(gc, sbg);
	}

	Level level;

	public void setLevel(Level l) {
		level = l;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		if (level != null) {
			clear();
			create(level);
		}
		changed();
		super.enter(container, game);
	}

	public List<ILoadable> idList = new LinkedList<ILoadable>();

	@Override
	public synchronized <T> T create(T object) {
		toAdd.add(object);
		if (object instanceof IControllable)
			((IControllable) object).changed(gameObjects);
		if (object instanceof ILoadable) {
			if (!(object instanceof IActivatable) || !((IActivatable) object).isState())
				idList.add((ILoadable) object);
		}
		return object;
	}

	public synchronized void clear() {
		Lists.forEach(gameObjects,o -> idList.contains(o),o->remove(o));
	}

	public Integer getId(Object object) {
		int i = idList.indexOf(object);
		return i == -1 ? null : i;
	}

	public Object getObjectById(int id) {
		if (idList.size() > id)
			return idList.get(id);
		return null;
	}

	@Override
	public void renderObjects(GameContainer gc, StateBasedGame sbg, Graphics g) {

		Object selected = getSelected();
		g.setBackground(Color.black);
		for (Object o : gameObjects) {
			if (o instanceof IRenderable && o != selected) {
				((IRenderable) o).render(camera, g);
			}
		}
		if (selected instanceof IRenderable) {
			((IRenderable) selected).render(camera, g);
		}

		// draw arrow
		g.resetTransform();
		IPositionable nearestObject = getNearestObject();
		if (nearestObject != null) {
			Vector2f v = camera.worldToScreenPoint((nearestObject.getPosition()));
			float range = 50;
			if (v.x < -range || v.x > Graphic.width + range || v.y < -range || v.y > Graphic.height + range) {
				float angle = (float) Tools.getAngle(new Vector2f(Graphic.width / 2, Graphic.height / 2), v);
				Image arrow = this.arrow.copy();
				arrow.setCenterOfRotation(arrow.getWidth(), arrow.getWidth() / 2);
				arrow.rotate((float) (Math.toDegrees(angle)));
				Vector2f v2 = new Vector2f(Math.toDegrees(angle)).scale(Graphic.height / 2 - 100f);
				arrow.drawCentered(v2.x + Graphic.width / 2, v2.y + Graphic.height / 2);

			}
		}
	}

	Vector2f moveDif = new Vector2f();
	boolean movingSetup;

	float turnDif = 0;
	boolean turnSetup;

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		super.update(gc, sbg, delta);

		IControllable c = getSelected();
		if (c != null && !Actions.isPressed(Input.KEY_LCONTROL) && !Actions.isPressed(Input.KEY_LALT)) {
			// drag object
			if (movingSetup && Actions.is(Controlls.leftMouse)) {
				c.setPosition(camera.screenToWorldPoint(Actions.mousePosition.copy().add(moveDif)));
				setInActiveState(c);
			}
			if ((c.isMovable() || StateHandler.isInEdit()) && isMouseOnObject(c)) {
				if (!movingSetup) {
					movingSetup = true;
					moveDif = Tools.getDistanceVector(camera.worldToScreenPoint(c.getPosition()),
							Actions.mousePosition);
				}
			}
			if (!Actions.is(Controlls.leftMouse)) {
				movingSetup = false;
			} else {
				changed();
			}
			// turn object
			if (turnSetup && Actions.is(Controlls.rightMouse)) {
				c.setRotation((float) Tools.getAngle(camera.screenToWorldPoint(Actions.mousePosition), c.getPosition())
						+ turnDif);
				setInActiveState(c);
			}
			if ((c.isRotatable() || StateHandler.isInEdit()) && !turnSetup) {
				turnSetup = true;
				turnDif = c.getRotation()
						- (float) Tools.getAngle(camera.screenToWorldPoint(Actions.mousePosition), c.getPosition());
			}
			if (!Actions.is(Controlls.rightMouse)) {
				turnSetup = false;
			} else {
				changed();
			}
		}
	}

	@Override
	protected void manageRemove() {
		for (Object o : toRemove) {
			gameObjects.remove(o);
			idList.remove(o);
		}
		toRemove.clear();
	}

	private void setInActiveState(IControllable c) {
		if (StateHandler.isInEdit() && c instanceof IActivatable && !((IActivatable) c).isActivated()) {
			IActivatable a = ((IActivatable) c);
			// if(a instanceof IColorable && a.getInActiveState() != null) {
			// ((IColorable)
			// a).setColor(Colors.getNearestColor(((IColorable)a).getColor()));
			// }
			a.setInActiveState(
			// (IActivatable) c.clone()
			);
		}
	}

	boolean isSelecting = true;

	IControllable lastSelected;
	Glow lastGlow;

	HashMap<IControllable, Integer> selectMap = new HashMap<>();
	IControllable lowestObject;
	int lowest;

	@Override
	public void mousePressed(int button, int x, int y) {
		if (isSelecting) {
			if (button == 0) {

				// deselect last
				if (!isMouseOnObject(lastSelected)) {
					deselect();
				}
				selectMap.keySet().removeIf((c) -> !gameObjects.contains(c));

				for (Object object : gameObjects) {
					if (object instanceof IControllable) {
						IControllable c = (IControllable) object;
						if (!c.isMovable() && !c.isRotatable() && !StateHandler.isInEdit()) {
							continue;
						}

						if (isMouseOnObject(c)) {

							selectMap.putIfAbsent(c, 0);

							deselect();
						} else {
							selectMap.remove(c);
						}
					}
				}

				lowest = Integer.MAX_VALUE;
				lowestObject = null;

				selectMap.forEach((o, i) -> {
					if (lowest > i) {
						lowestObject = o;
						lowest = i;
					}
				});
				selectMap.forEach((o, i) -> selectMap.put(o, i - lowest));
				selectMap.computeIfPresent(lowestObject, (o, i) -> i + 1);
				select(lowestObject);
			}
		}
		super.mousePressed(button, x, y);
	}

	private boolean isMouseOnObject(IControllable c) {
		if (c == null)
			return false;
		return Tools.isPointInRectangle(camera.screenToWorldPoint(Actions.mousePosition), c.getPosition(),
				c.getApparentSize(Direction.width), c.getApparentSize(Direction.height), c.getRotation());
	}

	protected void select(IControllable c) {
		if (c == null)
			return;
		lastGlow = create(new Glow(c));
		c.setSelected(true);
		lastSelected = c;
		changed();

	}

	protected void deselect() {
		remove(lastGlow);
		if (lastSelected != null)
			lastSelected.setSelected(false);
		lastSelected = null;
	}

	public void changed() {

		Lists.forEach(gameObjects, o -> o instanceof IControllable, o -> IControllable.restrict((IControllable) o));

	}

	public IPositionable getNearestObject() {

		IPositionable positionable = null;
		float distance = Float.MAX_VALUE;
		for (Object object : gameObjects) {
			if (object instanceof IActivatable && ((IActivatable) object).isState() || object instanceof Frame
					|| object instanceof Panel)
				continue;
			if (object instanceof IPositionable) {
				float dis = ((IPositionable) object).getPosition()
						.distance(camera.screenToWorldPoint(new Vector2f(Graphic.width / 2, Graphic.height / 2)));
				if (dis < distance) {
					distance = dis;
					positionable = (IPositionable) object;
				}
			}
		}
		return positionable;
	}

	public IControllable getSelected() {
		if (!gameObjects.contains(lastSelected)) {
			deselect();
		}
		return lastSelected;
	}

	public void win() {
		System.out.println("WON!");
	}

}
