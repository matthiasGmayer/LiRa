package gameStates;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;

import effects.FadeTransition;

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
			if (c.isInstance(s)) {
				System.out.println("Entering " + c);
				currentState = s;
				sbg.enterState(getID(s), new FadeTransition(transitionTime, Color.black, false), null);
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

}
