package liveness.verifier;

import java.util.ArrayList;

import Verifier.ModelChecker;
import lang.specs.LivenessSpecification;

public class LivenessVerifier {

	private ModelChecker mc;
	private ArrayList<LivenessSpecification> livenessProps;

	public LivenessVerifier(ModelChecker mc, ArrayList<LivenessSpecification> livenessProps) {
		this.mc = mc;
		this.livenessProps = livenessProps;
	}

	// main function
	public boolean checkLiveness() {

		for (LivenessSpecification livespec : livenessProps) {
			if (!checkLiveness(mc, livespec)) {
				return false;
			}
		}

		return true;
	}

	// per property
	private boolean checkLiveness(ModelChecker mc, LivenessSpecification livespec) {
		// build product structure
		ProductStructure product = new ProductStructure(mc, livespec);
		
		// TODO find accepting SCCs
		
		// TODO check fairness

		// TODO if fair return trace as stem and loop

		return true;
	}
}
