package lang.events;

public class EventPassive extends Event {

	private String chInst;

	public EventPassive(String chInst) {
		this.chInst = chInst;
	}

	@Override
	public String toString() {
		return "passive(" + chInst + ")";
	}

	public Event clone() {
		return new EventPassive(chInst);
	}

	public String getChID() {
		return chInst;
	}

}
