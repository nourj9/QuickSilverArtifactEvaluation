package cutoffs;

import java.sql.PseudoColumnUsage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import Core.Options;
import lang.core.Protocol;
import lang.expr.ExprConstant;
import lang.expr.ExprVar;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerValueCons;
import lang.specs.SafetySpecification;
import semantics.analysis.OnlyOneRegionDetector;
import semantics.analysis.PhaseAnalysis;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.Path;
import semantics.core.ProcessSemantics;

public class CutoffsUtils {
	private ProcessSemantics semantics;
	private SafetySpecification specs;
	private PhaseAnalysis phaseAnalysis;
	private Protocol p;
	private Options options;
	// private boolean threadDebugInfo;
	// private boolean useThreads;

	public CutoffsUtils(Protocol p, ProcessSemantics semantics, SafetySpecification specs, Options options) {
		this.p = p;
		this.semantics = semantics;
		this.specs = specs;
		this.phaseAnalysis = semantics.getPhaseAnalysis();
		this.options = options;
		// this.threadDebugInfo = options.cutoffThreadDebugInfo;
		// this.useThreads = options.useThreadsForCutoffs;
	}

	/*
	 * This function removes some states which are connected with a single internal
	 * or environmental transition by collapsing them into the state to which they
	 * are connected.
	 * 
	 * States are not removed if either of the following holds: - they appear in the
	 * specification. - allow reacting transitions to be skipped. - are a
	 * destination of value-cons: the reason here is that we use the winning var in
	 * these states to decide acting vs reacting transitions
	 */
	public void trimStates() {
		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\ Input Semantics \\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(semantics.prettyString());
		}
		HashSet<LocalState> targetStates = specs.extractLocalStates();

		HashSet<LocalState> onlyOneOutgoingTr = new HashSet<LocalState>();

		for (LocalState st : semantics.getStates()) {
			ArrayList<LocalTransition> outgoing = st.getOutgoingTransitions();
			if (outgoing.size() == 1 && outgoing.get(0).isInternalOrEnvPairwise()) {
				onlyOneOutgoingTr.add(st);
			}
		}
		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println("---------------------------------------");
			System.out.println("xx: only one outgoing tr:");
			for (LocalState localState : onlyOneOutgoingTr) {
				System.out.println("state: " + localState.toSmallString());
				System.out.println("outgoing tr: " + localState.getOutgoingTransitions().get(0));
				System.out.println();
			}

			System.out.println("---------------------------------------");
		}
		for (LocalState currentSt : onlyOneOutgoingTr) {

			if (!semantics.getStates().contains(currentSt)) { // has already been deleted
				continue;
			}

			LocalState destSt = currentSt.getOutgoingTransitions().get(0).getDest();

			// NEW123
			if (targetStates.contains(currentSt)) { // do not delete states that show up in specs.
				continue;
			}

			// test
//			if (currentSt.getLoc().getName().startsWith("Idle_des")) {
//				continue;
//			}

			semantics.collapseStateOneIntoTwo(currentSt, destSt);
			// System.err.println("collapsed (currentSt,destSt) = " + "(" +
			// currentSt.toSmallString() + "," + destSt.toSmallString() + ")");
		}

		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\ Modified Semantics \\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(semantics.prettyString());
		}
	}

	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	public void trimStates_backup() {
		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\ Input Semantics \\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(semantics.prettyString());
		}
		HashSet<LocalState> targetStates = specs.extractLocalStates();

		HashSet<LocalState> onlyOneIncomingTr = new HashSet<LocalState>();
		HashSet<LocalState> onlyOneOutgoingTr = new HashSet<LocalState>();

		for (LocalState st : semantics.getStates()) {

			ArrayList<LocalTransition> incoming = st.getIncomingTransitions();
			ArrayList<LocalTransition> outgoing = st.getOutgoingTransitions();

			if (incoming.size() == 1 && incoming.get(0).isInternalOrEnvPairwise()) {
				onlyOneIncomingTr.add(st);
			}

			if (outgoing.size() == 1 && outgoing.get(0).isInternalOrEnvPairwise()) {
				onlyOneOutgoingTr.add(st);
			}
		}
		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println("---------------------------------------");
			System.out.println("xx: only one outgoing tr:");
			for (LocalState localState : onlyOneOutgoingTr) {
				System.out.println("state: " + localState.toSmallString());
				System.out.println("outgoing tr: " + localState.getOutgoingTransitions().get(0));
				System.out.println();
			}

			System.out.println("---------------------------------------");

			System.out.println("xx: only one incoming tr:");
			for (LocalState localState : onlyOneIncomingTr) {
				System.out.println("state: " + localState.toSmallString());
				System.out.println("incoming tr: " + localState.getIncomingTransitions().get(0));
				System.out.println();
			}

			System.out.println("---------------------------------------");
		}
		for (LocalState currentSt : onlyOneOutgoingTr) {

			if (!semantics.getStates().contains(currentSt)) { // has already been deleted
				continue;
			}

			LocalState destSt = currentSt.getOutgoingTransitions().get(0).getDest();

			// NEW123
			if (targetStates.contains(currentSt)) { // do not delete states that show up in specs.
				// stateReplacementMap.put(currentSt, destSt);
				continue;
			}

			// keep if on of the incoming transitions are value-cons
			// if (hasIncomingValueConsTransition(currentSt)) {
			// continue;
			// }

			semantics.collapseStateOneIntoTwo(currentSt, destSt);
		}

		for (LocalState currentSt : onlyOneIncomingTr) {

			if (!semantics.getStates().contains(currentSt)) { // has already been deleted
				continue;
			}

			LocalState srcSt = currentSt.getIncomingTransitions().get(0).getSrc();

			if (hasOutgoingReactingTransition(srcSt)) { // do not delete internal transitions that might help us skip
														// things.
				continue;
			}

			// NEW123
			if (targetStates.contains(currentSt)) { // do not delete states that show up in specs.
				// stateReplacementMap.put(currentSt, srcSt);
				continue;
			}

			semantics.collapseStateOneIntoTwo(currentSt, srcSt);
		}
		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\ Modified Semantics \\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(semantics.prettyString());
		}
	}

//	private boolean hasIncomingValueConsTransition(LocalState currentSt) {
//		for (LocalTransition tr : currentSt.getIncomingTransitions()) {
//			if (tr.getTransitionType().equals(LocalTransition.Type.VALUE_CONS)) {
//				return true;
//			}
//		}
//		return false;
//	}

	private boolean hasOutgoingReactingTransition(LocalState srcSt) {

		for (LocalTransition tr : srcSt.getOutgoingTransitions()) {
			if (tr.isReactingGlobalTransition()) {
				return true;
			}
		}

		return false;
	}

	public void captureResetMechanism() {

		// find reset mechanisms
		LocalState initState = semantics.getInitialState();
		HashSet<LocalState> initialStates = new HashSet<LocalState>();
		initialStates.add(initState);

		// Experiment: expand the notion of "initial state" to leave special variables
		// unconstrained.
		for (Entry<String, HashSet<HandlerValueCons>> entry : p.getValconsInstMap().entrySet()) {
			ExprVar winVar = p.getSpecialVars().get(entry.getKey() + "_wval").asExprVar();

			outer: for (LocalState state : semantics.getStates()) {
				if (state.equals(initState)) {
					continue;
				}
				if (!state.getLoc().equals(initState.getLoc())) {
					continue;
				}

				for (String varName : p.getVarNames()) {
					if (varName.equals(winVar.getName())) {
						continue;
					}
					if (!state.getSigma().getValue(varName).equals(initState.getSigma().getValue(varName))) {
						continue outer;
					}

					initialStates.add(state);
				}
			}
		}

		// if the destination set is initial or crash, we are fine.
		OUTER: for (Entry<String, HashSet<LocalTransition>> globalSync : semantics.getGloballySyncLabels().entrySet()) {

			for (LocalState ls : semantics.getDestinationSet(globalSync.getKey())) {

				if (!(initialStates.contains(ls) || ls.equals(semantics.getCrashState()))) {
					continue OUTER;
				}
			}

			for (LocalTransition resetTr : globalSync.getValue()) {
				resetTr.markAsReset();
			}
		}
	}

	/*
	 * This function labels each transition in the semantics as Free or Non-Free
	 */
	public void labelTransitions() {
		for (LocalTransition tr : semantics.getTransitions()) {

			if (tr.isEnvironmentTransition()) {
				tr.markFree();
			} else {
				switch (tr.getTransitionType()) {
				case PAIRWISE_SEND:
					tr.markNonFree();
					break;
				case PAIRWISE_RECV:
					tr.markNonFree();
					break;
				case BROADCAST_SEND:
					tr.markFree();
					break;
				case BROADCAST_RECV:

					tr.markNonFree();
					// this is part of a negotiation
					ArrayList<LocalTransition> outgoingTrs = tr.getSrc().getOutgoingTransitions();
					for (LocalTransition outTr : outgoingTrs) {
						if (outTr.isSend() //
								&& outTr.getSrc().equals(tr.getSrc()) //
								&& outTr.getDest().equals(tr.getDest()) //
								&& outTr.getLabel().equals(tr.getLabel())) {
							tr.markFree();
							break;
						}
					}

					break;
				case PARTITION_CONS_WIN:
					tr.markFree();
					break;
				case PARTITION_CONS_LOSE:
					tr.markNonFree();
					break;
				case INTERNAL:
					tr.markFree();
					break;
				case VALUE_CONS:

					if (tr.isActingGlobalTransition()) {
						tr.markFree();
					} else {
						tr.markNonFree();
					}

//					String label = tr.getLabel();
//					Event event = TreatyVisitor.getChInstMap().get(label);
//
//					String propVarName = ((EventValueCons) event).getProposalVar().getName();
//					String winVarName = label + "_wval";
//
//					VariableValuation srcSigma = tr.getSrc().getSigma();
//					VariableValuation dstSigma = tr.getDest().getSigma();
//
//					if (srcSigma.getValue(propVarName).equals(dstSigma.getValue(winVarName))) {
//						tr.markFree();
//					} else {
//						tr.markNonFree();
//					}

					// System.out.println(tr);
					// System.out.println(tr.isFree());
					// System.out.println("srcSigma.getValue(propVarName): " +
					// srcSigma.getValue(propVarName));
					// System.out.println("dstSigma.getValue(winVarName): " +
					// dstSigma.getValue(winVarName));
					// System.out.println("xx: exit");

					// System.exit(-1);

					break;
				}
			}
		}
	}

	public void determineUnusableTransitions() {

		OnlyOneRegionDetector rd = new OnlyOneRegionDetector(semantics, phaseAnalysis);

		for (String action : semantics.getTransitionLabels()) {

			HashSet<LocalState> startStateSet = semantics.getSendingSet(action);

			HashSet<LocalState> exclusiveR = rd.buildMaximalExclusiveRegion(startStateSet);
			// If no exclusive region, just move on to the next transition
			if (exclusiveR == null) {
				continue;
			}

			// If there is an exclusive region...
			for (LocalState localState : exclusiveR) {

				// note all of the receiving transitions of this
				// action in this region, because they can be ignored.
				for (LocalTransition outgoingTrans : localState.getOutgoingTransitions()) {

					// Filter for receiving transitions.
					if (!(outgoingTrans.isReactingTransition())) {
						continue;
					}

					// If the label is of the same action, store it, because it is never used.
					if (outgoingTrans.getLabel().equals(action)) {
						outgoingTrans.markUnusable();
					}
				}
			}
		}
	}

	public boolean isIgnorableForkingReact(LocalTransition tr) {

		// from initial state, find a fork (acting and corresponding reacting)
		if (!isForkingReact(tr)) {
			return false;
		}

		// Get the corresponding acting transition
		LocalTransition actingForkTr = null;
		for (LocalTransition outgoing : tr.getSrc().getOutgoingTransitions()) {
			if (!outgoing.getLabel().equals(tr.getLabel())) {
				continue;
			}
			if (outgoing.isActingGlobalTransition()) {
				actingForkTr = outgoing;
				break;
			}
		}

		// get the losing destination region and winning destination region
		// (see what it can reach without taking reset)
		HashSet<LocalState> actingRegion = getReachableRegion(actingForkTr.getDest());
		HashSet<LocalState> reactingRegion = getReachableRegion(tr.getDest());

		// In the losing region:
		// - must be completely disjoint from the winning region
		if (!areDisjoint(reactingRegion, actingRegion)) {
			return false;
		}

		// - Any acting transition from this region either doesn't exist in the other
		// region or it self loops

		// HashSet<LocalTransition> actingTrsInReactingRegion =
		// getActingTransitionsIn(reactingRegion);
		HashSet<String> actingEventsInReactingRegion = getActingActionsIn(reactingRegion);
		HashSet<LocalTransition> reactingTrsInActingRegion = getReactingTransitionsIn(actingRegion);
		for (LocalTransition reaction : reactingTrsInActingRegion) {
			
			//in case this was a reaction for a reset, we can ignore it, as the reset:
			// 1. happens at the border of the regions, and,
			// 2. goes back to the initial state.
			if(reaction.isReset()) {
				continue;
			}
			
			if (!actingEventsInReactingRegion.contains(reaction.getLabel())) {
				continue;
			}
			if (reaction.isSelfLoop()) {
				continue;
			}
			// Here we have a reacting transition in the acting region which is not a self
			// loop
			return false;
		}

		// All conditions met
		return true;
	}

	// Return labels of all acting transitions which may be taken from some state in
	// `states`
	private HashSet<String> getActingActionsIn(HashSet<LocalState> states) {
		HashSet<String> result = new HashSet<String>();

		for (LocalState state : states) {
			for (LocalTransition outgoing : state.getOutgoingTransitions()) {
				if (outgoing.isActingTransition()) {
					result.add(outgoing.getLabel());
				}
			}
		}

		return result;
	}

	// Return all acting transitions which may be taken from some state in `states`
	private HashSet<LocalTransition> getReactingTransitionsIn(HashSet<LocalState> states) {
		HashSet<LocalTransition> result = new HashSet<LocalTransition>();

		for (LocalState state : states) {
			for (LocalTransition outgoing : state.getOutgoingTransitions()) {
				if (outgoing.isReactingTransition()) {
					result.add(outgoing);
				}
			}
		}

		return result;
	}

	// Return all acting transitions which may be taken from some state in `states`
//	private HashSet<LocalTransition> getActingTransitionsIn(HashSet<LocalState> states) {
//		HashSet<LocalTransition> result = new HashSet<LocalTransition>();
//
//		for (LocalState state : states) {
//			for (LocalTransition outgoing : state.getOutgoingTransitions()) {
//				if (outgoing.isActingTransition()) {
//					result.add(outgoing);
//				}
//			}
//		}
//
//		return result;
//	}

	// Return false if there exists a LocalState which is contained
	// within both reactingRegion and actingRegion; otherwise, return true.
	private boolean areDisjoint(HashSet<LocalState> reactingRegion, HashSet<LocalState> actingRegion) {

		HashSet<LocalState> smaller, larger;
		if (reactingRegion.size() > actingRegion.size()) {
			smaller = actingRegion;
			larger = reactingRegion;
		} else {
			smaller = reactingRegion;
			larger = actingRegion;
		}

		for (LocalState st : smaller) {
			if (larger.contains(st)) {
				return false;
			}
		}

		return true;
	}

	// Construct the set of states that may be reached from state st
	// through any type of transition, except reset transitions
	// edit: ignore crash location
	private HashSet<LocalState> getReachableRegion(LocalState st) {
		HashSet<LocalState> reachable = new HashSet<LocalState>();

		ArrayList<LocalState> unvisited = new ArrayList<LocalState>();
		unvisited.add(st);
		while (!unvisited.isEmpty()) {
			LocalState currentSt = unvisited.remove(0);

			if (currentSt.equals(semantics.getCrashState())) {
				continue;
			}

			reachable.add(currentSt);

			for (LocalTransition outgoing : currentSt.getOutgoingTransitions()) {
				if (outgoing.isReset()) {
					continue;
				}

				LocalState currDest = outgoing.getDest();
				if (!reachable.contains(currDest)) {
					unvisited.add(currDest);
				}
			}
		}

		return reachable;
	}

	// Return true if tr is the reacting half of a global
	// transition that splits the processes off into two
	// regions from the initial state, otherwise, return false.
	private boolean isForkingReact(LocalTransition tr) {

		// just in case.
		if (!tr.isReactingGlobalTransition()) {
			return false;
		}

		HashSet<LocalState> initialStates = semantics.getLogicalInitialStates();
		if (!initialStates.contains(tr.getSrc())) {
			return false;
		}
		for (LocalTransition outgoing : tr.getSrc().getOutgoingTransitions()) {
			if (!outgoing.getLabel().equals(tr.getLabel())) {
				continue;
			}
			if (outgoing.isActingGlobalTransition()) {
				return true;
			}
		}
		return false;
	}

	public boolean isDisjoint(Path p1, Path p2) {
		for (LocalTransition tr1 : p1.getTransitions()) {
			for (LocalTransition tr2 : p2.getTransitions()) {
				if (tr1.equals(tr2)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isFreeAndAllowsKProcessesSimultaneously(LocalTransition tr, int k) {

		if (tr.isEnvironmentTransition()) {
			return true;
		}

		switch (tr.getTransitionType()) {
		case PAIRWISE_SEND:
		case PAIRWISE_RECV:
		case BROADCAST_SEND:
		case BROADCAST_RECV:
		case VALUE_CONS:
		case PARTITION_CONS_LOSE:
			return false;
		case INTERNAL:
			return true;
		case PARTITION_CONS_WIN:

			HandlerPartitionCons h = (HandlerPartitionCons) tr.getHandler();
			int cardinality = Integer.valueOf(((ExprConstant) h.getEvent().getCardinality()).getVal());
			return cardinality >= k;

		default:
			return false;

		}
	}

	public void printDebugInfoForTransitions() {
		if (options.cutoffs_debugInfoForCutoffs) {
			for (LocalTransition tr : semantics.getTransitions()) {
				String actness = "";
				switch (tr.getActness()) {
				case Acting:
					actness = "Acting";
					break;
				case Internal:
					actness = "Internal";
					break;
				case Reacting:
					actness = "Reacting";
					break;
				case Crash:
					actness = "Crash";
					break;

				}

				System.out.println("xx: " + (tr.isFree() ? "free:\t" : "not free:\t") + tr.toString() + " \t " + actness
						+ "\t" + (tr.isReset() ? "reset" : "not reset"));
			}

		}
	}

	/**
	 * if s and s' are going to dummy, and s-->s' 
	 * using an internal transition, and s' has no other
	 * incoming transitions, then ignore s'.
	 **/
	public void refineTragetStates(HashSet<LocalState> targetStates) {
		HashSet<LocalState> toDelete = new HashSet<LocalState>();
		for (LocalState st1 : targetStates) {
			ArrayList<LocalTransition> incoming = st1.getIncomingTransitions();
			// can probably be relaxed
			if (incoming.size() == 1 // can be reached by one ..
					&& incoming.get(0).isInternalOrEnvPairwise() // internal transition ..
					&& targetStates.contains(incoming.get(0).getSrc())) // that originates from another target state.
				toDelete.add(st1);
		}

		for (LocalState st : toDelete) {
			targetStates.remove(st);
		}

	}

	public void printDebugInfoForPaths(LocalState initState, HashSet<LocalState> targetStates) {
		// Recomputed but it's only debug so it's ok
		HashSet<Path> nonFreeUsablePaths = semantics.getAllNonFreeUsablePaths(initState, targetStates, false);

		HashSet<Path> simpleFreePaths = semantics.getAllSimpleFreePaths(initState, targetStates);

		// Debug only:
		HashSet<Path> simplePaths = semantics.getAllSimplePaths(initState, targetStates);
		// HashSet<Path> paths = semantics.getAllPaths(initState, targetStates);

		System.out.println("////////////////////////----------////////////////////////////////");
		System.out.println("xx: different types of paths from " + initState.toSmallString() + " to " + targetStates);

		// print("paths", paths);
		print("nonFreeUsablePaths", nonFreeUsablePaths);
		print("simplePaths", simplePaths);
		print("simpleFreePaths", simpleFreePaths);
		System.out.println("//////////////////////-----------//////////////////////////////////");
	}

	private void print(String msg, HashSet<Path> paths) {
		System.out.println("----------------------------------------------------");
		System.out.println("xx: " + msg + "(" + paths.size() + "):");
		int i = 0;
		for (Path path : paths) {
			System.out.println("xx " + (++i) + ": " + path);
		}
		System.out.println("------ended---------------------------------------------");
	}

	public void removeCrashStateAndTransitions() {

		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println(
					"\\\\\\\\\\\\\\\\\\\\\\\\\\\\ Input Semantics before crash removal \\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(semantics.prettyString());
		}

		semantics.deleteCrashLogic();

		if (options.cutoffs_debugInfoForCutoffs) {
			System.out.println(
					"\\\\\\\\\\\\\\\\\\\\\\\\\\\\ Modified Semantics after crash removal \\\\\\\\\\\\\\\\\\\\\\\\\\\\");
			System.out.println(semantics.prettyString());
		}

	}

}
