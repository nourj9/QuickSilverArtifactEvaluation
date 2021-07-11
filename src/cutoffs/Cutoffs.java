package cutoffs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import CExample.CutoffAmenCE;
import CExample.CutoffAmenCEL2;
import CExample.CutoffAmenCEL31;
import CExample.CutoffAmenCEL32;
import Core.Options;
import cutoffs.CutoffInfo.Lemma;
import lang.core.Protocol;
import lang.specs.AtMostSpec;
import lang.specs.CompoundSpec;
import lang.specs.SafetySpecification;
import semantics.analysis.PhaseAnalysis;
import semantics.analysis.SplitAnalysis;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.Path;
import semantics.core.ProcessSemantics;
import utils.MyStringBuilder;
import semantics.core.LocalAction;

public class Cutoffs {
	private ProcessSemantics semantics;
	private SafetySpecification specs;
	private CutoffsUtils cutoffUtils;
	private ArrayList<CutoffAmenCE> counterExamples = new ArrayList<CutoffAmenCE>();
	private Options options;
	Lock addingLock = new ReentrantLock();
	private HashSet<HashSet<LocalState>> regoinCache;

	public Cutoffs(Protocol p, ProcessSemantics semantics, SafetySpecification specs, Options options) {
		if (specs == null) {
			System.err.println("Trying to compute cutoffs without specs!");
			System.exit(0);
		}

		// options
		this.options = options;

		// create a deep copy of the semantics and the specs objects?
		this.semantics = semantics.smartClone();
		this.specs = specs.smartClone();

		// just to restructure code.
		this.cutoffUtils = new CutoffsUtils(p, this.semantics, this.specs, options);

		// remove crash state and transitions
		if (options.cutoffs_removeCrashStateAndTransitions && p.hasCrashes()) {
			cutoffUtils.removeCrashStateAndTransitions();
		}

		// label freeness of transitions
		cutoffUtils.labelTransitions();

		// remove unneeded internal transitions, etc.
		if (options.cutoffs_trimStatesInCutoffs) {
			cutoffUtils.trimStates();
		}

		// Discover receive transitions which can be ignored.
		if (options.useExclusiveRegions) {
			cutoffUtils.determineUnusableTransitions();
		}

		// See which transitions are reset
		cutoffUtils.captureResetMechanism();

		// see the labeling of transitions into reset/free/unusable etc.
		cutoffUtils.printDebugInfoForTransitions();
		
		// keep track of already-used regions for new logic.
		regoinCache = new HashSet<HashSet<LocalState>>();
	}

	/*
	 * This is a recursive helper function which passes the specification object to
	 * the main cutoff computation function and interprets the result.
	 */
	public int computeCutoff() {
		CutoffInfo cutoffInfo = computeCutoff(specs);
		return cutoffInfo.getCutoff();
	}

	/*
	 * This is a recursive function which computes cutoff values by combining
	 * cutoffs from each atomic (atmost) predicate.
	 */
	private CutoffInfo computeCutoff(SafetySpecification inpSpecs) {

		// In the case of a simple specification, compute the cutoff directly.
		if (inpSpecs instanceof AtMostSpec) {

			AtMostSpec atmostSpecs = (AtMostSpec) inpSpecs;
			CutoffInfo info = computeCutoff(atmostSpecs);
			return info;

		}
		// In the case of a compound specification, first compute the cutoff
		// for each component, and then combine the results.
		else if (inpSpecs instanceof CompoundSpec) {
			CompoundSpec compoundSpecs = (CompoundSpec) inpSpecs;

			SafetySpecification arg1 = compoundSpecs.getArg1();
			SafetySpecification arg2 = compoundSpecs.getArg2();

			final CutoffInfo[] info = new CutoffInfo[2];
			if (options.cutoffs_useThreadsForCutoffs) {

				// recurse through the children in parallel
				recurseThroughBrachesInParallel(arg1, arg2, info);

			} else {

				// do it sequentially
				info[0] = computeCutoff(arg1);
				info[1] = computeCutoff(arg2);
			}

			if (compoundSpecs.getOpCode().equals(CompoundSpec.Op.AND)) {

				CutoffInfo conjunctiveCutoffInfo = new CutoffInfo();

				int cutoff = Math.max(info[0].getCutoff(), info[1].getCutoff());
				
				// add the region if never seen before
				if(info[0].getLemmaUsed().equals(Lemma.newLogic) && !regoinCache.contains(info[0].getRegoinUsed())) {
					regoinCache.add(info[0].getRegoinUsed());
					cutoff = (cutoff == Integer.MAX_VALUE? cutoff: cutoff+info[0].getNeededCardinality());
				}
				if(info[1].getLemmaUsed().equals(Lemma.newLogic) && !regoinCache.contains(info[1].getRegoinUsed())) {
					regoinCache.add(info[1].getRegoinUsed());
					cutoff = (cutoff == Integer.MAX_VALUE? cutoff: cutoff+info[1].getNeededCardinality());
				}
				
				conjunctiveCutoffInfo.setCutoff(cutoff);

				conjunctiveCutoffInfo.addSetsOfPaths(info[0].getSetsOfPaths());
				conjunctiveCutoffInfo.addSetsOfPaths(info[1].getSetsOfPaths());

				if (info[0].getLemmaUsed().equals(info[1].getLemmaUsed())) {
					conjunctiveCutoffInfo.setLemmaUsed(info[0].getLemmaUsed());
				} else {
					conjunctiveCutoffInfo.setLemmaUsed(Lemma.MultiLemma);
				}

				return conjunctiveCutoffInfo;

			} else if (compoundSpecs.getOpCode().equals(CompoundSpec.Op.OR)) {

				// do the non-interference check here
				if (!nonInterferenceIsMet(info[0], info[1])) {
					System.err.println("Lemma 3 failed: non-interference required for disjunctive Specs not met");
					// System.exit(-1); // for now
					CutoffInfo errInfo = new CutoffInfo();
					errInfo.setCutoff(Integer.MAX_VALUE);
					return errInfo;
				}

				CutoffInfo disunctiveCutoffInfo = new CutoffInfo();

				int cutoff1 = info[0].getCutoff();
				int cutoff2 = info[1].getCutoff();
				int cutoff = (cutoff1 == Integer.MAX_VALUE || cutoff2 == Integer.MAX_VALUE) ? Integer.MAX_VALUE
						: cutoff1 + cutoff2;
				
				
				// add the region if never seen before
				if(info[0].getLemmaUsed().equals(Lemma.newLogic) && !regoinCache.contains(info[0].getRegoinUsed())) {
					regoinCache.add(info[0].getRegoinUsed());
					cutoff = (cutoff == Integer.MAX_VALUE? cutoff: cutoff+info[0].getNeededCardinality());
				}
				if(info[1].getLemmaUsed().equals(Lemma.newLogic) && !regoinCache.contains(info[1].getRegoinUsed())) {
					regoinCache.add(info[1].getRegoinUsed());
					cutoff = (cutoff == Integer.MAX_VALUE? cutoff: cutoff+info[1].getNeededCardinality());
				}
				
				disunctiveCutoffInfo.setCutoff(cutoff);

				disunctiveCutoffInfo.addSetsOfPaths(info[0].getSetsOfPaths());
				disunctiveCutoffInfo.addSetsOfPaths(info[1].getSetsOfPaths());

				if (info[0].getLemmaUsed().equals(info[1].getLemmaUsed())) {
					disunctiveCutoffInfo.setLemmaUsed(info[0].getLemmaUsed());
				} else {
					disunctiveCutoffInfo.setLemmaUsed(Lemma.MultiLemma);
				}

				return disunctiveCutoffInfo;
			}

		}

		// Should never happen.
		System.err.println("unkown/unsupported spec type: " + inpSpecs);
		System.exit(-1); // for now
		CutoffInfo errInfo = new CutoffInfo();
		errInfo.setCutoff(Integer.MAX_VALUE);
		return errInfo;
	}

	private boolean nonInterferenceIsMet(CutoffInfo info1, CutoffInfo info2) {

		if (info1.getLemmaUsed() == null || info2.getLemmaUsed() == null) {
			return true;
		}
//
//		if (info1.getLemmaUsed().equals(CutoffInfo.Lemma.Lemma1)
//				&& info2.getLemmaUsed().equals(CutoffInfo.Lemma.Lemma1)) {
//
//			for (HashSet<Path> setOfPaths1 : info1.getSetsOfPaths()) {
//				for (HashSet<Path> setOfPaths2 : info2.getSetsOfPaths()) {
//					for (Path p1 : setOfPaths1) {
//						for (Path p2 : setOfPaths2) {
//
//							boolean disjoint = true;
//							for (LocalTransition tr1 : p1.getTransitions()) {
//								for (LocalTransition tr2 : p2.getTransitions()) {
//
//									if (tr1.equals(tr2)) { // found a shared transition,
//										disjoint = false;
//
//										if (!cutoffUtils.isFreeAndAllowsKProcessesSimultaneously(tr1, 2 /* happy? */)) {
//											// if it does not allows 2 processes to go through, we have a problem.
//											return false;
//										}
//									}
//								}
//							}
//							// a disjoint pair? perfect; each process can take one of these.
//							if (disjoint) {
//								return true;
//							}
//
//						}
//					}
//				}
//			}
//
//		} else {
////		for (HashSet<Path> setOfPaths1 : info1.getSetsOfPaths()) {
////			for (Path p1 : setOfPaths1) {
////				for (LocalState st1 : p1.getStates()) {
////					////////////////////////////////////////
////					for (HashSet<Path> setOfPaths2 : info2.getSetsOfPaths()) {
////						for (Path p2 : setOfPaths2) {
////							for (LocalState st2 : p2.getStates()) {
////								if (st1.equals(st2)) {
////									System.out.println("xxxxxx: shared states: " + st1);
////								}
////							}
////						}
////					}
////					////////////////////////////////////////
////				}
////			}
////		}
//
//			// System.out.println("xx: exit");
//			// System.exit(-1);
//		}
		return true; // TODO fix
	}

	public CutoffInfo computeCutoff(AtMostSpec atmostSpecs) {

		CutoffInfo info = new CutoffInfo();
		LocalState initState = semantics.getInitialState();
		int threshold = atmostSpecs.getThreshold();
		HashSet<LocalState> targetStates = atmostSpecs.extractLocalStates();

		// if s and s' are going to dummy, and s-->s' using an internal transition,
		// and s' has no other incoming transitions, then ignore s'.
		cutoffUtils.refineTragetStates(targetStates);

		if (options.cutoffs_debugInfoForCutoffs) {
			cutoffUtils.printDebugInfoForPaths(initState, targetStates);
		}

		HashSet<Path> nonFreeUsablePaths = null;
		HashSet<Path> simpleFreePaths = null;

		if (amenabilityConditionsPerLemma2Hold(initState, targetStates, nonFreeUsablePaths)) {
			if (options.cutoffs_printWhichLemmaIsUsed) {
				System.out.println(">>> Lemma 2 was used for target states: " + atmostSpecs);
			}
			info.setLemmaUsed(CutoffInfo.Lemma.Lemma2);
			info.setCutoff(threshold + 1);
			info.addSetOfPaths(nonFreeUsablePaths);
		} else if (amenabilityConditionsPerLemma3Hold(initState, targetStates, simpleFreePaths)) {
			if (options.cutoffs_printWhichLemmaIsUsed) {
				System.out.println(">>> Lemma 3 was used for target states: " + atmostSpecs);
			}
			info.setLemmaUsed(CutoffInfo.Lemma.Lemma3);
			info.setCutoff(threshold + 1);
			info.addSetOfPaths(simpleFreePaths);
		} else {
			SplitAnalysis split = new SplitAnalysis(initState, targetStates, semantics);
			if (split.targetStatesAreInAReceiveOnlyParametricZone()) {
				info.setLemmaUsed(CutoffInfo.Lemma.newLogic);
				info.setCutoff(threshold + 1);
				// info.addSetOfPaths(...); //TODO not sure
				 info.setUsedRegoin(split.getParametericReactingRegoin());
				 info.setCardNeededToEnterRegoin(split.getNeededCardinality());
			} else {
				info.setCutoff(Integer.MAX_VALUE);
				info.setLemmaUsed(Lemma.NoLemma);
			}
		}
		return info;
	}

	private boolean amenabilityConditionsPerLemma2Hold(LocalState initState, HashSet<LocalState> targetStates,
			HashSet<Path> nonFreeUsablePaths) {
		nonFreeUsablePaths = semantics.getAllNonFreeUsablePaths(initState, targetStates,
				options.cutoffs_earlyTerminationForCutoffs);

		if (nonFreeUsablePaths.isEmpty()) {
			return true;
		}
		Path shortest = nonFreeUsablePaths.iterator().next();
		if (options.cutoffs_useShortestPathInLemma2CE) {
			for (Path p : nonFreeUsablePaths) {
				if (p.getTransitions().size() < shortest.getTransitions().size()) {
					shortest = p;
				}
			}
		}
		CutoffAmenCE cex = new CutoffAmenCEL2(initState, targetStates, shortest, nonFreeUsablePaths, semantics);
		addToCounterExamples(cex);

		return false;
	}

	private boolean amenabilityConditionsPerLemma3Hold(LocalState initState, HashSet<LocalState> targetStates,
			HashSet<Path> simpleFreePaths) {

		simpleFreePaths = semantics.getAllSimpleFreePaths(initState, targetStates);

		if (simpleFreePaths.isEmpty()) {

			debugInfo("lemma 3 failed: no simple free paths exists.");
			//System.err.println("lemma 3 failed: no simple free paths exists.");
			return false;
		}

		HashSet<LocalTransition> transitionsOnFreePaths = new HashSet<LocalTransition>();
		HashSet<LocalState> statesOnFreePaths = new HashSet<LocalState>();

		boolean noViolations = true;

		// some caching for performance
		// transitionsOnFreePaths.clear();
		// statesOnFreePaths.clear();

		for (Path path : simpleFreePaths) {
			for (LocalTransition tr : path.getTransitions()) {
				transitionsOnFreePaths.add(tr);
			}
			for (LocalState st : path.getStates()) {
				statesOnFreePaths.add(st);
			}
		}
		HashSet<LocalState> currentTargetStates = targetStates;

		/** 
		 * All acting \ValueCons transitions appear in paths in f, and,
		 */
		// if (!allActingValstoreTransitionsAppearInFreePaths()) {
		// return false;
		// }

		/**
		 * For each acting transition of any kind:
		 * 
		 */

		for (LocalTransition acting : semantics.getTransitions()) {
			if (!actingWRTcutoffs(acting)) {
				continue;
			}

			ArrayList<LocalTransition> reactingTrs = getCorrespondingReactingTransitions(acting);

			if (!transitionsOnFreePaths.contains(acting)) {
				debugInfo("!!!!! ACTTING transition " + acting + " DOES NOT appear on free paths and:");
				/**
				 *	 (1) the transition does not appear in paths in f and..,
				 */

				/**	  
				 *     the corresponding reacting transitions 
				 *     $\trans{ss}{}{sd}$ with $ss \in p$ for some $p \in f$ have $sd=ss$,
				 */
				// begin in-lining reactingTrsStartingInFreePathsDoSelfLoop(reactingTrs)
				{

					for (LocalTransition reacting : reactingTrs) {

						if (canSkipItUsingInternalTr(reacting.getSrc(), statesOnFreePaths)) { // ignore receives that
																								// can be
							// skipped.
							debugInfo("reacting trasnsition" + reacting + " was skipped using an internal transition");
							continue;
						}

						if (reacting.isReset()) {
							debugInfo("reacting trasnsition" + reacting + " was skipped becasue it is a reset react.");
							continue;
						}

						if (currentTargetStates.contains(reacting.getSrc())) { // ignore receives out of the last state
							debugInfo("reacting trasnsition" + reacting
									+ " was ignored as it's coming out of a target state");
							continue;
						}

						if (statesOnFreePaths.contains(reacting.getSrc())) { // if a receive starts on the path and,
							if (reacting.isSelfLoop() || // is a self loop, then skip it.
									canLoopBackUsingInternalTransitions(reacting)) { // not a strict self-loop, but does
																						// come
								// back.
								debugInfo("reacting trasnsition" + reacting
										+ " was skipped becasue it's kind of a self-loop");
								continue;
							}

							if (reacting.getTransitionType().equals(LocalAction.ActionType.VALUE_CONS)) {
								// If you have a VS receive that is on a free path
								// and goes to a state with a VS self loop with the same label,
								// then that VS receive is free.
//								if (statesOnFreePaths.contains(reacting.getDest())) {
								if (destStateOnFreePathAndContainsASelfLoopVSwithSameLabel(reacting,
										statesOnFreePaths)) {
									debugInfo("reacting trasnsition" + reacting + " was skipped  "
											+ "becasue it is a VC transition that goes "
											+ "to a state with a self loop on the same label ");
									continue;
								}
								// }
							}

							noViolations = false;

							CutoffAmenCE cex = new CutoffAmenCEL31(initState, targetStates, acting, reacting,
									simpleFreePaths, semantics);

							debugInfo("fail: reacting transition " + reacting
									+ " started on the path and did not self loop");

							addToCounterExamples(cex);

							if (options.cutoffs_earlyTerminationForCutoffs) {
								return false;
							}
						}
					}

				}
				// end in-lining reactingTrsStartingInFreePathsDoSelfLoop

			} else {
				debugInfo("!!!!! ACTTING transition " + acting + " DOES appear on free paths and:");
				/**
				 *	 (2) the transition appears in paths in f and the following holds for every corresponding 
				 *       reacting transition... 
				 */

				/**
				 *     $\trans{ss}{}{sd}$ where $ss \in p$ for some $p \in f$ and 
				 *     $sd \notin p$ for any $p \in f$: either (a) there exists an internal transition 
				 *     $\trans{ss}{}{sd'}$ with $sd' \in p$ for some $p \in f$, or (b) all paths out of 
				 *     $sd$ lead back to a state $sf$ in a path in f and are \freech between $sd$ and $sf$.
				 * */
				// begin in-lining
				// reactingTrsStartingInFreePathsAreSkippableOrComeBackFreely(actingTr,
				// reactingTrs)
				{
					for (LocalTransition reacting : reactingTrs) {

						/*
						 * from initial state, find a fork (acting and corresponding reacting) get the
						 * losing destination region and winning destination region (see what it can
						 * reach without taking reset) In the losing region: - must be completely
						 * disjoint from the winning region - Any acting transition from this region
						 * either doesn't exist in the other region or it self loops
						 *
						 * Functions: - get reachable region w/o resets - check disjoint (state sets) -
						 * get acting transitions in region - get reacting transitions in region
						 */
						if (cutoffUtils.isIgnorableForkingReact(reacting)) {
							debugInfo(
									"reacting trasnsition" + reacting + " was skipped becasue it is a forking react.");
							continue;
						}

						if (reacting.isReset()) {
							debugInfo("reacting trasnsition" + reacting + " was skipped becasue it is a reset react.");
							continue;
						}

						if (currentTargetStates.contains(reacting.getSrc()) || // ignore receives out of the last state
								reacting.isFree()) { // to avoid worrying about receive transitions that are part of
							// negotiation.
							debugInfo("reacting trasnsition" + reacting
									+ " was skipped because it comes out of the target states.");
							continue;
						}

						if (statesOnFreePaths.contains(reacting.getSrc())
								&& !statesOnFreePaths.contains(reacting.getDest())) { // starts
							// on
							// a
							// path
							// and
							// leaves
							// F
							if (!(canSkipItUsingInternalTr(reacting.getSrc(), statesOnFreePaths) || comesBackFreely(
									reacting.getDest(), acting, statesOnFreePaths, currentTargetStates))) {
								noViolations = false;

								CutoffAmenCE cex = new CutoffAmenCEL32(initState, targetStates, acting, reacting,
										simpleFreePaths, semantics);

								debugInfo("fail: reacting transition " + reacting + " diverged and did not come back");

								addToCounterExamples(cex);

								if (options.cutoffs_earlyTerminationForCutoffs) {
									return false;
								}

							} else {
								debugInfo("reacting trasnsition" + reacting
										+ " was ignored because it can be skipped or comes back freely");
							}
						}
					}
				}
				// end in-lining reactingTrsStartingInFreePathsAreSkippableOrComeBackFreely

			}
		}

		return noViolations;

	}

	private void debugInfo(Object obj) {
		if (options.cutoffs_detailedDubgInfoForCutoffs) {
			System.out.println(">>>>>> INFO: " + obj);
		}
	}

//	private boolean reactingTrsStartingInFreePathsAreSkippableOrComeBackFreely(LocalTransition actingTr,
//			ArrayList<LocalTransition> reactingTrs) {
//
//		/**
//		 *     $\trans{ss}{}{sd}$ where $ss \in p$ for some $p \in f$ and 
//		 *     $sd \notin p$ for any $p \in f$: either (a) there exists an internal transition 
//		 *     $\trans{ss}{}{sd'}$ with $sd' \in p$ for some $p \in f$, or (b) all paths out of 
//		 *     $sd$ lead back to a state $sf$ in a path in f and are \freech between $sd$ and $sf$.
//		 * */
//
//		for (LocalTransition tr : reactingTrs) {
//
//			if (currentTargetStates.contains(tr.getSrc())) { // ignore receives out of the last state
//				continue;
//			}
//
//			if (tr.isFree()) { // to avoid worrying about receive transitions that are part of negotiation.
//				continue;
//			}
//
//			if (statesOnFreePaths.contains(tr.getSrc()) && !statesOnFreePaths.contains(tr.getDest())) { // starts on a
//																										// path and
//																										// leaves F
//				if (!(canSkipItUsingInternalTr(tr.getSrc()) || comesBackFreely(tr.getDest(), actingTr))) {
//					System.err.println(
//							"Lemma 3 failed: The following reacting transition diverged and couldn't be skipped by internal or come back freely:\n"
//									+ tr);
//					return false;
//				}
//			}
//		}
//
//		return true;
//	}

	/**
	 * all paths out of $sd$ lead back to a state $sf$ 
	 * in a path in f and are \freech between $sd$ and $sf$.
	 * @param actingTr 
	 * */
	private boolean comesBackFreely(LocalState st, LocalTransition actingTr, HashSet<LocalState> statesOnFreePaths,
			HashSet<LocalState> currentTargetStates) {

		// if it is already there, we're done
		if (statesOnFreePaths.contains(st)) {
			return true;
		}

		ArrayList<Path> pathsInProgress = new ArrayList<Path>();

		for (LocalTransition tr : st.getOutgoingTransitions()) {
			if (!tr.isFree() && !tr.isSelfLoop()
					&& !guarnteedToBeFreeAtThatPoint(tr, actingTr, statesOnFreePaths, currentTargetStates)) {
				// has a non-free actual transition out if it.

				// System.err.println("Lemma 3 failed: While coming back from " +
				// st.toSmallString()
				// + " we encountered the following non-free transition: " + tr);
				return false;
			} else { // otherwise, add to explore
				Path p = new Path();
				if (p.addTransition(tr)) {
					pathsInProgress.add(p);
				}
			}
		}

		if (pathsInProgress.isEmpty()) {
			// System.err.println("Lemma 3 failed: No way to come back from" +
			// st.toSmallString());
			return false;
		}

		boolean newTransitionsAreAdded = false;
		while (!pathsInProgress.isEmpty()) {
			Path currentPath = pathsInProgress.remove(0);

			if (!statesOnFreePaths.contains(currentPath.getLastState())) { // did not reach a free path yet

				newTransitionsAreAdded = false;
				for (LocalTransition tr : currentPath.getLastState().getOutgoingTransitions()) {

					if (!tr.isFree() && !tr.isSelfLoop()) { // we did hit a actual non-free transition..
						// System.err.println("Lemma 3 failed: While coming back from " +
						// st.toSmallString()
						// + " we encountered the following non-free transition: " + tr);
						return false;
					} else {
						Path p = new Path(currentPath);
						if (p.addTransition(tr)) {
							pathsInProgress.add(p);
							newTransitionsAreAdded = true;
						}
//						else {
//							//  think more about looping back with free a transition. seems ok
//						}
					}
				}

				if (!newTransitionsAreAdded) {
					// System.err.println("Lemma 3 failed: While coming back from " +
					// st.toSmallString()
					// + " the following path hits a dead-end: " + currentPath);
					return false; // some path couldn't do progress or make it to the set of free paths.
				}

			} else {
				// nothing, the path is removed.
			}
		}

		// if (!pathsInProgress.isEmpty()) { // some path couldn't do progress or make
		// it to the set of free paths.
		// return false;
		// }

		return true; // all good
	}

	/**
	 * This handles cases like DMR where we have the following: [actingTr] s1 -- a!! --> s2 -- b!! --> s3 and
	 * the [divergingReaction] s4 -- a?? --> s5 is diverging. Then, from s5, the receive -- b?? --> is ok (aka free)
	 * @param actingTr 
	 * */
	private boolean guarnteedToBeFreeAtThatPoint(LocalTransition divergingReaction, LocalTransition actingTr,
			HashSet<LocalState> statesOnFreePaths, HashSet<LocalState> currentTargetStates) {

		ArrayList<LocalTransition> outoingTrs = actingTr.getDest().getOutgoingTransitions();
		HashSet<LocalTransition> availableActingTrs = new HashSet<LocalTransition>();
		for (LocalTransition outgoingTr : outoingTrs) {
			if (actingWRTcutoffs(outgoingTr)) { // acting
				if (currentTargetStates.contains(actingTr.getDest())) { // last state on path? then we don't care where
																		// it goes.
					availableActingTrs.add(outgoingTr);
				} else {// then we require it to also be on the free path.
					if (currentTargetStates.contains(outgoingTr.getDest())) {
						availableActingTrs.add(outgoingTr);
					}
				}
			}
		}

		// System.out.println(availableActingTrs);
		// System.exit(-1);

		// if the reacting transition corresponds to any of the available acting ones,
		// we can consider it ok.
		for (LocalTransition availableActingTr : availableActingTrs) {
			LocalAction.ActionType reactingType = LocalTransition
					.correspondingReceiveType(availableActingTr.getTransitionType());

			if (divergingReaction.getTransitionType().equals(reactingType)
					&& divergingReaction.getLabel().equals(availableActingTr.getLabel())) {
				return true;
			}

		}
		return false;
	}

	/**
	 * returns ture if that source state includes an internal/env pairwise transition to a free path.
	 * */
	private boolean canSkipItUsingInternalTr(LocalState src, HashSet<LocalState> statesOnFreePaths) {

		for (LocalTransition tr : src.getOutgoingTransitions()) {
			if (tr.isInternalOrEnvPairwise() && statesOnFreePaths.contains(tr.getDest())) {
				return true;
			}
		}

		return false;
	}

//	private boolean reactingTrsStartingInFreePathsDoSelfLoop(ArrayList<LocalTransition> reactingTrs) {
//
//		for (LocalTransition tr : reactingTrs) {
//
//			// NEW123
//			if (canSkipItUsingInternalTr(tr.getSrc())) { // ignore receives that can be skipped.
//				continue;
//			}
//
//			if (currentTargetStates.contains(tr.getSrc())) { // ignore receives out of the last state
//				continue;
//			}
//
//			if (statesOnFreePaths.contains(tr.getSrc())) { // if a receive starts on the path and,
//				if (tr.isSelfLoop()) { // is a self loop, then skip it.
//					continue;
//				}
//
//				if (canLoopBackUsingInternalTransitions(tr)) { // not a strict self-loop, but does come back.
//					continue;
//				}
//
//				if (tr.getTransitionType().equals(LocalAction.Type.VALUE_CONS)) {
//					// If you have a VS receive that is on a free path
//					// and goes to a state with a VS self loop with the same label,
//					// then that VS receive is free.
//					if (statesOnFreePaths.contains(tr.getDest())) {
//						if (destStateContainsASelfLoopVSwithSameLabel(tr)) {
//							continue;
//						}
//					}
//				}
//
//				System.err.println(
//						"Lemma 3 failed: The following reacting transition started on the path and did not self loop:\n"

//				return false;
//			}
//		}
//
//		return true;
//	}

	private boolean destStateOnFreePathAndContainsASelfLoopVSwithSameLabel(LocalTransition inTr,
			HashSet<LocalState> statesOnFreePaths) {
		// note: if two vc transitions are going to the same location, then they are
		// trying to
		// set the same value as winning

		LocalState destState = inTr.getDest();

		// Edit: if this state is a "railroad state": a state that can only be left by
		// an internal transition (which we usually used to trim), then use the dest of
		// that internal transition instead. can while-loop this too.
		while (destState.isRailRoadState()) {
			LocalState nextDestState = destState.getOutgoingTransitions().get(0).getDest();

			// avoid infinite loops? (needs more than this for longer paths..)
			if (destState.equals(nextDestState)) {
				break;
			} else {
				destState = nextDestState;
			}
		}

		// if on a free path and has a conceptual self loop on that action, then ok,
		// otherwise a problem.
		if (statesOnFreePaths.contains(destState)) {
			LocalAction inAction = inTr.getLocalAction();

			if (destState.hasRailRoadSelfLoopOn(inAction)) {

				return true;
			}
		}

//		old code.
//		for (LocalTransition tr : destState.getOutgoingTransitions()) {
//			if (tr.isSelfLoop() && tr.getTransitionType().equals(LocalAction.Type.VALUE_CONS)
//					&& tr.getLabel().equals(inLabel)) {
//				return true;
//			}
//		}

		return false;
	}

	private boolean canLoopBackUsingInternalTransitions(LocalTransition tr) {

		LocalState targetState = tr.getSrc();
		LocalState startState = tr.getDest();

		HashSet<Path> paths = semantics.getAllSimpleInternalPathsFromTo(startState, targetState);

		return !paths.isEmpty();
	}

	private ArrayList<LocalTransition> getCorrespondingReactingTransitions(LocalTransition actingTr) {
		ArrayList<LocalTransition> toRet = new ArrayList<LocalTransition>();

		for (LocalTransition tr : semantics.getTransitions()) {

			if (!tr.getLabel().equals(actingTr.getLabel())) {
				continue;
			}

			if (!tr.getTransitionType()
					.equals(LocalTransition.correspondingReceiveType(actingTr.getTransitionType()))) {
				continue;
			}

			toRet.add(tr);
		}

		return toRet;
	}

	private boolean actingWRTcutoffs(LocalTransition tr) {

		if (tr.isEnvironmentTransition()) {
			return false;
		} else {
			return tr.isActingGlobalTransition();
		}
	}

	/**
	 * arg1, arg2 are the two branches
	 * info is an array (of length 2) containing the results from each branch
	 * */
	private void recurseThroughBrachesInParallel(SafetySpecification arg1, SafetySpecification arg2,
			CutoffInfo[] info) {
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				if (options.cutoffs_cutoffThreadDebugInfo) {
					System.out.println("Thread " + this.hashCode() + " started");
				}

				info[0] = computeCutoff(arg1);

				if (options.cutoffs_cutoffThreadDebugInfo) {
					System.out.println("Thread " + this.hashCode() + " done");
				}
			}
		});

		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				if (options.cutoffs_cutoffThreadDebugInfo) {
					System.out.println("Thread " + this.hashCode() + " started");
				}

				info[1] = computeCutoff(arg2);

				if (options.cutoffs_cutoffThreadDebugInfo) {
					System.out.println("Thread " + this.hashCode() + " done");
				}
			}
		});

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// to securely add to this when using threads.
	private void addToCounterExamples(CutoffAmenCE cex) {
		if (options.cutoffs_useThreadsForCutoffs) {
			addingLock.lock();
			counterExamples.add(cex);
			addingLock.unlock();
		} else {
			counterExamples.add(cex);
		}
	}

	public ProcessSemantics getSemantics() {
		return semantics;
	}

	public void setSemantics(ProcessSemantics semantics) {
		this.semantics = semantics;
	}

	public SafetySpecification getSpec() {
		return specs;
	}

	public void setSpec(SafetySpecification specs) {
		this.specs = specs;
	}

	public void printErrorsAndSuggestions(Options options) {

		MyStringBuilder sb = new MyStringBuilder();

		sb.appn("All feedback for cutoffs:");
		for (CutoffAmenCE ce : counterExamples) {
			sb.appn(ce.toString());
		}
		System.err.println(sb.toString());

	}

	public void printErrors(Options options) {

		MyStringBuilder sb = new MyStringBuilder();

		sb.appn("All feedback for cutoffs:");
		for (CutoffAmenCE ce : counterExamples) {
			sb.appn(ce.getDescription());
		}
		System.err.println(sb.toString());

	}

}
