package lang.handler;

import java.util.ArrayList;

import lang.core.Action;
import lang.events.Event;
import lang.stmts.StmtBlock;

public class HandlerReply extends Handler {

	private Event event;
	private Action action;

	public HandlerReply(Event event, ReactionReply reaction) {
		this.event = event;
		this.action = reaction.getAction();
	}

	public Action getAction() {
		return action;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return " on " + event + " reply " + action + "\n";
	}
	@Override
	public ArrayList<StmtBlock> getBodies() {
		return new ArrayList<StmtBlock>();// could also return null and have the caller deal with it
	}

}
