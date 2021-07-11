package lang.handler;

import lang.stmts.Pair;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtGoto;
import java.util.ArrayList;
import lang.core.Location;
import lang.core.Protocol;
import lang.expr.Expression;

public class HandlerCrash extends Handler {

	private String crashedEvent;
	private StmtBlock body;
	private Expression predicate;
	private Handler originatingHandler;
	private boolean normalized = false;

	public HandlerCrash(String crashedEvent, Expression predicate, Handler originatingHandler, Protocol p) {
		this.crashedEvent = crashedEvent;
		this.predicate = predicate;
		this.originatingHandler = originatingHandler;

		// build the body (late we might make it more intelligent)
		this.body = new StmtBlock(new StmtGoto(p.CRASH.getName(), p));

		normalized = checkNormalization().getFirst();
	}

	public String getCrashedEvent() {
		return crashedEvent;
	}

	public StmtBlock getBody() {
		return body;
	}

	public Handler getOriginatingHandler() {
		return originatingHandler;
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
		return " on crash(" + crashedEvent + ") where (" + predicate + ")" + " do\n" + body;
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

//	public void setTargetLoc(Location loc) {
//		for (Statement stmt : body.getStmts()) {
//			if (stmt instanceof StmtGoto) {
//				((StmtGoto) stmt).setTargetLoc(loc);
//			}
//		}
//	}

	// returns a pair of <isNormalized, NormalizationErrors>
	public Pair<Boolean, String> checkNormalization() {
		String errs = "";
		boolean isNormalized = true;

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
}
