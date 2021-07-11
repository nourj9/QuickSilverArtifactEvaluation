package lang.specs;

import liveness.buchi.BuchiAutomaton;
import liveness.buchi.BuchiState;
import liveness.buchi.LivenessConstTrue;
import liveness.buchi.LivenessExprOp;

public class EventuallySpec extends LivenessSpecification {
	private LivenessPred pred;

	public EventuallySpec(LivenessPred pred) {
		this.pred = pred;
	}

	@Override
	public String toString() {
		return "AF " + pred;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}


		if (obj instanceof EventuallySpec) {
			EventuallySpec other = (EventuallySpec) obj;
			return this.pred.equals(other.pred);
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

			BuchiState initAceppSt = new BuchiState("InitAccepting", true, true, false);
			BuchiState finalSt = new BuchiState("Final", false, false, true);

			buchiAutomation.addState(initAceppSt);
			buchiAutomation.addState(finalSt);

			buchiAutomation.addTransition(initAceppSt, initAceppSt, new LivenessExprOp(LivenessExprOp.Op.NOT, pred));
			buchiAutomation.addTransition(initAceppSt, finalSt, pred);
			buchiAutomation.addTransition(finalSt, finalSt, new LivenessConstTrue());

		}

		return buchiAutomation;
	}

}
