package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import Core.KinaraCodeGenerator;
import Core.Options;
import Core.PreProcessing;
import Core.RunInfo;
import antlr.parsing.MyParser;
import cutoffs.Cutoffs;
import lang.core.Protocol;
import semantics.analysis.StructureSummarizer;
import semantics.core.PhaseCompatibilityChecker;
import semantics.core.ProcessSemantics;
import semantics.core.SemanticsGenerator;
import utils.ReadStream;

public class Utils {
	public static RunInfo runBM(String prefix, String bmName, Options options) throws Exception {
		RunInfo info = new RunInfo();

		print("\nbenchmark: " + bmName);
		String[] parts = bmName.split("/");
		info.benchmarkName = parts[parts.length - 1];
		long before = System.currentTimeMillis();

		CharStream input = null;
		try {
			input = CharStreams.fromFileName(prefix + "/" + bmName + ".ch");
		} catch (NoSuchFileException e) {
			System.err.println("The benchmark file " + prefix + "/" + bmName + ".ch does not exist.");
			System.exit(-1);
		}

		// temp for eval
		String[] lines = input.toString().split("\r\n|\r|\n");
		info.LoC = lines.length;

		Protocol p = MyParser.parse(input);
		long after = System.currentTimeMillis();
		info.parseTime = after - before;

		info.numOfAMLHandlers = p.getTotalNumOfHandlers();

		// print("crashes", p.getCrashs());

		if (options.main_verbose) {
			print("before preprocessing", p);
		}
		before = System.currentTimeMillis();
		new PreProcessing(p, options).preprocess();
		after = System.currentTimeMillis();
		info.preProcessingTime = after - before;

		if (options.main_verbose) {
			print("after preprocessing", p);
		}

		info.numOfCoreHandlers = p.getTotalNumOfHandlers();

		// Edit: we will start as follows: we will detect BAC, and if it exists, we will
		// add the needed transition to the crash location, then generate code, then
		// remove it, remove bac and proceed normally.

		long semanticsGenerationTime = 0;

		before = System.currentTimeMillis();
		StructureSummarizer summarizer = new StructureSummarizer(false,
				options.summerizer_removeNegotationStepResultingFromBAC, p, options);

		summarizer.detectBACs();
		boolean hasBACs = summarizer.hasBACs();
		after = System.currentTimeMillis();
		semanticsGenerationTime += (after - before);

		if (hasBACs) {

			// 1. remove the "self-loop" reply on the broadcast receive
			// 2. inject the negative reply on the crash state
			before = System.currentTimeMillis();
			summarizer.enableNegativeReplyFromCrashLocs();
			after = System.currentTimeMillis();
			semanticsGenerationTime += (after - before);

			// 3. generate code
			before = System.currentTimeMillis();
			new KinaraCodeGenerator(options).generateCode(p);
			after = System.currentTimeMillis();
			info.codeGenerationTime = after - before;

			// 4. delete BAC and the crash reply mechanism
			before = System.currentTimeMillis();
			summarizer.removecrashReplyLogicAndRestoreSelfLoopOnCrash();
			summarizer.removeBACs();
			summarizer.postBACleanUp();
			after = System.currentTimeMillis();
			semanticsGenerationTime += (after - before);

		} else {

			// just generate code
			before = System.currentTimeMillis();
			new KinaraCodeGenerator(options).generateCode(p);
			after = System.currentTimeMillis();
			info.codeGenerationTime = after - before;

		}

		before = System.currentTimeMillis();
		SemanticsGenerator sg = new SemanticsGenerator(p, summarizer, options);
		ProcessSemantics ps = sg.createSemantics();
		after = System.currentTimeMillis();
		semanticsGenerationTime += (after - before);
		info.procSemGenTime = semanticsGenerationTime;

		before = System.currentTimeMillis();
		PhaseCompatibilityChecker wb = new PhaseCompatibilityChecker(ps, options);
		after = System.currentTimeMillis();
		boolean phaseCompatible = wb.check();
		info.wellBehavCheckTime = after - before;

		info.isPhaseCompatible = phaseCompatible;

		if (options.main_verbose) {
			print("semantics", ps.prettyString());
			wb.dumpPhases();
		}
		System.out.println(" phase-compatible? " + phaseCompatible);

		if (phaseCompatible) {

			before = System.currentTimeMillis();
			Cutoffs cutoffs = new Cutoffs(p, ps, p.getSafetySpecs(), options);
			int c = cutoffs.computeCutoff();
			after = System.currentTimeMillis();
			info.cutoffCheckTime = after - before;
			info.cutoff = c;
			if (c != Integer.MAX_VALUE) {
				System.out.println(" cutoff value: " + c);
			} else {

				System.out.println("Couldn't compute cutoffs..");

				if (options.main_verbose) {
					System.out.println(cutoffs.getSemantics().prettyString());
				}
				cutoffs.printErrors(options);
			}

		} else {
			wb.printErrorsAndSuggestions(options);
		}

		info.numOfPhases = ps.getPhaseAnalysis().getPhases().size();
		info.computeTotalTime();

		return info;

	}

	public static void print(String msg) {
		System.out.println(msg);
	}

	public static void print(String msg, Object obj) {
		System.out.println("======== " + msg + " =============================================================");
		System.out.println(obj);
	}

	public static void printTableLike(HashMap<String, ArrayList<RunInfo>> data, ArrayList<String> benchmarks) {
		System.out.println();

		StringBuilder sb = new StringBuilder();

		sb.append("Benchmark                        LoC\tPhases\tCutoff\tTime(s)\n");
		sb.append("------------------------------------------------------------------\n");

		// table-like
		for (String bm : benchmarks) {
			RunInfo info = data.get(bm).get(0);

			String name = info.benchmarkName;
			sb.append(name);

			int spaces = 33 - name.length();
			for (int i = 0; i < spaces; i++) {
				sb.append(" ");
			}

			sb.append(info.LoC + "\t");
			sb.append(info.numOfPhases + "\t");
			sb.append(info.cutoff + "\t");
			sb.append(info.totalTime / 1000.0); // so it's in seconds
			sb.append("\n");
		}
		System.out.println(sb.toString());

	}

	public static void printCutoffs(HashMap<String, ArrayList<RunInfo>> data) {
		// cutoff
		System.out.println("\nCutoffs:");
		for (Entry<String, ArrayList<RunInfo>> entry : data.entrySet()) {
			System.out.println(entry.getValue().get(0).benchmarkName + " " + entry.getValue().get(0).cutoff);
		}

	}

	public static void printCheckTime(HashMap<String, ArrayList<RunInfo>> data) {
		// check time
		System.out.println("\nCheck Time:");
		for (Entry<String, ArrayList<RunInfo>> entry : data.entrySet()) {
			System.out.print(entry.getValue().get(0).benchmarkName + " ");
			int sum = 0;
			for (RunInfo info : entry.getValue()) {
				sum += info.totalTime;
			}
			System.out.println((double) sum / entry.getValue().size());
		}

	}

	public static void printAllInfo(HashMap<String, ArrayList<RunInfo>> data) {
		// all of it
		for (Entry<String, ArrayList<RunInfo>> entry : data.entrySet()) {
			System.out.println("Data for benckmark: " + entry.getValue().get(0).benchmarkName);
			for (RunInfo info : entry.getValue()) {
				System.out.println(info);
			}
			System.out.println();
		}

	}

	public static String generatCompileCommadForKinara(ArrayList<String> benchmarks, String kinaraBemckmarkFolder) {
		StringBuilder sb = new StringBuilder();

		sb.append("cd ");
		sb.append(kinaraBemckmarkFolder);
		sb.append(" && make eopt ");
		sb.append("'PROJECT_EXECUTABLES=");
		for (String bm : benchmarks) {
			String[] parts = bm.split("/");
			sb.append("gen_" + parts[parts.length - 1] + " ");
		}
		sb.append("'");

		return sb.toString();

	}

//	public static void runtimeCommand(String inputCmd) throws Exception {
//
//		String[] commands = { "/bin/bash", "-c", inputCmd };
//		System.out.println("Running command: " + commands[0] + " " + commands[1] + " " + commands[2]);
//
//		Process pr = Runtime.getRuntime().exec(commands);
//
//		ReadStream s1 = new ReadStream("stdin", pr.getInputStream());
//		ReadStream s2 = new ReadStream("stderr", pr.getErrorStream());
//
//		s1.start();
//		s2.start();
//
//		pr.waitFor();
//
//		// int exitVal = pr.waitFor();
//		// System.out.println("ExitValue: " + exitVal);
//
//	}

	public static void runtimeCommand(String inputCmd) throws Exception {

		String[] commands = { "/bin/bash", "-c", inputCmd };
		// System.out.println("Running command: " + commands[0] + " " + commands[1] + "
		// " + commands[2]);

		Process pr = Runtime.getRuntime().exec(commands);

		ReadStream s1 = new ReadStream("stdin", pr.getInputStream(), new File("logs/out.txt"));
		ReadStream s2 = new ReadStream("stderr", pr.getErrorStream(), new File("logs/err.txt"));

		s1.start();
		s2.start();

		pr.waitFor();

		// new
		s1.join();
		s2.join();

		// int exitVal = pr.waitFor();
		// System.out.println("ExitValue: " + exitVal);

	}

	public static void runtimeCommand(String inputCmd, boolean stdOut) throws Exception {

		String[] commands = { "/bin/bash", "-c", inputCmd };
		// System.out.println("Running command: " + commands[0] + " " + commands[1] + "
		// " + commands[2]);

		Process pr = Runtime.getRuntime().exec(commands);

		ReadStream s1 = null;
		if (stdOut) {
			s1 = new ReadStream("stdin", pr.getInputStream());
		} else {
			s1 = new ReadStream("stdin", pr.getInputStream(), new File("logs/out.txt"));
		}

		ReadStream s2 = new ReadStream("stderr", pr.getErrorStream(), new File("logs/err.txt"));

		s1.start();
		s2.start();

		pr.waitFor();

		// int exitVal = pr.waitFor();
		// System.out.println("ExitValue: " + exitVal);

	}

	public static String generateRunCommandForBM(RunInfo bmInfo, String kinaraDestFolder) {

		int cutoffVal = bmInfo.cutoff;
		String bmName = "gen_" + bmInfo.benchmarkName;

		return kinaraDestFolder + "bin/opt/" + bmName + " " + cutoffVal;
	}

	public static void runAllBMs(boolean useFirabilityAwareness, Options options) throws Exception {

		ArrayList<String> benchmarks = new ArrayList<String>();

		// benchmarks, normal operation
		String prefix = "benchmarks";

		if (!useFirabilityAwareness) {
			prefix = "benchmarks_no_crashes"; // without the extension (and crashes)
			options.phComp_enableFirabilityAwareness = false;
		}

		benchmarks.add("DistributedStore");
		benchmarks.add("Consortium");
		benchmarks.add("TwoObjectTracker");
		benchmarks.add("DistributedRobotFlocking");
		benchmarks.add("DistributedLockService");
		benchmarks.add("DistributedSensorNetwork");
		benchmarks.add("SensorNetworkwithReset");
		benchmarks.add("SATSLandingProtocol");
		benchmarks.add("SATSLandingProtocol_II");
		benchmarks.add("MobileRoboticsMotionPlanning");
		benchmarks.add("MobileRoboticswithReset");
		benchmarks.add("DistributedRegister");

		int repeats = 1;
		LinkedHashMap<String, ArrayList<RunInfo>> data = new LinkedHashMap<String, ArrayList<RunInfo>>();
		for (String bm : benchmarks) {
			ArrayList<RunInfo> bmRuns = new ArrayList<RunInfo>();
			for (int i = 0; i < repeats; i++) {
				RunInfo rinfo = Utils.runBM(prefix, bm, options);
				// if (i >= 2) {
				bmRuns.add(rinfo);
				// }
				Thread.sleep(500);
			}
			data.put(bm, bmRuns);
		}

		if (useFirabilityAwareness) {
			// kinara compilation
			System.out.println("Compiling generated kinara models...");
			String compileCommand = Utils.generatCompileCommadForKinara(benchmarks, options.kinaraDestFolder);
			Utils.runtimeCommand(compileCommand, true);

			// running kinara
			System.out.println("Running the kinara model checker...");
			for (Entry<String, ArrayList<RunInfo>> bmPair : data.entrySet()) {
				RunInfo runInfo = bmPair.getValue().get(0);
				String runCmd = Utils.generateRunCommandForBM(runInfo, options.kinaraDestFolder);

				long before = System.currentTimeMillis();
				System.out.print("Running " + runInfo.benchmarkName + " ... ");
				Utils.runtimeCommand(runCmd);
				long after = System.currentTimeMillis();
				System.out.println("Done");
				runInfo.totalTime += (after - before);
			}

			// dump results
			Utils.printTableLike(data, benchmarks);
		} else {

			System.out.println("\n---------------------------------------------------");
			System.out.println("The following systems are now not phase-compatible:");
			for (Entry<String, ArrayList<RunInfo>> bmPair : data.entrySet()) {
				RunInfo runInfo = bmPair.getValue().get(0);
				if (!runInfo.isPhaseCompatible) {
					System.out.println(runInfo.benchmarkName);
				}
			}
			System.out.println();

		}
	}

	public static void runBM(String bmName, Options options) throws Exception {

		ArrayList<String> benchmarks = new ArrayList<String>();

		// benchmarks, normal operation
		String prefix = "benchmarks";
		benchmarks.add(bmName);

		int repeats = 1;
		LinkedHashMap<String, ArrayList<RunInfo>> data = new LinkedHashMap<String, ArrayList<RunInfo>>();
		for (String bm : benchmarks) {
			ArrayList<RunInfo> bmRuns = new ArrayList<RunInfo>();
			for (int i = 0; i < repeats; i++) {
				RunInfo rinfo = Utils.runBM(prefix, bm, options);
				// if (i >= 2) {
				bmRuns.add(rinfo);
				// }
				Thread.sleep(500);
			}
			data.put(bm, bmRuns);
		}

		long before = System.currentTimeMillis();
		// kinara compilation
		System.out.println("Compiling generated kinara model...");
		String compileCommand = Utils.generatCompileCommadForKinara(benchmarks, options.kinaraDestFolder);
		Utils.runtimeCommand(compileCommand, true);

		// running kinara
		System.out.println("Running the kinara model checker...");
		for (Entry<String, ArrayList<RunInfo>> bmPair : data.entrySet()) {
			RunInfo runInfo = bmPair.getValue().get(0);
			String runCmd = Utils.generateRunCommandForBM(runInfo, options.kinaraDestFolder);
			System.out.print("Running " + runInfo.benchmarkName + " ... ");
			Utils.runtimeCommand(runCmd);
			long after = System.currentTimeMillis();
			System.out.println("Done");
			runInfo.totalTime += (after - before);
			checkNoKinaraErrors();
		}

		// dump results
		Utils.printTableLike(data, benchmarks);

	}

	public static void runBM(String bmName, int userCutoff, Options options) throws Exception {

		ArrayList<String> benchmarks = new ArrayList<String>();

		// benchmarks, normal operation
		String prefix = "benchmarks";
		benchmarks.add(bmName);

		int repeats = 1;
		LinkedHashMap<String, ArrayList<RunInfo>> data = new LinkedHashMap<String, ArrayList<RunInfo>>();
		for (String bm : benchmarks) {
			ArrayList<RunInfo> bmRuns = new ArrayList<RunInfo>();
			for (int i = 0; i < repeats; i++) {
				RunInfo rinfo = Utils.runBM(prefix, bm, options);
				// if (i >= 2) {
				bmRuns.add(rinfo);
				// }
				Thread.sleep(500);
			}
			data.put(bm, bmRuns);
		}

		// kinara compilation
		System.out.println("Compiling generated kinara model...");
		String compileCommand = Utils.generatCompileCommadForKinara(benchmarks, options.kinaraDestFolder);
		Utils.runtimeCommand(compileCommand, true);

		// running kinara
		System.out.println("Running the kinara model checker...");
		for (Entry<String, ArrayList<RunInfo>> bmPair : data.entrySet()) {
			RunInfo runInfo = bmPair.getValue().get(0);

			// here, inject the user-specified cutoff
			runInfo.cutoff = userCutoff;

			String runCmd = Utils.generateRunCommandForBM(runInfo, options.kinaraDestFolder);

			long before = System.currentTimeMillis();
			System.out.print("Running " + runInfo.benchmarkName + " ... ");
			Utils.runtimeCommand(runCmd);
			long after = System.currentTimeMillis();
			System.out.println("Done");
			runInfo.totalTime += (after - before);
			checkNoKinaraErrors();
		}

		// dump results
		Utils.printTableLike(data, benchmarks);

	}

	private static void checkNoKinaraErrors() {

		StringBuilder content = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("logs/out.txt"));

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				content.append(sCurrentLine).append("\n");
			}
			if (br != null) {
				br.close();
			}
		} catch (FileNotFoundException e) {
			// we'll get an empty content
		} catch (IOException e) {
			// we'll get an empty content
		}

		String s = content.toString();

		if (s.contains("AQS contains one or more errors") || // some error
				!s.contains("Found Correct Completion!")) { // no success

			if (s.length() != 0) {
				System.err.println("Kinara returned the following violation:");
				System.err.println(s);
				System.exit(-1);
			} else {
				System.err.println("Kinara failed. Check logs/err.txt");
			}
		}
	}

	public static void cleanLogs() {
		File logs = new File("logs");
		for (File file : logs.listFiles())
			if (!file.isDirectory())
				file.delete();
	}
}
