package semantics.core;

import java.util.ArrayList;
import java.util.HashSet;

public class Trail {

	private ArrayList<LocalTransition> transitions = new ArrayList<LocalTransition>();
	private HashSet<LocalState> states = new HashSet<LocalState>();
	private LocalState initialCoreState;

	public Trail() {
	}

	@SuppressWarnings("unchecked")
	public Trail(Trail path) {
		transitions = (ArrayList<LocalTransition>) path.transitions.clone();
		states = (HashSet<LocalState>) path.states.clone();
		initialCoreState = path.initialCoreState;
	}

	public void addLast(LocalTransition tr) {
		transitions.add(tr);
		states.add(tr.getSrc());
		states.add(tr.getDest());
	}

	public void addFirst(LocalTransition tr) {
		transitions.add(0, tr);
		states.add(tr.getSrc());
		states.add(tr.getDest());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Trail) {
			Trail other = (Trail) obj;
			return this.transitions.equals(other.transitions);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (LocalTransition tr : transitions) {
			b.append(tr.toString() + "\n");
		}
		return b.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public LocalState getLastState() {

		if (transitions.isEmpty())
			return null;

		return transitions.get(transitions.size() - 1).getDest();
	}

	public ArrayList<LocalTransition> getTransitions() {
		return transitions;
	}

	public HashSet<LocalState> getStates() {
		return states;
	}

	public LocalState getInitialCoreState() {
		return initialCoreState;
	}

	public void setInitialCoreState(LocalState initialCoreState) {
		this.initialCoreState = initialCoreState;
	}

	public boolean stateIsDestOfSomeTrInTrail(LocalState ls) {
		for (LocalTransition tr : transitions) {
			if (tr.getDest().equals(ls)) {
				return true;
			}
		}
		return false;
	}

}
