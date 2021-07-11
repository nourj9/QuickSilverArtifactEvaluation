package semantics.core;

public class LocalAction { // a!! is not the same as a??
	public enum ActionType {
		PAIRWISE_SEND, PAIRWISE_RECV, BROADCAST_SEND, BROADCAST_RECV, INTERNAL, VALUE_CONS, PARTITION_CONS_WIN,
		PARTITION_CONS_LOSE, CRASH, UNKNOWN
	}

	private String label;
	private ActionType actionType;
	private boolean isEnv;

	private LocalAction(String label, ActionType actionType, boolean isEnv) {
		this.label = label;
		this.actionType = actionType;
		this.isEnv = isEnv;
	}

	public static ActionType correspondingReceiveType(ActionType type) {
		switch (type) {
		case PAIRWISE_SEND:
			return ActionType.PAIRWISE_RECV;
		case BROADCAST_SEND:
			return ActionType.BROADCAST_RECV;
		case PARTITION_CONS_WIN:
			return ActionType.PARTITION_CONS_LOSE;
		default:
			return type;
		}
	}

	public boolean isGloballySynchronizing() {

		return actionType == ActionType.BROADCAST_RECV || actionType == ActionType.BROADCAST_SEND
				|| actionType == ActionType.VALUE_CONS || actionType == ActionType.PARTITION_CONS_WIN
				|| actionType == ActionType.PARTITION_CONS_LOSE;
	}

	public boolean isEnvironment() {
		return isEnv;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public String getLabel() {
		return label;
	}

	public String punctuation() {
		switch (actionType) {
		case PAIRWISE_SEND:
			return "!";
		case PAIRWISE_RECV:
			return "?";
		case BROADCAST_SEND:
			return "!!";
		case BROADCAST_RECV:
			return "??";
		case PARTITION_CONS_WIN:
			return ":w";
		case PARTITION_CONS_LOSE:
			return ":l";
		default:
			return "";
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(isEnv ? "E:" : "").append(label).append(punctuation());
		return b.toString();
	}

	public String getPunctuatedLabel() {
		return label + punctuation();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof LocalAction) {
			LocalAction other = (LocalAction) obj;
			return this.label.equals(other.label) && this.actionType.equals(other.actionType)
					&& (this.isEnv == other.isEnv);
		}
		return false;
	}

	public boolean isInternalOrEnvPairwise() {

		if (actionType.equals(ActionType.INTERNAL)) {
			return true;
		}

		if (isEnv && (actionType.equals(ActionType.PAIRWISE_RECV) || actionType.equals(ActionType.PAIRWISE_SEND))) {
			return true;
		}

		return false;
	}

	public boolean isReceive() {
		return actionType == ActionType.BROADCAST_RECV //
				|| actionType == ActionType.VALUE_CONS //
				|| actionType == ActionType.PARTITION_CONS_LOSE //
				|| actionType == ActionType.PAIRWISE_RECV;
	}

	public boolean isInternal() {
		return actionType == ActionType.INTERNAL;
	}

	public boolean isAgreement() {
		return actionType == ActionType.VALUE_CONS //
				|| actionType == ActionType.PARTITION_CONS_LOSE //
				|| actionType == ActionType.PARTITION_CONS_WIN;
	}

	public boolean isSend() {
		return actionType == ActionType.BROADCAST_SEND || actionType == ActionType.PARTITION_CONS_WIN
				|| actionType == ActionType.PAIRWISE_SEND;
	}

	public boolean isPairwise() {
		return actionType == ActionType.PAIRWISE_SEND || actionType == ActionType.PAIRWISE_RECV;
	}

	@Override
	public int hashCode() {
		// TODO yeah?
		return toString().hashCode();
	}

	public static LocalAction make(String label, ActionType actionType, boolean isEnvTr) {
		// only create every action once? do an asString fun, etc.?
		// for now, just create an object anyway.
		return new LocalAction(label, actionType, isEnvTr);
	}

	public boolean isCrash() {
		return actionType == ActionType.CRASH;
	}

	// public getAssociatedActions(String Label)

}
