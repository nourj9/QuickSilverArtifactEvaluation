package Verifier;

public class GlobalTransition {
	private Action action;
	private Matrix recMatrix;
	private VectorPair sendVecPiar;
	private GlobalState from;
	private GlobalState to;
	private GlobalTransitionManager gtm;

	public GlobalTransition(GlobalTransitionManager gtm, Action action, VectorPair sendVecPiar, GlobalState from,
			GlobalState to) {
		this.gtm = gtm;
		this.action = action;
		this.recMatrix = action.getRecMatrix();//gtm.getActionToRecMatrix().get(action);
		this.sendVecPiar = sendVecPiar;
		this.from = from;
		this.to = to;
	}

	public Action getAction() {
		return action;
	}

	public Matrix getRecMatrix() {
		return recMatrix;
	}

	public VectorPair getSendVecPair() {
		return sendVecPiar;
	}

	public GlobalState getFrom() {
		return from;
	}

	public GlobalState getTo() {
		return to;
	}

	public GlobalTransitionManager getGtm() {
		return gtm;
	}

	@Override
	public int hashCode() {
		return (from.getKey() + to.getKey()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof GlobalTransition))
			return false;
		GlobalTransition other = ((GlobalTransition) obj);
		return other.from.equals(this.from) && //
				other.to.equals(this.to) && //
				other.action.equals(this.action) && //
				other.sendVecPiar.equals(this.sendVecPiar);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(from.getKey());
		sb.append(" --").append(action).append("--> ");
		sb.append(to.getKey());
		return sb.toString();
	}

	public boolean isSelfLoop() {
		return from.equals(to);
	}

	public String getPrettyLabelForAction() {
		StringBuilder sb = new StringBuilder();
		sb.append("Action " + action + "\n");
		sb.append("Matrix \n");
		sb.append(recMatrix);
		sb.append("Vector-Pair\n");
		sb.append(sendVecPiar);
		return sb.toString();

	}
}
