package settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import tools.CSV;
import util.KeyListener;

public class Actions {
	private static ArrayList<Key> keyList = new ArrayList<Key>();

	private static HashMap<Controls, Key[]> keyMap = new HashMap<Controls, Key[]>();
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
		return lastKey.getID();
	}

	private static Key getByID(int iD) {
		for (Key k : keyList)
			if (k.getID() == iD)
				return k;
		Key key;
		keyList.add(key = new Key(iD));
		return key;
	}

	public static void subscribe(KeyListener object) {
		subscribers.add(object);
	}

	static {

		reload();

	}

	private static Key[] getKeys(ArrayList<String> row) {
		Key[] k = new Key[row.size() - 1];
		for (int i = 1; i < row.size(); i++) {
			k[i - 1] = new Key(Integer.parseInt(row.get(i)));
		}
		return k;
	}

	public static Key[] getIds(Controls c) {
		return keyMap.get(c);
	}

	public static void setKeys(Controls c, Key[] k) {
		keyMap.put(c, k);
	}

	public static void save() {
		CSV k = new CSV();
		int r = 0;
		for (Controls c : Controls.values()) {
			Key[] array = keyMap.get(c);
			k.add(r, c.name());
			for (int i = 0; i < array.length; i++) {
				k.add(r, Integer.toString(array[i].getID()));
			}
			r++;
		}
		k.write("!keys");
	}

	public static void reload() {

		keyList.clear();
		keyMap.clear();

		CSV k = new CSV("!keys");
		File file = new File(k.getPath());
		try {
			if (!file.exists() || new BufferedReader(new FileReader(file)).readLine() == null) {
				k.add("cameraUp", "200", "17");
				k.add("cameraLeft", "203", "30");
				k.add("cameraDown", "208", "31");
				k.add("cameraRight", "205", "32");
				k.add("leftMouse", "-3");
				k.add("rightMouse", "-2");
				k.add("middleMouse", "-1");
				k.add("limitMovement", "29", "-4");
				k.add("copy", "42", "-4");
				k.add("delete", "45", "-4");
				k.write();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (Controls c : Controls.values()) {
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

	private static boolean isPressed(Key[] keys) {
		for (Key k : keys) {
			if (k != null && k.isPressed())
				return true;
		}
		return false;
	}

	public static boolean is(Controls c) {
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

	public static String getName(Key k) {
		switch (k.getID()) {
		case -1:
			return "MOUSE LEFT";
		case -2:
			return "MOUSE RIGHT";
		case -3:
			return "MOUSE MIDDLE";
		case -4:
			return "NONE";
		default:
			try {
				return Input.getKeyName(k.getID());
			} catch (Exception e) {
				return "NONE";
			}
		}
	}

}
