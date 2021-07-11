package CExample;

import java.util.HashSet;

import feedback.Ranker;
import feedback.Suggestion;
import feedback.Suggestion.UserAction;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.Path;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;

public class CutoffAmenCEL31 extends CutoffAmenCE {

	private LocalTransition acting, reacting;
	private HashSet<Path> simpleFreePaths;

	public CutoffAmenCEL31(LocalState initState, HashSet<LocalState> targetStates, LocalTransition acting,
			LocalTransition reacting, HashSet<Path> simpleFreePaths, ProcessSemantics semantics) {
		super(CutoffAmenCE.Lemma.Lemma3, initState, targetStates, semantics);
		this.acting = acting;
		this.reacting = reacting;
		this.simpleFreePaths = simpleFreePaths;
		generateSuggestions();
	}

	@Override
	public Constraint generateConstraint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return "Lemma 3 failed: The following reacting transition started on the path and did not self loop:\n"
				+ reacting + "\n" + acting + "\n" + targetStates + "\n" + simpleFreePaths;
	}

	private void generateSuggestions() {
		// suggestion 1: replace the receive with a self-loop.
		LocalTransition tr1 = LocalTransition.make(reacting.getSrc(), reacting.getSrc(), reacting.getTransitionType(),
				reacting.getLabel());
		Suggestion s1 = new Suggestion(UserAction.REPLACE, reacting, tr1);
		addSuggestion(s1);

		// suggestion 2: remove the receive.
		Suggestion s2 = new Suggestion(UserAction.REMOVE, reacting);
		addSuggestion(s2);

		/**
		 * Note: as there are many suggestions, we should rank then elsewhere
		 * */
		Ranker.rankSuggestionsAccordingToLemma(this);
	}
}
