package util;

public interface ButtonAction {
	default public void onPress(Object source) {
	}

	default public void onDoublePress(Object source) {
	}

	default public void onRelease(Object source) {
	}

	default public void onDeselect(Object source) {
	}

	default public void onEnter(Object source) {
	}

	default public void onLeave(Object source) {
	}
}
