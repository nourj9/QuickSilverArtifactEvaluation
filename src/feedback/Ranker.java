package feedback;

import java.util.ArrayList;

import CExample.*;
import CExample.PhaseCompCE.Condition;
import Core.Options.PhCompFeedback;
import semantics.core.LocalState;

public class Ranker {
	private static final int Points_ActionIsAdd = 1;
	private static final int Points_destIsNotAnywhere = 1;
	private static final int Points_TransitionIsselfLoop = 1;
	private static final int Points_NoGeneratedLocsNeeded = 1;
	private static final int Points_SamePhaseSrc = 1;
	private static final int Points_SamePhaseDst = 1;

	// ranking suggestions per counter example, taking into account the condition
	public static void rankSuggestionsAccordingToCondition(PhaseCompCE cex, Condition cond) {
		switch (cond) {
		case Condition2:
			applyGeneralRankingRules(cex);
			// add more rules?
			break;

		case Condition31:
			applyGeneralRankingRules(cex);
			// add more rules?
			break;

		case Condition32:
			applyGeneralRankingRules(cex);
			// add more rules?
			break;

		case Condition4:
			applyGeneralRankingRules(cex);
			// add more rules?
			break;

		default:
			System.err.println(
					"Ranking for an unkown condition: " + cond + " in Ranker.rankSuggestionsAccordingToCondition");
			System.exit(-1);

		}
	}

	// ranking suggestions per counter example, taking into account the condition
	public static void rankSuggestionsAccordingToLemma(CutoffAmenCE cex) {
		switch (cex.getLemma()) {
		case Lemma2:
			applyGeneralRankingRules(cex);
			// add more rules?
			break;

		case Lemma3:
			applyGeneralRankingRules(cex);
			// add more rules?
			break;

		default:
			String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName(); 
			
			System.err.println(
					"Ranking for an unkown lemma: " + cex.getLemma()+ " in Ranker." + nameofCurrMethod);
			System.exit(-1);

		}
	}

	private static void applyGeneralRankingRules(CEWithFeedback cex) {
		ArrayList<Suggestion> suggestions = cex.getSuggestions();
		for (Suggestion suggestion : suggestions) {

			// suggestion is an add
			if (suggestion.getAction().equals(Suggestion.UserAction.ADD)) {
				suggestion.pumpRankBy(Points_ActionIsAdd);
			}

			// destination is not anywhere
			if (!suggestion.getTransition().getDest().equals(LocalState.ANYWHERE)) {
				suggestion.pumpRankBy(Points_destIsNotAnywhere);
			}

			// suggestion is a self loop
			if ((suggestion.getAction().equals(Suggestion.UserAction.ADD) //
					&& suggestion.getTransition().isSelfLoop())) {
				suggestion.pumpRankBy(Points_TransitionIsselfLoop);
			}

			// no generated locations needed
			if (suggestion.getAction().equals(Suggestion.UserAction.ADD) //
					&& suggestion.getTransition().getSrc().getLoc().isUserProvidedLocation() //
					&& suggestion.getTransition().getDest().getLoc().isUserProvidedLocation()) {
				suggestion.pumpRankBy(Points_NoGeneratedLocsNeeded);
			}

		}
	}

	// LATER: award points if a suggestion replaces a transition with one where the new
	// source/destination is in the same phase as the original
	// LATER: get ahold of the well-behavedness object so we can use the phases...
	private static void addPtsReplaceSameDestinationPhase(CutoffAmenCE cex) {
		ArrayList<Suggestion> suggestions = cex.getSuggestions();
		for (Suggestion suggestion : suggestions) {
		}
	}

	public static PhaseCompCE getHighestRankedCounterExamplesAccordingToCondition(ArrayList<PhaseCompCE> cExs) {
		// since we really only need one counter example, for now we do n't sort in
		// place, and instead just return one directly.
		for (PhaseCompCE cEx : cExs) {
			if(cEx.getCond().equals(PhaseCompCE.Condition.Condition2))
				return cEx;
		}
		
		for (PhaseCompCE cEx : cExs) {
			if(cEx.getCond().equals(PhaseCompCE.Condition.Condition31))
				return cEx;
		}
		
		for (PhaseCompCE cEx : cExs) {
			if(cEx.getCond().equals(PhaseCompCE.Condition.Condition32))
				return cEx;
		}
		
		for (PhaseCompCE cEx : cExs) {
			if(cEx.getCond().equals(PhaseCompCE.Condition.Condition4))
				return cEx;
		}
		
		return null;
		
		
	}
//	public static ArrayList<PhaseCompCE> rankCounterExamplesAccordingToCondition(ArrayList<PhaseCompCE> cExs) {
//		
//	}
}
