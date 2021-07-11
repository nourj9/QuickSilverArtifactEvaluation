package semantics.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;

public class PhaseAnalysis {
	private ProcessSemantics semantics;
	private HashSet<Phase> phases = null;
	private HashMap<LocalState, HashSet<Phase>> phaseMap = null;

	public PhaseAnalysis(ProcessSemantics semantics) {
		this.semantics = semantics;
		computePhases();
	}

	public void computePhases() {

		// Always start with a fresh set of phases
		phases = new HashSet<Phase>();
		phaseMap = new HashMap<LocalState, HashSet<Phase>>();

		// Start by creating phases from the source and destination sets
		for (String label : semantics.getTransitionLabels()) {

			// skip the non-globally-synchronizing labels
			if (!semantics.isGloballySynchronizingLabel(label)) {
				continue;
			}

			phases.add(new CorePhase(this, Phase.T.src, semantics.getSourceSet(label), label));
			phases.add(new CorePhase(this, Phase.T.dst, semantics.getDestinationSet(label), label));
		}
		// mergeOverlappingPhases();

		// Create a phase for any state which is not already in a source/destination set
		SINGLETON_OUTER: for (LocalState s : semantics.getStates()) {

			// Check if the state is already in another phase
			for (Phase phase : phases) {
				if (phase.contains(s))
					continue SINGLETON_OUTER;
			}

			// If not, create the singleton phase and add it.
			HashSet<LocalState> states = new HashSet<LocalState>();
			states.add(s);
			CorePhase singletonPhase = new CorePhase(this, Phase.T.singeleton, states, "");
			// Phase singletonPhase = new Phase(this, Phase.T.singeleton);
			// singletonPhase.add(s);
			phases.add(singletonPhase);
		}

		// Expand each phase through its internal and pairwise transitions to a fixed
		// point.
		HashSet<Phase> newPhases = new HashSet<Phase>();
		for (Phase phase : phases) {
			// System.out.println("PHASE BEFORE: (" + phase.hashCode() + ")" + phase);
			expandPhase((CorePhase) phase);
			// System.out.println("PHASE AFTER: (" + phase.hashCode() + ")" + phase);
			if (newPhases.contains(phase)) {
				newPhases.remove(phase);
			}
			newPhases.add(phase);
		}
		phases = newPhases;

		// Combine any intersecting sets to get the transitive closure
		// mergeOverlappingPhases();

		// No reason to expand more than once,
		// since states that are directly related (srcSet, destSet, or internal/pw)
		// have already been put in the same phase.

		mergePhasesInternalPw();

		// Finally, compute the phase lookup map
		for (LocalState s : semantics.getStates()) {
			HashSet<Phase> containingPhases = new HashSet<Phase>();
			for (Phase phase : phases) {
				if (phase.contains(s)) {
					containingPhases.add(phase);
				}
			}
			phaseMap.put(s, containingPhases);
		}

		// Finally, compute the phase lookup map
		/*
		 * for (HashSet<LocalState> phase : phases) { for (LocalState s : phase) {
		 * if(!phaseMap.containsKey(s)) { phaseMap.put(s, new
		 * HashSet<HashSet<LocalState>>()); } phaseMap.get(s).add(phase); } }
		 */

	}

	private void mergePhasesInternalPw() {
		for (LocalTransition trans : semantics.getTransitions()) {
			
			// Filter for internal and pairwise
			if (!(trans.isInternal() || trans.isPairwiseTransition())) {
				continue;
			}

			// ignore internal self-loops here.
			if (trans.isInternal() && trans.isSelfLoop()) {
				continue;
			}

			HashSet<Phase> containingPhases = new HashSet<Phase>();
			for (Phase phase : phases) {
				if (phase.contains(trans.getSrc())) {
					containingPhases.add(phase);
				}
			}

			// now the merging happens in the constructor
			// remove the parts, and add the merged one
			if (containingPhases.size() > 1) {
				MergedPhase mergedPhase = new MergedPhase(this, containingPhases, trans);
				phases.removeAll(containingPhases);
				phases.add(mergedPhase);
			}
		}
	}

	// Expand the given phase through internal and pairwise transitions.
	private void expandPhase(CorePhase phase) {

		Iterator<LocalState> phaseItr;

		boolean changeMade = true;
		PHASE_OUTER: while (changeMade) {
			changeMade = false;
			for (LocalTransition t : semantics.getTransitions()) {

				// filter for internal and pairwise transitions
				// this removes globally sync actions and crash actions.
				if (!(t.isInternal() || t.isPairwiseTransition())) {
					continue;
				}

//				if (t.isGloballySynchronizing()) {
//					continue;
//				}

				// ignore internal self loops here, too
				if (t.isInternal() && t.isSelfLoop()) {
					continue;
				}

				// Check if the transition expands the current phase
				phaseItr = phase.iterator();
				while (phaseItr.hasNext()) {
					LocalState s = phaseItr.next();
					if (t.getSrc() == s && !(phase.contains(t.getDest()))) { // transition starts in phase and leaves it
						// phase.add(t.getDest());
						phase.expandToDestThrough(t);

						if (t.isPairwiseTransition() && !t.isEnvironmentTransition()) { // If pairwise, add a source and//TODO
																						// destination states of this
																						// action...
							for (LocalTransition pwTrans : semantics.getTransitions()) {
								if (t == pwTrans || !pwTrans.isPairwiseTransition()) {
									continue;
								}
								if (pwTrans.getLabel().equals(t.getLabel())) {
									// phase.add(pwTrans.getSrc());
									// phase.add(pwTrans.getDest());
									phase.expandToSrcThrough(pwTrans);
									phase.expandToDestThrough(pwTrans);
								}
							}
						}
						changeMade = true;
						continue PHASE_OUTER;
					}
					if (t.getDest() == s && !(phase.contains(t.getSrc()))) { // transition starts outside and ends in

						// phase.add(t.getSrc());
						phase.expandToSrcThrough(t);

						if (t.isPairwiseTransition() &&  !t.isEnvironmentTransition()) { // If pairwise, add a source and//TODO
																						// destination states of this
							// action...
							for (LocalTransition pwTrans : semantics.getTransitions()) {
								if (t == pwTrans || !pwTrans.isPairwiseTransition()) {
									continue;
								}
								if (pwTrans.getLabel().equals(t.getLabel())) {
									// phase.add(pwTrans.getSrc());
									// phase.add(pwTrans.getDest());
									phase.expandToSrcThrough(pwTrans);
									phase.expandToDestThrough(pwTrans);
								}
							}
						}
						changeMade = true;
						continue PHASE_OUTER;
					}
				}
			}
		}
	}

	public HashSet<Phase> getPhases() {
		return phases;
	}

	public HashMap<LocalState, HashSet<Phase>> getPhaseMap() {
		return phaseMap;
	}

	// return all phases containing s1 and s2
	public HashSet<Phase> getPhasesContaining(LocalState s1, LocalState s2) {
		HashSet<Phase> toRet = new HashSet<Phase>();
		for (Phase phase : phases) {
			if (phase.contains(s1) && phase.contains(s2)) {
				toRet.add(phase);
			}
		}
		return toRet;
	}

}
