package CExample;

import java.util.HashSet;
import java.util.LinkedHashSet;

import CEgeneralizations.ExistenceChecker;
import feedback.Ranker;
import feedback.Suggestion;
import feedback.Suggestion.UserAction;
import lang.expr.ExprOp;
import lang.expr.Expression;
import lang.expr.ExpressionBuilder;
import lang.expr.ExprOp.Op;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;
import utils.MyStringBuilder;

public class PhaseCompCEC31 extends PhaseCompCE {
	private LocalTransition acting, otherActing, outgoing;

	public PhaseCompCEC31(LocalTransition acting, LocalTransition otherActing, LocalTransition outgoing,
			ProcessSemantics semantics) {
		super(PhaseCompCE.Condition.Condition31, semantics);
		this.acting = acting;
		this.otherActing = otherActing;
		this.outgoing = outgoing;
		generateSuggestions();

	}

	@Override
	public Constraint generateConstraint() {

		/**
		 * To give some names, let:
		 * acting:	 s1 --a!!--> s2
		 * outgoing: s2 --b??--> s3
		 * otherActing: s4 --a??--> s5
		 * s5 has no b??
		 * The counter example encodes: 
		 * exist(acting) ^ exist(outgoing) ^ exist(otherActing) ^ !exist(s5 --b??-->*)
		 * 
		 * */
		ExistenceChecker exChecker = semantics.getExistenceChecker();

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		// encoding
		conjuncts.add(exChecker.exists(acting, true, true, true));
		conjuncts.add(exChecker.exists(outgoing, false, true, false));
		conjuncts.add(exChecker.exists(otherActing, true, true, true));
		Expression existsAct = exChecker.existsAction(otherActing.getDest(), outgoing.getLocalAction()).getFirst(); //note this.
		conjuncts.add(new ExprOp(Op.NOT, existsAct));

		Expression conjunction = ExpressionBuilder.makeConjunction(conjuncts);
		Expression predicate = new ExprOp(ExprOp.Op.NOT, conjunction);
		return new Constraint(predicate);
		}

	@Override
	public String getDescription() {
		MyStringBuilder sb = new MyStringBuilder();

		sb.appn("CONDITION 3.1");
		sb.appn("Acting transition " + acting.toString() + " ");
		sb.appn("ends in a state with a reacting");
		sb.appn("transition " + outgoing + ", but acting transition ");
		sb.appn(otherActing.toString() + " ends in a state without such a reacting transition.");
		sb.appn("Short version(3.1): " + otherActing.getDest().toSmallString() + " needs a reacting transition on "
				+ outgoing.getLabel() + " (just like " + acting.getDest().toSmallString() + ")");

		return sb.toString();
	}

	private void generateSuggestions() {

		// suggestion 1: add a receive from otherActing.dst to anywhere
		LocalTransition tr1 = LocalTransition.make(otherActing.getDest(), LocalState.ANYWHERE,
				outgoing.getTransitionType(), outgoing.getLabel());
		Suggestion s1 = new Suggestion(UserAction.ADD, tr1);
		addSuggestion(s1);

		// suggestion 2: replace otherActing with a transition that starts from the same
		// source goes to a destination in the source set of outgoing

		HashSet<LocalState> srcOfOutgoing = semantics.getSourceSet(outgoing.getLabel());

		for (LocalState st : srcOfOutgoing) {
			LocalTransition tr = LocalTransition.make(otherActing.getSrc(), st, otherActing.getTransitionType(),
					otherActing.getLabel());
			Suggestion s = new Suggestion(UserAction.REPLACE, otherActing, tr);
			addSuggestion(s);
		}

		/**
		 * Note: as there are many suggestions, we should rank then elsewhere
		 * */
		Ranker.rankSuggestionsAccordingToCondition(this, Condition.Condition31);

	}
}
