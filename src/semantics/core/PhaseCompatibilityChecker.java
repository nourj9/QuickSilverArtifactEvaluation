package semantics.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import CExample.PhaseCompCE;
import CExample.PhaseCompCEC2;
import CExample.PhaseCompCEC31;
import CExample.PhaseCompCE.Condition;
import CExample.PhaseCompCEC32;
import CExample.PhaseCompCEC4;
import Core.Options;
import feedback.Ranker;
import semantics.analysis.OnlyOneRegionDetector;
import semantics.analysis.Phase;
import semantics.analysis.PhaseAnalysis;
import utils.MyStringBuilder;

public class PhaseCompatibilityChecker {

	/***************************************
	 * Private Member Variables
	 ***************************************/
	private ProcessSemantics semantics;
	PhaseAnalysis pa;
	private ArrayList<PhaseCompCE> counterExamples = new ArrayList<PhaseCompCE>();

	HashMap<PhaseCompCE.Condition, ArrayList<PhaseCompCE>> conditionToCEmap = new HashMap<PhaseCompCE.Condition, ArrayList<PhaseCompCE>>();
	HashMap<String, ArrayList<PhaseCompCE>> actionToCEmap = new HashMap<String, ArrayList<PhaseCompCE>>();

	HashMap<String, HashSet<LocalState>> statesWithNoNeedToReactTo = new HashMap<String, HashSet<LocalState>>();

	// private ArrayList<LocalState> states;
	// private ArrayList<LocalTransition> transitions;

	private HashSet<Phase> phases = null;
	private HashMap<LocalState, HashSet<Phase>> phaseMap = null;
	private Options options;

	/***************************************
	 * Constructors
	 * 
	 * @param b
	 ***************************************/
	public PhaseCompatibilityChecker(ProcessSemantics semantics, Options options) {

		this.semantics = semantics;
		this.pa = semantics.getPhaseAnalysis();
		this.phases = pa.getPhases();
		this.phaseMap = pa.getPhaseMap();
		this.options = options;
		// this.states = semantics.getStates();
		// this.transitions = semantics.getTransitions();
		if (options.useExclusiveRegions) {
			determineUnusableTransitions();
		}
	}

	/***************************************
	 * Public Member Functions
	 ***************************************/

	// Return true if the semantics are well-behaved. Return false otherwise.
	public boolean check() {
		counterExamples.clear();
		conditionToCEmap.clear();
		actionToCEmap.clear();

		// (1) The state space of P is fixed and finite.
		// Note: We don't really need to check that the process state space is fixed and
		// finite since we already have the ProcessSemantics object.
		// If we were able to rewrite it to a finite semantics object, then the state
		// space is fixed and finite.
		boolean condition1 = allSetsCanBeRemoved();
		if (options.phComp_earlyTerminationForWellBehavedness && !condition1) {
			return false;
		}

		// (1) The state space of P is fixed and finite.
		// Note: We don't really need to check that the process state space is fixed and

		// (1.5) There is only one pairwise-receive per action per phase.
		// TODO
		//

		boolean condition2 = allGlobalSyncActionsPaired();

		if (options.phComp_earlyTerminationForWellBehavedness && !condition2) {
			return false;
		}
		// (3) For each acting transition of a globally synchronizing action which ends
		// at a
		// state with a reacting transition of some globally synchronizing action, all
		// other
		// such acting transitions much transition to a state with the same reacting
		// action,
		// and every corresponding reacting action must have an internal path to a state
		// with the same reacting action as the acting transition(s).
		// if (!phaseGlobalSyncTransitions()) {
		// return false;
		// }
		boolean condition3 = phaseGlobalSyncTransitions();

		if (options.phComp_earlyTerminationForWellBehavedness && !condition3) {
			return false;
		}

		// (4) For each internal or pairwise transition which ends at a
		// state with a reacting transition of some globally synchronizing action, all
		// other
		/// // states in the same phase as the source/destination of the internal
		// transition
		// states in the a phase with the source/destination of the internal transition
		// must have an internal path to a state with the same reacting action.
		// if (!internalAndPwSamePhaseBorders()) {
		// return false;
		// }
		boolean condition4 = internalAndPwSamePhaseBorders();

		if (options.phComp_earlyTerminationForWellBehavedness && !condition4) {
			return false;
		}

		// everything ok?
		return condition1 && condition2 && condition3 && condition4;
		// return true;
	}

	private boolean allSetsCanBeRemoved() {
//		SetRemover sr = new SetRemover(this, semantics, pa);
		return true;
//		return sr.check();
	}

	/****************************************************
	 * Private Member Functions - Well-behavedness check
	 ****************************************************/

	// Return false if there exists a state in P which has a
	// globally-synchronizing acting transition without the
	// corresponding reacting transition, and true otherwise.
	private boolean allGlobalSyncActionsPaired() {
		// Every state in P...
		boolean noViolations = true;
		for (LocalState s : semantics.getStates()) {
			for (LocalTransition acting : s.getOutgoingTransitions()) {

				// which has an (globally-synchronizing) acting transition...
				if (acting.isActingGlobalTransition()) {

					// must have the corresponding reacting transition from the same state.
					// orrrrr, does not need to
					if (!stateHasReactiveActionOrDoesNotNeedTo(s, acting.getLabel(), acting.getTransitionType())) {

						noViolations = false;
						PhaseCompCE cex = new PhaseCompCEC2(acting, semantics);
						counterExamples.add(cex);
						add(conditionToCEmap, Condition.Condition2, cex);
						add(actionToCEmap, acting.getLabel(), cex);

						if (options.phComp_earlyTerminationForWellBehavedness) {
							return false;
						}

					}
				}
			}
		}

		// Found no violations.
		return noViolations;
	}

	// Check to make sure that globally-synchronizing transitions move in phases
	// (WBC 3).
	// (3) For each acting transition of a globally synchronizing action which ends
	// at a state with a reacting transition of some globally synchronizing action,
	// all other such acting transitions must transition to a state with the same
	// reacting action, and every corresponding reacting action must have an
	// internal path to a state with the same reacting action as the acting
	// transition(s).
	private boolean phaseGlobalSyncTransitions() {

		// Isolate the acting globally-synchronizing actions.
		ArrayList<LocalTransition> actingGlobals = new ArrayList<LocalTransition>();
		for (LocalTransition t : semantics.getTransitions()) {
			if (t.isActingGlobalTransition()) {
				actingGlobals.add(t);
			}
		}

		// Isolate the reacting globally-synchronizing actions.
		ArrayList<LocalTransition> reactingGlobals = new ArrayList<LocalTransition>();
		for (LocalTransition t : semantics.getTransitions()) {
			if (t.isReactingGlobalTransition()) {
				reactingGlobals.add(t);
			}
		}

		boolean noViolations = true;

		// For each acting transition of a globally synchronizing action...
		for (LocalTransition acting : actingGlobals) {

			HashSet<LocalState> actingDstSet = semantics.getDestinationSet(acting.getLabel());

			LocalState dest = acting.getDest();
			for (LocalTransition outgoing : dest.getOutgoingTransitions()) {

				// which ends at a state with a reacting transition of some globally
				// synchronizing action...
				if (!(outgoing.isReactingGlobalTransition())) {
					continue;
				}

				if (options.phComp_enableFirabilityAwareness) {

					// Edit: make this firability aware: if the destination set of the acting action
					// does not intersect with the "send set" of the outgoing reacting action, then
					// continue (since the outgoing action will not be able to fire anyway!)

					// if the receive is an enviroment, then it is always firable, becuase the
					// enviroment is always ready to fire.
					// if (!outgoing.isEnvironmentTransition()) {

					// HashSet<LocalState> outgoingSendSet =
					// semantics.getSendingSet(outgoing.getLabel());
					HashSet<LocalState> outgoingSendSet;
					if (outgoing.isEnvironmentTransition()) {
						outgoingSendSet = semantics.determineSendingSetForEnvBr(outgoing.getLabel());
					} else {
						outgoingSendSet = semantics.getSendingSet(outgoing.getLabel());
					}
					// find if the intersection is not empty
					boolean intersect = setIntersection(outgoingSendSet, actingDstSet);

					// disjoint? then the outgoing action is not firable.
					if (!intersect) {
						if (options.phComp_displayFirabilityPrintOuts) {
							System.out.println(">>>> firability extension used in cond 3: " //
									+ "\n >>>>>> acting = " + acting.getLocalAction() //
									+ "\n >>>>>> outgoing = " + outgoing.getLocalAction());
						}
						continue;
					}

				}

				for (LocalTransition otherActing : actingGlobals) {
					if (otherActing == acting) {
						continue;
					}
					if (otherActing.getLabel().equals(acting.getLabel())) { // all other such acting transitions...
						LocalState otherDest = otherActing.getDest(); // must transition to a state...

						// with the same reacting action,
						if (!stateHasAction(otherDest, outgoing.getLabel(), outgoing.getTransitionType())) {

							noViolations = false;
							PhaseCompCE cex = new PhaseCompCEC31(acting, otherActing, outgoing, semantics);
							counterExamples.add(cex);
							add(conditionToCEmap, Condition.Condition31, cex);
							add(actionToCEmap, acting.getLabel(), cex);

							if (options.phComp_earlyTerminationForWellBehavedness) {
								return false;
							}

						}
					}
				}

				// and every corresponding reacting action...
				for (LocalTransition reacting : reactingGlobals) {
					if (reacting.getLabel().equals(acting.getLabel())) {
						LocalState reactingDest = reacting.getDest();

						// must have an internal path to a state with the same reacting action.
						if (!stateCanReachReactiveAction(reactingDest, outgoing.getLabel(),
								outgoing.getTransitionType())) {

							noViolations = false;
							PhaseCompCE cex = new PhaseCompCEC32(acting, reacting, outgoing, semantics);

							counterExamples.add(cex);
							add(conditionToCEmap, Condition.Condition32, cex);
							add(actionToCEmap, acting.getLabel(), cex);

							if (options.phComp_earlyTerminationForWellBehavedness) {
								return false;
							}

						}
					}
				}
			}
		}

		// No violations found.
		// return true;
		return noViolations;
	}

	// Check to make sure that states in the same phase can reach the same phase
	// borders.
	// (4) For each internal or pairwise transition which ends at a
	// state with a reacting transition of some globally synchronizing action, all
	// other
	// states in the same phase as the source/destination of the internal transition
	// must have an internal path to a state with the same reacting action.
	private boolean internalAndPwSamePhaseBorders() {

		// Isolate the internal and pairwise transitions.
		ArrayList<LocalTransition> internalPwCrashes = new ArrayList<LocalTransition>();
		for (LocalTransition t : semantics.getTransitions()) {
			if (t.isInternal() || t.isPairwiseTransition() || t.isCrash()) {
				internalPwCrashes.add(t);
			}
//			if (!(t.isGloballySynchronizing())) {
//				internalPw.add(t);
//			}
		}

		boolean noViolations = true;
		// For each internal or pairwise transition...
		for (LocalTransition intPwCrash : internalPwCrashes) {

			// ignore internal self-loops here.
			if (intPwCrash.isInternal() && intPwCrash.isSelfLoop()) {
				continue;
			}

			LocalState dest = intPwCrash.getDest();
			for (LocalTransition outgoing : dest.getOutgoingTransitions()) {

				// which ends at a state with a reacting transition of some globally
				// synchronizing action...
				if (!(outgoing.isReactingGlobalTransition())) {
					continue;
				}

				// all other states in the same phase as the source/destination of the internal
				// transition...
				for (Phase internalPhase : phaseMap.get(intPwCrash.getSrc())) {

					if (options.phComp_enableFirabilityAwareness) {
						// Edit: make this firability aware: if the current phase does not intersect
						// with the "send set" of the outgoing reacting action, then continue (since the
						// outgoing action will not be able to fire anyway!)

						// if the receive is an enviroment, then it is always firable, becuase the
						// enviroment is always ready to fire.

						HashSet<LocalState> outgoingSendSet;
						if (outgoing.isEnvironmentTransition()) {
							outgoingSendSet = semantics.determineSendingSetForEnvBr(outgoing.getLabel());
						} else {
							outgoingSendSet = semantics.getSendingSet(outgoing.getLabel());
						}

						// HashSet<LocalState> outgoingSendSet =
						// semantics.getSendingSet(outgoing.getLabel());

						// find if the intersection is not empty
						boolean intersect = setIntersection(outgoingSendSet, internalPhase.getStates());

						// disjoint? then the outgoing action is not firable.
						if (!intersect) {
							if (options.phComp_displayFirabilityPrintOuts) {
								System.out.println(">>>> firability extension used in cond 4: " //
										+ "\n >>>>>> state = " + intPwCrash.getSrc().toSmallString() //
										+ "\n >>>>>> outgoing = " + outgoing.getLocalAction());
							}
							continue;
						}

					}

					for (LocalState stateInSamePhase : internalPhase) {
						if (stateInSamePhase == dest) {
							continue;
						}
						// must have an internal path to a state with the same reacting action.
						if (!stateCanReachReactiveAction(stateInSamePhase, outgoing.getLabel(),
								outgoing.getTransitionType())) {

							// if this state does not need to react, then just continue
							if (statesWithNoNeedToReactTo.containsKey(outgoing.getLabel()) //
									&& statesWithNoNeedToReactTo.get(outgoing.getLabel()).contains(stateInSamePhase)) {
								continue;
							}

							//
							noViolations = false;
							PhaseCompCE cex = new PhaseCompCEC4(intPwCrash, outgoing, stateInSamePhase, internalPhase,
									semantics);
							counterExamples.add(cex);
							add(conditionToCEmap, Condition.Condition4, cex);
							add(actionToCEmap, intPwCrash.getLabel(), cex);

							if (options.phComp_earlyTerminationForWellBehavedness) {
								return false;
							}

						}
					}
				}
			}
		}

		// No violations found.
		// return true;
		return noViolations;
	}

//	private boolean internalAndPwSamePhaseBorders_oldversionbeforecrashes() {
//
//		// Isolate the internal and pairwise transitions.
//		ArrayList<LocalTransition> internalPw = new ArrayList<LocalTransition>();
//		for (LocalTransition t : semantics.getTransitions()) {
//			if ((t.isInternal() || t.isPairwiseTransition())) {
//				internalPw.add(t);
//			}
////			if (!(t.isGloballySynchronizing())) {
////				internalPw.add(t);
////			}
//		}
//
//		boolean noViolations = true;
//		// For each internal or pairwise transition...
//		for (LocalTransition intPw : internalPw) {
//
//			// ignore internal self-loops here.
//			if (intPw.isInternal() && intPw.isSelfLoop()) {
//				continue;
//			}
//
//			LocalState dest = intPw.getDest();
//			for (LocalTransition outgoing : dest.getOutgoingTransitions()) {
//
//				// which ends at a state with a reacting transition of some globally
//				// synchronizing action...
//				if (!(outgoing.isReactingGlobalTransition())) {
//					continue;
//				}
//
//				// all other states in the same phase as the source/destination of the internal
//				// transition...
//				for (Phase internalPhase : phaseMap.get(intPw.getSrc())) {
//
//					if (options.phComp_enableFirabilityAwareness) {
//						// Edit: make this firability aware: if the current phase does not intersect
//						// with the "send set" of the outgoing reacting action, then continue (since the
//						// outgoing action will not be able to fire anyway!)
//
//						// if the receive is an enviroment, then it is always firable, becuase the
//						// enviroment is always ready to fire.
//						// edit: we will try to "squeeze" a little more information by treating
//						// enviroment
//						// receives as a negotation step that can fire from anywhere other than the
//						// crashed state.
//						HashSet<LocalState> outgoingSendSet;
//						if (outgoing.isEnvironmentTransition()) {
//							outgoingSendSet = semantics.determineSendingSetForEnvBr(outgoing.getLabel());
//						} else {
//							outgoingSendSet = semantics.getSendingSet(outgoing.getLabel());
//						}
//
//						// find if the intersection is not empty
//						boolean intersect = setIntersection(outgoingSendSet, internalPhase.getStates());
//
//						// disjoint? then the outgoing action is not firable.
//						if (!intersect) {
//							System.out.println(">>>> firability extension used in cond 4: " //
//									+ "\n >>>>>> state = " + intPw.getSrc().toSmallString() //
//									+ "\n >>>>>> outgoing = " + outgoing.getLocalAction());
//							continue;
//						}
//
//					}
//
//					for (LocalState stateInSamePhase : internalPhase) {
//						if (stateInSamePhase == dest) {
//							continue;
//						}
//						// must have an internal path to a state with the same reacting action.
//						if (!stateCanReachReactiveAction(stateInSamePhase, outgoing.getLabel(),
//								outgoing.getTransitionType())) {
//
//							// if this state does not need to react, then just continue
//							if (statesWithNoNeedToReactTo.containsKey(outgoing.getLabel()) //
//									&& statesWithNoNeedToReactTo.get(outgoing.getLabel()).contains(stateInSamePhase)) {
//								continue;
//							}
//
//							//
//							noViolations = false;
//							PhaseCompCE cex = new PhaseCompCEC4(intPw, outgoing, stateInSamePhase, internalPhase,
//									semantics);
//							counterExamples.add(cex);
//							add(conditionToCEmap, Condition.Condition4, cex);
//							add(actionToCEmap, intPw.getLabel(), cex);
//
//							if (options.phComp_earlyTerminationForWellBehavedness) {
//								return false;
//							}
//
//						}
//					}
//				}
//			}
//		}
//
//		// No violations found.
//		// return true;
//		return noViolations;
//	}

	private boolean setIntersection(HashSet<LocalState> set1, HashSet<LocalState> set2) {

		boolean intersect = false;
		for (LocalState e : set1) {
			if (set2.contains(e)) {
				intersect = true;
				break;
			}
		}

		return intersect;
	}

	// Expand the given phase through internal and pairwise transitions, looking for
	// a reactive transition
	private boolean stateCanReachReactiveAction(LocalState state, String label, LocalAction.ActionType type) {
		LocalAction.ActionType reactionType = LocalTransition.correspondingReceiveType(type);

		HashSet<LocalState> reachableStates = new HashSet<LocalState>();
		reachableStates.add(state);

		Iterator<LocalState> phaseItr;

		boolean changeMade = true;
		REACHABLE_OUTER: while (changeMade) {
			changeMade = false;
			phaseItr = reachableStates.iterator();
			while (phaseItr.hasNext()) {
				LocalState s = phaseItr.next();
				if (stateHasAction(s, label, reactionType)) {
					return true;
				}

				for (LocalTransition t : semantics.getTransitions()) {

					// filter for internal (and environment) transitions
//					boolean cond;
//					if (options.phComp_useCrashTransitionsInInternalPaths) {
//						cond = t.getTransitionType() != LocalAction.ActionType.INTERNAL
//								&& !(t.isEnvironmentTransition() && !t.isGloballySynchronizing())
//								&& t.getTransitionType() != LocalAction.ActionType.CRASH;
//					} else {
//						cond = t.getTransitionType() != LocalAction.ActionType.INTERNAL
//								&& !(t.isEnvironmentTransition() && !t.isGloballySynchronizing());
//					}
//					
					if (t.getTransitionType() != LocalAction.ActionType.INTERNAL // not internal
							&& !(t.isEnvironmentTransition() && !t.isGloballySynchronizing()) // not pairwise env.
							&& !(options.phComp_useCrashTransitionsInInternalPaths && // if allowed to use..
									t.getTransitionType() == LocalAction.ActionType.CRASH)) { // ..not crash
						continue;
					}

					if (t.getSrc() == s && !(reachableStates.contains(t.getDest()))) {
						reachableStates.add(t.getDest());
						changeMade = true;
						continue REACHABLE_OUTER;
					}
				}
			}
		}
		return false;
	}

	private boolean stateHasAction(LocalState state, String label, LocalAction.ActionType type) {

		for (LocalTransition t : state.getOutgoingTransitions()) {

			if (t.getTransitionType() != type) {
				continue;
			}

			if (t.getLabel().equals(label)) {
				return true;
			}
		}
		return false;
	}

	private boolean stateHasReactiveActionOrDoesNotNeedTo(LocalState state, String label, LocalAction.ActionType type) {

		HashSet<LocalState> statesThatDoesNotNeedToReact = statesWithNoNeedToReactTo.get(label);

		// if this state does not need to react, then just return true.
		if (statesThatDoesNotNeedToReact != null) {
			if (statesThatDoesNotNeedToReact.contains(state)) {
				return true;
			}
		}
		// otherwise proceed normally
		return stateHasAction(state, label, LocalTransition.correspondingReceiveType(type));
	}

	public ArrayList<PhaseCompCE> getCounterExamples() {
		return counterExamples;
	}

	public void printErrorsAndSuggestions(Options options) {
		MyStringBuilder sb = new MyStringBuilder();

		Options.PhCompFeedback feedBackLevel = options.phCompFeedbackLevel;

		switch (feedBackLevel) {
		case All:
			sb.appn("All feedback:");
			for (PhaseCompCE ce : counterExamples) {
				if (options.fancyPrints) {
					sb.appn(ce.toFancyString());
				} else {
					sb.appn(ce.toString());
				}
			}
//			sb.line();
			break;

		case OnePerAction:
			sb.appn("OnePerAction feedback:");
			for (Entry<String, ArrayList<PhaseCompCE>> entry : actionToCEmap.entrySet()) {
				// Condition cond = entry.getKey();
				// rank, w.r.t. conditions which counter example to return
				PhaseCompCE cEx = Ranker.getHighestRankedCounterExamplesAccordingToCondition(entry.getValue());
				sb.appn(cEx.getDescription());
				if (options.fancyPrints) {
					sb.appn(cEx.getHighestRankedSuggestion().toFancyString());
				} else {
					sb.appn(cEx.getHighestRankedSuggestion().toString());
				}
			}
			break;

		case OnePerCondition:
			sb.appn("OnePerCondition feedback:");
			for (Entry<Condition, ArrayList<PhaseCompCE>> entry : conditionToCEmap.entrySet()) {
				// Condition cond = entry.getKey();
				// no need to rank, just pick the first one, if any.
				PhaseCompCE cEx = entry.getValue().get(0);
				sb.appn(cEx.getDescription());
				sb.appn(cEx.getHighestRankedSuggestion().toFancyString());
			}
			break;

		case CounterExampleOnly:
			for (Entry<Condition, ArrayList<PhaseCompCE>> entry : conditionToCEmap.entrySet()) {
				PhaseCompCE cEx = entry.getValue().get(0);
				sb.appn(cEx.getDescription());
			}
			break;
		}

		System.err.println(sb.toString());
	}

	private void add(HashMap<String, ArrayList<PhaseCompCE>> actionToCEmap, String action, PhaseCompCE cEx) {
		if (!actionToCEmap.containsKey(action)) {
			actionToCEmap.put(action, new ArrayList<PhaseCompCE>());
		}
		actionToCEmap.get(action).add(cEx);
	}

	private void add(HashMap<Condition, ArrayList<PhaseCompCE>> conditionToCEmap, Condition cond, PhaseCompCE cEx) {
		if (!conditionToCEmap.containsKey(cond)) {
			conditionToCEmap.put(cond, new ArrayList<PhaseCompCE>());
		}
		conditionToCEmap.get(cond).add(cEx);
	}

	public void dumpPhases() {
		System.out.println("# of phases? " + phases.size());
		for (Phase phase : phases) {
			System.out.println(phase);
//			for (LocalState ls : phase) {
//				System.out.print(ls.toSmallString());
//			}
//			System.out.println();
		}
	}

	private void determineUnusableTransitions() {

		OnlyOneRegionDetector rd = new OnlyOneRegionDetector(semantics, pa);

		for (String action : semantics.getTransitionLabels()) {

			HashSet<LocalState> startStateSet = semantics.getSendingSet(action);

			HashSet<LocalState> exclusiveR = rd.buildMaximalExclusiveRegion(startStateSet);
			// If no exclusive region, just move on to the next transition
			if (exclusiveR == null) {
				continue;
			}

			// states that do not need to react to action.
			statesWithNoNeedToReactTo.put(action, exclusiveR);

		}
	}

	// private boolean isUnusable(LocalTransition trans) {
	// return unusableTransitions.contains(trans);
	// }

}
