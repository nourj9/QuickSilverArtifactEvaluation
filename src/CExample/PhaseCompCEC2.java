package CExample;

import java.util.LinkedHashSet;

import CEgeneralizations.ExistenceChecker;
import feedback.Ranker;
import feedback.Suggestion;
import feedback.Suggestion.UserAction;
import lang.expr.ExprOp;
import lang.expr.Expression;
import lang.expr.ExpressionBuilder;
import lang.expr.ExpressionSimplifier;
import lang.expr.ExprOp.Op;
import semantics.core.LocalAction;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;
import utils.MyStringBuilder;

public class PhaseCompCEC2 extends PhaseCompCE {
	private LocalTransition acting;

	public PhaseCompCEC2(LocalTransition acting, ProcessSemantics semantics) {
		super(PhaseCompCE.Condition.Condition2, semantics);
		this.acting = acting;
		generateSuggestions();
	}

	@Override
	public Constraint generateConstraint() {
		/**
		 * To give some names, let:
		 * acting:	 s1 --a!!--> s2
		 * s2 has no b??
		 * The counter example encodes: 
		 * exist(acting) ^ !exist(s5 --b??-->*)
		 * 
		 * */
		ExistenceChecker exChecker = semantics.getExistenceChecker();

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();
		// encoding
		conjuncts.add(exChecker.exists(acting, true, true, false)); // doesn't matter where it goes.

		LocalAction sendAction = acting.getLocalAction();
		LocalAction receiveAction = LocalAction.make(sendAction.getLabel(),
				LocalAction.correspondingReceiveType(sendAction.getActionType()), sendAction.isEnvironment());

		Expression existsAct = exChecker.existsAction(acting.getSrc(), receiveAction).getFirst(); // note this.

		conjuncts.add(new ExprOp(Op.NOT, existsAct));

		Expression conjunction = ExpressionBuilder.makeConjunction(conjuncts);

		Expression predicate = new ExprOp(ExprOp.Op.NOT, conjunction);
		return new Constraint(ExpressionSimplifier.simplify(predicate));
	}

	@Override
	public String getDescription() {
		MyStringBuilder sb = new MyStringBuilder();
		sb.appn("CONDITION 2");
		sb.appn("State " + acting.getSrc().toSmallString() + " has a globally-synchronizing ");
		sb.appn("acting transition " + acting.toString() + " ");
		sb.appn("but no corresponding reacting transition.");
		sb.appn("Short version(2): " + acting.getSrc().toSmallString() + " needs a corresponding reacting transition on "
				+ acting.getLabel());
		return sb.toString();
	}

	private void generateSuggestions() {
		// suggestion 1: add a receive from t.src to t.dest on the same action.
		LocalTransition tr1 = LocalTransition.make(acting.getSrc(), acting.getDest(),
				LocalTransition.correspondingReceiveType(acting.getTransitionType()), acting.getLabel());
		Suggestion s1 = new Suggestion(UserAction.ADD, tr1);
		// s1.setRank(5); //give this a head start!
		addSuggestion(s1);

		// suggestion 2: add t.src --> anywhere?
		LocalTransition tr2 = LocalTransition.make(acting.getSrc(), LocalState.ANYWHERE,
				LocalTransition.correspondingReceiveType(acting.getTransitionType()), acting.getLabel());
		Suggestion s2 = new Suggestion(UserAction.ADD, tr2);
		addSuggestion(s2);

		/**
		 * Note: as there are many suggestions, we should rank then elsewhere
		 * */
		Ranker.rankSuggestionsAccordingToCondition(this, Condition.Condition2);
	}

}
