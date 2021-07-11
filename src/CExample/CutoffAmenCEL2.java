package CExample;

import java.util.HashSet;

import semantics.core.LocalState;
import semantics.core.Path;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;

public class CutoffAmenCEL2 extends CutoffAmenCE {

	private HashSet<Path> nonFreeUsablePaths;
	private Path shortest;

	public CutoffAmenCEL2(LocalState initState, HashSet<LocalState> targetStates, Path shortest,
			HashSet<Path> nonFreeUsablePaths, ProcessSemantics semantics) {

		super(CutoffAmenCE.Lemma.Lemma2, initState, targetStates, semantics);
		this.nonFreeUsablePaths = nonFreeUsablePaths;
		this.shortest = shortest;
		generateSuggestions();
	}

	@Override
	public Constraint generateConstraint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return "Lemma 2 failed: the following transitions are not independent on path:\n" + shortest + "\n"
				+ shortest.getNonFreeUsableTransitions();
	}

	private void generateSuggestions() {
		// TODO see the phase comp classes for examples.

	}

}
