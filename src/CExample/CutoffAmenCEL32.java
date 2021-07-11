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
import semantics.core.LocalAction;

public class CutoffAmenCEL32 extends CutoffAmenCE {

	private LocalTransition acting, reacting;
	private HashSet<Path> simpleFreePaths;

	public CutoffAmenCEL32(LocalState initState, HashSet<LocalState> targetStates, LocalTransition acting,
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
		return "Lemma 3 failed: The following reacting transition diverged and couldn't be skipped by "
				+ "internal or come back freely:\n" + reacting;
	}

	private void generateSuggestions() {
		// suggestion 1: remove the receive.
		Suggestion s1 = new Suggestion(UserAction.REMOVE, reacting);
		addSuggestion(s1);

		// suggestion 2: replace the receive with one that has a destination on a free
		// path.
		for (Path path : simpleFreePaths) {
			for (LocalState state : path.getStates()) {
				LocalTransition tr1 = LocalTransition.make(reacting.getSrc(), state, reacting.getTransitionType(),
						reacting.getLabel());
				Suggestion s2 = new Suggestion(UserAction.REPLACE, reacting, tr1);
				addSuggestion(s2);
			}
		}

		// suggestion 3: add an internal transition to skip the receive.
		for (Path p : simpleFreePaths) {
			for (LocalState state : p.getStates()) {
				LocalTransition tr1 = LocalTransition.make(reacting.getSrc(), state, LocalAction.ActionType.INTERNAL, "");
				Suggestion s3 = new Suggestion(UserAction.ADD, tr1);
				addSuggestion(s3);
			}
		}

		/**
		 * Note: as there are many suggestions, we should rank then elsewhere
		 * */
		Ranker.rankSuggestionsAccordingToLemma(this);

	}

}
