package Verifier;

import java.util.ArrayList;
import java.util.HashSet;

public class Action {

	enum Type {
		MULTI, MAXIMAL
	}

	private String name;
	private Type type;
	private Matrix recMatrix;
	private ArrayList<VectorPair> sndVectorPairs;
	private HashSet<Integer> handlingStates;
	private GlobalTransitionManager gtm;

	private Action(String name, Type type, Matrix recMatrix, ArrayList<VectorPair> sndVectorPairs,
			HashSet<Integer> handlingStates, GlobalTransitionManager gtm) {
		this.name = name;
		this.type = type;
		this.recMatrix = recMatrix;
		this.sndVectorPairs = sndVectorPairs;
		this.handlingStates = handlingStates;
		this.gtm = gtm;
		sanityCheckMatrix(recMatrix);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public Matrix getRecMatrix() {
		return recMatrix;
	}

	public ArrayList<VectorPair> getSndVectorPairs() {
		return sndVectorPairs;
	}

	public HashSet<Integer> getHandlingStates() {
		return handlingStates;
	}

	public static Action makeAction(GlobalTransitionManager gtm, String name, Type type, Matrix recMatrix,
			ArrayList<VectorPair> sndVectorPairs, HashSet<Integer> handlingStates) {

		Action act = new Action(name, type, recMatrix, sndVectorPairs, handlingStates, gtm);
		// make sure no two actions have the same name since this is important for
		// hashing and equality.
		if (gtm.getActions().contains(act)) {
			System.err.println("Error: action " + act + " already exists.");
			Thread.dumpStack();
			System.exit(-1);
		}
		return act;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Action) {
			Action other = (Action) obj;
			return other.name.equals(this.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	// make sure that every column sums to 1, i.e., deterministic receives
	private void sanityCheckMatrix(Matrix matrix) {
		for (int col = 0; col < matrix.matrix.length; col++) {
			int sum = 0;

			for (int row = 0; row < matrix.matrix.length; row++) {
				sum += matrix.matrix[row][col];
			}

			if (sum != 1) {
				System.err.println("Error: something is off about the matrix of action " + name + ":");
				System.err.println(matrix);

				Thread.dumpStack();
				System.exit(-1);
			}
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("\n");
		sb.append("Matrix").append("\n");
		sb.append(recMatrix);
		sb.append("Vectors").append("\n");
		for (VectorPair pair : sndVectorPairs) {
			sb.append(pair);
			sb.append("\n");
		}
		// sb.append("---------------------");
		sb.append("can be handled in\n");
		sb.append(" ");

		if (handlingStates.size() == gtm.getGlobalStateManager().getProcessSemantics().getStates().size()) {
			sb.append("all states\n");
		} else {
			for (Integer index : handlingStates) {
				sb.append(gtm.getGlobalStateManager().getStateOf(index).toSmallString()).append(" ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

}
