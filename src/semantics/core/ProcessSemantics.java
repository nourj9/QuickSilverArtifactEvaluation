package semantics.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import CEgeneralizations.ExistenceChecker;
import Core.Options;
import lang.core.Protocol;
import semantics.analysis.PhaseAnalysis;
import semantics.core.LocalAction.ActionType;

public class ProcessSemantics implements Cloneable {

	/***************************************
	 * Private Member Variables
	 ***************************************/
	private Protocol p;
	private Options options;
	private HashSet<LocalState> states;
	private ArrayList<LocalTransition> transitions;
	private HashMap<String, HashSet<LocalTransition>> globallySyncLabels;

	// useful for global stuff:
	private HashMap<String, HashSet<LocalTransition>> broadcastLabels;
	private HashMap<String, HashSet<LocalTransition>> valstoreLabels;
	private HashMap<String, HashSet<LocalTransition>> partitionconsLabels;

	private HashMap<String, HashSet<LocalTransition>> pairwiseLabels;
	private HashMap<String, HashSet<LocalTransition>> internalLabels;
	private HashMap<String, HashSet<LocalTransition>> envLabels;
	private LocalState initialState;
	// Source/destination set caches for performance
	private HashMap<String, HashSet<LocalState>> sourceSetCache = new HashMap<String, HashSet<LocalState>>();
	private HashMap<String, HashSet<LocalState>> destinationSetCache = new HashMap<String, HashSet<LocalState>>();

	// PathFinder Object for optimized path computation
	private PathFinder pathfinder;
	private ExistenceChecker existenceChecker;
	private PhaseAnalysis phaseAnalysis;
	private LocalState crashState;

	public ProcessSemantics(Protocol p, LocalState initialState, LocalState crashState, HashSet<LocalState> states,
			ArrayList<LocalTransition> transitions, Options options) {
		this.p = p;

		this.options = options;

		// give the protocol a reference to its semantics.
		this.p.setSemantics(this);

		this.crashState = crashState;
		this.initialState = initialState;

		this.states = states;
		// add back links
		for (LocalState state : states) {
			state.setSemantics(this);
		}

		this.transitions = transitions;
		// add back links
		for (LocalTransition trans : transitions) {
			trans.setSemantics(this);
		}

		globallySyncLabels = new HashMap<String, HashSet<LocalTransition>>();

		broadcastLabels = new HashMap<String, HashSet<LocalTransition>>();
		valstoreLabels = new HashMap<String, HashSet<LocalTransition>>();
		partitionconsLabels = new HashMap<String, HashSet<LocalTransition>>();

		pairwiseLabels = new HashMap<String, HashSet<LocalTransition>>();
		internalLabels = new HashMap<String, HashSet<LocalTransition>>();
		envLabels = new HashMap<String, HashSet<LocalTransition>>();

		for (LocalTransition trans : transitions) {
			String label = trans.getLabel();
			if (trans.isEnvironmentTransition()) {
				initializeAndAddIfNotThere(envLabels, label, trans);
			}

			switch (trans.getTransitionType()) {
			case BROADCAST_RECV:
			case BROADCAST_SEND:

				initializeAndAddIfNotThere(broadcastLabels, label, trans);
				initializeAndAddIfNotThere(globallySyncLabels, label, trans);
				break;

			case PARTITION_CONS_LOSE:
			case PARTITION_CONS_WIN:

				initializeAndAddIfNotThere(partitionconsLabels, label, trans);
				initializeAndAddIfNotThere(globallySyncLabels, label, trans);
				break;

			case VALUE_CONS:

				initializeAndAddIfNotThere(valstoreLabels, label, trans);
				initializeAndAddIfNotThere(globallySyncLabels, label, trans);
				break;

			case PAIRWISE_RECV:
			case PAIRWISE_SEND:

				initializeAndAddIfNotThere(pairwiseLabels, label, trans);
				break;

			case INTERNAL:

				initializeAndAddIfNotThere(internalLabels, label, trans);
				break;
			}
		}
		pathfinder = new PathFinder(this, options);
		existenceChecker = new ExistenceChecker(this);
		phaseAnalysis = new PhaseAnalysis(this);
	}

	private void initializeAndAddIfNotThere(HashMap<String, HashSet<LocalTransition>> map, String key,
			LocalTransition trans) {
		if (!map.containsKey(key)) {
			map.put(key, new HashSet<LocalTransition>());
		}
		map.get(key).add(trans);
	}

	/***************************************
	 * Public Accessors
	 ***************************************/
	public HashSet<LocalState> getStates() {
		return states;
	}

	public ArrayList<LocalTransition> getTransitions() {
		return transitions;
	}

	/***************************************
	 * Public Member Functions
	 ***************************************/
	// Returns the source set of transitions with the given label
	public HashSet<LocalState> getSendingSet(String action) {
		HashSet<LocalState> startStateSet = new HashSet<LocalState>();

		for (LocalState sendingState : getSourceSet(action)) {
			for (LocalTransition outgoingTrans : sendingState.getOutgoingTransitions()) {
				if (outgoingTrans.getLabel().equals(action)) {
					if (outgoingTrans.isActingTransition()) {
						startStateSet.add(sendingState);
					}
				}
			}
		}

		return startStateSet;
	}

	public HashSet<LocalState> getSourceSet(String label) {
		if (sourceSetCache.containsKey(label)) {
			return sourceSetCache.get(label);
		}

		HashSet<LocalState> result = new HashSet<LocalState>();

		for (LocalTransition t : transitions) {
			if (t.getLabel().equals(label)) {
				result.add(t.getSrc());
			}
		}

		sourceSetCache.put(label, result);
		return result;
	}

	// Returns the destination set of transitions with the given label
	public HashSet<LocalState> getDestinationSet(String label) {
		if (destinationSetCache.containsKey(label)) {
			return destinationSetCache.get(label);
		}

		HashSet<LocalState> result = new HashSet<LocalState>();

		for (LocalTransition t : transitions) {
			if (t.getLabel().equals(label)) {
				result.add(t.getDest());
			}
		}

		destinationSetCache.put(label, result);
		return result;
	}

	// Return the set of labels on non-internal transitions
	public HashSet<String> getTransitionLabels() {

		HashSet<String> result = new HashSet<String>();

		for (LocalTransition t : transitions) {
			if (t.getTransitionType() != LocalAction.ActionType.INTERNAL) {
				result.add(t.getLabel());
			}
		}

		return result;
	}

	public String prettyString() {
		StringBuilder sb = new StringBuilder();

		sb.append("States(" + states.size() + "): \n");
		for (LocalState ls : states) {
			sb.append("\t" + ls.toString() + "\t\t" + (ls.isTentative() ? "tentative" : "not tentative") + "\n");
		}

		sb.append("\nTransitions(" + transitions.size() + "): \n");
		for (LocalTransition lt : transitions) {
			sb.append("\t" + lt.toString() + "\t\t" + (lt.hasHoles() ? "Holes: " + lt.getHolesIdsString() : "concrete")
					+ "\n");

		}
		return sb.toString();
	}

	public String detailString() {
		StringBuilder sb = new StringBuilder();

		sb.append("States: \n");
		for (LocalState ls : states) {
			sb.append("\t" + ls.toString() + "\n");
		}

		sb.append("\nTransitions: \n");
		for (LocalTransition lt : transitions) {
			sb.append("\t" + lt.detailedString() + "\n");

		}
		return sb.toString();
	}

	public LocalState getInitialState() {
		return initialState;
	}

	public LocalState getCrashState() {
		return crashState;
	}

	// Returns states have the same location as the initial state, and equivalent
	// variable valuations,
	// EXCEPT FOR SPECIAL value-consensus VARIABLES
	public HashSet<LocalState> getLogicalInitialStates() {

		HashSet<LocalState> result = new HashSet<LocalState>();
		result.add(initialState);

		// Experiment: expand the notion of "initial state" to leave special variables
		// unconstrained.
		outer: for (LocalState state : getStates()) {
			if (state.equals(initialState)) {
				continue;
			}
			if (!state.getLoc().equals(initialState.getLoc())) {
				continue;
			}

			for (String varName : p.getVarNames()) {
				if (isInvariantToControlFlow(varName)) {
					continue;
				}
				if (!state.getSigma().getValue(varName).equals(initialState.getSigma().getValue(varName))) {
					continue outer;
				}

				result.add(state);
			}
		}
		return result;
	}

	// LATER: include other ways to determine when variables can be ignored for the
	// sake of being in an "initial" state
	private boolean isInvariantToControlFlow(String varName) {
		return false;
	}

	// public void setInitialState(LocalState initialState) {
	// this.initialState = initialState;
	// }

	public HashMap<String, HashSet<LocalTransition>> getGloballySyncLabels() {
		return globallySyncLabels;
	}

	public HashMap<String, HashSet<LocalTransition>> getBroadcastLabels() {
		return broadcastLabels;
	}

	public HashMap<String, HashSet<LocalTransition>> getPartitionConsLabels() {
		return partitionconsLabels;
	}

	public HashMap<String, HashSet<LocalTransition>> getValueStoreLabels() {
		return valstoreLabels;
	}

	public void setGloballySyncLabels(HashMap<String, HashSet<LocalTransition>> globallySyncLabels) {
		this.globallySyncLabels = globallySyncLabels;
	}

	public HashMap<String, HashSet<LocalTransition>> getPairwiseLabels() {
		return pairwiseLabels;
	}

	public void setPairwiseLabels(HashMap<String, HashSet<LocalTransition>> pairwiseLabels) {
		this.pairwiseLabels = pairwiseLabels;
	}

	public HashMap<String, HashSet<LocalTransition>> getInternalLabels() {
		return internalLabels;
	}

	public void setInternalLabels(HashMap<String, HashSet<LocalTransition>> internalLabels) {
		this.internalLabels = internalLabels;
	}

	public HashMap<String, HashSet<LocalTransition>> getEnvLabels() {
		return envLabels;
	}

	public void setEnvLabels(HashMap<String, HashSet<LocalTransition>> envLabels) {
		this.envLabels = envLabels;
	}

	public boolean isGloballySynchronizingLabel(String label) {
		return globallySyncLabels.containsKey(label);
	}

	public boolean isBroadcastLabel(String label) {
		return broadcastLabels.containsKey(label);
	}

	public boolean isPartitionConsLabel(String label) {
		return partitionconsLabels.containsKey(label);
	}

	public boolean isValStoreLabel(String label) {
		return valstoreLabels.containsKey(label);
	}

	public boolean isPairwiseLabel(String label) {
		return pairwiseLabels.containsKey(label);
	}

	public LocalAction.ActionType getReceiveTypeOfLabel(String label) {
		if (isBroadcastLabel(label)) {
			return ActionType.BROADCAST_RECV;
		} else if (isPartitionConsLabel(label)) {
			return ActionType.PARTITION_CONS_LOSE;
		} else if (isValStoreLabel(label)) {
			return ActionType.VALUE_CONS;
		} else if (isPairwiseLabel(label)) {
			return ActionType.PAIRWISE_RECV;
		} else if (label.equals("")) {
			return ActionType.INTERNAL;
		} else {
			return ActionType.UNKNOWN;
		}
	}

	/**
	 * This function works as follows:
	 * 1. checks if both are there, otherwise just return.
	 * 2. deletes the transitions between the two states.
	 * 3. makes any (remaining) incoming transition of the state to delete an incoming transition of the state to stay	 
	 * 4. makes any (remaining) outgoing transition of the state to delete an outgoing transition of the state to stay
	 * 5. deletes the now-detached state.
	 * */

	public void collapseStateOneIntoTwo(LocalState stToRemove, LocalState stToStay) {

		if (!states.contains(stToRemove) || !states.contains(stToStay)) {
			return;
		}

		if (stToRemove.equals(stToStay)) {
			return;
		}

		// delete transitions between the states.
		HashSet<LocalTransition> transitionsToDelete = new HashSet<LocalTransition>();

		for (LocalTransition tr : transitions) {
			// System.out.println(tr);
			if ((tr.isInternalOrEnvPairwise()) && // tr is "removable"
					((tr.getSrc().equals(stToRemove) && tr.getDest().equals(stToStay)) || // stToRemove --> stToStay
																							// ,or,
							(tr.getSrc().equals(stToStay) && tr.getDest().equals(stToRemove)))) { // stToStay -->
																									// stToRemove
				transitionsToDelete.add(tr);
			}
		}
		deleteTransitions(transitionsToDelete);

		// make any (remaining) incoming transition of the state to delete an incoming
		// transition of the state to stay
		ArrayList<LocalTransition> incomingTrs = stToRemove.getIncomingTransitions();
		globallyChangeDestOfTrs(incomingTrs, stToStay);
		stToStay.getIncomingTransitions().addAll(incomingTrs);
		stToRemove.getIncomingTransitions().removeAll(incomingTrs);

		// make any (remaining) outgoing transition of the state to delete an outgoing
		// transition of the state to stay
		ArrayList<LocalTransition> outgoingTrs = stToRemove.getOutgoingTransitions();
		globallyChangeSrcOfTrs(outgoingTrs, stToStay);
		stToStay.getOutgoingTransitions().addAll(outgoingTrs);
		stToRemove.getOutgoingTransitions().removeAll(outgoingTrs);

		// delete the now-detached state.
		deleteState(stToRemove);

		// in case the deleted state happened to be the initial state, make the new on
		// the initial state.
		if (initialState.equals(stToRemove)) {
			initialState = stToStay;
		}

	}

	private void deleteState(LocalState stToRemove) {

		states.remove(stToRemove);

		// clear caches.
		sourceSetCache.clear();
		destinationSetCache.clear();

		pathfinder.cleanCaches();
	}

	private void globallyChangeSrcOfTrs(ArrayList<LocalTransition> outgoingTrs, LocalState stToStay) {
		// since states and transitions are array lists, the local change to the
		// transitions is enough.
		// what happens to local transitions stored in a hashset? perfect. it works.
		for (LocalTransition tr : outgoingTrs) {
			tr.setSrc(stToStay);
		}

		pathfinder.cleanCaches();
	}

	private void globallyChangeDestOfTrs(ArrayList<LocalTransition> incomingTrs, LocalState stToStay) {
		// since states and transitions are array lists, the local change to the
		// transitions is enough.
		// what happens to local transitions stored in a hashset? perfect. it works.
		for (LocalTransition tr : incomingTrs) {
			tr.setDest(stToStay);
		}

		pathfinder.cleanCaches();
	}

	private void deleteTransitions(HashSet<LocalTransition> transitionsToDelete) {

		// go over the relevant data structures and delete the transitions

		Predicate<LocalTransition> RemovePredicate = new Predicate<LocalTransition>() {

			@Override
			public boolean test(LocalTransition tr) {
				return transitionsToDelete.contains(tr);
			}
		};

		for (LocalState st : states) {
			st.getIncomingTransitions().removeIf(RemovePredicate);
			st.getOutgoingTransitions().removeIf(RemovePredicate);
		}

		transitions.removeIf(RemovePredicate);

		globallySyncLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});

		broadcastLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});
		valstoreLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});
		partitionconsLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});

		pairwiseLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});

		internalLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});

		envLabels.forEach((key, value) -> {
			value.removeAll(transitionsToDelete);
		});

		// clear caches.
		sourceSetCache.clear();
		destinationSetCache.clear();
		pathfinder.cleanCaches();
	}

	public boolean isEnvLabel(String action) {
		return envLabels.containsKey(action);
	}

	public String graphvizString(Protocol p) {
		StringBuilder sb = new StringBuilder();

//		sb.append("States: \n");
//		for (LocalState ls : states) {
//			sb.append("\t" + ls.toString() + "\n");
//		}
		sb.append("digraph ").append(p.getName()).append(" {\n");

		for (LocalTransition lt : transitions) {
			sb.append("\t" + lt.graphvizString() + "\n");
		}

		sb.append(" }\n");
		return sb.toString();
	}

	public HashSet<LocalTransition> getTransitionsWithLabel(String label) {

		if (globallySyncLabels.containsKey(label)) {
			return new HashSet<LocalTransition>(globallySyncLabels.get(label));
		}
		if (pairwiseLabels.containsKey(label)) {
			return new HashSet<LocalTransition>(pairwiseLabels.get(label));
		}
		if (envLabels.containsKey(label)) {
			return new HashSet<LocalTransition>(envLabels.get(label));
		}
		if (internalLabels.containsKey(label)) {
			return new HashSet<LocalTransition>(internalLabels.get(label));
		}

		/*
		 * in case of emergency... for (LocalTransition localTransition : transitions) {
		 * if(localTransition.getLabel().equals(label)) {
		 * transitionsWithLabel.add(localTransition); } }
		 */

		return new HashSet<LocalTransition>(); // or null?
	}

//	public HashSet<Path> getAllPaths(LocalState initState, HashSet<LocalState> targetStates) {
//		return getAllPaths(initState, targetStates, 1);
//	}

	public HashSet<Path> getAllNonFreeUsablePaths(LocalState initState, HashSet<LocalState> targetStates,
			boolean earlyTerminate) {
		return pathfinder.getAllNonFreeUsablePaths(initState, targetStates, earlyTerminate);
	}

	public HashSet<Path> getAllSimplePaths(LocalState initState, HashSet<LocalState> targetStates) {
		return pathfinder.getAllSimplePaths(initState, targetStates);
	}

	public HashSet<Path> getAllSimpleFreePaths(LocalState initState, HashSet<LocalState> targetStates) {
		return pathfinder.getAllSimpleFreePaths(initState, targetStates);
	}

	public HashSet<Path> getAllSimpleInternalPathsFromTo(LocalState startState, LocalState targetState) {
		return pathfinder.getAllSimpleInternalPathsFromTo(startState, targetState);
	}

	public HashSet<Path> getAllPaths(LocalState initState, HashSet<LocalState> targetStates) {
		return pathfinder.getAllPaths(initState, targetStates);
	}

	public ProcessSemantics smartClone() {

		HashSet<LocalState> freshStates = new HashSet<LocalState>();
		ArrayList<LocalTransition> freshTransitions = new ArrayList<LocalTransition>();

		HashMap<String, LocalState> freshStateMap = new HashMap<String, LocalState>();
		// HashMap<String, LocalTransition> freshStateMap = new HashMap<String,
		// LocalState> ();

		// create a fresh copy of the states
		for (LocalState ls : states) {
			LocalState freshState = new LocalState(ls.getLoc(), ls.getSigma());
			freshStates.add(freshState);
			freshStateMap.put(freshState.toString(), freshState);
		}

		// create a fresh copy of the transitions, and use them to connect states.
		for (LocalTransition tr : transitions) {

			LocalState freshSrc = freshStateMap.get(tr.getSrc().toString());
			LocalState freshDst = freshStateMap.get(tr.getDest().toString());

			LocalTransition freshTr = LocalTransition.make(freshSrc, freshDst, tr.getTransitionType(), tr.getLabel(),
					tr.getHandler(), tr.isEnvironmentTransition(), tr.hasProposal());

			freshTransitions.add(freshTr);

			// Additionally, use the transitions to populate the references in the states.
			freshSrc.addOutgoingTransition(freshTr);
			freshDst.addIncomingTransition(freshTr);
		}

		// Finally, get the new initial state
		LocalState freshInitialState = freshStateMap.get(initialState.toString());

		LocalState freshCrashlState = null;

		if (crashState != null) {
			freshCrashlState = freshStateMap.get(crashState.toString());
		}

		return new ProcessSemantics(p, freshInitialState, freshCrashlState, freshStates, freshTransitions, options);
	}

	public Protocol getProtocol() {
		return p;
	}

	public void setExistenceChecker(ExistenceChecker existenceChecker) {
		this.existenceChecker = existenceChecker;
	}

	public ExistenceChecker getExistenceChecker() {
		return existenceChecker;
	}

	public PathFinder getPathFinder() {
		return pathfinder;
	}

	public PhaseAnalysis getPhaseAnalysis() {
		return phaseAnalysis;
	}

	public HashSet<LocalAction> getAssociatedLocalActions(String actLabel) {
		HashSet<LocalAction> acts = new HashSet<LocalAction>();
		// TODO make more efficient?
		for (LocalTransition tr : getTransitionsWithLabel(actLabel)) {
			acts.add(tr.getLocalAction());
		}
		return acts;
	}

	public HashSet<LocalState> determineSendingSetForEnvBr(String action) {
		HashSet<LocalState> startStateSet = new HashSet<LocalState>();

		for (LocalState st : getSourceSet(action)) {
			if (!st.equals(crashState)) {
				startStateSet.add(st);
			}
		}

		return startStateSet;
	}

	public void deleteCrashLogic() {

		// remove transition
		HashSet<LocalTransition> incoming = new HashSet<LocalTransition>();
		incoming.addAll(crashState.getIncomingTransitions());
		deleteTransitions(incoming);

		// remove the state itself.
		deleteState(crashState);
	}

}
