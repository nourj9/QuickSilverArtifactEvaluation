package lang.specs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import lang.core.Location;
import lang.core.Protocol;
import lang.expr.ExprVar;
import semantics.core.LocalState;

public class CompoundSpec extends SafetySpecification {
	public enum Op {
		AND, OR
	};

	private SafetySpecification arg1;
	private SafetySpecification arg2;
	private Op opCode;

	public CompoundSpec(Op opCode, SafetySpecification arg1, SafetySpecification arg2) {
		this.opCode = opCode;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	public SafetySpecification getArg1() {
		return arg1;
	}

	public void setArg1(SafetySpecification arg1) {
		this.arg1 = arg1;
	}

	public SafetySpecification getArg2() {
		return arg2;
	}

	public void setArg2(SafetySpecification arg2) {
		this.arg2 = arg2;
	}

	public Op getOpCode() {
		return opCode;
	}

	public void setOpCode(Op opCode) {
		this.opCode = opCode;
	}

	@Override
	public String toString() {
		return "( " + arg1 + " " + getOpString() + " " + arg2 + " )";
	}

	private String getOpString() {
		switch (opCode) {
		case AND:
			return "&&";
		case OR:
			return "||";
		default:
			return "hmm?";
		}
	}

	@Override
	public void findReplace(Location toRep, ArrayList<Location> replacements) {
		arg1.findReplace(toRep, replacements);
		arg2.findReplace(toRep, replacements);
	}

	@Override
	public void complainIfLocIsUsed(HashSet<Location> locs) {
		arg1.complainIfLocIsUsed(locs);
		arg2.complainIfLocIsUsed(locs);
	}

	@Override
	public HashSet<Integer> getThresholds() {
		HashSet<Integer> toRet = new HashSet<Integer>();
		toRet.addAll(arg1.getThresholds());
		toRet.addAll(arg2.getThresholds());
		return toRet;
	}

	@Override
	public HashSet<ExprVar> getAllVarsInPreds() {
		HashSet<ExprVar> toRet = new HashSet<ExprVar>();
		toRet.addAll(arg1.getAllVarsInPreds());
		toRet.addAll(arg2.getAllVarsInPreds());
		return toRet;
	}

	public boolean containsDisjuncts() {
		return opCode.equals(Op.OR) || arg1.containsDisjuncts() || arg2.containsDisjuncts();
	}

	@Override
	public void complainIfVarIsUsed(ExprVar var) {
		arg1.complainIfVarIsUsed(var);
		arg2.complainIfVarIsUsed(var);
	}

	@Override
	public SafetySpecification smartClone() {
		return new CompoundSpec(opCode, arg1.smartClone(), arg2.smartClone());
	}

	@Override
	public HashSet<LocalState> extractLocalStates() {
		HashSet<LocalState> toRet = new HashSet<LocalState>();
		toRet.addAll(arg1.extractLocalStates());
		toRet.addAll(arg2.extractLocalStates());
		return toRet;
	}
}
