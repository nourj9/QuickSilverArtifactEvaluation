package ScriptGenerator;

public class BmInfo {

	String name;
	int numOfHoles;
	int numOfProcceses;
	int maxProcNum;
	int timeOut;

	public BmInfo() {
	}

	public BmInfo(String name, int numOfProcceses, int numOfHoles, int maxProcNum, int timeOut) {
		this.name = name;
		this.numOfHoles = numOfHoles;
		this.numOfProcceses = numOfProcceses;
		this.maxProcNum = maxProcNum;
		this.timeOut = timeOut;
	}

}
