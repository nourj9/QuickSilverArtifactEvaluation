package liveness.buchi;

import java.util.ArrayList;
import java.util.HashSet;

public class BuchiAutomaton {

	private HashSet<BuchiState> states;
	private BuchiState initialState; // can be made a set
	private ArrayList<BuchiTransition> transitions;

	public BuchiAutomaton() {
		this.states = new HashSet<BuchiState>();
		this.transitions = new ArrayList<BuchiTransition>();
	}

	public void addState(BuchiState state) {

		states.add(state);

		if (state.isInitial()) {
			initialState = state;
		}
	}

	public void addTransition(BuchiState src, BuchiState dest, LivenessExpression label) {

		BuchiTransition trans = new BuchiTransition(src, dest, label);
		transitions.add(trans);
	}

	public HashSet<BuchiState> getStates() {
		return states;
	}

	public BuchiState getInitialState() {
		return initialState;
	}

	public ArrayList<BuchiTransition> getTransitions() {
		return transitions;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("States(" + states.size() + "): \n");
		for (BuchiState ls : states) {
			sb.append("\t" + ls.toString() + "\t\t");
			if (ls.isInitial()) {
				sb.append("initial ");
			}
			if (ls.isAccepting()) {
				sb.append("accepting ");
			}
			if (ls.isFinal()) {
				sb.append("final ");
			}

			sb.append("\n");
		}

		sb.append("\nTransitions(" + transitions.size() + "): \n");
		for (BuchiTransition lt : transitions) {
			sb.append("\t" + lt.toString() + "\n");

		}

		return sb.toString();
	}

}
