package settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;

import tools.CSV;
import util.KeyListener;

public class Actions {
	private static ArrayList<Key> keyList = new ArrayList<Key>();

	private static HashMap<Controlls, Key[]> keyMap = new HashMap<Controlls, Key[]>();
	private static Key lastKey;

	private static ArrayList<KeyListener> subscribers = new ArrayList<KeyListener>();

	private static float wheelAmount;

	public static final Vector2f mousePosition = new Vector2f();

	public static void setPressed(int iD, boolean pressed) {
		lastKey = getByID(iD);
		lastKey.setPressed(pressed);
		if (pressed) {
			for (KeyListener keyL : subscribers) {
				keyL.keyPressed(iD);
			}
		}
	}

	public static int getLastKeyPressed() {
		return lastKey.ID;
	}

	private static Key getByID(int iD) {
		for (Key k : keyList)
			if (k.ID == iD)
				return k;
		Key key;
		keyList.add(key = new Key(iD));
		return key;
	}

	public static void subscribe(KeyListener object) {
		subscribers.add(object);
	}

	static {

		CSV k = new CSV("!keys");
		File file = new File(k.getPath());
		if (!file.exists()) {
			k.add("cameraUp", "200", "17");
			k.add("cameraLeft", " 203", " 30");
			k.add("cameraDown", " 208", " 31");
			k.add("cameraRight", " 205", " 32");
			k.add("leftMouse", " -3");
			k.add("rightMouse", " -2");
			k.add("middleMouse", " -1");
			k.write();
		}

		for (Controlls c : Controlls.values()) {
			try {
				ArrayList<String> s = k.get(c.name());
				Key[] l = getKeys(s);
				keyMap.put(c, l);
				for (Key t : l) {
					keyList.add(t);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static Key[] getKeys(ArrayList<String> row) {
		Key[] k = new Key[row.size() - 1];
		for (int i = 1; i < row.size(); i++) {
			k[i - 1] = new Key(Integer.parseInt(row.get(i)));
		}
		return k;
	}

	private static boolean isPressed(Key[] keys) {
		for (Key k : keys) {
			if (k != null && k.isPressed())
				return true;
		}
		return false;
	}

	public static boolean is(Controlls c) {
		return isPressed(keyMap.get(c));
	}

	public static boolean isPressed(int iD) {
		Key key = getByID(iD);
		if (key == null)
			return false;
		return key.isPressed();
	}

	public static float getWheelAmount() {
		return wheelAmount;
	}

	public static void addWheelAmount(float wheelAmount) {
		Actions.wheelAmount += wheelAmount;
	}

}
