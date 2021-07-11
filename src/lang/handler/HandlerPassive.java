package lang.handler;

import java.util.ArrayList;

import lang.events.EventList;
import lang.stmts.StmtBlock;;

public class HandlerPassive extends Handler {

	private EventList evntList;

	public HandlerPassive(EventList evntList) {
		this.evntList = evntList;
	}

	public EventList getEventList() {
		return evntList;
	}

	@Override
	public String toString() {
		return " passive " + evntList.toString();
	}

	@Override
	public ArrayList<StmtBlock> getBodies() {
		return new ArrayList<StmtBlock>();// could also return null and have the caller deal with it
	}

}
