package lang.handler;

import lang.core.Action;

public class ReactionReply extends Reaction {

	private Action action;

	public ReactionReply(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

}
