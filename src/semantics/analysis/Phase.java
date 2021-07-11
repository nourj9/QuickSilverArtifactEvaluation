package semantics.analysis;

import java.util.HashSet;
import java.util.Iterator;

import semantics.core.LocalState;

public abstract class Phase implements Iterable<LocalState> {
	protected PhaseAnalysis pa;
	protected HashSet<LocalState> states;
	protected T type;
	public static enum T {
		src, dst, singeleton, merged
	};

	public Phase(PhaseAnalysis pa, T type) {
		this.pa = pa;
		this.type = type;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Phase) {
			Phase other = (Phase) obj;
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

	public T getType() {
		return type;
	}
	
}
