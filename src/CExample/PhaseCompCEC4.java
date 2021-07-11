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
import lang.expr.ExpressionSimplifier;
import lang.expr.ExprOp.Op;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;
import utils.MyStringBuilder;
import semantics.analysis.Phase;
import semantics.core.LocalAction;

public class PhaseCompCEC4 extends PhaseCompCE {

	private LocalTransition intPw, outgoing;
	private LocalState stateInSamePhase;
	private Phase phase;

	public PhaseCompCEC4(LocalTransition intPw, LocalTransition outgoing, LocalState stateInSamePhase, Phase phase,
			ProcessSemantics semantics) {
		super(PhaseCompCE.Condition.Condition4, semantics);
		this.intPw = intPw;
		this.outgoing = outgoing;
		this.stateInSamePhase = stateInSamePhase;
		this.phase = phase;
		generateSuggestions();
	}

	@Override
	public Constraint generateConstraint() {
		/**
		 * To give some names, let:
		 * intPw:	 s1 -------> s2
		 * outgoing: s2 --b??--> s3
		 * s4 in same phase as s1 has no b??
		 * The counter example encodes: 
		 * exist(acting) ^ exist(outgoing) ^ exist(reacting) ^ !exist(s5 --b??-->*)
		 * 
		 * */
		ExistenceChecker exChecker = semantics.getExistenceChecker();

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();
		// encoding
		conjuncts.add(exChecker.exists(intPw, true, true, true));
		conjuncts.add(exChecker.exists(outgoing, false, true, false));
//		conjuncts.add(exChecker.samePhase(intPw.getSrc(), stateInSamePhase));
		conjuncts.add(exChecker.samePhase(phase, intPw.getSrc(), stateInSamePhase));
		Expression existsAct = exChecker.existsAction(stateInSamePhase, outgoing.getLocalAction()).getFirst(); // note
																												// this.
		conjuncts.add(new ExprOp(Op.NOT, existsAct));

		Expression conjunction = ExpressionBuilder.makeConjunction(conjuncts);
		Expression predicate = new ExprOp(ExprOp.Op.NOT, conjunction);
		return new Constraint(ExpressionSimplifier.simplify(predicate));
	}

	@Override
	public String getDescription() {
		MyStringBuilder sb = new MyStringBuilder();
		sb.appn("CONDITION 4");
		sb.appn("Transition " + intPw.toString() + " ");
		sb.appn("ends in a state with a reacting transition " + outgoing + ", ");
		sb.appn("but state " + stateInSamePhase.toSmallString() + " in the same phase as "
				+ intPw.getSrc().toSmallString() + " ");
		sb.appn("cannot reach such a reacting transition via an internal (and environment) path.");

		return sb.toString();
	}

	private void generateSuggestions() {
		// suggestion 1: add a receive from s to anywhere

		LocalTransition tr1 = LocalTransition.make(stateInSamePhase, LocalState.ANYWHERE, intPw.getTransitionType(),
				intPw.getLabel());
		Suggestion s1 = new Suggestion(UserAction.ADD, tr1);
		addSuggestion(s1);

		// suggestion 2: add an internal/env transition from the s to
		// to a destination in the source set of outgoing

		HashSet<LocalState> srcOfOutgoing = semantics.getSourceSet(outgoing.getLabel());

		for (LocalState st : srcOfOutgoing) {
			LocalTransition tr = LocalTransition.make(stateInSamePhase, st, LocalAction.ActionType.INTERNAL, "");
			Suggestion sug = new Suggestion(UserAction.ADD, tr);
			addSuggestion(sug);
		}

		/**
		 * Note: as there are many suggestions, we should rank then elsewhere
		 * */
		Ranker.rankSuggestionsAccordingToCondition(this, Condition.Condition4);

	}

}
