package Core;

import java.util.ArrayList;

import Core.Options.PhCompFeedback;

public class Options {
	public static enum PhCompFeedback {
		All, OnePerAction, OnePerCondition, CounterExampleOnly
	}

	public static enum Verifier {
		Kinara
	}

	public PhCompFeedback phCompFeedbackLevel = PhCompFeedback.All;
	public boolean printPhases = false;
	public boolean fancyPrints = false;
	public boolean detailedStatesAndTransitions = false;
	public boolean printSemantics = false;
	public boolean useExclusiveRegions = true;
	public Verifier verifier;
	public boolean phComp_earlyTerminationForWellBehavedness = false;
	public boolean cutoffs_earlyTerminationForCutoffs = false;
	public boolean cutoffs_debugInfoForCutoffs;
	public boolean cutoffs_cutoffThreadDebugInfo;
	public boolean cutoffs_useThreadsForCutoffs;
	public boolean pathFinder_pathFinderThreadDebugInfo;
	public boolean pathFinder_useThreadsForPathFinder;
	public boolean cutoffs_useShortestPathInLemma2CE;
	public boolean summerizer_removeSetsAndIDsInSummarizer;
	public boolean cutoffs_printWhichLemmaIsUsed;
	public boolean cutoffs_trimStatesInCutoffs;
	public boolean trimStatesFromTheBeginning;
	public boolean useGeneralizedPhaseCompConstraints;
	public boolean synthesisMainDebugInfo;
	public boolean includeProcessFailureModeling;
	public boolean summerizer_removeNegotationStepResultingFromBAC;
	public boolean phComp_enableFirabilityAwareness;
	public boolean includeProcessFailureModeling_inSemGen;
	public boolean codeGen_generateDebugInfoInKinara;
	public boolean cutoffs_detailedDubgInfoForCutoffs;
	public boolean phComp_useCrashTransitionsInInternalPaths;
	public boolean summerizer_removeNegotationStepInGeneral;
	public boolean cutoffs_removeCrashStateAndTransitions;
	public boolean phComp_displayFirabilityPrintOuts;
	public boolean main_verbose;
	public String kinaraDestFolder;
	
	public boolean mainParams_runAllBMs;
	public boolean mainParams_runWithoutFirabilityAwareness;
	public boolean mainParams_runBM;
	public String mainParam_bmName;
	public boolean mainParams_runBMWithForcedCutoff;
	public Integer mainParam_cutoff;

	@Override
	public String toString() {
		return "Options, needs implementing";
	}

	public void parseOptions(String[] args) {
		// LATER parse it from the args.
		phCompFeedbackLevel = PhCompFeedback.All;
		printPhases = false;
		fancyPrints = false;
		detailedStatesAndTransitions = false;
		printSemantics = false;
		useExclusiveRegions = true;
		verifier = Verifier.Kinara;

		phComp_earlyTerminationForWellBehavedness = false; // well-behavedness check will terminate upon the first error
		phComp_enableFirabilityAwareness = true;

		cutoffs_earlyTerminationForCutoffs = true; // same for cutoffs
		cutoffs_debugInfoForCutoffs = false;
		cutoffs_cutoffThreadDebugInfo = false;
		cutoffs_useThreadsForCutoffs = true;
		cutoffs_trimStatesInCutoffs = true;
		cutoffs_useShortestPathInLemma2CE = true;
		cutoffs_printWhichLemmaIsUsed = true;
		cutoffs_detailedDubgInfoForCutoffs = false;
		pathFinder_pathFinderThreadDebugInfo = true;
		pathFinder_useThreadsForPathFinder = true;

		summerizer_removeSetsAndIDsInSummarizer = true;
		summerizer_removeNegotationStepResultingFromBAC = true;
		summerizer_removeNegotationStepInGeneral = true;
		trimStatesFromTheBeginning = true;
		useGeneralizedPhaseCompConstraints = true;
		synthesisMainDebugInfo = false;
		includeProcessFailureModeling = true;
		includeProcessFailureModeling_inSemGen = false; // do not turn this on if includeProcessFailureModeling is on

		codeGen_generateDebugInfoInKinara = false;
		phComp_useCrashTransitionsInInternalPaths = true;
		cutoffs_removeCrashStateAndTransitions = true;
		phComp_displayFirabilityPrintOuts = false;

		main_verbose = false;

		// add the command line options for eval
		parseCommandLineOptions(args);

	}

	private void parseCommandLineOptions(String[] args) {

		kinaraDestFolder = "/home/ae/kinara/test/mc/benchmarks/";

		phCompFeedbackLevel = PhCompFeedback.OnePerAction;

		// printouts
		codeGen_generateDebugInfoInKinara = false;
		cutoffs_debugInfoForCutoffs = false;
		cutoffs_detailedDubgInfoForCutoffs = false;
		cutoffs_printWhichLemmaIsUsed = false;
		main_verbose = false;

		// extension
		phComp_enableFirabilityAwareness = false;

		if (args.length == 1) {
			if (args[0].equals("runAllBMs")) {
				mainParams_runAllBMs = true;
			} else if (args[0].equals("runWithoutFirabilityAwareness")) {
				mainParams_runWithoutFirabilityAwareness = true;
			} else {
				complainAndExit();
			}
		} else if (args.length == 2) {
			if (args[0].equals("runBM")) {
				mainParams_runBM = true;
				mainParam_bmName = args[1];
			} else {
				complainAndExit();
			}
		} else if (args.length == 3) {
			if (args[0].equals("runBM")) {
				mainParams_runBMWithForcedCutoff = true;
				mainParam_bmName = args[1];
				mainParam_cutoff = Integer.valueOf(args[2]);
			} else {
				complainAndExit();
			}
		} else {
			complainAndExit();
		}

	}

	private void complainAndExit() {
		System.out.println("invalid arguments!");
		System.out.println("accepted options: ");
		System.out.println(" runAllBMs");
		System.out.println(" runWithoutFirabilityAwareness");
		System.out.println(" runBM <bmName> ");
		System.out.println(" runBM <bmName> <cutoff>");
		System.exit(-1);
	}

}
