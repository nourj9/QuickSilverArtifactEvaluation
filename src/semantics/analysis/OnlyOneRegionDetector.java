package semantics.analysis;

import java.util.HashSet;

import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;

public class OnlyOneRegionDetector {
	ProcessSemantics ps;
	PhaseAnalysis pa;

	public OnlyOneRegionDetector(ProcessSemantics ps, PhaseAnalysis pa) {
		this.ps = ps;
		this.pa = pa;
	}

	/**
	 * initialStates: the set of initial states in the region
	 * returns: an expanded exclusive region with single entry, or null
	 * */
	public HashSet<LocalState> buildExclusiveRegion(HashSet<LocalState> initialStates) {

		HashSet<LocalState> expandedExclusiveRegion = expandRegion(initialStates);

		if (expandedExclusiveRegion == null) {
			return null;
		}

		if (!onlyEnteredBySingleProcess(expandedExclusiveRegion)) {
			return null;
		}

		return expandedExclusiveRegion;
	}

	/**
	 * initialStates: the set of initial states in the region
	 * returns: an expanded exclusive region with single entry, or null
	 * */
	public HashSet<LocalState> buildMaximalExclusiveRegion(HashSet<LocalState> initialStates) {
		HashSet<LocalState> initialExclusiveRegion = buildExclusiveRegion(initialStates);
		if(initialExclusiveRegion == null) {
			return null;
		}
		
		return maximizeExclusiveRegionWRT(initialExclusiveRegion, ps.getStates());
	}

	/**
	 * Takes two sets, 
	 * initialRegion: 
	 * candidateStates: 
	 * initialStates: the set of initial states in the region
	 * returns: an expanded exclusive region with single entry, or null
	 * */
	public HashSet<LocalState> maximizeExclusiveRegionWRT(HashSet<LocalState> initialExclusiveRegion,
			HashSet<LocalState> candidateStates) {

		if (!isExclusiveRegion(initialExclusiveRegion)) {
			System.err.println(
					"maximizeExclusiveRegionWRT must be called with an exclusive region as the first parameter.");
			Thread.dumpStack();
		}

		// Loop through all the candidate states, and if any of them while keeping the
		// region exclusive, add them.
		HashSet<LocalState> runningSet = new HashSet<LocalState>(initialExclusiveRegion);
		for (LocalState candidateState : candidateStates) {
			HashSet<LocalState> candidateSet = new HashSet<LocalState>(runningSet);
			candidateSet.add(candidateState);

			if (isExclusiveRegion(candidateSet)) {
				runningSet = candidateSet;
			}
		}
		return runningSet;
	}

	// Returns true if the candidate region can only hold at most one process at a
	// time.
	private boolean isExclusiveRegion(HashSet<LocalState> candidateExclusiveRegion) {

		if (candidateExclusiveRegion == null) {
			return false;
		}

		// if the initial state of the process is included,
		// the region is not exclusive, so return false
		if (candidateExclusiveRegion.contains(ps.getInitialState())) {
			return false;
		}

		if (!onlyEnteredBySingleProcess(candidateExclusiveRegion)) {
			return false;
		}

		for (LocalTransition t : ps.getTransitions()) {

			// if the transition is not enabled, keep going
			if (isDisabledByRegion(t, candidateExclusiveRegion)) {
				continue;
			}

			// if the transition is enabled, and comes into the region from outside, the
			// region is not exclusive.
			if (!candidateExclusiveRegion.contains(t.getSrc()) && candidateExclusiveRegion.contains(t.getDest())) {
				return false;
			}
		}

		// No enabled transitions can come in from outside, so the region is exclusive.
		return true;
	}

	/**4. For each transition t': start -t'-> end where start $\not \in$ c end $\in$ c
	1. If t' is not a broadcast send or partition win with k = 1, return false/null/failure
	*/
	private boolean onlyEnteredBySingleProcess(HashSet<LocalState> expandedExclusiveRegion) {

		for (LocalTransition t : ps.getTransitions()) {

			if (!expandedExclusiveRegion.contains(t.getSrc()) && //
					expandedExclusiveRegion.contains(t.getDest())) {

				if (t.isSingleTransition()) {
					continue;
				}

				return false;
			}
		}

		return true;
	}

	/**
	 * 3. For each transition t: start -t'-> end where start $\not \in$ *region* and end $\in$ *region*
	1. If the intersection of *region* with phase(start) is not empty (possible for *region* to be occupied
	 without disabling t), add start to *region* (*region* := *region* $\cup$ start) and go to step 2*/
	private HashSet<LocalState> expandRegion(HashSet<LocalState> initialStates) {

		// if the initial state of the process is included, just return null
		LocalState initial = ps.getInitialState();
		if (initialStates.contains(initial)) {
			return null;
		}

		HashSet<LocalState> region = new HashSet<LocalState>(initialStates);
		boolean changeMade;
		do {
			changeMade = false;
			for (LocalTransition t : ps.getTransitions()) {

				// if the transition is not enabled, keep going
				if (isDisabledByRegion(t, region)) {
					continue;
				}

				LocalState src = t.getSrc();

				if (!region.contains(src) && region.contains(t.getDest())) {

					if (src.equals(initial)) {
						return null;
					} else {
						region.add(src);
						changeMade = true;
					}
				}
			}
		} while (changeMade);

		return region;
	}

	// the transition cannot fire if there is a process in this region
	private boolean isDisabledByRegion(LocalTransition t, HashSet<LocalState> region) {

		LocalState src = t.getSrc();

		HashSet<LocalState> guard;
		if (t.isGloballySynchronizing()) {
			guard = ps.getSourceSet(t.getLabel());
		} else {
			HashSet<Phase> allPhasesOfState = pa.getPhaseMap().get(src);
			guard = getMergedPhase(allPhasesOfState);
		}

		// check intersection
		for (LocalState localState : guard) {
			if (region.contains(localState)) {
				return false; // because the region and the guard are not completely disjoined, you
				// can be in the region and have this transition enabled.
			}
		}

		return true;

	}

	// get the merged phase
	private HashSet<LocalState> getMergedPhase(HashSet<Phase> allPhasesOfState) {
		HashSet<LocalState> merged = new HashSet<LocalState>();
		for (Phase phase : allPhasesOfState) {
			merged.addAll(phase.getStates());
		}
		return merged;
	}

}
