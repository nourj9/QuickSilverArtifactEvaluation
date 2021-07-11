package Holes;

import java.util.HashMap;

public class HoleManager {
	private static HashMap<Integer, Hole> holes = new HashMap<Integer, Hole>();

	public static HashMap<Integer, Hole> getHoles() {
		return holes;
	}

	public static Hole getHole(int holeID) {
		return holes.get(holeID);
	}

	public static Hole addtHole(Hole hole) {
		return holes.put(hole.getId(), hole);
	}

	public static void printHoles() {
		StringBuilder b = new StringBuilder();
		b.append("------------- current holes status ------------\n");
		for (Hole hole : holes.values()) {
			b.append(hole).append("\n");
		}
		b.append("-----------------------------------------------\n");
		System.out.println(b.toString());
	}

}
