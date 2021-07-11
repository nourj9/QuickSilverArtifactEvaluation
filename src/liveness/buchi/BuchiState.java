package liveness.buchi;

import java.util.ArrayList;

public class BuchiState {

	private String name;
	private boolean isInitial;
	private boolean isAccepting;
	private boolean isFinal;
	private ArrayList<BuchiTransition> outgoingTransitions = new ArrayList<BuchiTransition>();
	private ArrayList<BuchiTransition> incomingTransitions = new ArrayList<BuchiTransition>();

	public BuchiState(String name, boolean isInitial, boolean isAccepting, boolean isFinal) {
		this.name = name;
		this.isInitial = isInitial;
		this.isAccepting = isAccepting;
		this.isFinal = isFinal;
	}

	public String getName() {
		return name;
	}

	public boolean isInitial() {
		return isInitial;
	}

	public boolean isAccepting() {
		return isAccepting;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public ArrayList<BuchiTransition> getOutgoingTransitions() {
		return outgoingTransitions;
	}

	public ArrayList<BuchiTransition> getIncomingTransitions() {
		return incomingTransitions;
	}

	@Override
	public String toString() {
		return name;
	}

	public void addOutgoingTrans(BuchiTransition trans) {
		outgoingTransitions.add(trans);
	}

	public void addIncomingTrans(BuchiTransition trans) {
		incomingTransitions.add(trans);
	}

}
