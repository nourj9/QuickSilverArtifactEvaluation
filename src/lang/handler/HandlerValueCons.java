package lang.handler;

import lang.stmts.Statement;
import lang.stmts.StmtBlock;
import lang.stmts.StmtGoto;

import java.util.ArrayList;

import lang.core.Location;
import lang.events.EventValueCons;;

public class HandlerValueCons extends Handler {

	private EventValueCons event;
	private StmtBlock body;

	public HandlerValueCons(EventValueCons event, StmtBlock body) {
		this.event = event;
		this.body = body;
	}

	public HandlerValueCons(EventValueCons event, Statement stmt) {
		this.event = event;
		this.body = new StmtBlock(stmt);
	}

//	public HandlerValueCons(EventValueCons event, ReactionRegular reaction) {
//		this.event = event;
//		this.body = reaction.getBlock();
//	}

	public EventValueCons getEvent() {
		return event;
	}

	public StmtBlock getBody() {
		return body;
	}

	public void setBody(StmtBlock body) {
		this.body = body;
	}

	//public ExprVar getWinVar (){
	//	return event.
	//}
	
	@Override
	public String toString() {
		return " on " + event + " do\n" + body;
	}

	public Location getTargetLoc() {
		for (Statement stmt : body.getStmts()) {
			if (stmt instanceof StmtGoto) {
				return ((StmtGoto) stmt).getTargetLoc();
			}
		}
		return null;
	}

	public void setTargetLoc(Location loc) {
		for (Statement stmt : body.getStmts()) {
			if (stmt instanceof StmtGoto) {
				((StmtGoto) stmt).setTargetLoc(loc);
			}
		}
	}
	
	@Override
	public ArrayList<StmtBlock> getBodies() {
		ArrayList<StmtBlock> bodies = new ArrayList<StmtBlock>();
		bodies.add(body);
		return bodies;
	}

}
