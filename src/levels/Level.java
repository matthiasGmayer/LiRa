package levels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entites.Activator;
import entites.ColoredGlass;
import entites.Goal;
import entites.IActivatable;
import entites.IControllable;
import entites.ILoadable;
import entites.LightEmitter;
import entites.Mirror;
import entites.Wall;
import tools.BidirectionalMap;
import tools.CSV;

public class Level implements Iterable<ILoadable> {

	public static final String standardDirectory = "levels/";
	public static final BidirectionalMap<String, Class<? extends ILoadable>> classMap = new BidirectionalMap<String, Class<? extends ILoadable>>();
	static {
		add(Mirror.class);
		add(ColoredGlass.class);
		add(LightEmitter.class);
		add(Wall.class);
		add(Activator.class);
		add(Goal.class);
	}

	private static void add(Class<? extends ILoadable> c) {
		classMap.put(c.getSimpleName().toLowerCase(), c);
	}

	private List<ILoadable> gameObjects = new ArrayList<ILoadable>();
	private String path = "";

	public Level(CSV csv) {
		path = csv.getPath();
		load(csv);
	}

	public Level(String path) {
		load(this.path = path.replace("!", standardDirectory));
	}

	public Level(List<ILoadable> gameObjects) {
		super();
		copyIntoList(gameObjects);
	}

	public Level(List<ILoadable> gameObjects, String path) {
		super();
		copyIntoList(gameObjects);
		this.path = path.replace("!", standardDirectory);
		save();
	}

	private void copyIntoList(List<ILoadable> l) {
		gameObjects.clear();
		l.forEach(i -> gameObjects.add(i));
	}

	public Level() {
	}

	public void load(CSV csv) {
		gameObjects.clear();
		Class<?> last = null;
		ILoadable i = null;
		for (ArrayList<String> l : csv) {
			Class<?> c = classMap.get(l.get(0));
			if (c == null) {
				if (last == null) {
					continue;
				}
				try {
					ILoadable i2 = (ILoadable) last.newInstance();
					if (i instanceof IActivatable && i instanceof IControllable) {
						i2.load(l);
						IActivatable a;
						((IActivatable) i).setActiveState(a = (IActivatable) i2);
						a.setState(true);

						gameObjects.add(i2);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				try {
					i = (ILoadable) c.newInstance();
					try {
						i.load(l);
						if (i instanceof IActivatable) {
							((IActivatable) i).setInActiveState();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					gameObjects.add(i);

				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			last = c;
		}
	}

	public void load(String path) {
		load(new CSV(path.replace("!", standardDirectory)));
	}

	public void load() {
		load(path);
	}

	public void save(CSV csv) {
		csv.clear();
		for (ILoadable i : gameObjects) {
			String s = classMap.getKey(i.getClass());
			ILoadable l = null;
			if (i instanceof IActivatable) {
				IActivatable a = (IActivatable) i;
				if (a.isState()) {
					continue;
				} else {
					l = (ILoadable) a.getInActiveState();
					if (l != null) {
						i = l;
					}
					l = (ILoadable) a.getActiveState();
				}
			}
			i.save(csv, s);
			if (l != null)
				l.save(csv, "<" + s + ">");

		}
		csv.write();
	}

	public void save(String path) {
		CSV csv = new CSV();
		csv.setPath(path.replace("!", standardDirectory));
		save(csv);
	}

	public void save() {
		save(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path.replace("!", standardDirectory);
	}

	@Override
	public Iterator<ILoadable> iterator() {
		return gameObjects.iterator();
	}

	public int size() {
		return gameObjects.size();
	}

}
