package lang.events;

import lang.core.Action;

public class EventAction extends Event {

	private Action action;

	public EventAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	@Override
	public String toString() {
		if (action == null) {
			System.out.println("An action name is null, please check action names, as they are case-sensetive");
			System.exit(1);
			return "";
		} else {

			return "recv(" + action.toString() + ")";
		}
	}

	public Event clone() {
		return new EventAction(action);
	}
}
