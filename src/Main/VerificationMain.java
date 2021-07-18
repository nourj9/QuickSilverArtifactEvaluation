package Main;

import Core.Options;

public class VerificationMain {

	public static void main(String[] args) throws Exception {

		System.out.println("Running ...");
		Options options = new Options();
		options.parseOptions(args);

		Utils.cleanLogs();
		
		// options.phCompFeedbackLevel = PhCompFeedback.All;


		options.run_repeats = 3;
		options.run_confidenceLevel95 = true;

		if (options.mainParams_runAllBMs) {
			Utils.runAllBMs(true /* use Firability Awareness */, options);
		} else if (options.mainParams_runWithoutFirabilityAwareness) {
			Utils.runAllBMs(false /* don't use Firability Awareness */, options);
		} else if (options.mainParams_runBM) {
			Utils.runBM(options.mainParam_bmName, options);
		} else if (options.mainParams_runBMWithForcedCutoff) {
			Utils.runBM(options.mainParam_bmName, options.mainParam_cutoff, options);
		}

		System.out.println("Done.");

	}

}
