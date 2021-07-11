package ScriptGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Core.RunInfo;

public class Generator {

	private static void VaryingNExp(ArrayList<BmInfo> benchmarks, String outDirName, String mainDir) {
		mkdir(mainDir + "/" + outDirName);
		for (BmInfo bm : benchmarks) {
			String bmName = bm.name;
			int timeoutInSeconds = bm.timeOut;
			int numHoles = bm.numOfHoles;
			int numProcesses = bm.numOfProcceses;
			int maxProcNum = bm.maxProcNum;

			String folder = mainDir + "/" + outDirName + "/" + bmName;
			mkdir(folder);

			System.out.println("echo \"Beginning test run for " + bmName + "\";");

			String lineDelim = "\n";
			String running = "";
			String indent = "";
			String indentStep = "  ";
			for (int pNum = maxProcNum; pNum-- > 2;) {
				indent += indentStep;
			}
			for (int pNum = maxProcNum; pNum-- > 2;) {

				// String prefix = "sleep 1s;\\\n";
				String prefix = "";
				prefix += (timeoutInSeconds == 0 ? "" : "timeout " + timeoutInSeconds + "s");
				prefix += " ./bin/opt/";
				prefix += bmName;
				prefix += " " + pNum;
				String fname = folder + "/" + bmName + "_c" + pNum + ".txt";
//				System.out.println(prefix + " " + verification(numHoles) + " > " + fname);
				// System.out.println(prefix + " > " + fname);

				running = wrapWithTimeoutCheck(
						prefix + " > " + fname + ";" + (running.length() > 0 ? lineDelim + running : ""), indent,
						indentStep, lineDelim);
				indent = indent.substring(0, indent.length() - indentStep.length());

			}
			System.out.println(running);

		}

	}

	private static String wrapWithTimeoutCheck(String str, String indent, String indentStep, String lineDelim) {

		String result = indent + "if [ $? -ne 124 ]; then" + lineDelim;
		result += indent + indentStep + str + lineDelim;
		result += indent + "fi;";
		return result;
	}

	private static void mkdir(String name) {
		System.out.println("mkdir " + name);
	}

	public static void printRunCommandsForVerificationInKinara(HashMap<String, ArrayList<RunInfo>> data, String mainDir,
			int timeout) {

		mkdir(mainDir);

		for (Entry<String, ArrayList<RunInfo>> entry : data.entrySet()) {

			String[] parts = entry.getKey().split("/");
			String bmName = "gen_" + parts[parts.length - 1];

			int cutoff = entry.getValue().get(0).cutoff;

			String folder = mainDir + "/" + bmName;
			mkdir(folder);

			// System.out.println("echo \"Beginning test run for " + bmName + "\";");

			String prefix = "";
			prefix += "timeout ";
			prefix += timeout + "s";
			prefix += " ./bin/opt/";
			prefix += bmName;
			prefix += " " + cutoff;
			String fname = folder + "/" + bmName + "_c" + cutoff + ".txt";

			System.out.println(prefix + " > " + fname + " 2>&1");

		}
	}

	public static void printVaryNCommands(HashMap<String, ArrayList<RunInfo>> data, String mainDir) {

		ArrayList<BmInfo> benchmarks = new ArrayList<>();
		for (Entry<String, ArrayList<RunInfo>> entry : data.entrySet()) {

			String[] parts = entry.getKey().split("/");
			String bmName = "gen_" + parts[parts.length - 1];
			int cutoff = entry.getValue().get(0).cutoff;

			benchmarks.add(new BmInfo(bmName, 0, cutoff, 11, 1800));
		}

		VaryingNExp(benchmarks, "varyN", mainDir);
	}

}