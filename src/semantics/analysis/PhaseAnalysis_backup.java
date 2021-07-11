package semantics.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;

public class PhaseAnalysis_backup {
	private ProcessSemantics semantics;
	private HashSet<HashSet<LocalState>> phases = null;
	private HashMap<LocalState, HashSet<HashSet<LocalState>>> phaseMap = null;

	public PhaseAnalysis_backup(ProcessSemantics semantics) {
		this.semantics = semantics;
		computePhases();
	}

	public void computePhases() {

		// Always start with a fresh set of phases
		phases = new HashSet<HashSet<LocalState>>();
		phaseMap = new HashMap<LocalState, HashSet<HashSet<LocalState>>>();

		// Start by creating phases from the source and destination sets
		for (String label : semantics.getTransitionLabels()) {

			// skip the non-globally-synchronizing labels
			if (!semantics.isGloballySynchronizingLabel(label)) {
				continue;
			}

			phases.add(semantics.getSourceSet(label));
			phases.add(semantics.getDestinationSet(label));
		}
		// mergeOverlappingPhases();

		// Create a phase for any state which is not already in a source/destination set
		SINGLETON_OUTER: for (LocalState s : semantics.getStates()) {

			// Check if the state is already in another phase
			for (HashSet<LocalState> phase : phases) {
				if (phase.contains(s))
					continue SINGLETON_OUTER;
			}

			// If not, create the singleton phase and add it.
			HashSet<LocalState> singletonPhase = new HashSet<LocalState>();
			singletonPhase.add(s);
			phases.add(singletonPhase);
		}

		// Expand each phase through its internal and pairwise transitions to a fixed
		// point.
		HashSet<HashSet<LocalState>> newPhases = new HashSet<HashSet<LocalState>>();
		for (HashSet<LocalState> phase : phases) {
			// System.out.println("PHASE BEFORE: (" + phase.hashCode() + ")" + phase);
			expandPhase(phase);
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
			HashSet<HashSet<LocalState>> containingPhases = new HashSet<HashSet<LocalState>>();
			for (HashSet<LocalState> phase : phases) {
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
			if (trans.isGloballySynchronizing()) {
				continue;
			} // Filter for internal and pairwise

			// ignore internal self-loops here.
			if (trans.isInternal() && trans.isSelfLoop()) {
				continue;
			}

			ArrayList<HashSet<LocalState>> containingPhases = new ArrayList<HashSet<LocalState>>();
			for (HashSet<LocalState> phase : phases) {
				if (phase.contains(trans.getSrc())) {
					containingPhases.add(phase);
				}
			}
			if (containingPhases.size() > 1) {
				HashSet<LocalState> mergedPhase = new HashSet<LocalState>();
				for (int i = containingPhases.size(); i-- > 0;) {
					HashSet<LocalState> currPhase = containingPhases.get(i);
					for (LocalState relatedState : currPhase) {
						mergedPhase.add(relatedState);
					}
					phases.remove(currPhase);
				}
				phases.add(mergedPhase);
			}
		}
	}

	// Expand the given phase through internal and pairwise transitions.
	private void expandPhase(HashSet<LocalState> phase) {

		Iterator<LocalState> phaseItr;

		boolean changeMade = true;
		PHASE_OUTER: while (changeMade) {
			changeMade = false;
			for (LocalTransition t : semantics.getTransitions()) {

				// filter for internal and pairwise transitions
				if (t.isGloballySynchronizing()) {
					continue;
				}

				// ignore internal self loops here, too
				if (t.isInternal() && t.isSelfLoop()) {
					continue;
				}

				// Check if the transition expands the current phase
				phaseItr = phase.iterator();
				while (phaseItr.hasNext()) {
					LocalState s = phaseItr.next();
					if (t.getSrc() == s && !(phase.contains(t.getDest()))) {
						phase.add(t.getDest());
						if (t.isPairwiseTransition() && !t.isEnvironmentTransition()) { // If pairwise, add a source and
																						// destination states of this
																						// action...
							for (LocalTransition pwTrans : semantics.getTransitions()) {
								if (t == pwTrans || !pwTrans.isPairwiseTransition()) {
									continue;
								}
								if (pwTrans.getLabel().equals(t.getLabel())) {
									phase.add(pwTrans.getSrc());
									phase.add(pwTrans.getDest());
								}
							}
						}
						changeMade = true;
						continue PHASE_OUTER;
					}
					if (t.getDest() == s && !(phase.contains(t.getSrc()))) {
						phase.add(t.getSrc());
						if (t.isPairwiseTransition() && !t.isEnvironmentTransition()) { // If pairwise, add a source and
																						// destination states of this
							// action...
							for (LocalTransition pwTrans : semantics.getTransitions()) {
								if (t == pwTrans || !pwTrans.isPairwiseTransition()) {
									continue;
								}
								if (pwTrans.getLabel().equals(t.getLabel())) {
									phase.add(pwTrans.getSrc());
									phase.add(pwTrans.getDest());
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

	public HashSet<HashSet<LocalState>> getPhases() {
		return phases;
	}

	public HashMap<LocalState, HashSet<HashSet<LocalState>>> getPhaseMap() {
		return phaseMap;
	}

}
