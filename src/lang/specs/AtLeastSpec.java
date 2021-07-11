package lang.specs;

import lang.core.Protocol;

public class AtLeastSpec extends ThresholdSpec {
	public AtLeastSpec(int threshold, StateDescList sdlist, Protocol p) {
		super(threshold, sdlist, p);
	}

	@Override
	public String toString() {
		return "atmost lol (" + threshold + " , {" + sdlist + "})";
	}

	@Override
	public boolean containsDisjuncts() {
		return false;
	}

	@Override
	public SafetySpecification smartClone() {
		return null;
		// return new AtLeastSpec(threshold, sd.smartClone());
	}
//	@Override
//	public Specification clone() {
//		return null;
//	}

//	@Override
//	public String getKinaraCode() {
//		return "getKinaraCode is not implemented for AtLeastSpec";
//	}
}