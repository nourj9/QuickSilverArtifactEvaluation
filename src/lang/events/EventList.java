package lang.events;

import java.util.ArrayList;

import lang.core.ChooseNode;

public class EventList extends ChooseNode {
	private ArrayList<String> eventlist = new ArrayList<String>();

	public EventList() {
	}

	public EventList(ArrayList<String> eventlist) {
		this.eventlist = eventlist;
	}

	public EventList(String event) {
		eventlist.add(event);
	}

	public ArrayList<String> getEventlist() {
		return eventlist;
	}

	public void addEvent(String event) {
		eventlist.add(event);
	}

	@Override
	public String toString() {
		String str = "";

		for (String e : eventlist) {
			if (str.equals("")) {
				str = e;
			} else {
				str = str + ", " + e;
			}
		}
		str = str + "\n";
		return str;
	}

	public void addEventsInlist(EventList el) {
		for (String e : el.getEventlist()) {
			eventlist.add(e);
		}
	}

}
