package liveness.verifier;

import java.util.HashSet;

import Verifier.ModelChecker;
import lang.specs.LivenessSpecification;

public class ProductStructure {
	private ModelChecker mc;
	private LivenessSpecification livespec;
	private HashSet<ProductState> states;
	private HashSet<ProductTransition> transitions;
	private ProductState initialState;

	public ProductStructure(ModelChecker mc, LivenessSpecification livespec) {
		this.mc = mc;
		this.livespec = livespec;
		this.states = new HashSet<ProductState>();
		this.transitions = new HashSet<ProductTransition>();

		constructProduct();
	}

	// do the cross-product of the monitor and the GTS
	private void constructProduct() {

		// TODO determine initial state

		// TODO BFS over the rest.

	}

	public ModelChecker getMc() {
		return mc;
	}

	public LivenessSpecification getLivespec() {
		return livespec;
	}

	public HashSet<ProductState> getStates() {
		return states;
	}

	public ProductState getInitialState() {
		return initialState;
	}

	public HashSet<ProductTransition> getTrans() {
		return transitions;
	}

	public void addState(ProductState state) {
		states.add(state);
	}

	public void addTransition(ProductTransition trans) {
		transitions.add(trans);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	

}
