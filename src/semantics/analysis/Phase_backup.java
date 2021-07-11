package semantics.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import semantics.core.LocalState;
import semantics.core.LocalTransition;
import utils.MyStringBuilder;

public abstract class Phase_backup implements Iterable<LocalState> {
	private PhaseAnalysis pa;
	private HashSet<LocalState> states;
	private T type;
	private String label;
	private HashSet<LocalState> coreStates = null;
	// key = expanded state, val = through which transition.
	private HashMap<LocalState, LocalTransition> destExpantions; 
	private HashMap<LocalState, LocalTransition> srcExpantions;

	public static enum T {
		src, dst, singeleton, expanded, merged
	};

	public Phase_backup(PhaseAnalysis pa, T type, HashSet<LocalState> states, String label) {
		this.states = states;
		this.pa = pa;
		this.type = type;
		this.label = label;
		destExpantions = new HashMap<LocalState, LocalTransition>();
		srcExpantions = new HashMap<LocalState, LocalTransition>();
	}

	public Phase_backup(PhaseAnalysis pa, T type) {
		this(pa, type, new HashSet<LocalState>(), "");
	}

	// mirror the behavior of hashsets.
	public boolean add(LocalState st) {
		return states.add(st);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Phase_backup) {
			Phase_backup other = (Phase_backup) obj;
			return states.equals(other.states);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return states.hashCode();
	}

	public boolean contains(LocalState st) {
		return states.contains(st);
	}

	public Iterator<LocalState> iterator() {
		return states.iterator();
	}

	public HashSet<LocalState> getStates() {
		return states;
	}

	@Override
	public String toString() {
		MyStringBuilder sb = new MyStringBuilder();

		sb.apps("Phase{ type = " + type + ", label = " + label + ", states = ");
		for (LocalState ls : states) {
			sb.app(ls.toSmallString());
		}

		sb.app("}");
		return sb.toString();

	}

	public void expandToDestThrough(LocalTransition t) {

		states.add(t.getDest());
	}

	public void expandToSrcThrough(LocalTransition t) {
		states.add(t.getSrc());
	}

}
