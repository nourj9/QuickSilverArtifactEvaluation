package lang.core;

import java.util.ArrayList;
import java.util.HashSet;
import lang.events.EventList;
import lang.expr.Expression;
import lang.handler.Handler;
import lang.handler.HandlerCrash;
import lang.stmts.Pair;
import semantics.analysis.BACInstance;
import semantics.core.LocalAction;

public class Location extends ChooseNode {
	// reference to protocol is useful
	private Protocol p;
	private String name;
	private ArrayList<Handler> handlers;
	private EventList passiveEventList = new EventList();
	private Location parentLocation;

	public Location(String name) {
		this.name = name;
		this.handlers = new ArrayList<Handler>();
		this.parentLocation = null;
	}

	public Location(String name, Location parentLocation, Protocol p) {
		this.name = name;
		this.parentLocation = parentLocation;
		this.handlers = new ArrayList<Handler>();
		this.p = p;
	}

	public Location(String name, ArrayList<Handler> handlers, Protocol p) {
		this.handlers = handlers;
		this.name = name;
		this.parentLocation = null;
		this.p = p;
		for (Handler handler : this.handlers) {
			handler.setLocation(this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Handler> getHandlers() {
		return handlers;
	}

	public void addHandler(Handler handler) {
		handler.setLocation(this);
		handlers.add(handler);
	}

	@Override
	public String toString() {
		String toRet = "location " + name + "\n";
		for (Handler handler : handlers) {
			toRet = toRet + " " + handler;
		}
		return toRet;
	}

	public void replaceHandler(Handler currentH, Handler replaceH) {
		handlers.remove(currentH);
		replaceH.setLocation(this);
		handlers.add(replaceH);

	}

	public void replaceHandlers(ArrayList<Pair<Handler, Handler>> replacements) {
		for (Pair<Handler, Handler> pair : replacements) {
			replaceHandler(pair.getFirst(), pair.getSecond());
		}
	}

	public void deleteHandlers(HashSet<Handler> handlersToDelete) {
		handlers.removeAll(handlersToDelete);
	}

	public void deleteHandler(Handler handlersToDelete) {
		handlers.remove(handlersToDelete);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Location) {
			Location other = (Location) obj;
			return this.name.equals(other.name) && this.handlers.equals(other.handlers);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public EventList getPassiveEventList() {
		return passiveEventList;
	}

	public void addToPassiveEventList(EventList el) {
		passiveEventList.addEventsInlist(el);
	}

	public void addToPassiveEventList(String event) {
		passiveEventList.addEvent(event);
	}

	public Location getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Location parentLocation) {
		this.parentLocation = parentLocation;
	}

	public boolean isGeneratedByPreProcessing() {
		return parentLocation != null;
	}

	public boolean isUserProvidedLocation() {
		return parentLocation == null;
	}

//	public Handler getRecHandlerOverAction(String label) {
//		for (Handler handler : handlers) {
//			if(handler instanceof HandlerPredicated) {
//				HandlerPredicated hp = (HandlerPredicated)handler;
//				// a receive handler over the same action
//				if(hp.isReceive() && ((EventAction)hp.getEvent()).getAction().getName().equals(label)) {
//					return handler;
//				}					
//			}
//		}
//		return null;
//	}

	public HashSet<Handler> getAllHandlersOverAction(LocalAction action) {
		HashSet<Handler> toRet = new HashSet<Handler>();

		for (Handler handler : handlers) {
			for (LocalAction handlerAction : handler.extractLocalActions()) {
				if (handlerAction.equals(action)) {
					toRet.add(handler);
				}
			}
		}

		return toRet;
	}

	public Protocol getProtocol() {
		return p;
	}

	public void setProtocol(Protocol protocol) {
		this.p = protocol;
	}

	public ArrayList<Handler> getNonCrashHandlers() {
		ArrayList<Handler> toRet = new ArrayList<Handler>();
		for (Handler handler : handlers) {
			if (!(handler instanceof HandlerCrash)) {
				toRet.add(handler);
			}
		}
		return toRet;
	}

//	public HashSet<Handler> getAllHandlersOverLabel(String actLabel) {
//		HashSet<Handler> toRet = new HashSet<Handler>();
//
//		for (Handler handler : handlers) {
//			for (LocalAction handlerAction : handler.extractLocalActions()) {
//				if (handlerAction.getLabel().equals(actLabel)) {
//					toRet.add(handler);
//				}
//			}
//		}
//
//		return toRet;
//	}

//	public Handler getHandlerOverAction(LocalAction action) {
//		HashSet<Handler> allHandlers = getAllHandlersOverAction(action);
//		if (allHandlers.isEmpty()) {
//			return null;
//		} else if (allHandlers.size() == 1) {
//			return allHandlers.iterator().next();
//		} else {
//			// this can be on recv(a) where x and on recv(a) where y : make this a disjunction !
//			System.err.println("Two or more handlers over one action? is this internal handlers or something?");
//			System.err.println("local action: " + action);
//			System.err.println("handlers: " + allHandlers);
//			Thread.dumpStack();
//			System.exit(-1);
//			return null;
//		}
//	}
}
