package lang.specs;

import java.util.ArrayList;
import java.util.HashSet;

import lang.core.Location;
import lang.expr.ExprVar;
import semantics.core.LocalState;

public abstract class SafetySpecification extends Specification {

	public abstract void findReplace(Location toRep, ArrayList<Location> replacements);

	// public abstract String getKinaraCode();
	public abstract HashSet<Integer> getThresholds();

	public abstract HashSet<ExprVar> getAllVarsInPreds();
	
	// this will print an error if so, we shouldn't delete locs used by the specs.
	public abstract void complainIfLocIsUsed(HashSet<Location> locs);

	public abstract boolean containsDisjuncts();
	// public abstract Specification clone();

	// this will print an error if so, we shouldn't delete vars used by the specs.
	public abstract void complainIfVarIsUsed(ExprVar var);

	public abstract SafetySpecification smartClone();

	// flattens out state desc list and gets local states. 
	public abstract HashSet<LocalState> extractLocalStates();

	//public abstract void setProtocol(Protocol p);
}
