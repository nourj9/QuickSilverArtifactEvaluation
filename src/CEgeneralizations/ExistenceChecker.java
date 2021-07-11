package CEgeneralizations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import lang.expr.ExprConstant;
import lang.expr.ExprHole;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.expr.ExpressionBuilder;
import lang.expr.Gatherer;
import lang.expr.ExprOp.Op;
import lang.handler.Handler;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerSymbolic;
import lang.handler.HandlerValueCons;
import lang.stmts.Pair;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtBroadcast;
import lang.stmts.StmtGoto;
import lang.stmts.StmtSend;
import lang.type.TypeBool;
import lang.type.TypeLoc;
import semantics.analysis.CorePhase;
import semantics.analysis.MergedPhase;
import semantics.analysis.Phase;
import semantics.analysis.PhaseAnalysis;
import semantics.core.LocalAction;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.Path;
import semantics.core.PathFinder;
import semantics.core.ProcessSemantics;
import semantics.core.Trail;

public class ExistenceChecker {

	private ProcessSemantics ps;
	PathFinder pathFinder;

	public ExistenceChecker(ProcessSemantics ps) {
		this.ps = ps;
		this.pathFinder = ps.getPathFinder();

		// backlink to ps
		ps.setExistenceChecker(this);

		// For synthesis, mark states as tentative vs not tentative:
		decideTentativnessOfStates(ps);
	}

	/** 
	 * Mark states as tentative vs not tentative:
	 * 1. initially all states are tentative.
	 * 2. initial state is not tentative
	 * 3. a state that is reachable from a non-tentative state by a concrete
	 *    transition is non-tentative.
	 * 4. otherwise, the state stays tentative
	*/
	public void decideTentativnessOfStates(ProcessSemantics ps) {
		// mark initial state as non-tentative.
		ps.getInitialState().markConcrete();

		HashSet<LocalState> visited = new HashSet<LocalState>();
		ArrayList<LocalState> queue = new ArrayList<LocalState>();
		queue.add(ps.getInitialState());
		while (!queue.isEmpty()) {
			LocalState currentState = queue.remove(0);
			visited.add(currentState);

			for (LocalTransition tr : currentState.getOutgoingTransitions()) {

				LocalState nextState = tr.getDest();
				if (currentState.isConcrete() && tr.isConcrete()) {
					nextState.markConcrete();
				}

				if (!visited.contains(nextState)) {
					queue.add(nextState);
				}
			}
		}
	}

	/** this works as follows:
	* - if a state is concrete, it depends on no holes to exist, so return true.
	* - if a state is tentative, then get all the possible paths to get to it and,
	* return a conjunction of all path's existence conditions.
	**/
	public Expression exists(LocalState s) {

		// does the state exist in the semantics?
		if (!ps.getStates().contains(s)) {
			System.err.println("Cannot check the existence of state " + "(" + s.toSmallString()
					+ ") which is not in the semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}
		// move logic to state for chasing the results.
		return s.getExistenceCondition();
	}

	/** The state does exist, and we only return condition for the action to exist.
	edit: we will return the handlers, if any.*/
	public Pair<Expression, HashSet<Handler>> existsAction(LocalState src, LocalAction action) {

		// could also skip this if need be
		if (!ps.getStates().contains(src)) {
			System.err.println("Cannot check the existence of state " + "(" + src.toSmallString()
					+ ") which is not in the semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}

		// get the handlers from this state over this action.
		HashSet<Handler> handlers = src.getLoc().getAllHandlersOverAction(action);
		HashSet<Handler> usedHandlers = new HashSet<Handler>();

		// build a disjunction of all predicates for the handlers.
		LinkedHashSet<Expression> disjuncts = new LinkedHashSet<Expression>();

		for (Handler handler : handlers) {

			if (handler instanceof HandlerPartitionCons || handler instanceof HandlerValueCons
					|| handler instanceof HandlerSymbolic) {
				// as of now, those have no predicate (i.e. predicate is always true) so just go
				// ahead and return true
				disjuncts.add(TypeBool.T().makeTrue());
				usedHandlers.add(handler);

			} else if (handler instanceof HandlerPredicated) {

				// get a hold of the predicate.
				HandlerPredicated h = (HandlerPredicated) handler;
				Expression pred = h.getPredicate();

				// collect all the holes in the predicate.
				HashSet<ExprHole> holesExpr = new Gatherer<ExprHole>(ExprHole.class).find(pred);

				// if the predicate is concrete, evaluate it and return the result
				if (holesExpr.isEmpty()) {

					if ((boolean) pred.eval(src.getSigma())) {
						disjuncts.add(TypeBool.T().makeTrue());
						usedHandlers.add(handler);

					} else {
						disjuncts.add(TypeBool.T().makeFalse());
						usedHandlers.add(handler);
					}

				} else { // pred has holes.
					/**  p,sigma
					 * p 	= (a==b) and (c)
					 * p' 	= h1(a,b) and h3(a,b,c)   map[a->1,b->2,c->3]
					 * p'' 	= h1(1,2) and h3(1,2,3)
					 * 
					 * 
					 * partial eval: same things, but for exprHole, it makes it a function application over the given sigma.
					 * 
					 * p 	= (a==b) and (c) or [[(d<1)]]   (terminal/not an op)
					 * 
					 * */

					// h(a,b) --> h(1,2)
					// otherwise, we need a way to substitute holes with their function applications
					// over the concrete values from the current sigma.
					Expression elevatedPred = pred.elevateHolesAndPartialEval(src.getSigma());
					disjuncts.add(elevatedPred);
					usedHandlers.add(handler);
				}
			} else {
				System.err.println("Unexpeceted handler type. Handlder: " + handler);
				Thread.dumpStack();
				System.exit(-1);
				return null;
			}
		}

		Expression disjunction = ExpressionBuilder.makeDisjunction(disjuncts);
		return new Pair<Expression, HashSet<Handler>>(disjunction, usedHandlers);
	}

	/**
	 * the "pro" version, if you know what you're doing.
	 * */
	public Expression exists(LocalState src, LocalAction action, LocalState dst, boolean includeSrcExistence,
			boolean includeActionExistence, boolean includeDestExistence) {
		LinkedHashSet<Expression> existenceConjuncts = new LinkedHashSet<Expression>();

		if (includeSrcExistence) {
			// the source state exists
			existenceConjuncts.add(exists(src));
		}

		Pair<Expression, HashSet<Handler>> hasActPair = existsAction(src, action);
		if (includeActionExistence) {
			// the action exists
			existenceConjuncts.add(hasActPair.getFirst());
		}

		if (includeDestExistence) {
			// the updates lead to the right state!
			HashSet<Handler> handlers = hasActPair.getSecond();

			LinkedHashSet<Expression> bodyDisjuncts = new LinkedHashSet<Expression>();
			for (Handler handler : handlers) {
				for (StmtBlock body : handler.getBodies()) {

					// compute the condition for this body to end in the right state. Which is the
					// case when: the goto statement goes to the dest location, and the updates over
					// the current sigma end in the destination sigma.
					LinkedHashSet<Expression> currentBodyConjuncts = new LinkedHashSet<Expression>();

					// let us handle the updates, one by one. Note: we allow assign stmts and gotos
					// for now.
					for (Statement stmt : body.getStmts()) {
						if (stmt instanceof StmtAssign) {
							StmtAssign assignStmt = (StmtAssign) stmt;
							HashSet<ExprHole> holesInStmt = new Gatherer<ExprHole>(ExprHole.class).find(assignStmt);

							Expression rhs = assignStmt.getRHS();
							ExprVar lhs = (ExprVar) assignStmt.getLHS();

							if (holesInStmt.isEmpty()) { // concrete, then check if it updates the value in a good way.

								// src_sigma(rhs) = dst_sigma(lhs)
								// TODO check this if statement...
								if (lhs.eval(dst.getSigma()).toString().equals(rhs.eval(src.getSigma()).toString())) {
									currentBodyConjuncts.add(TypeBool.T().makeTrue());
								} else {
									currentBodyConjuncts.add(TypeBool.T().makeFalse());
								}

							} else { // has holes? constrain it to be completed to yield dest sigma

								// get the elevated rhs w.r.t. src_sigma
								Expression elevatedRHSExpr = rhs.elevateHolesAndPartialEval(src.getSigma());

								// create a constraint to force it to be the value of the lhs in the target
								// sigma.
								ExprConstant valOfLHSinDestSigma = new ExprConstant(
										String.valueOf(lhs.eval(dst.getSigma())), lhs.getType());
								ExprOp holeEqDestVal = new ExprOp(Op.EQ, elevatedRHSExpr, valOfLHSinDestSigma);
								currentBodyConjuncts.add(holeEqDestVal);
							}

						} else if (stmt instanceof StmtGoto) {

							// let us start with the location.
							StmtGoto gotoStmt = (StmtGoto) stmt;
							HashSet<ExprHole> holesInGoto = new Gatherer<ExprHole>(ExprHole.class).find(gotoStmt);

							if (holesInGoto.isEmpty()) { // a concrete statement
								if (gotoStmt.getTargetLoc().equals(dst.getLoc())) { // ends in the dest loc? return true
									currentBodyConjuncts.add(TypeBool.T().makeTrue());
								} else { // return false.
									currentBodyConjuncts.add(TypeBool.T().makeFalse());
								}
							} else { // the target is a hole? then make sure it completes to that state!
								ExprHole targetLocHole = (ExprHole) gotoStmt.getTargetLocExpr();
								// elevate it
								Expression elevatedTargetHoleExpr = targetLocHole
										.elevateHolesAndPartialEval(src.getSigma());
								// create a constraint to force it to be the target loc
								ExprOp holeEqDestLoc = new ExprOp(Op.EQ, elevatedTargetHoleExpr,
										new ExprConstant(dst.getLoc().getName(), TypeLoc.T()));
								currentBodyConjuncts.add(holeEqDestLoc);
							}
						} else if (stmt instanceof StmtBroadcast || stmt instanceof StmtSend) {
							// nothing
						} else {
							System.err.println("non-assign, non-goto stmt update in existenceChecker: " + stmt);
						}
					}

					// 3 - finally, add this body requirement.
					Expression currentBodyReqs = ExpressionBuilder.makeConjunction(currentBodyConjuncts);
					bodyDisjuncts.add(currentBodyReqs);
				}

			}

			// one of the handlers over this action leads to the required state!
			existenceConjuncts.add(ExpressionBuilder.makeDisjunction(bodyDisjuncts));
		}
		return ExpressionBuilder.makeConjunction(existenceConjuncts);
	}

	// helper versions.
	public Expression exists(LocalState src, LocalAction action) {
		return exists(src, action, null, true, true, false);
	}

	public Expression exists(LocalState src, LocalAction action, LocalState dest) {
		return exists(src, action, dest, true, true, true);
	}

	public Expression exists(LocalTransition trans, boolean includeSrcExistence, boolean includeActionExistence,
			boolean includeDestExistence) {

		if (!ps.getTransitions().contains(trans)) {
			System.err.println("Cannot check the existence of local transition " + "(" + trans
					+ ") which is not in the semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}

		// delegate the logic to LocalTransition,
		// so it can save things for performance reasons.
		return trans.getExistenceCondition(includeSrcExistence, includeActionExistence, includeDestExistence);
	}

	// more helpers
	public Expression exists(LocalTransition trans) {

		if (!ps.getTransitions().contains(trans)) {
			System.err.println("Cannot check the existence of local transition " + "(" + trans
					+ ") which is not in the semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}
		return exists(trans, true, true, true);
	}

	public Expression exists(Path path) {
		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		// add the initial state existence condition.
		conjuncts.add(exists(path.getInitialState()));

		for (LocalTransition tr : path.getTransitions()) {

			// the condition for the transition to exist over
			// this action, and that it leads to where to leads.
			// Note: we do not include the src existence because we did it for the initial
			// state, and the fact that we require an action to exist and that it leads to
			// the right state means that we do not need to ask for any state to exist (we
			// already are guaranteeing it! something like induction)
			Expression exActToDst = exists(tr, false, true, true);
			conjuncts.add(exActToDst);
		}

		return ExpressionBuilder.makeConjunction(conjuncts);
	}

//	/**
//	 * assume src and action exist, only need condition for dest to exist.
//	 * */
//	public Object existsDest(LocalState src, LocalAction action, LocalState dest) {
//		//  Auto-generated method stub
//		return null;
//	}

	public Expression samePhase(LocalState s1, LocalState s2) {

		// get a hold of all phases that contain the states
		PhaseAnalysis pa = ps.getPhaseAnalysis();
		HashSet<Phase> enclosingPhases = pa.getPhasesContaining(s1, s2);

		// for each phase, calculate the root cause for these two states to be in this
		// phase together. Create a disjunction containing all the phases
		LinkedHashSet<Expression> phasesDisjuncts = new LinkedHashSet<Expression>();
		for (Phase phase : enclosingPhases) {
			Expression phaseConstraint = extractConstraintForPhaseToContainStates(phase, s1, s2);
			phasesDisjuncts.add(phaseConstraint);
		}
		return ExpressionBuilder.makeDisjunction(phasesDisjuncts);
	}

	/**
	 * This function works as follows. First, we check the nature of the phase:
	 * 
	 * 1. if the phase is a core src phase and
	 * 	  
	 *    a. both states are in the core states of the phase. Then, we return the following constraint:
	 * 		   exists(s1, phase.label, null, true, true, false) and 
	 *         exists(s2, phase.label, null, true, true, false)
	 *       Essentially, both states exist and have an action with that label.
	 *    
	 *    b. one state, say s1, is core and one state, say s2, is added through the expansion s3, s4,...,s2 
	 *       where s3 is a core state and s4,... are expansions as well. Then, we add this constraint:
	 *         exists(s1, phase.label, null, true, true, false) and
	 *         exists(s3, phase.label, null, true, true, false) and
	 *         existsTrail(s3, s4, ..., s2) 
	 *       Note that the existsTrail function is like a path one, just that transitions can be in any direction.
	 *       Also, note that this call should ensure s2 exists,       		  
	 * 
	 *    c. both states s1 and s2 are added through expansions e1: s3,...,s1 and e2: s4,...,s2 respectively. 
	 *       Then, we add this constraint:
	 *         exists(s3, phase.label, null, true, true, false) and
	 *         exists(s4, phase.label, null, true, true, false) and
	 *         existsTrail(e1) and
	 *         existsTrail(e2)  
	 *       Note that the existsTrail functions should ensure s1 and s2 exist.       		  
	 * 
	 * 2. if the phase is a core singleton, then we use a similar concept to the one up there, distinguishing 
	 *    two possible scenarios: one is core and one is an expansion, and, both are expansions.
	 * 
	 * 3. if the phase is a core dst phase and
	 * 	  
	 *    a. both states are in the core states of the phase. Then, we return the following constraint. Let s3 be any 
	 *    	 state such that tr1: s3 --phase.label--> s1 and s4 be any state such that tr2: s4 --phase.label--> s2 
	 * 		   exists(tr1, true, true, true) and 
	 *         exists(tr2, true, true, true) and
	 *       Essentially, both states exist and are transitioned to with an action with that label.
	 *    
	 *    b. one state, say s1, is core and one state, say s2, is added through the expansion s3, s4,...,s2 
	 *       where s3 is a core state and s4,... are expansions as well. Then, we add this constraint. Let s5 be any 
	 *    	 state such that tr1: s5 --phase.label--> s1 and s6 be any state such that tr2: s6 --phase.label--> s3
	 * 		   exists(tr1, true, true, true) and 
	 *         exists(tr2, true, true, true) and
	 *         existsTrail(s3, s4, ..., s2) 
	 *       Essentially, both core states exist and are transitioned to with an action with that label, and the
	 *       extension is preserved. Also, note that this call should ensure s2 exists,       		  
	 * 
	 *    c. both states s1 and s2 are added through expansions e1: s3,...,s1 and e2: s4,...,s2 respectively. 
	 *       Then, we add this constraint. Let s5 be any state such that tr1: s5 --phase.label--> s1 and s6 be 
	 *       any state such that tr2: s6 --phase.label--> s3.
	 * 		   exists(tr1, true, true, true) and 
	 *         exists(tr2, true, true, true) and
	 *         existsTrail(e1) and
	 *         existsTrail(e2)  
	 *       Note that the existsTrail functions should ensure s1 and s2 exist.       		  
	 * 
	 * 4. if the phase is a merged one and
	 * 
	 *    a. both s1 and s2 are in the same core enclosing phase, then we add a constraint as above.
	 *    
	 *    b. states s1 and s2 are in different core enclosing phases, then we add the following constraint.
	 *       Let p1 and p2 be the two phases containing s1 and s2. Let the merger transition be mtr: s3 ----> s4.
	 *       Then let us add the following constraint: 
	 * 		   exists(mtr, true, true, true) and 
	 *         extractConstraintForPhaseToContainStates(p1,s1,s3) and
	 *         extractConstraintForPhaseToContainStates(p2,s2,s4)  
	 * */
	private Expression extractConstraintForPhaseToContainStates(Phase phase, LocalState s1, LocalState s2) {

		switch (phase.getType()) {
		case src:
			CorePhase srcPhase = (CorePhase) phase;
			return extractConstraintForPhaseToContainStates_src(srcPhase, s1, s2);
		case singeleton:
			CorePhase singeletonPhase = (CorePhase) phase;
			return extractConstraintForPhaseToContainStates_singeleton(singeletonPhase, s1, s2);
		case dst:
			CorePhase destPhase = (CorePhase) phase;
			return extractConstraintForPhaseToContainStates_dest(destPhase, s1, s2);
		case merged:
			MergedPhase mergedPhase = (MergedPhase) phase;
			return extractConstraintForPhaseToContainStates_merged(mergedPhase, s1, s2);
		default:
			return null;
		}
	}

	private Expression extractConstraintForPhaseToContainStates_src(CorePhase srcPhase, LocalState s1, LocalState s2) {

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		Trail trailToS1 = srcPhase.buildTrailToOrNullIfStateIsCore(s1);
		Trail trailToS2 = srcPhase.buildTrailToOrNullIfStateIsCore(s2);

		if (trailToS1 == null) { // s1 is core
			conjuncts.add(exists(s1));
			conjuncts.add(existsLabelFrom(s1, srcPhase.getLabel()).getFirst());
		} else { // s1 is extended
			conjuncts.add(exists(trailToS1));
			conjuncts.add(existsLabelFrom(trailToS1.getInitialCoreState(), srcPhase.getLabel()).getFirst());
		}

		if (trailToS2 == null) { // s2 is core
			conjuncts.add(exists(s2));
			conjuncts.add(existsLabelFrom(s2, srcPhase.getLabel()).getFirst());
		} else { // s2 is extended
			conjuncts.add(exists(trailToS2));
			conjuncts.add(existsLabelFrom(trailToS2.getInitialCoreState(), srcPhase.getLabel()).getFirst());
		}

		return ExpressionBuilder.makeConjunction(conjuncts);
	}

	private Expression extractConstraintForPhaseToContainStates_singeleton(CorePhase singeletonPhase, LocalState s1,
			LocalState s2) {

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		Trail trailToS1 = singeletonPhase.buildTrailToOrNullIfStateIsCore(s1);
		Trail trailToS2 = singeletonPhase.buildTrailToOrNullIfStateIsCore(s2);

		if (trailToS1 == null) { // s1 is core
			conjuncts.add(exists(s1));
		} else { // s1 is extended
			conjuncts.add(exists(trailToS1));
		}

		if (trailToS2 == null) { // s2 is core
			conjuncts.add(exists(s2));
		} else { // s2 is extended
			conjuncts.add(exists(trailToS2));
		}

		return ExpressionBuilder.makeConjunction(conjuncts);
	}

	private Expression extractConstraintForPhaseToContainStates_dest(CorePhase destPhase, LocalState s1,
			LocalState s2) {

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		Trail trailToS1 = destPhase.buildTrailToOrNullIfStateIsCore(s1);
		Trail trailToS2 = destPhase.buildTrailToOrNullIfStateIsCore(s2);

		if (trailToS1 == null) { // s1 is core
			conjuncts.add(existsLabelTo(s1, destPhase.getLabel()).getFirst());
		} else { // s1 is extended
			conjuncts.add(exists(trailToS1));
			conjuncts.add(existsLabelTo(trailToS1.getInitialCoreState(), destPhase.getLabel()).getFirst());
		}

		if (trailToS2 == null) { // s2 is core
			conjuncts.add(existsLabelTo(s2, destPhase.getLabel()).getFirst());
		} else { // s2 is extended
			conjuncts.add(exists(trailToS2));
			conjuncts.add(existsLabelTo(trailToS2.getInitialCoreState(), destPhase.getLabel()).getFirst());
		}

		return ExpressionBuilder.makeConjunction(conjuncts);
	}

	/**
	 *    a. both s1 and s2 are in the same core enclosing phase, then we add a constraint as above.
	 * 
	 *    b. states s1 and s2 are in different core enclosing phases, then we add the following constraint.
	 *       Let p1 and p2 be the two phases containing s1 and s2. Let the merger transition be mtr: s3 ----> s4.
	 *       Then let us add the following constraint: 
	 * 		   exists(mtr, true, true, true) and 
	 *         extractConstraintForPhaseToContainStates(p1,s1,s3) and
	 *         extractConstraintForPhaseToContainStates(p2,s2,s4)  
	 * */
	private Expression extractConstraintForPhaseToContainStates_merged(MergedPhase mergedPhase, LocalState s1,
			LocalState s2) {

		LinkedHashSet<Expression> disjuncts = new LinkedHashSet<Expression>();
		HashSet<Phase> enclosedPhases = mergedPhase.getEnclosedPhases();
		LocalTransition mergerTr = mergedPhase.getMergerTr();
		// case a: get all core phases that contain s1 and s2 and build a constraint for
		// each
		for (Phase enclosedPhase : enclosedPhases) {
			if (enclosedPhase.contains(s1) && enclosedPhase.contains(s2)) {
				disjuncts.add(extractConstraintForPhaseToContainStates(enclosedPhase, s1, s2));
			}
		}

		// case b: get all the pair of phases such that one contains a state but not the
		// other.
		for (Phase phase1 : enclosedPhases) {
			for (Phase phase2 : enclosedPhases) {

				if (phase1 == phase2) {
					continue;
				}

				if (phase1.contains(s1) && !phase1.contains(s2) // s1 in phase1 only
						&& !phase2.contains(s1) && phase2.contains(s2)) { // and s2 in phase2 only

					Expression part1 = extractConstraintForPhaseToContainStates(phase1, s1, mergerTr.getSrc());
					Expression part2 = extractConstraintForPhaseToContainStates(phase2, s2, mergerTr.getDest());

					disjuncts.add(ExpressionBuilder.makeConjunction(part1, part2));
				}

				if (phase1.contains(s2) && !phase1.contains(s1) // s2 in phase1 only
						&& !phase2.contains(s2) && phase2.contains(s1)) { // and s1 in phase2 only

					Expression part1 = extractConstraintForPhaseToContainStates(phase1, s2, mergerTr.getSrc());
					Expression part2 = extractConstraintForPhaseToContainStates(phase2, s1, mergerTr.getDest());

					disjuncts.add(ExpressionBuilder.makeConjunction(part1, part2));
				}

			}
		}

		return ExpressionBuilder.makeDisjunction(disjuncts);

	}

	/**
	 * Let the trail be as follows: a --> b --> c <-- d --> e <-- f. Since this is not really a path, we need to be careful
	 * how to generate these constraints, here is how it is done now. Let neededStates be any src state of a transition that 
	 * is NOT a destination of any transition in trail. E.g., states a, f, and d.
	 * These, we do exists() normally. We then just do exists (tr,false,true,true): the action and dest are required only.
	 * */
	private Expression exists(Trail trail) {

		LinkedHashSet<Expression> conjuncts = new LinkedHashSet<Expression>();

		HashSet<LocalState> neededStates = new HashSet<LocalState>();

		for (LocalState ls : trail.getStates()) {

			// skip if it's a dest for some tr on trail
			if (trail.stateIsDestOfSomeTrInTrail(ls)) {
				continue;
			}

			// otherwise add it
			neededStates.add(ls);
		}

		// require these to exist
		for (LocalState ls : neededStates) {
			conjuncts.add(exists(ls));
		}

		// require the "has action" and "ends in" for all.
		for (LocalTransition tr : trail.getTransitions()) {
			Expression exActToDst = exists(tr, false, true, true);
			conjuncts.add(exActToDst);
		}

		return ExpressionBuilder.makeConjunction(conjuncts);
	}

	private Pair<Expression, HashSet<Handler>> existsLabelTo(LocalState dst, String actLabel) {

		// could also skip this if need be
		if (!ps.getStates().contains(dst)) {
			System.err.println("Cannot check the existence of state " + "(" + dst.toSmallString()
					+ ") which is not in the semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}

		// get the incoming trans to this state with this label.

		HashSet<LocalTransition> incomingTrsWithThisLabel = new HashSet<LocalTransition>();

		for (LocalTransition incomingTr : dst.getIncomingTransitions()) {
			if (incomingTr.getLocalAction().getLabel().equals(actLabel)) {
				incomingTrsWithThisLabel.add(incomingTr);
			}
		}

		HashSet<Handler> usedHandlers = new HashSet<Handler>();
		LinkedHashSet<Expression> disjuncts = new LinkedHashSet<Expression>();

		// build a disjunct over the existence of all these transitions
		for (LocalTransition tr : incomingTrsWithThisLabel) {
			Expression exitsTr = exists(tr, true, true, true);
			disjuncts.add(exitsTr);
			usedHandlers.add(tr.getHandler());
		}

		Expression disjunction = ExpressionBuilder.makeDisjunction(disjuncts);
		return new Pair<Expression, HashSet<Handler>>(disjunction, usedHandlers);
	}

	// here, the idea is that we want to check local actions a!! and a??, not just
	// one of them (i.e. ignoring transition type.)
	private Pair<Expression, HashSet<Handler>> existsLabelFrom(LocalState src, String actLabel) {

		// could also skip this if need be
		if (!ps.getStates().contains(src)) {
			System.err.println("Cannot check the existence of state " + "(" + src.toSmallString()
					+ ") which is not in the semantics!");
			Thread.dumpStack();
			System.exit(-1);
		}

		// get all LocalActions associated with this label, and call the hasAction
		// function
		HashSet<LocalAction> actions = ps.getAssociatedLocalActions(actLabel);
		HashSet<Handler> usedHandlers = new HashSet<Handler>();
		LinkedHashSet<Expression> disjuncts = new LinkedHashSet<Expression>();

		for (LocalAction action : actions) {
			Pair<Expression, HashSet<Handler>> actInfo = existsAction(src, action);
			disjuncts.add(actInfo.getFirst());
			usedHandlers.addAll(actInfo.getSecond());
		}

		Expression disjunction = ExpressionBuilder.makeDisjunction(disjuncts);
		return new Pair<Expression, HashSet<Handler>>(disjunction, usedHandlers);

	}

	public Expression samePhase(Phase phase, LocalState s1, LocalState s2) {
		return extractConstraintForPhaseToContainStates(phase, s1, s2);
	}
}
