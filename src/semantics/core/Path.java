package semantics.core;

import java.util.ArrayList;
import java.util.HashSet;

public class Path {

	private ArrayList<LocalTransition> transitions = new ArrayList<LocalTransition>();
	private HashSet<LocalState> states = new HashSet<LocalState>();

	private HashSet<LocalTransition> nonFreeUsableTransitions = new HashSet<LocalTransition>();

	public Path() {
	}

	@SuppressWarnings("unchecked")
	public Path(Path path) {
		transitions = (ArrayList<LocalTransition>) path.transitions.clone();
		states = (HashSet<LocalState>) path.states.clone();
		nonFreeUsableTransitions = (HashSet<LocalTransition>) path.nonFreeUsableTransitions.clone();
	}

	public Path join(Path after) {
		Path result = new Path(this);

		for (LocalTransition tr : after.getTransitions()) {
			// same as !tr.isFree() && !tr.isUnusable()
			if (!(tr.isFree() || tr.isUnusable())) {
				result.nonFreeUsableTransitions.add(tr);
			}
		}

		result.states.addAll(after.states);
		result.transitions.addAll(after.transitions);
		return result;
	}

	public boolean containsNonFreeUsableTransitions() {
		return !nonFreeUsableTransitions.isEmpty();
	}

	/* ignores self-loops */
	public boolean addTransition(LocalTransition tr) {

		if (tr.isSelfLoop()) {
			return false;
		}

		if (states.contains(tr.getDest())) {
			return false;
		}

		// same as !tr.isFree() && !tr.isUnusable()
		if (!(tr.isFree() || tr.isUnusable())) {
			nonFreeUsableTransitions.add(tr);
		}

		transitions.add(tr);
		states.add(tr.getSrc());
		states.add(tr.getDest());
		return true;
	}

	/*
	 * public HashSet<LocalTransition> getNonFreeUsableTransitions() {
	 * HashSet<LocalTransition> result = new HashSet<LocalTransition>();
	 * 
	 * for (LocalTransition tr : transitions) {
	 * 
	 * // same as !tr.isFree() && !tr.isUnusable() if(!(tr.isFree() ||
	 * tr.isUnusable())) { result.add(tr); }
	 * 
	 * }
	 * 
	 * return result; }
	 */
	public HashSet<LocalTransition> getNonFreeUsableTransitions() {
		return nonFreeUsableTransitions;
	}

	public boolean addTransitionEvenSelfLoops(LocalTransition tr) {

		if (states.contains(tr.getDest())) {
			return false;
		}

		// same as !tr.isFree() && !tr.isUnusable()
		if (!(tr.isFree() || tr.isUnusable())) {
			nonFreeUsableTransitions.add(tr);
		}

		transitions.add(tr);
		states.add(tr.getSrc());
		states.add(tr.getDest());
		return true;
	}

	/* ignores self-loops */
	public boolean addTransitionIfNotPresent(LocalTransition tr) {

		if (tr.isSelfLoop()) {
			return false;
		}

		if (transitions.contains(tr)) {
			return false;
		}

		// same as !tr.isFree() && !tr.isUnusable()
		if (!(tr.isFree() || tr.isUnusable())) {
			nonFreeUsableTransitions.add(tr);
		}

		transitions.add(tr);
		states.add(tr.getSrc());
		states.add(tr.getDest());
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Path) {
			Path other = (Path) obj;
			return this.transitions.equals(other.transitions);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (LocalTransition tr : transitions) {
			b.append(tr.getSrc().toSmallString()).append(" ").append("--")
					.append(tr.isEnvironmentTransition() ? "E:" : "").append(tr.getPunctuatedLabel()).append("--> ");
		}
		b.append(transitions.get(transitions.size() - 1).getDest().toSmallString());
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

	public void setTransitions(ArrayList<LocalTransition> transitions) {
		this.transitions = transitions;
	}

	public HashSet<LocalState> getStates() {
		return states;
	}

	public void setStates(HashSet<LocalState> states) {
		this.states = states;
	}

	public LocalState getInitialState() {

		if (transitions.isEmpty()) {
			return null;
		}

		return transitions.get(0).getSrc();
	}

}
