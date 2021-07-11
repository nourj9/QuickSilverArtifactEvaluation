package lang.specs;

import liveness.buchi.BuchiAutomaton;

public abstract class LivenessSpecification extends Specification {
	protected BuchiAutomaton buchiAutomation;

	public abstract BuchiAutomaton getBuchiAutomaton();
}
