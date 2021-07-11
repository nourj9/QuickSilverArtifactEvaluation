package feedback;

import lang.core.Location;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import utils.MyStringBuilder;

public class Suggestion {
	public static enum UserAction {
		ADD, REMOVE, REPLACE
	};

	private UserAction action;
	private LocalTransition transition;
	private LocalTransition transition2;
	private int rank;

	public Suggestion(UserAction action, LocalTransition transition, LocalTransition transition2) {
		this.action = action;
		this.transition = transition;
		this.transition2 = transition2;
	}

	public Suggestion(UserAction action, LocalTransition transition) {
		this(action, transition, null);
	}

	public UserAction getAction() {
		return action;
	}

	public void setAction(UserAction action) {
		this.action = action;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void pumpRankBy(int rank) {
		this.rank += rank;
	}

	public LocalTransition getTransition() {
		return transition;
	}

	public LocalTransition getTransition2() {
		return transition2;
	}

	public void setTransition(LocalTransition transition) {
		this.transition = transition;
	}

	public void setTransition2(LocalTransition transition2) {
		this.transition2 = transition2;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Suggestion))
			return false;
		Suggestion other = (Suggestion) obj;

		switch (action) {
		case ADD:
		case REMOVE:
			return action.equals(other.action) //
					&& transition.equals(other.transition);
		case REPLACE:
			return action.equals(other.action) //
					&& transition.equals(other.transition) //
					&& transition2.equals(other.transition2);
		default:
			return false;
		}
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		switch (action) {
		case ADD:
			return "add transition " + transition;
		case REMOVE:
			return "remove transition " + transition;
		case REPLACE:
			return "replace transition " + transition + " with " + transition2;
		default:
			return "some suggestion cannot be printed!";
		}
	}

	public String toFancyString() {
		MyStringBuilder sb = new MyStringBuilder();
		Location srcLoc = transition.getSrc().getLoc();
		Location dstLoc = transition.getDest().getLoc();

		// same source and destination on a receive?
		if (action.equals(UserAction.ADD) //
				&& transition.isSelfLoop() //
				&& transition.isReceive() //
				&& srcLoc.isUserProvidedLocation()) {
			sb.appn("Make the process passive on action " + transition.getLabel() + " in location " + srcLoc.getName());

			// add of a top-level transition? make it a handler
		} else if (action.equals(UserAction.ADD) //
				&& srcLoc.isUserProvidedLocation() //
				&& dstLoc.isUserProvidedLocation()) {
			sb.appn("Add the following handler to location " + srcLoc.getName() + ":");
			sb.appn(generateHandlerText(transition));

			// replace a top-level transition with another one? make them handlers
		} else if (action.equals(UserAction.REPLACE) //
				&& srcLoc.isUserProvidedLocation() //
				&& dstLoc.isUserProvidedLocation() //
				&& transition2.getSrc().getLoc().isUserProvidedLocation() //
				&& transition2.getSrc().getLoc().isUserProvidedLocation()) {
			sb.appn("Replace the following handler in location " + srcLoc.getName() + "");
			sb.appn(generateHandlerText(transition));
			sb.appn("with the following handler: ");
			sb.appn(generateHandlerText(transition2));
			//

		} else {
			sb.app("make fancy: " + toString());
		}

		// handle message bla in location bla

		// save a string for the original code before preprocessing.
		// change location s to all of its children
		// ?? make the preprocessing leveled and print only the level where the needed
		// locations first show up.

		return sb.toString();
	}

	private String generateHandlerText(LocalTransition transition) {
		MyStringBuilder sb = new MyStringBuilder();
		// LATER make better
		if (transition.isAgreement()) {
			sb.appn("on agreement over " + transition.getLabel());
			sb.app("goto " + transition.getDest().getLoc().getName());
		} else if (transition.isReceive()) {
			sb.appn("on recv(" + transition.getLabel() + ") do");
			sb.app("goto " + transition.getDest().getLoc().getName());
		} else if (transition.isInternal()) {
			sb.appn("on _ do");
			sb.app("\tgoto " + transition.getDest().getLoc().getName());
		} else {
			sb.appn("on _ do");
			sb.appn("\t send " + transition.getLabel());
			sb.app("\tgoto " + transition.getDest().getLoc().getName());
		}

		return sb.toString();
	}
}
