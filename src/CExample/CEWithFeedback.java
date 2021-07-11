package CExample;

import java.util.ArrayList;

import feedback.Suggestion;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;
import utils.MyStringBuilder;

public abstract class CEWithFeedback extends CounterExample {

	// assumed to be ranked from best (index 0) to meh (index n)
	protected ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
	protected ProcessSemantics semantics;

	public String printSuggestions() {
		MyStringBuilder sb = new MyStringBuilder();
		for (Suggestion suggestion : suggestions) {
			sb.appn(suggestion);
		}
		return sb.toString();
	}
	public String printFancySuggestions() {
		MyStringBuilder sb = new MyStringBuilder();
		for (Suggestion suggestion : suggestions) {
			sb.appn(suggestion.toFancyString());
		}
		return sb.toString();
	}

	public void setSuggestions(ArrayList<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public ArrayList<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void addSuggestion(Suggestion s) {
		suggestions.add(s);
	}

	public abstract Constraint generateConstraint();

	public abstract String getDescription();

}
