package lang.specs;

import lang.core.Protocol;

public class AtMostSpec extends ThresholdSpec {

	public AtMostSpec(int threshold, StateDesc sd, Protocol p) {
		this.threshold = threshold;
		StateDescList sdlist = new StateDescList();
		sdlist.add(sd);
		this.sdlist = sdlist;
		this.p = p;
	}

	public AtMostSpec(int threshold, StateDescList sdlist, Protocol p) {
		super(threshold, sdlist, p);
	}

	@Override
	public String toString() {
		return "atmost (" + threshold + " , {" + sdlist + "})";
	}

	@Override
	public boolean containsDisjuncts() {
		return false;
	}

	public void setSdlist(StateDesc sd) {
		StateDescList sdlist = new StateDescList();
		sdlist.add(sd);
		this.sdlist = sdlist;
	}

	@Override
	public SafetySpecification smartClone() {
		return new AtMostSpec(threshold, sdlist.smartClone(), p);
	}

}
