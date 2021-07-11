package lang.specs;

import liveness.buchi.BuchiAutomaton;
import liveness.buchi.BuchiState;
import liveness.buchi.LivenessConstTrue;
import liveness.buchi.LivenessExprOp;

public class ReactiveSpec extends LivenessSpecification {
	private LivenessPred premise;
	private LivenessPred conclusion;

	public ReactiveSpec(LivenessPred premise, LivenessPred conclusion) {
		this.premise = premise;
		this.conclusion = conclusion;
	}

	@Override
	public String toString() {
		return "AG " + premise + " implies F " + conclusion;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof ReactiveSpec) {
			ReactiveSpec other = (ReactiveSpec) obj;
			return this.premise.equals(other.premise) //
					&& this.conclusion.equals(other.conclusion);
		}
		return false;

	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public BuchiAutomaton getBuchiAutomaton() {

		if (buchiAutomation == null) {

			buchiAutomation = new BuchiAutomaton();

			BuchiState initSt = new BuchiState("Init", true, false, false);
			BuchiState accepSt = new BuchiState("Accepting", false, true, false);
			BuchiState finalSt = new BuchiState("Final", false, false, true);

			buchiAutomation.addState(initSt);
			buchiAutomation.addState(accepSt);
			buchiAutomation.addState(finalSt);

			buchiAutomation.addTransition(initSt, initSt, new LivenessConstTrue());
			buchiAutomation.addTransition(initSt, accepSt, premise);
			buchiAutomation.addTransition(accepSt, accepSt, new LivenessExprOp(LivenessExprOp.Op.NOT, conclusion));
			buchiAutomation.addTransition(accepSt, finalSt, conclusion);
			buchiAutomation.addTransition(finalSt, finalSt, new LivenessConstTrue());

		}

		return buchiAutomation;

	}
}
