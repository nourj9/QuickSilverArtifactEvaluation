package CExample;

import feedback.Suggestion;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;

public abstract class PhaseCompCE extends CEWithFeedback {

	public static enum Condition {
		Condition2, Condition31, Condition32, Condition4
	};

	// the violated condition
	private Condition cond;

	protected PhaseCompCE(Condition cond, ProcessSemantics semantics) {
		this.semantics = semantics;
		this.cond = cond;
	}

	public Condition getCond() {
		return cond;
	}

	@Override
	public String toString() {
		return "CE to phase-compatability: " + getDescription() + "\n\n" + "Suggestions to solve this: \n"
				+ printSuggestions();
	}

	public Suggestion getHighestRankedSuggestion() {
		int maxRank = -1;
		Suggestion suggestionWithMaxRank = null;
		for (Suggestion suggestion : suggestions) {
			if (suggestion.getRank() > maxRank) {
				maxRank = suggestion.getRank();
				suggestionWithMaxRank = suggestion;
			}
		}
		return suggestionWithMaxRank;
	}

	public String toFancyString() {
		return "CE to phase-compatability: " + getDescription() + "\n\n" + "Suggestions to solve this:\n"
				+ printFancySuggestions();
	}
}
