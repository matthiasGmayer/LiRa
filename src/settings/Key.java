package settings;

import org.newdawn.slick.Input;

public class Key {
	private int ID;
	private boolean pressed;

	public Key(int iD) {
		super();
		ID = iD;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public String toString() {
		return Input.getKeyName(ID);
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
}
