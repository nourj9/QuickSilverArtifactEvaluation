package liveness.verifier;

import Verifier.GlobalState;
import liveness.buchi.BuchiState;

public class ProductState {
	private GlobalState globalState;
	private BuchiState buchiState;

	public ProductState(GlobalState globalState, BuchiState buchiState) {
		this.globalState = globalState;
		this.buchiState = buchiState;
	}

	public GlobalState getGlobalState() {
		return globalState;
	}

	public BuchiState getBuchiState() {
		return buchiState;
	}

	public boolean isAccepting() {
		return buchiState.isAccepting();
	}

	// TODO
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
