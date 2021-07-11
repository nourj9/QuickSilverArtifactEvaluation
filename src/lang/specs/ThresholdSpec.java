package lang.specs;

import java.awt.geom.FlatteningPathIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

import lang.core.Location;
import lang.core.Protocol;
import lang.expr.ExprVar;
import lang.expr.Gatherer;
import semantics.core.LocalState;

public abstract class ThresholdSpec extends SafetySpecification {
	protected int threshold;
	protected StateDescList sdlist;
	protected HashSet<LocalState> falttenedLocalStates;

	public ThresholdSpec(int threshold, StateDescList sdlist, Protocol p) {
		this.p = p;
		this.threshold = threshold;
		this.sdlist = sdlist;
	}

	protected ThresholdSpec() {
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public StateDescList getSdlist() {
		return sdlist;
	}

	public void setSdlist(StateDescList sdlist) {
		this.sdlist = sdlist;
	}

//	@Override
//	public void setProtocol(Protocol p) {
//		this.p = p;
//	}

	@Override
	public void findReplace(Location toRep, ArrayList<Location> replacements) {

		// for (StateDesc sd : sdlist.getList()) {
		int size = sdlist.getList().size();
		for (int i = 0; i < size; i++) {
			StateDesc sd = sdlist.getList().get(i);
			if (sd.getLoc(p).equals(toRep)) {
				for (Location reploc : replacements) {
					sdlist.add(new StateDesc(reploc.getName(), sd.getPredicate()));
				}
			}
		}
	}

	@Override
	public void complainIfLocIsUsed(HashSet<Location> locs) {
		sdlist.getList().removeIf(new Predicate<StateDesc>() {

			@Override
			public boolean test(StateDesc sd) {
				if (locs.contains(sd.getLoc(p))) {
					System.err.println("Error: location being deleted is mentioned in specs: " + this);
					System.exit(-1);
				}
				return false;
			}
		});
	}

	@Override
	public void complainIfVarIsUsed(ExprVar var) {
		sdlist.getList().removeIf(new Predicate<StateDesc>() {

			@Override
			public boolean test(StateDesc sd) {

//				if (Gatherer.FindExprVars(sd.getPredicate()).contains(var)) {
				if (new Gatherer<ExprVar>(ExprVar.class).find(sd.getPredicate()).contains(var)) {

					System.err.println("Error: variable being deleted is mentioned in specs: " + this);
					System.exit(-1);
				}
				return false;
			}
		});
	}

	@Override
	public HashSet<Integer> getThresholds() {
		HashSet<Integer> toRet = new HashSet<Integer>();
		toRet.add(threshold);
		return toRet;
	}

	@Override
	public HashSet<ExprVar> getAllVarsInPreds() {
		HashSet<ExprVar> toRet = new HashSet<ExprVar>();
		for (StateDesc sd : sdlist.getList()) {
			// toRet.addAll(Gatherer.FindExprVars(sd.getPredicate()));
			toRet.addAll(new Gatherer<ExprVar>(ExprVar.class).find(sd.getPredicate()));
		}

		return toRet;
	}

	@Override
	public HashSet<LocalState> extractLocalStates() {

		// if flattened before, return it
		if (falttenedLocalStates != null) {
			return falttenedLocalStates;
		}

		HashSet<LocalState> toRet = new HashSet<LocalState>();
		for (StateDesc stateDesc : sdlist.getList()) {
			for (LocalState state : p.getSemantics().getStates()) {
				if (stateDesc.getLoc(p).equals(state.getLoc())
						&& (Boolean) stateDesc.getPredicate().eval(state.getSigma())) {
					// same loc and pred evals to true in this state.
					toRet.add(state);
				}
			}
		}

		// save it for later calls.
		if (falttenedLocalStates == null) {
			falttenedLocalStates = toRet;
		}
		return toRet;
	}

}
