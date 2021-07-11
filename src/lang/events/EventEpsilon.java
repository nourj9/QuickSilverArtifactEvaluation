package lang.events;

public class EventEpsilon extends Event {

	@Override
	public String toString() {
		return "_";
	}

	public Event clone() {
		return new EventEpsilon();
	}
}
