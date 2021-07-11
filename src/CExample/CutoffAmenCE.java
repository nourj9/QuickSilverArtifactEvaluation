package CExample;

import java.util.HashSet;

import semantics.core.*;

public abstract class CutoffAmenCE extends CEWithFeedback {

	public static enum Lemma {
		Lemma2, Lemma3
	};

	/*
	 * Member Variables
	 */
	private Lemma lemma; // lemma which does not hold

	public Lemma getLemma() {
		return lemma;
	}

	public void setLemma(Lemma lemma) {
		this.lemma = lemma;
	}

	private ProcessSemantics semantics; // process semantics associated with this counterexample

	public ProcessSemantics getSemantics() {
		return semantics;
	}

	protected LocalState initState; // initial state

	public LocalState getInitState() {
		return initState;
	}

	protected HashSet<LocalState> targetStates; // target states from specification

	public HashSet<LocalState> getTargetStates() {
		return targetStates;
	}

	/*
	 * Constructors
	 */
	public CutoffAmenCE(Lemma lemma, LocalState initState, HashSet<LocalState> targetStates,
			ProcessSemantics semantics) {
		this.lemma = lemma;
		this.initState = initState;
		this.targetStates = targetStates;
		this.semantics = semantics;
	}

	@Override
	public String toString() {
		return "CE to cutoff-amenability: " + getDescription() + "\n\n" + "Suggestions to solve this: \n"
				+ printSuggestions();
	}

}
