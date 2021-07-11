package semantics.analysis;

import java.util.HashMap;
import java.util.HashSet;

import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.Trail;
import utils.MyStringBuilder;

public class CorePhase extends Phase {
	private String label = "";
	// key = expanded state, val = through which transition.
	private HashMap<LocalState, LocalTransition> destExpantions;
	private HashMap<LocalState, LocalTransition> srcExpantions;

	public CorePhase(PhaseAnalysis pa, Phase.T type, HashSet<LocalState> states, String label) {
		super(pa, type);
		this.states = new HashSet<>(states); // this is important because we edit this set!
		this.label = label;
		destExpantions = new HashMap<LocalState, LocalTransition>();
		srcExpantions = new HashMap<LocalState, LocalTransition>();
	}

	@Override
	public String toString() {
		MyStringBuilder sb = new MyStringBuilder();

		sb.app("Core " + type + (label.equals("") ? "" : "(" + label + ")") + " Phase (" + states.size() + "){");

		boolean isFirst = true;
		for (LocalState ls : states) {

			if (isFirst) {
				isFirst = false;
			} else {
				sb.app(", ");
			}

			if (srcExpantions.containsKey(ls)) {
				// sb.app(ls.toSmallString() + " added via expantion " + srcExpantions.get(ls));
				sb.app(ls.toSmallString() + " (expan)");
			} else if (destExpantions.containsKey(ls)) {
				// sb.app(ls.toSmallString() + " added via expantion: " +
				// srcExpantions.get(ls));
				sb.app(ls.toSmallString() + " (expan)");
			} else {
				sb.app(ls.toSmallString() + " (core)");
			}
		}

		sb.app("}");
		return sb.toString();

	}

	public void expandToSrcThrough(LocalTransition tr) {
		srcExpantions.put(tr.getSrc(), tr);
		states.add(tr.getSrc());
	}

	public void expandToDestThrough(LocalTransition tr) {
		destExpantions.put(tr.getDest(), tr);
		states.add(tr.getDest());
	}

	public boolean isCoreState(LocalState s) {
		return !isExpandedState(s);
	}

	public boolean isExpandedState(LocalState s) {
		return srcExpantions.containsKey(s) || destExpantions.containsKey(s);
	}

	public String getLabel() {
		return label;
	}

	// if the state is core nothing happens, otherwise, you get a trail
	public Trail buildTrailToOrNullIfStateIsCore(LocalState s) {

		if (isCoreState(s)) {
			return null;
		}

		return buildTrailTo(s);
	}

	private Trail buildTrailTo(LocalState s) {

		LocalState currentState = s;
		Trail trail = new Trail();

		while (!isCoreState(currentState)) {

			// how was this added?
			LocalTransition adderTr = null;
			if (srcExpantions.containsKey(currentState)) {
				adderTr = srcExpantions.get(currentState); // this entry is {currentState, currentState --> something}
				currentState = adderTr.getDest(); // the something
			} else if (destExpantions.containsKey(currentState)) {
				adderTr = destExpantions.get(currentState); // this entry is {currentState, something --> currentState}
				currentState = adderTr.getSrc(); // the something
			} else {
				System.err.println("shouldn't happen, but here we are..");
				Thread.dumpStack();
				System.exit(0);
			}

			trail.addFirst(adderTr); // we're building this backwards.
			trail.setInitialCoreState(currentState); // the last one will be correct.
		}

		return trail;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj == null) {
//			return false;
//		}
//
//		if (obj == this) {
//			return true;
//		}
//
//		if (obj instanceof CorePhase) {
//			CorePhase other = (CorePhase) obj;
//			return states.equals(other.states) && label.equals(other.label);
//		}
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//		return toString().hashCode();
//	}

}