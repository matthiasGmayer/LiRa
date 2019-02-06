package gameStates;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class StateHandler {

	private static List<BasicState> states = new LinkedList<BasicState>();

	public static int getID(BasicState s) {
		if (!states.contains(s))
			states.add(s);
		return states.indexOf(s);
	}

	public static int transitionTime = 200;

	private static BasicState currentState;

	public static void enterState(StateBasedGame sbg, Class<? extends BasicState> c) {
		for (BasicState s : states) {
			if (s.getClass() == c) {
				currentState = s;
				sbg.enterState(getID(s), new FadeOutTransition(Color.black, transitionTime), null);
			}
		}
	}

	public static BasicState getCurrentState() {
		return currentState;
	}

	public static boolean isInEdit() {
		return currentState instanceof Edit;
	}

	public static boolean isInGame() {
		return currentState instanceof Game;
	}

	public static <T extends BasicState> T getState(Class<T> c) {
		for (BasicState s : states) {
			if (s.getClass() == c)
				return c.cast(s);
		}
		return null;
	}

	public static void clear() {
		states.clear();
		currentState = null;
	}

}
