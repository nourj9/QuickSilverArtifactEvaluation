package Verifier;

import java.util.ArrayList;
import java.util.HashSet;

import lang.specs.AtLeastSpec;
import lang.specs.AtMostSpec;
import lang.specs.CompoundSpec;
import lang.specs.SafetySpecification;
import lang.specs.ThresholdSpec;
import semantics.core.LocalState;

public class GlobalState {
	private ArrayList<GlobalTransition> outgoingTransitions = new ArrayList<GlobalTransition>();
	private ArrayList<GlobalTransition> incomingTransitions = new ArrayList<GlobalTransition>();
	private GlobalStateManager gsm;
	private boolean initial = false;
	private final String key;
	private int[] values;

	/** 
	* values [1, 4, 0, 0, 2]
	* index 0 1 2 3 4
	* mapkey s1 s2 s3 s4 s5
	*/
	public GlobalState(GlobalStateManager gsm, int[] values, boolean initial) {
		this.gsm = gsm;
		this.values = values;
		this.initial = initial;
		this.key = computeKey();
	}

	public GlobalState(GlobalStateManager gsm, int[] values) {
		this(gsm, values, false);
	}

	// public GlobalState() {
	// this(null, false);
	// }

	// values as a string "1,4,0,0,2"
	private String computeKey() {
		StringBuilder key = new StringBuilder();

		for (int i = 0; i < values.length - 1; i++) {
			key.append(values[i]).append(",");
		}
		key.append(values[values.length - 1]);

		return key.toString();
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GlobalState))
			return false;
		return ((GlobalState) other).key.equals(this.key);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append("[");
//		for (int i = 0; i < values.length - 1; i++) {
//			sb.append(values[i]).append("  ");
//		}
//		sb.append(values[values.length - 1]);
//		sb.append("]");

		//sb.append("\n details: \n");
		for (int i = 0; i < values.length; i++) {
			if (values[i] != 0) {
				sb.append("[" + values[i] + "]").append(" in ").append(gsm.getStateOf(i).toSmallString()).append("\n");
			}
		}
		//sb.append("\n");

		return sb.toString();

	}

	public String prettyString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]).append(":\t").append(gsm.getStateOf(i).toSmallString()).append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	// public String prettyString() {
	// StringBuilder sb = new StringBuilder();
	// sb.append("[");
	// for (int i = 0; i < values.length - 1; i++) {
	// sb.append(gsm.getStateOf(i).toSmallString()).append("\t\t\t");
	// }
	// sb.append("\n");
	// for (int i = 0; i < values.length - 1; i++) {
	// sb.append(values[i]).append("\t\t\t");
	// }
	// sb.append(values[values.length - 1]);
	// sb.append("]");
	// return sb.toString();
	// }

	public ArrayList<GlobalTransition> getOutgoingTransitions() {
		return outgoingTransitions;
	}

	public void setOutgoingTransitions(ArrayList<GlobalTransition> outgoingTransitions) {
		this.outgoingTransitions = outgoingTransitions;
	}

	public ArrayList<GlobalTransition> getIncomingTransitions() {
		return incomingTransitions;
	}

	public void setIncomingTransitions(ArrayList<GlobalTransition> incomingTransitions) {
		this.incomingTransitions = incomingTransitions;
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	public int[] getValues() {
		if (values == null) {
			System.err.println("Error: Trying to use unset values in GlobalState.");
			Thread.dumpStack();
		}
		return values;
	}

	public void setValues(int[] values) {
		this.values = values;
	}

	public String getKey() {
		return key;
	}

	public void addOutgoingTransition(GlobalTransition tr) {
		outgoingTransitions.add(tr);
	}

	public void addIncomingTransition(GlobalTransition tr) {
		incomingTransitions.add(tr);
	}

	public boolean isSafe(SafetySpecification specs) {
		// handle base cases then recurse

		if (specs instanceof ThresholdSpec) {
			ThresholdSpec thSpecs = (ThresholdSpec) specs;

			int thrshold = thSpecs.getThreshold();
			HashSet<LocalState> flattenedStates = thSpecs.extractLocalStates();

			int total = 0;
			// total in states
			for (LocalState ls : flattenedStates) {
				total += values[gsm.getIndexOf(ls)];
			}

			// decide based on type.
			if (specs instanceof AtMostSpec) {
				return total <= thrshold;
			} else if (specs instanceof AtLeastSpec) {
				return total >= thrshold;
			}

		} else if (specs instanceof CompoundSpec) {
			CompoundSpec cspecs = (CompoundSpec) specs;
			if (cspecs.getOpCode().equals(CompoundSpec.Op.AND)) {
				return isSafe(cspecs.getArg1()) && isSafe(cspecs.getArg2());
			} else {
				return isSafe(cspecs.getArg1()) || isSafe(cspecs.getArg2());
			}
		} else {
			System.err.println("Error: unkown specs.. " + specs);
			Thread.dumpStack();
			System.exit(-1);
		}
		return false; // lol
	}

}
