package lang.expr;

import java.util.HashSet;

import lang.handler.Handler;
import lang.handler.HandlerCrash;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerSymbolic;
import lang.handler.HandlerValueCons;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtBroadcast;
import lang.stmts.StmtGoto;
import lang.stmts.StmtIfThen;
import lang.stmts.StmtSend;
import lang.stmts.StmtSetIndexedAssign;
import lang.stmts.StmtSetUpdate;

public class Gatherer<T> {
	private Class<T> type;

	public Gatherer(Class<T> type) {
		this.type = type;
	}

	public HashSet<T> find(Expression expr) {
		HashSet<T> gatheredExprT = new HashSet<T>();

		if (expr != null) {
			visit(expr, gatheredExprT);
		}

		return gatheredExprT;
	}

	@SuppressWarnings("unchecked")
	private void visit(Expression expr, HashSet<T> gatheredExprT) {

		if (expr == null) {
			return;
		}

		// found one? add it
		if (type.isInstance(expr)) {
			gatheredExprT.add((T) expr);
		}

		// then recurse
		if (expr instanceof ExprVar) {
		} else if (expr instanceof ExprConstant) {
		} else if (expr instanceof ExprLoc) {
		} else if (expr instanceof ExprOp) {
			ExprOp opExpr = (ExprOp) expr;
			visit(opExpr.getArg1(), gatheredExprT);
			if (opExpr.getArg2() != null)
				visit(opExpr.getArg2(), gatheredExprT);
		} else if (expr instanceof ExprHole) {
			ExprHole holeExpr = (ExprHole) expr;
			visit(holeExpr.getHole().getCompletion(), gatheredExprT);
			// } else if (expr == null) {
			// System.err.println("Warning: trying to gather from a null expression,
			// ignoring..");
		} else {
			System.out.println("Unhandled expression in Gatherer: " + expr.toString());
		}
	}

	public HashSet<T> find(Handler handler) {
		HashSet<T> toRet = new HashSet<T>();
//		if (handler instanceof HandlerRegular) {
//			HandlerRegular currentH = (HandlerRegular) handler;
//			for (Statement stmt : currentH.getBody().getStmts()) {
//				toRet.addAll(find(stmt));
//			}
//		} else 
//
		if (handler == null) {
			System.err.println("Warning: collecting from a null handler");
			return toRet;
		} else if (handler instanceof HandlerPredicated) {
			HandlerPredicated currentH = (HandlerPredicated) handler;
			toRet.addAll(find(currentH.getPredicate()));
			for (Statement stmt : currentH.getBody().getStmts()) {
				toRet.addAll(find(stmt));
			}
		} else if (handler instanceof HandlerPartitionCons) {
			HandlerPartitionCons currentH = (HandlerPartitionCons) handler;
			toRet.addAll(find(currentH.getEvent().getParticipants()));
			toRet.addAll(find(currentH.getEvent().getCardinality()));
			for (Statement stmt : currentH.getWinBlock().getStmts()) {
				toRet.addAll(find(stmt));
			}
			for (Statement stmt : currentH.getLoseBlock().getStmts()) {
				toRet.addAll(find(stmt));
			}
		} else if (handler instanceof HandlerValueCons) {
			HandlerValueCons currentH = (HandlerValueCons) handler;
			toRet.addAll(find(currentH.getEvent().getParticipants()));
			toRet.addAll(find(currentH.getEvent().getCardinality()));
			toRet.addAll(find(currentH.getEvent().getProposalVar()));
			for (Statement stmt : currentH.getBody().getStmts()) {
				toRet.addAll(find(stmt));
			}
		} else if (handler instanceof HandlerSymbolic) {

		} else if (handler instanceof HandlerCrash) {
			// nothing for now
		} else {
			System.err.println("Warning: passing an unsupported handler to gatherer: " + handler.toString());
		}
		return toRet;
	}

	public HashSet<T> find(Statement stmt) {
		HashSet<T> toRet = new HashSet<T>();
		if (stmt instanceof StmtSend) {
			StmtSend currentS = (StmtSend) stmt;
			toRet.addAll(find(currentS.getTo()));
			toRet.addAll(find(currentS.getAct().getValue()));

		} else if (stmt instanceof StmtSetUpdate) {
			StmtSetUpdate currentS = (StmtSetUpdate) stmt;
			toRet.addAll(find(currentS.getInp()));
			toRet.addAll(find(currentS.getSet()));

		} else if (stmt instanceof StmtBroadcast) {
			StmtBroadcast currentS = (StmtBroadcast) stmt;
			toRet.addAll(find(currentS.getAct().getValue()));

		} else if (stmt instanceof StmtIfThen) {
			StmtIfThen currentS = (StmtIfThen) stmt;
			toRet.addAll(find(currentS.getCond()));
			toRet.addAll(find(currentS.getThen()));
			toRet.addAll(find(currentS.getElse()));

		} else if (stmt instanceof StmtBlock) {
			StmtBlock currentS = (StmtBlock) stmt;
			for (Statement s : currentS.getStmts()) {
				toRet.addAll(find(s));
			}

		} else if (stmt instanceof StmtAssign) {
			StmtAssign currentS = (StmtAssign) stmt;
			toRet.addAll(find(currentS.getLHS()));
			toRet.addAll(find(currentS.getRHS()));

		} else if (stmt instanceof StmtSetIndexedAssign) {
			StmtSetIndexedAssign currentS = (StmtSetIndexedAssign) stmt;
			toRet.addAll(find(currentS.getIndex()));
			toRet.addAll(find(currentS.getInp()));
			toRet.addAll(find(currentS.getSet()));

		} else if (stmt instanceof StmtGoto) {
			StmtGoto currentS = (StmtGoto) stmt;
			toRet.addAll(find(currentS.getTargetLocExpr()));

		} else {
			System.err.println("Warning: passing an unsupported stmt to gatherer: " + stmt);
		}
		return toRet;
	}
}
