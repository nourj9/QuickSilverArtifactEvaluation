package lang.handler;

import lang.stmts.Statement;
import lang.stmts.StmtBlock;

import java.util.ArrayList;

import lang.events.Event;;

/**
 * A regular handler that is excluded from code generation
 * */
public class HandlerSymbolic extends Handler {

	private Event event;
	private StmtBlock body;

	public HandlerSymbolic(Event event, StmtBlock body) {
		this.event = event;
		this.body = body;
	}

	public HandlerSymbolic(Event event, Statement stmt) {
		this.event = event;
		this.body = new StmtBlock(stmt);
	}

	public HandlerSymbolic(Event event, ReactionPredicated reaction) {
		this.event = event;
		this.body = reaction.getBlock();
	}

	public Event getEvent() {
		return event;
	}

	public StmtBlock getBody() {
		return body;
	}

	@Override
	public String toString() {
		return " [Symbolic] on " + event + " do\n" + body;
	}

	@Override
	public ArrayList<StmtBlock> getBodies() {
		ArrayList<StmtBlock> bodies = new ArrayList<StmtBlock>();
		bodies.add(body);
		return bodies;
	}

}
