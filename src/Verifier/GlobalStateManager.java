package Verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import semantics.core.LocalState;
import semantics.core.ProcessSemantics;

public class GlobalStateManager {
	private ProcessSemantics ps;
	private HashMap<LocalState, Integer> stToIndex = null;
	private LocalState[] indexToSt = null;
	private int s; // local state space size
	private ModelChecker mc;
	private HashSet<GlobalState> allStates;
	private GlobalState initialState;

	public GlobalStateManager(ModelChecker mc) {
		this.mc = mc;
		this.ps = mc.getProcessSemantics();
		this.s = mc.getLocalStateSpaceSize();
		this.allStates = new HashSet<GlobalState>();
		computeStateToIndexMap();
	}

	public void computeStateToIndexMap() {

		if (stToIndex != null) {
			return;
		}

		stToIndex = new HashMap<LocalState, Integer>();
		HashSet<LocalState> localStates = ps.getStates();
		indexToSt = new LocalState[localStates.size()];

		int i = 0;
		for (LocalState localState : localStates) {
			indexToSt[i] = localState;
			stToIndex.put(localState, i++);
		}
	}

	public HashMap<LocalState, Integer> getStateToIndexMap() {
		return stToIndex;
	}

	public LocalState[] getIndexToStateMap() {
		return indexToSt;
	}

	public int getIndexOf(LocalState localState) {
		return stToIndex.get(localState);
	}

	public LocalState getStateOf(int index) {
		return indexToSt[index];
	}

	public GlobalState makeGlobalState(int[] values, boolean initial) {
		GlobalState gs = new GlobalState(this, values, initial);
		allStates.add(gs);
		return gs;
	}

	public GlobalState makeGlobalState(int[] values) {
		return makeGlobalState(values, false);
	}

	public GlobalState getInitialState() {
		// as an optimization, compute it once.
		if (initialState == null) {
			int[] values = new int[s];
			LocalState initialLocalState = ps.getInitialState();
			int index = getIndexOf(initialLocalState);
			values[index] = mc.getCutoff();
			initialState = makeGlobalState(values, true);
		}
		return initialState;
	}

	public HashSet<GlobalState> getAllStates() {
		return allStates;
	}

	public ProcessSemantics getProcessSemantics() {
		return ps;
	}

}
