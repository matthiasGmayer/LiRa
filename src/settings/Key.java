package settings;

public class Key {
	public final int ID;
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
}
