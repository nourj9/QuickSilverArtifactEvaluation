package synthesis;

import java.util.HashSet;

public abstract class Synthesizer {
	protected HashSet<Constraint> constraints = new HashSet<Constraint>();
	
	/**
	 * This includes setting up the background theories
	 * and any options that the solvers may need. Note that
	 * for now we use can use a fixed theory, but we can at
	 * one point be smarter about it based on the holes.
	 * */
	public abstract void initialize();

	/**
	 * Generating the synthesis functions with their inputs, 
	 * outputs, and any grammar they might need.
	 * */
	public abstract void generateSynthFuns();

	/**
	 * Any one-time initial constraints we need to add. E.g.,
	 * bounds on range, etc.
	 * */
	public abstract void generateInitialConstraints();

	public abstract void flushCompletions();

	public abstract void completeHoles();

	public abstract boolean addConstraint(Constraint c);
	
	public abstract void removeConstraint(Constraint c);
	
	public abstract String toString();

	/**
	 * add the negation of the current completion to the 
	 * constraints. 
	 * */	
	public abstract void rejectCurrentCompletion();

}
