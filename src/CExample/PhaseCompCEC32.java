package CExample;

import java.util.HashSet;
import java.util.LinkedHashSet;

import CEgeneralizations.ExistenceChecker;
import feedback.Ranker;
import feedback.Suggestion;
import feedback.Suggestion.UserAction;
import lang.expr.ExprOp;
import lang.expr.ExprOp.Op;
import lang.expr.Expression;
import lang.expr.ExpressionBuilder;
import lang.expr.ExpressionSimplifier;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;
import synthesis.Constraint;
import utils.MyStringBuilder;
import semantics.core.LocalAction;

public class PhaseCompCEC32 extends PhaseCompCE {

	private LocalTransition acting, reacting, outgoing;

	public PhaseCompCEC32(LocalTransition acting, LocalTransition reacting, LocalTransition outgoing,
			ProcessSemantics semantics) {
		super(PhaseCompCE.Condition.Condition32, semantics);
		this.acting = acting;
		this.reacting = reacting;
		this.outgoing = outgoing;
		generateSuggestions();
	}

	@Override
	public Constraint generateConstraint() {
		/**
		 * To give some names, let:
		 * acting:	 s1 --a!!--> s2
		 * outgoing: s2 --b??--> s3
		 * reacting: s4 --a??--> s5
		 * s5 has no b??
		 * The counter example encodes: 
		 * exist(acting) ^ exist(outgoing) ^ exist(reacting) ^ !exist(s5 --b??-->*)
		 * 
		 * */
		ExistenceChecker exChecker = semantics.getExistenceChecker();

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();
		// encoding
		conjuncts.add(exChecker.exists(acting, true, true, true));
		conjuncts.add(exChecker.exists(outgoing, false, true, false));
		conjuncts.add(exChecker.exists(reacting, true, true, true));
		Expression existsAct = exChecker.existsAction(reacting.getDest(), outgoing.getLocalAction()).getFirst(); //note this.
		conjuncts.add(new ExprOp(Op.NOT, existsAct));

		Expression conjunction = ExpressionBuilder.makeConjunction(conjuncts);
		Expression predicate = new ExprOp(ExprOp.Op.NOT, conjunction);
		return new Constraint(ExpressionSimplifier.simplify(predicate));
	}

//	@Override
//	public Constraint generateConstraint() {
//		/**
//		 * To give some names, let:
//		 * acting:	 s1 --a!!--> s2
//		 * outgoing: s2 --b??--> s3
//		 * reacting: s4 --a??--> s5
//		 * s5 has no b??
//		 * The counter example encodes: 
//		 * exist(acting) ^ exist(outgoing) ^ exist(reacting) ^ !exist(s5 --b??-->*)
//		 * 
//		 *   revised: !exist(s5 --b??-->*) this is always FALSE! because we are working w.r.t. current interpretation.
//		 *  existence conditions in general are, of course, different. Generally, do not build "fake" transitions and ask 
//		 *  if they exist, because we are working under a given interpretation, so they obviously don't
//		 * */
//
////		// Let's build the missing, needed transition.
////		LocalState stateWithMissingTr = reacting.getDest();
////
////		Handler handler = stateWithMissingTr.getLoc().getRecHandlerOverAction(outgoing.getLabel()); //  can this be
////																									// multiple
////																									// handlers? hmm.
////		boolean hasProposal = false; // check handler/actionType and fix.
////		
////		LocalTransition missing = LocalTransition.make(stateWithMissingTr, LocalState.ANYWHERE,
////				outgoing.getTransitionType(), outgoing.getLabel(), handler, outgoing.isEnvironmentTransition(),
////				hasProposal);
////
//
//		ArrayList<Expression> conjuncts = new ArrayList<>();
//		conjuncts.add(acting.getExistenceCondition(true));
//		conjuncts.add(outgoing.getExistenceCondition(false));
//		conjuncts.add(reacting.getExistenceCondition(true));
////		conjuncts.add(new ExprOp(ExprOp.Op.NOT, missing.getExistenceConditionWRTCurrentCompletion()));
//		Expression conjunction = ExpressionBuilder.makeConjunction(conjuncts);
//		Expression predicate = new ExprOp(ExprOp.Op.NOT, conjunction);
//		return new Constraint(predicate);
//	}

	@Override
	public String getDescription() {
		MyStringBuilder sb = new MyStringBuilder();
		sb.appn("CONDITION 3.2");
		sb.appn("Acting transition " + acting.toString() + " ");
		sb.appn("ends in a state with a reacting transition " + outgoing + ", ");
		sb.appn("but reacting transition " + reacting.toString() + " ");
		sb.appn("ends in a state which cannot reach such a reacting transition "
				+ "via an internal (and environment) path.");
		sb.appn("Short version(3.2): " + reacting.getDest().toSmallString() + " needs a reacting transition on "
				+ outgoing.getLabel() + " (just like " + acting.getDest().toSmallString() + ", in the same phase)");
		return sb.toString();

	}

	private void generateSuggestions() {

		// optimization: any time you want to add s -->everywhere, add s-->s first.
		// suggestion 0: add t.src --> t.src
		LocalTransition tr0 = LocalTransition.make(acting.getSrc(), acting.getSrc(),
				LocalTransition.correspondingReceiveType(acting.getTransitionType()), acting.getLabel());
		Suggestion s0 = new Suggestion(UserAction.ADD, tr0);
		addSuggestion(s0);

		// suggestion 1: add a receive from reacting.dst to anywhere

		LocalTransition tr1 = LocalTransition.make(reacting.getDest(), LocalState.ANYWHERE,
				LocalTransition.correspondingReceiveType(acting.getTransitionType()), acting.getLabel());
		Suggestion s1 = new Suggestion(UserAction.ADD, tr1);
		addSuggestion(s1);

		// suggestion 2: add an internal/env transition from the reacting.dest to
		// to a destination in the source set of outgoing

		HashSet<LocalState> srcOfOutgoing = semantics.getSourceSet(outgoing.getLabel());

		for (LocalState st : srcOfOutgoing) {
			LocalTransition tr = LocalTransition.make(reacting.getSrc(), st, LocalAction.ActionType.INTERNAL, "");
			Suggestion s = new Suggestion(UserAction.ADD, tr);
			addSuggestion(s);
		}

		// suggestion 3: replace reacting with a transition that starts from the same
		// source and goes to a destination in the source set of outgoing

		for (LocalState st : srcOfOutgoing) {
			LocalTransition tr = LocalTransition.make(reacting.getSrc(), st, reacting.getTransitionType(),
					reacting.getLabel());
			Suggestion s = new Suggestion(UserAction.REPLACE, reacting, tr);
			addSuggestion(s);
		}

		/**
		 * Note: as there are many suggestions, we should rank then elsewhere
		 * */
		Ranker.rankSuggestionsAccordingToCondition(this, Condition.Condition32);
	}
}
