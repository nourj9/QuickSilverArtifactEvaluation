package trace;

import java.util.ArrayList;
import java.util.HashSet;

import Verifier.GlobalState;
import Verifier.GlobalTransition;

public class Trace {
	private ArrayList<GlobalTransition> transitions;
	private HashSet<GlobalState> states;

	public Trace() {
		this.transitions = new ArrayList<GlobalTransition>();
		this.states = new HashSet<GlobalState>();
	}

	@SuppressWarnings("unchecked")
	public Trace(Trace trace) {
		transitions = (ArrayList<GlobalTransition>) trace.transitions.clone();
		states = (HashSet<GlobalState>) trace.states.clone();
	}

	public boolean addTransition(GlobalTransition tr) {

		if (tr.isSelfLoop()) {
			return false;
		}

		if (states.contains(tr.getTo())) {
			return false;
		}

		transitions.add(tr);
		states.add(tr.getFrom());
		states.add(tr.getTo());

		return true;

	}

	public GlobalState getLastState() {

		if (transitions.isEmpty())
			return null;

		return transitions.get(transitions.size() - 1).getTo();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("============================================\n");
		sb.append("Trace from:\n");
		sb.append(transitions.get(0).getFrom());
		sb.append("to:\n");
		sb.append(getLastState());
		sb.append("Goes as follows:\n\n");
		sb.append(getFirstState());
		for (GlobalTransition trans : transitions) {
			sb.append("On ");
			sb.append(trans.getPrettyLabelForAction());
			sb.append("\nGoes To >>>>>>>> \n");
			sb.append(trans.getTo());
		}
		sb.append("============================================\n");
		return sb.toString();
	}

	private GlobalState getFirstState() {
		return transitions.get(0).getFrom();
	}

}
