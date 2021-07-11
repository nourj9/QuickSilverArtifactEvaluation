package Verifier;

import java.util.ArrayList;
import java.util.HashSet;

import lang.core.Protocol;
import lang.specs.SafetySpecification;
import lang.stmts.Pair;
import semantics.core.ProcessSemantics;
import trace.*;

public class ModelChecker {

	private ProcessSemantics ps;
	private SafetySpecification specs;
	private int cutoff;
	private int s; // local state space size
	private HashSet<GlobalState> visitedStates;
	private HashSet<GlobalState> errorStates;
	private HashSet<GlobalState> deadlockStates;
	private GlobalTransitionManager gtm;
	private GlobalStateManager gsm;
	private HashSet<Action> actions;
	private TraceFinder traceFinder;
	// private int i;
	// traces
	// BFS

	public ModelChecker(ProcessSemantics ps, SafetySpecification specs, int cutoff) {
		this.ps = ps;
		this.specs = specs;
		this.cutoff = cutoff;
		this.visitedStates = new HashSet<GlobalState>();
		this.errorStates = new HashSet<GlobalState>();
		this.deadlockStates = new HashSet<GlobalState>();
		// order here matters
		this.gsm = new GlobalStateManager(this);
		this.gtm = new GlobalTransitionManager(this);
		this.s = getLocalStateSpaceSize();
		this.actions = gtm.getActions();
		this.traceFinder = new TraceFinder(this);
	}

	public ProcessSemantics getProcessSemantics() {
		return ps;
	}

	public GlobalStateManager getGlobalStateManager() {
		return gsm;
	}

	public GlobalTransitionManager getGlobalTransitionManager() {
		return gtm;
	}

	public int getLocalStateSpaceSize() {
		return ps.getStates().size();
	}

	public int getCutoff() {
		return cutoff;
	}

	public boolean buildGTS() {
		ArrayList<GlobalState> BFSQueue = new ArrayList<GlobalState>();

		GlobalState initState = gsm.getInitialState();
		BFSQueue.add(initState);

		while (!BFSQueue.isEmpty()) {

			GlobalState curState = BFSQueue.remove(0);
			visitedStates.add(curState);
			boolean deadlock = true;

			for (Action action : actions) {

				ArrayList<Pair<GlobalState, VectorPair>> nextStates = gtm.getNextStates(curState, action);

				if (!nextStates.isEmpty()) {

					deadlock = false;

					for (Pair<GlobalState, VectorPair> nsPair : nextStates) {

						GlobalState nextState = nsPair.getFirst();
						VectorPair sendVecPair = nsPair.getSecond();

						gtm.makeGlobalTransition(action, sendVecPair, curState, nextState);

						if (isErrorState(nextState)) {
							errorStates.add(nextState);
							continue;
						}

						if (!visitedStates.contains(nextState)) {
							BFSQueue.add(nextState);
							// System.out.println(i++);
							// System.out.println(nextState);
						}
					}
				}
			}

			if (deadlock) {
				deadlockStates.add(curState);
			}
		}

		return errorStates.isEmpty() && deadlockStates.isEmpty();
	}

	private boolean isErrorState(GlobalState nextState) {
		return !nextState.isSafe(specs);
	}

	public void dumpCurrentState() {
		StringBuilder sb = new StringBuilder();

		sb.append("All States(" + gsm.getAllStates().size() + ")\n");
		// sb.append(gsm.getAllStates());
		sb.append("\nAll Transitions(" + gtm.getAllTransitions().size() + ")\n");
		// for (GlobalTransition tr : gtm.getAllTransitions()) {
		// sb.append(tr).append("\n");
		// }
		sb.append("\nVisited States(" + visitedStates.size() + ")\n");
		// sb.append(visitedStates);
		sb.append("\nDeadlock States(" + deadlockStates.size() + ")\n");
		sb.append(deadlockStates);
		sb.append("\nError States(" + errorStates.size() + ")\n");
		sb.append(errorStates);

		System.out.println(sb.toString());
	}

	public ArrayList<Trace> getTracesForErrorStates() {

		ArrayList<Trace> traces = new ArrayList<Trace>();

		for (GlobalState errorState : errorStates) {
			traces.add(traceFinder.findTrace(gsm.getInitialState(), errorState));
		}

		return traces;
	}

	public ArrayList<Trace> getTracesForDeadlockStates() {

		ArrayList<Trace> traces = new ArrayList<Trace>();

		for (GlobalState deadlockState : deadlockStates) {
			traces.add(traceFinder.findTrace(gsm.getInitialState(), deadlockState));
		}

		return traces;
	}

	public Protocol getProtocol() {
		return ps.getProtocol();
	}

	// public void dummyTest() {
	// GlobalState initialState = gsm.getInitialState();
	// System.out.println(initialState);
	// System.out.println(initialState.prettyString());
	//
	// for (String action : actions) {
	// ArrayList<Pair<GlobalState, VectorPair>> nextStates =
	// gtm.getNextStates(initialState, action);
	// System.out.println("on action: " + action + " next states are");
	// for (Pair<GlobalState, VectorPair> ns : nextStates) {
	// System.out.println(ns.getFirst().prettyString());
	// }
	// }
	// }
}
