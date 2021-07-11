package lang.handler;

import lang.stmts.Pair;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtBroadcast;
import lang.stmts.StmtGoto;
import lang.stmts.StmtSend;
import lang.type.TypeBool;

import java.util.ArrayList;

import lang.core.Action;
import lang.core.Location;
import lang.events.Event;
import lang.events.EventAction;
import lang.events.EventEpsilon;
import lang.events.EventPassive;
import lang.expr.ExprConstant;
import lang.expr.Expression;;

public class HandlerPredicated extends Handler {

	private Event event;
	private StmtBlock body;
	private Expression predicate;
	private boolean normalized = false;

	public HandlerPredicated(Event event, StmtBlock body, Expression predicate) {
		this.event = event;
		this.body = body;
		this.predicate = predicate;
		normalized = checkNormalization().getFirst();
	}

	public HandlerPredicated(Event event, ReactionPredicated reaction) {
		this.event = event;
		this.body = reaction.getBlock();
		this.predicate = reaction.getPredicate();
		normalized = checkNormalization().getFirst();
	}

//	public HandlerPredicated(HandlerRegular handler) {
//		this.event = handler.getEvent();
//		this.body = handler.getBody();
//		this.predicate = new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
//		normalized = checkNormalization().getFirst();
//	}

	public HandlerPredicated(Event event, StmtBlock body) {
		this.event = event;
		this.body = body;
		this.predicate = new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
		normalized = checkNormalization().getFirst();
	}

	public Event getEvent() {
		return event;
	}

	public StmtBlock getBody() {
		return body;
	}

	public StmtBlock getUpdates() {
		StmtBlock updates = new StmtBlock();

		for (Statement s : body.getStmts()) {
			if (s instanceof StmtAssign) {
				updates.addStmt(s);
			}
		}
		return updates;
	}

	public StmtBlock getNonSetUpdates() {
		StmtBlock updates = new StmtBlock();

		for (Statement s : body.getStmts()) {
			if (s instanceof StmtAssign) {
				//((StmtAssign) s).getRHS().ge
				updates.addStmt(s);
			}
		}
		return updates;
	}

	public Expression getPredicate() {
		return predicate;
	}

	public void setPredicate(Expression predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return " on " + event + " where (" + predicate + ")" + " do\n" + body;
	}

	// this banks on the fact that the handler is normalized. we should have a flag
	// "normalized" in this handler!
	public Location getTargetLoc() {

		// give this one last chance, as the body might have changed and the cached
		// value is not correct.
		if (!normalized) {
			normalized = checkNormalization().getFirst();
		}

		if (!normalized) {
			System.err.println("Error: calling getTargetLoc() on a non-normalized HandlerPredicated: " + this);
			Thread.dumpStack();
			System.exit(-1);
		}

		for (Statement stmt : body.getStmts()) {
			if (stmt instanceof StmtGoto) {
				return ((StmtGoto) stmt).getTargetLoc();
			}
		}
		return null;
	}

	public boolean isSend() {
		
		// give this one last chance, as the body might have changed and the cached
		// value is not correct.
		if (!normalized) {
			normalized = checkNormalization().getFirst();
		}

		if (!normalized) {
			System.err.println("Error: calling isSend() on a non-normalized HandlerPredicated: " + this);
			Thread.dumpStack();
			System.exit(-1);
		}
		return (event instanceof EventEpsilon && body.getNumOfSnds() != 0);
	}

	public boolean isInternal() {
		// give this one last chance, as the body might have changed and the cached
		// value is not correct.
		if (!normalized) {
			normalized = checkNormalization().getFirst();
		}

		if (!normalized) {
			System.err.println("Error: calling isInternal() on a non-normalized HandlerPredicated: " + this);
			Thread.dumpStack();
			System.exit(-1);
		}
		return (event instanceof EventEpsilon && body.getNumOfSnds() == 0);
	}

	public boolean isReceive() {
		return (event instanceof EventAction);
	}

	public Action getSendAction() {
		if (isSend()) {
			for (Statement stmt : body.getStmts()) {
				if (stmt instanceof StmtSend) {
					return ((StmtSend) stmt).getAct();
				} else if (stmt instanceof StmtBroadcast)
					return ((StmtBroadcast) stmt).getAct();
			}
		}
		return null;// or throw an exception
	}

	public void setTargetLoc(Location loc) {
		for (Statement stmt : body.getStmts()) {
			if (stmt instanceof StmtGoto) {
				((StmtGoto) stmt).setTargetLoc(loc);
			}
		}
	}

	// returns a pair of <isNormalized, NormalizationErrors>
	public Pair<Boolean, String> checkNormalization() {
		String errs = "";
		boolean isNormalized = true;

		if (isReceive() && body.getNumOfSnds() > 0) {
			errs += "this receive handler has one or more send statements\n";
			isNormalized = false;
		}
		if (body.getNumOfConds() > 0) {

			errs += "this predicated handler has one or more if statements\n";
			isNormalized = false;
		}

		if (!body.endsWithGoto()) {
			errs += "this predicated handler does not end with a Goto\n";
			isNormalized = false;
		}

		if (body.getNumOfSnds() > 1) {
			errs += "this predicated handler has more than one send\n";
			isNormalized = false;
		}

		String msg = "";
		if (!isNormalized) {
			msg = "Errors: Handler " + this + " in location "
					+ (location == null ? "not computed yet" : location.getName())
					+ " has the following normalization errors:" + errs + "\n";
		}

		return new Pair<Boolean, String>(isNormalized, msg);
	}

	@Override
	public ArrayList<StmtBlock> getBodies() {
		ArrayList<StmtBlock> bodies = new ArrayList<StmtBlock>();
		bodies.add(body);
		return bodies;
	}

	public boolean isPassive() {
		return (event instanceof EventPassive);
	}

}
