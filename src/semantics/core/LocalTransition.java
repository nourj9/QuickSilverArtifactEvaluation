package semantics.core;

import java.util.HashSet;
import java.util.LinkedHashSet;

import CEgeneralizations.ExistenceChecker;
import Holes.Hole;
import lang.events.Event;
import lang.events.EventPartitionCons;
import lang.events.EventValueCons;
import lang.expr.ExprConstant;
import lang.expr.ExprHole;
import lang.expr.ExprOp;
import lang.expr.Expression;
import lang.expr.ExpressionBuilder;
import lang.expr.Gatherer;
import lang.handler.Handler;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerSymbolic;
import lang.handler.HandlerValueCons;
import lang.stmts.StmtBlock;
import lang.type.TypeBool;
import lang.type.TypeCard;
import lang.type.TypeInt;
import lang.type.TypeLoc;
import semantics.core.LocalAction.ActionType;

public class LocalTransition {

	private ProcessSemantics semantics;

	private LocalState src;
	private LocalState dest;

	// EDIT: this will now include the three pieces below
	private LocalAction action;
	// private Type transitionType;
	// private String label;
	// private boolean isEnvTr;

	private Freeness freeness = Freeness.NotDetermindYet;
	private boolean unusable = false;
	private boolean isReset = false;
	private Handler handler; // the handler used to generate this transition
	private Actness actness;
	private boolean actnessDetermined = false;

	private HashSet<Hole> holes = new HashSet<Hole>();
	// classify them as well.
	private HashSet<Hole> updateHoles = new HashSet<Hole>();
	private HashSet<Hole> predicateHoles = new HashSet<Hole>();
	private HashSet<Hole> cardinalityHoles = new HashSet<Hole>();
	private HashSet<Hole> gotoHoles = new HashSet<Hole>();

	private boolean concrete = false; // true if there is handler with no holes, and false if there no handler, or
										// handler with holes!

	// private boolean existenceCondComputed = false; // for clarity.

	private Expression hasActionCond;
	private Expression srcStateCond;
	private Expression destBindingCond;

	public enum Freeness {
		Free, NonFree, NotDetermindYet
	}

	public enum Actness {
		Acting, Reacting, Internal, Crash
	}

	/*
	 * CONSTRUCTORS
	 */
	protected LocalTransition(LocalState src, LocalState dest, ActionType transitionType, String label, Handler handler,
			boolean isEnvTr) {

		if (src == null || dest == null) {
			System.err.println("Creating a local transition with a src and/or dest is null!");
			Thread.dumpStack();
			System.exit(-1);
		}

		this.src = src;
		this.dest = dest;
		this.handler = handler;
		this.action = LocalAction.make(label, transitionType, isEnvTr);

		// this.label = label;
		// this.transitionType = transitionType;
		// this.isEnvTr = isEnvTr;

	}

	public void setSemantics(ProcessSemantics semantics) {
		this.semantics = semantics;
	}

	public ProcessSemantics getSemantics() {
		return semantics;
	}

	public static ActionType correspondingReceiveType(ActionType type) {
		return LocalAction.correspondingReceiveType(type);
	}

	public enum arity {
		PAIRWISE, BROADCAST
	}

	public boolean isGloballySynchronizing() {
		return action.isGloballySynchronizing();
	}

	public boolean isActingGlobalTransition() {
		return isGloballySynchronizing() && isActingTransition();
	}

	public boolean isActingTransition() {
		determineActness();
		return actness == Actness.Acting;
	}

	public boolean isReactingTransition() {
		determineActness();
		return actness == Actness.Reacting;
	}

	public boolean isReactingGlobalTransition() {
		return isGloballySynchronizing() && isReactingTransition();
	}

	public void determineActness() {
		if (!actnessDetermined) {
			actnessDetermined = true;

			switch (action.getActionType()) {
			case INTERNAL:
				actness = Actness.Internal;
				break;
			case BROADCAST_RECV:
			case PAIRWISE_RECV:
			case PARTITION_CONS_LOSE:
				actness = Actness.Reacting;
				break;
			case BROADCAST_SEND:
			case PAIRWISE_SEND:
			case PARTITION_CONS_WIN:
				actness = Actness.Acting;
				break;
			case VALUE_CONS:
				/*
				 * revise: just because prop = win does not mean you were acting simply because
				 * you may not have a proposal to start with
				 */
				LocTransValueCons trAsValCons = (LocTransValueCons) this;
				if (!trAsValCons.hasProposal) {
					actness = Actness.Reacting;// you cannot be an acting tr without a proposal!
				} else {
					Event event = ((HandlerValueCons) handler).getEvent();

					String propVarName = ((EventValueCons) event).getProposalVar().getName();
					String winVarName = action.getLabel() + "_wval";

					VariableValuation srcSigma = src.getSigma();
					VariableValuation dstSigma = dest.getSigma();

					if (srcSigma.getValue(propVarName).equals(dstSigma.getValue(winVarName))) {
						actness = Actness.Acting;
					} else {
						actness = Actness.Reacting;
					}
				}
				break;
			case CRASH:
				actness = Actness.Crash;
				break;
			case UNKNOWN:
				break;
			default:
				break;
			}

		}

	}

	public boolean isPairwiseTransition() {
		return action.isPairwise();
	}

	public boolean isEnvironmentTransition() {
		return action.isEnvironment();
	}

	public LocalState getSrc() {
		return src;
	}

	public void setSrc(LocalState src) {
		this.src = src;
	}

	public LocalState getDest() {
		return dest;
	}

	public void setDest(LocalState dest) {
		this.dest = dest;
	}

	public ActionType getTransitionType() {
		return action.getActionType();
	}

//public void setTransitionType(Type transitionType) {
//		this.transitionType = transitionType;
//	}

	public String getLabel() {
		return action.getLabel();
	}

//	public void setLabel(String label) {
//		this.label = label;
//	}

//	public static Type typeFromHandler(HandlerPredicated h) {
//		if (h.isInternal()) {
//			return Type.INTERNAL;
//		}
//		if (h.isReceive()) {
//			EventAction ea = (EventAction) h.getEvent();
//			if (ea.getAction().isBr()) {
//				return Type.BROADCAST_RECV;
//			}
//			return Type.PAIRWISE_RECV;
//		}
//		if (h.isSend()) {
//			if (h.getSendAction().isBr()) {
//				return Type.BROADCAST_SEND;
//			}
//			return Type.PAIRWISE_SEND;
//		}
//		return null;
//	}

	// private static String punctuation(Type transitionType) {
	// return LocalAction.punctuation(transitionType);
	// }

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append(src.toSmallString()).append("\t--").append(action).append("-->\t").append(dest.toSmallString());
//		if (!hasHoles()) {
//			b.append("\t\tConcrete");
//		} else {
//			b.append("\t\tHoles: ");
//			for (Hole hole : holes) {
//				b.append(hole.getId() + " ");
//			}
//		}

		return b.toString();

	}

	public String detailedString() {

		StringBuilder b = new StringBuilder();

		b.append(src.toString()).append("\t--").append(action).append("-->\t").append(dest.toString());

		return b.toString();

	}

	public String getPunctuatedLabel() {
		return action.getPunctuatedLabel();
	}

//	public String getPunctuatedLabel(Type transitionType) {
//		return label + punctuation(transitionType);
//	}

	public boolean isSelfLoop() {
		return src.equals(dest);
	}

	public boolean isUnusable() {
		return unusable;
	}

	public void markUnusable() {
		unusable = true;
	}

	public boolean isFree() {
		return freeness.equals(Freeness.Free);
	}

	public Freeness getFreeness() {
		return freeness;
	}

	public void setFreeness(Freeness freeness) {
		this.freeness = freeness;
	}

	public boolean isReset() {
		return isReset;
	}

	public void markAsReset() {
		isReset = true;
	}

	public void markFree() {
		freeness = Freeness.Free;
	}

	public void markNonFree() {
		freeness = Freeness.NonFree;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof LocalTransition) {
			LocalTransition other = (LocalTransition) obj;
			return this.src.equals(other.src) && this.dest.equals(other.dest) && this.action.equals(other.action);
		}

		return false;
	}

	public boolean isInternalOrEnvPairwise() {
		return action.isInternalOrEnvPairwise();
	}

	@Override
	public int hashCode() {
		// if the default implementation is going to change, change the implementation
		// of ProcessSemantics.globallyChangeDestOfTrs where
		// the hashed transitions have been modified internally.generally look at
		// ProcessSemantics.collapseStateOneIntoTwo.
		return super.hashCode();
	}

	public boolean hasHoles() {
		return !holes.isEmpty();
	}

	public HashSet<Hole> getHoles() {
		return holes;
	}

	public boolean isReceive() {
		return action.isReceive();
	}

	public boolean isInternal() {
		return action.isInternal();
	}

	public boolean isAgreement() {
		return action.isAgreement();
	}

	public static LocalTransition make(//
			LocalState src, //
			LocalState dest, //
			ActionType transitionType, //
			String label, //
			Handler h, //
			boolean isEnv, //
			boolean hasProposal// for valueCons
	) {
		LocalTransition tr;
		switch (transitionType) {
		case BROADCAST_RECV:
			tr = new LocTransBroadcastReceive(src, dest, label, h, isEnv);
			break;
		case BROADCAST_SEND:
			tr = new LocTransBroadcastSend(src, dest, label, (HandlerPredicated) h, isEnv);
			break;
		case INTERNAL:
			tr = new LocTransInternal(src, dest, label, (HandlerPredicated) h, isEnv);
			break;
		case PAIRWISE_RECV:
			tr = new LocTransPairwiseReceive(src, dest, label, h, isEnv);
			break;
		case PAIRWISE_SEND:
			tr = new LocTransPairwiseSend(src, dest, label, h, isEnv);
			break;
		case PARTITION_CONS_LOSE:
			tr = new LocTransPartitionLose(src, dest, label, h, isEnv);
			break;
		case PARTITION_CONS_WIN:
			tr = new LocTransPartitionWin(src, dest, label, h, isEnv);
			break;
		case VALUE_CONS:
			tr = new LocTransValueCons(src, dest, label, h, isEnv, hasProposal);
			break;
		case CRASH:
			tr = new LocalTransCrash(src, dest, label);
			break;
		default:
			// should never happen.
			tr = null;
			break;
		}

		// this is annoying, but we need to check of the handler is Partition as it
		// generates two transitions. So we need to know which "body" to check for
		// holes.
		if (h != null) {
			HashSet<ExprHole> holesExpr;
			switch (transitionType) {
			case PARTITION_CONS_LOSE:

				StmtBlock loseBody = null;
				if (h instanceof HandlerPartitionCons) {
					loseBody = ((HandlerPartitionCons) h).getLoseBlock();
				} else if (h instanceof HandlerSymbolic) {
					loseBody = ((HandlerSymbolic) h).getBody();
				}

				holesExpr = new Gatherer<ExprHole>(ExprHole.class).find(loseBody);
				// collect from predicates if we ever support that for consensus
				break;
			case PARTITION_CONS_WIN:
				holesExpr = new Gatherer<ExprHole>(ExprHole.class).find(((HandlerPartitionCons) h).getWinBlock());
				// collect from predicates if we ever support that for consensus
				break;
			default:
				holesExpr = new Gatherer<ExprHole>(ExprHole.class).find(h); // this also collects from predicates
				break;
			}

			for (ExprHole exprHole : holesExpr) {
				Hole hole = exprHole.getHole();
				tr.holes.add(hole);
				// also, classify them
				lang.type.Type t = hole.getType();
				if (t instanceof TypeInt) {
					tr.updateHoles.add(hole);
				} else if (t instanceof TypeCard) {
					tr.cardinalityHoles.add(hole);
				} else if (t instanceof TypeBool) {
					tr.predicateHoles.add(hole);
				} else if (t instanceof TypeLoc) {
					tr.gotoHoles.add(hole);
				} else {
					System.err.println("looks like a new type of holes is being supported, "
							+ "please classify it. Unknown type: " + t);
					Thread.dumpStack();
					System.exit(-1);
				}
			}

			// note that, if the handler is null, concrete will still be false.
			if (!tr.hasHoles()) {
				tr.concrete = true;
			}
		}
		return tr;
	}

	// no handler, not env, and has proposal if value cons
	public static LocalTransition make(LocalState src, LocalState dest, ActionType transitionType, String label) {
		return make(src, dest, transitionType, label, null, false, true);
	}

	// inner classes

	public static class LocTransInternal extends LocalTransition {

		public LocTransInternal(LocalState src, LocalState dest, String label, HandlerPredicated h, boolean isEnvTr) {
			super(src, dest, ActionType.INTERNAL, label, h, isEnvTr);
		}
	}

	public static class LocTransBroadcastSend extends LocalTransition {

		public LocTransBroadcastSend(LocalState src, LocalState dest, String label, HandlerPredicated h,
				boolean isEnvTr) {
			super(src, dest, ActionType.BROADCAST_SEND, label, h, isEnvTr);
		}

	}

	public static class LocTransBroadcastReceive extends LocalTransition {

		public LocTransBroadcastReceive(LocalState src, LocalState dest, String label, Handler handler, boolean isEnv) {
			super(src, dest, ActionType.BROADCAST_RECV, label, handler, isEnv);
		}
	}

	public static class LocTransPairwiseSend extends LocalTransition {

		public LocTransPairwiseSend(LocalState src, LocalState dest, String label, Handler handler, boolean isEnv) {
			super(src, dest, ActionType.PAIRWISE_SEND, label, handler, isEnv);
		}
	}

	public static class LocTransPairwiseReceive extends LocalTransition {

		public LocTransPairwiseReceive(LocalState src, LocalState dest, String label, Handler handler, boolean isEnv) {
			super(src, dest, ActionType.PAIRWISE_RECV, label, handler, isEnv);

		}
	}

	public static class LocTransPartitionWin extends LocalTransition {

		public LocTransPartitionWin(LocalState src, LocalState dest, String label, Handler handler, boolean isEnv) {
			super(src, dest, ActionType.PARTITION_CONS_WIN, label, handler, isEnv);
		}
	}

	public static class LocTransPartitionLose extends LocalTransition {
		public LocTransPartitionLose(LocalState src, LocalState dest, String label, Handler handler, boolean isEnv) {
			super(src, dest, ActionType.PARTITION_CONS_LOSE, label, handler, isEnv);
		}
	}

	public static class LocTransValueCons extends LocalTransition {
		private boolean hasProposal;

		public LocTransValueCons(LocalState src, LocalState dest, String label, Handler handler, boolean isEnv,
				boolean hasProposal) {
			super(src, dest, ActionType.VALUE_CONS, label, handler, isEnv);
			this.hasProposal = hasProposal;
		}

		public boolean hasProposal() {
			return hasProposal;
		}
	}

	public static class LocalTransCrash extends LocalTransition {
//		private boolean hasProposal;

		public LocalTransCrash(LocalState src, LocalState dest, String label) {
			super(src, dest, ActionType.CRASH, label, null, false);
//			this.hasProposal = hasProposal;
		}

//		public boolean hasProposal() {
//			return hasProposal;
//		}
	}

	public boolean isSend() {
		return action.isSend();
	}

	public String graphvizString() {
		StringBuilder b = new StringBuilder();

		b.append(src.graphvizString());
		b.append(" -> ").append(dest.graphvizString());
		b.append(" [ label = \"");
		b.append(action);
		b.append("\" ];");

		return b.toString();
	}

	/**
	 * This function return true if the transition is a broadcast send, or a parition cons win with
	 * cardinality = 1
	 * */
	public boolean isSingleTransition() {

		if (action.getActionType() == ActionType.BROADCAST_SEND)
			return true;

		if (action.getActionType() == ActionType.PARTITION_CONS_WIN) {

			EventPartitionCons event = ((HandlerPartitionCons) handler).getEvent();

			ExprConstant one = new ExprConstant("1", event.getCardinality().getType());
			Expression card = event.getCardinality();
			ExprOp cardEqOne = new ExprOp(ExprOp.Op.EQ, card, one);
			if ((boolean) cardEqOne.eval(null)) {
				return true;
			}
		}

		return false;

	}

	public Handler getHandler() {
		return handler;
	}

//	public void setActness(Actness actness) {
//		this.actness = actness;
//	}

	public Actness getActness() {
		determineActness();
		return actness;
	}

	public boolean hasProposal() {
		if (this instanceof LocTransValueCons) {
			LocTransValueCons trAsValCons = (LocTransValueCons) this;
			return trAsValCons.hasProposal;
		}

		// other types of transitions do not have proposals.
		return false;
	}

	public String getHolesIdsString() {
		StringBuilder b = new StringBuilder();
		for (Hole hole : holes) {
			b.append(hole.getId() + " ");
		}
		return b.toString();
	}

	// if the transition has holes h1 and h2, this returns (h1 = x and h2 = y) where
	// x and y are the current interpretation.
	public Expression getHoleEqCompPredicate(HashSet<Hole> holesToUse) {

		Expression conjunction = TypeBool.T().makeTrue();
		for (Hole hole : holesToUse) {
			// need a nice way to get from
			// synthesis? currently: through holes.
			// function to a hole.
//				conjunction = new ExprOp(ExprOp.Op.AND, conjunction,
//						hole.getSynthsisFunction().makeFunEqCompExpression());

			conjunction = ExpressionBuilder.makeConjunction(conjunction,
					hole.getSynthsisFunction().makeFunEqCompExpression());

		}
		return conjunction;
	}

//	public Expression getExistenceCondition() {
//		
//		//compute it if not there
//		if(existenceCond==null) {
//			computeExistenceConditions();
//		}
//		
//		return existenceCond;
//	}

//	public void setExistenceCond(Expression existenceCond) {
//		this.existenceCond = existenceCond;
//	}
	public Expression getExistenceCondition(boolean includeSrcExistence, boolean includeActionExistence,
			boolean includeDestExistence) {
		// never been called before? call it once and store things
		// in the transition for later!
		ExistenceChecker exChecker = semantics.getExistenceChecker();

		// this is an optimization, we couldv'e computed them every time.
		// edit, do a selective computation as needed.
//		if (!existenceCondComputed) {
//			// compute the "parts"
//			srcStateCond = exChecker.exists(src, action, dest, true, false, false);
//			hasActionCond = exChecker.exists(src, action, dest, false, true, false);
//			destBindingCond = exChecker.exists(src, action, dest, false, false, true);
//			existenceCondComputed = true;
//		}

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		if (includeSrcExistence) {

			if (srcStateCond == null) {
				srcStateCond = exChecker.exists(src, action, dest, true, false, false);
			}

			conjuncts.add(srcStateCond);
		}
		if (includeActionExistence) {
			if (hasActionCond == null) {
				hasActionCond = exChecker.exists(src, action, dest, false, true, false);
			}
			conjuncts.add(hasActionCond);
		}
		if (includeDestExistence) {
			if (destBindingCond == null) {
				destBindingCond = exChecker.exists(src, action, dest, false, false, true);
			}
			conjuncts.add(destBindingCond);
		}

		return ExpressionBuilder.makeConjunction(conjuncts);

	}
//	// the condition without src state requirement included
//	public Expression getExistenceConditionWithoutSrcState() {
//		return existenceCondWithoutSrcState;
//	}
//
//	public void setExistenceCondWithoutSrcState(Expression existenceCondWithoutSrcState) {
//		this.existenceCondWithoutSrcState = existenceCondWithoutSrcState;
//	}
//
//	// the condition without src state requirement included and without it leading
//	// to the current dest state
//	public Expression getExistenceCondWithoutSrcStateAndDestStateBinding() {
//		return existenceCondWithoutSrcStateAndDestStateBinding;
//	}
//
//	public void setExistenceCondWithoutSrcStateAndDestStateBinding(
//			Expression existenceCondWithoutSrcStateAndDestStateBinding) {
//		this.existenceCondWithoutSrcStateAndDestStateBinding = existenceCondWithoutSrcStateAndDestStateBinding;
//	}

	public boolean isConcrete() {
		return concrete;
	}

	public HashSet<Hole> getPredicateHoles() {
		return predicateHoles;
	}

	public HashSet<Hole> getCardinalityHoles() {
		return cardinalityHoles;
	}

	public HashSet<Hole> getUpdateHoles() {
		return updateHoles;
	}

	public HashSet<Hole> getGotoHoles() {
		return gotoHoles;
	}

	public LocalAction getLocalAction() {
		return action;
	}

	public boolean isCrash() {
		return action.isCrash();
	}

}
