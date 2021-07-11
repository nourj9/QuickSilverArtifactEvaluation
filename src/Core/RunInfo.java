package Core;

public class RunInfo {

	public String benchmarkName;
	public int numOfAMLHandlers;
	public long parseTime;
	public long preProcessingTime;
	public long codeGenerationTime;
	public int numOfCoreHandlers;
	public long procSemGenTime;
	public long wellBehavCheckTime;
	public long cutoffCheckTime;
	public double totalTime;
	public int numOfPhases;
	public int cutoff;
	public int LoC;
	public boolean isPhaseCompatible;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(benchmarkName).append("\t");
		sb.append(numOfPhases).append("\t");
		sb.append(cutoff).append("\t");
		sb.append(totalTime/1000.0).append("\t");
				
		return sb.toString();
		
//		return "RunInfo [benchmarkName=" + benchmarkName + ", numOfAMLHandlers=" + numOfAMLHandlers + ", parseTime="
//				+ parseTime + ", preProcessingTime=" + preProcessingTime + ", codeGenerationTime=" + codeGenerationTime
//				+ ", numOfCoreHandlers=" + numOfCoreHandlers + ", procSemGenTime=" + procSemGenTime
//				+ ", wellBehavCheckTime=" + wellBehavCheckTime + ", cutoffCheckTime=" + cutoffCheckTime + ", totalTime="
//				+ totalTime + ", numOfPhases=" + numOfPhases + ", cutoff=" + cutoff + "]";
	}

	public void computeTotalTime() {
		totalTime = parseTime + preProcessingTime + codeGenerationTime + procSemGenTime + wellBehavCheckTime
				+ cutoffCheckTime;
	}

}
