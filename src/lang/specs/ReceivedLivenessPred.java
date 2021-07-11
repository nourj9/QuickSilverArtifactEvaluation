package lang.specs;

public class ReceivedLivenessPred extends LivenessPred {
	private String actionName;

	public ReceivedLivenessPred(String actionName) {
		this.actionName = actionName;
	}

	@Override
	public String toString() {
		return "received(" + actionName + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof ReceivedLivenessPred) {
			ReceivedLivenessPred other = (ReceivedLivenessPred) obj;
			return this.actionName.equals(other.actionName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
