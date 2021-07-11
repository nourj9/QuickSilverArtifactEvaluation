package lang.handler;
//package lang.core;
//
//import lang.stmts.Statement;
//import lang.stmts.StmtBlock;
//import lang.events.Event;
//import lang.events.EventAction;;
//
//public class HandlerRegular_notused extends Handler {
//
//	private Event event;
//	private StmtBlock body;
//
//	public HandlerRegular(Event event, StmtBlock body) {
//		this.event = event;
//		this.body = body;
//	}
//
//	public HandlerRegular(Event event, Statement stmt) {
//		this.event = event;
//		this.body = new StmtBlock(stmt);
//	}
//
//	public HandlerRegular(Event event, ReactionRegular reaction) {
//		this.event = event;
//		this.body = reaction.getBlock();
//	}
//
//	public Event getEvent() {
//		return event;
//	}
//
//	public StmtBlock getBody() {
//		return body;
//	}
//
//	@Override
//	public String toString() {
//		return " on " + event + " do\n" + body;
//	}
//
//	public boolean isReceive() {
//		return (event instanceof EventAction);
//	}
//
//}
