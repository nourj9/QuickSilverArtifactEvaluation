package semantics.analysis;

import java.util.ArrayList;
import java.util.HashSet;

import lang.events.EventPartitionCons;
import lang.expr.ExprConstant;
import lang.expr.ExprOp;
import lang.expr.Expression;
import lang.handler.HandlerPartitionCons;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;

public class SplitAnalysis {
	private LocalState initState;
	private HashSet<LocalState> targetStates;
	private ProcessSemantics semantics;
	private HashSet<LocalState> fixedActingRegoin;
	private HashSet<LocalState> parametericReactingRegoin;
	private LocalTransition acting, reacting;
	private int neededCardinality;

	public SplitAnalysis(LocalState initState, HashSet<LocalState> targetStates, ProcessSemantics semantics) {
		this.initState = initState;
		this.targetStates = targetStates;
		this.semantics = semantics;
	}

	public boolean targetStatesAreInAReceiveOnlyParametricZone() {

		// Does the initial state has a forking receive?
		// Return true if tr is the reacting half of a global
		// transition that splits the processes off into two
		// regions from the initial state, otherwise, return false.

		// has two transitions
		if (initState.getOutgoingTransitions().size() != 2) { // can be made more general using logical init states.
			return false;
		}

		LocalTransition tr1 = initState.getOutgoingTransitions().get(0);
		LocalTransition tr2 = initState.getOutgoingTransitions().get(1);

		// same label
		if (!tr1.getLabel().equals(tr2.getLabel())) {
			return false;
		}

		// globally sync
		if (!tr1.isGloballySynchronizing()) {
			return false;
		}

		// one is acting and one is reacting?
		if (tr1.isActingGlobalTransition() && tr2.isReactingGlobalTransition()) {
			acting = tr1;
			reacting = tr2;
		} else if (tr2.isActingGlobalTransition() && tr1.isReactingGlobalTransition()) {
			acting = tr2;
			reacting = tr1;
		} else {
			return false;
		}

		// at this point, we have both transitions, and can build the regions
		fixedActingRegoin = getReachableRegion(acting.getDest());
		parametericReactingRegoin = getReachableRegion(reacting.getDest());

		if (!hasOnlyGloballySyncReactingTrs(parametericReactingRegoin)) {
			return false;
		}

		// the parametricReactingRegoin has the target states.
		for (LocalState localState : targetStates) {
			if (!parametericReactingRegoin.contains(localState)) {
				return false;
			}
		}

		// get the needed cardinality
		neededCardinality = -1;
		if (semantics.isBroadcastLabel(acting.getLabel())) {
			neededCardinality = 1;
		} else if (semantics.isPartitionConsLabel(acting.getLabel())) {
			EventPartitionCons event = ((HandlerPartitionCons) acting.getHandler()).getEvent();
			Expression card = event.getCardinality();
			neededCardinality = (int) card.eval(null);

		}

		return true;
	}

	private boolean hasOnlyGloballySyncReactingTrs(HashSet<LocalState> regoin) {
		for (LocalState s : regoin) {
			for (LocalTransition outgoing : s.getOutgoingTransitions()) { // check incoming too?

				if (outgoing.isReset()) {
					continue;
				}

				if (!outgoing.isReactingGlobalTransition()) {
					return false;
				}
			}
		}
		return true;
	}

	// COPIED FROM CUTOFF UTILS
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

	public int getNeededCardinality() {
		return neededCardinality;
	}

	public LocalState getInitState() {
		return initState;
	}

	public HashSet<LocalState> getTargetStates() {
		return targetStates;
	}

	public ProcessSemantics getSemantics() {
		return semantics;
	}

	public HashSet<LocalState> getFixedActingRegoin() {
		return fixedActingRegoin;
	}

	public HashSet<LocalState> getParametericReactingRegoin() {
		return parametericReactingRegoin;
	}

	public LocalTransition getActing() {
		return acting;
	}

	public LocalTransition getReacting() {
		return reacting;
	}

}
