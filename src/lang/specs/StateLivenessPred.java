package lang.specs;

public class StateLivenessPred extends LivenessPred {
	private StateDescList stateDescList;

	public StateLivenessPred(StateDescList stateDescList) {
		this.stateDescList = stateDescList;
	}

	@Override
	public String toString() {
		return stateDescList.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof StateLivenessPred) {
			StateLivenessPred other = (StateLivenessPred) obj;
			return this.stateDescList.equals(other.stateDescList);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return stateDescList.hashCode();
	}

}
