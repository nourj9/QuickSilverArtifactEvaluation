package liveness.buchi;

public class BuchiTransition {
	private BuchiState src;
	private BuchiState dest;
	private LivenessExpression label;

	public BuchiTransition(BuchiState src, BuchiState dest, LivenessExpression label) {
		this.src = src;
		this.dest = dest;
		this.label = label;

		// connect it
		src.addOutgoingTrans(this);
		dest.addIncomingTrans(this);

	}

	public BuchiState getSrc() {
		return src;
	}

	public BuchiState getDest() {
		return dest;
	}

	public LivenessExpression getLabel() {
		return label;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(src).append("--").append(label).append("-->").append(dest);

		return sb.toString();

	}

}
