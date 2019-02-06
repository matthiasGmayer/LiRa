package util;

import sound.Sounds;

public interface ButtonAction {
	default public void onPress(Object source) {
		Sounds.play("clap");
	}

	default public void onDoublePress(Object source) {
	}

	default public void onRelease(Object source) {
		Sounds.play("knock");
	}

	default public void onDeselect(Object source) {
	}

	default public void onEnter(Object source) {
	}

	default public void onLeave(Object source) {
	}
}
