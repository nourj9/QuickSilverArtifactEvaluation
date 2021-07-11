package trace;

import java.util.ArrayList;
import Verifier.GlobalState;
import Verifier.GlobalTransition;
import Verifier.ModelChecker;

public class TraceFinder {
	private ModelChecker mc;

	public TraceFinder(ModelChecker mc) {
		this.mc = mc;
	}

	// TODO wasteful?
	public Trace findTrace(GlobalState from, GlobalState to) {

		ArrayList<Trace> TracesInProgress = new ArrayList<Trace>();

		for (GlobalTransition tr : from.getOutgoingTransitions()) {
			Trace trace = new Trace();
			if (trace.addTransition(tr)) {
				TracesInProgress.add(trace);
			}
		}

		while (!TracesInProgress.isEmpty()) {
			Trace currentTrace = TracesInProgress.remove(0);

			if (currentTrace.getLastState().equals(to)) {
				return currentTrace;
			} else {
				for (GlobalTransition tr : currentTrace.getLastState().getOutgoingTransitions()) {

					Trace trace = new Trace(currentTrace);
					if (trace.addTransition(tr)) {
						TracesInProgress.add(trace);
					}
				}
			}
		}
		return null;
	}

}
