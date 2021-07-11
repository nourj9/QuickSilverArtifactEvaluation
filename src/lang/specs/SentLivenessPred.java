package lang.specs;

public class SentLivenessPred extends LivenessPred {
	private String actionName;

	public SentLivenessPred(String actionName) {
		this.actionName = actionName;
	}

	@Override
	public String toString() {
		return "sent(" + actionName + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof SentLivenessPred) {
			SentLivenessPred other = (SentLivenessPred) obj;
			return this.actionName.equals(other.actionName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
