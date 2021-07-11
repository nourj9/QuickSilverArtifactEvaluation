package lang.handler;

import java.util.ArrayList;
import java.util.HashSet;

import lang.core.Action;
import lang.core.ChooseNode;
import lang.core.Location;
import lang.events.Event;
import lang.events.EventAction;
import lang.events.EventPartitionCons;
import lang.events.EventValueCons;
import lang.stmts.StmtBlock;
import semantics.core.LocalAction;
import semantics.core.LocalAction.ActionType;

public abstract class Handler extends ChooseNode {
	protected Location location;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public abstract ArrayList<StmtBlock> getBodies();

	public HashSet<LocalAction> extractLocalActions() { // a set since the PartitionHnalder has two actions...
		HashSet<LocalAction> actions = new HashSet<LocalAction>();
		if (this instanceof HandlerPredicated) {
			HandlerPredicated h = (HandlerPredicated) this;
			if (h.isInternal()) {
				actions.add(LocalAction.make("", ActionType.INTERNAL, false));
			} else if (h.isReceive()) {
				Action act = ((EventAction) h.getEvent()).getAction();
				if (act.isBr()) {
					actions.add(LocalAction.make(act.getName(), ActionType.BROADCAST_RECV, act.isEnv()));
				} else {
					actions.add(LocalAction.make(act.getName(), ActionType.PAIRWISE_RECV, act.isEnv()));
				}
			} else if (h.isSend()) {
				Action act = h.getSendAction();
				if (h.getSendAction().isBr()) {
					actions.add(LocalAction.make(act.getName(), ActionType.BROADCAST_SEND, act.isEnv()));
				} else {
					actions.add(LocalAction.make(act.getName(), ActionType.PAIRWISE_SEND, act.isEnv()));
				}
			}
		} else if (this instanceof HandlerPartitionCons) {

			HandlerPartitionCons h = (HandlerPartitionCons) this;
			actions.add(LocalAction.make(h.getEvent().getChID(), ActionType.PARTITION_CONS_WIN, false));
			actions.add(LocalAction.make(h.getEvent().getChID(), ActionType.PARTITION_CONS_LOSE, false));

		} else if (this instanceof HandlerValueCons) {

			HandlerValueCons h = (HandlerValueCons) this;
			actions.add(LocalAction.make(h.getEvent().getChID(), ActionType.VALUE_CONS, false));

		}else if (this instanceof HandlerSymbolic) {

			HandlerSymbolic h = (HandlerSymbolic) this;
			Event event = h.getEvent();
			if (event instanceof EventAction) {
				Action act = ((EventAction) h.getEvent()).getAction();
				if (act.isBr()) {
					actions.add(LocalAction.make(act.getName(), ActionType.BROADCAST_RECV, act.isEnv()));
				} else {
					actions.add(LocalAction.make(act.getName(), ActionType.PAIRWISE_RECV, act.isEnv()));
				}
			} else if (event instanceof EventPartitionCons) {
				EventPartitionCons epc = (EventPartitionCons) event;
				actions.add(LocalAction.make(epc.getChID(), ActionType.PARTITION_CONS_LOSE, false));
			} else if (event instanceof EventValueCons) {
				EventValueCons epc = (EventValueCons) event;
				actions.add(LocalAction.make(epc.getChID(), ActionType.VALUE_CONS, false));
			} else {
				System.err.println("warning: calling extractLocalAction with an unknown event type. event: " + event);
			}
		} else {
			System.err.println("warning: calling extractLocalAction on an unknown handler type. Handler: " + this);
		}

		return actions;
	}
}
