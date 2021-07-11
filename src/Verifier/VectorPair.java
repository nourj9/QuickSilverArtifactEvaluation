package Verifier;

public class VectorPair {
	public int[] sendSrcVec;
	public int[] sendDstVec;
	private GlobalTransitionManager gtm;

	public VectorPair(int[] sendSrcVec, int[] sendDstVec, GlobalTransitionManager gtm) {
		this.sendSrcVec = sendSrcVec;
		this.sendDstVec = sendDstVec;
		this.gtm = gtm;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// print them vertically
		// sb.append("srcVec").append("\t").append("recVec").append("\n");
		// for (int i = 0; i < sendDstVec.length; i++) {
		// sb.append(sendSrcVec[i]).append("\t").append(sendDstVec[i]).append("\n");
		// }
		// sb.append("\n");

		// print them horz.
		// sb.append("\nsrcVec: ");
		// for (int i = 0; i < sendDstVec.length; i++) {
		// sb.append(sendSrcVec[i]).append(" ");
		// }
		// sb.append("\ndstVec ");
		// for (int i = 0; i < sendDstVec.length; i++) {
		// sb.append(sendDstVec[i]).append(" ");
		// }
		// sb.append("\n");

		//
		//
		//
		//
		sb.append("-------------\nsrcVec: \n");
		for (int i = 0; i < sendDstVec.length; i++) {
			if (sendSrcVec[i] != 0) {
				sb.append(" [" + sendSrcVec[i] + "]").append(" in ")
						.append(gtm.getGlobalStateManager().getStateOf(i).toSmallString()).append("\n");
			}
		}
		sb.append("dstVec:");
		for (int i = 0; i < sendDstVec.length; i++) {
			if (sendDstVec[i] != 0) {
				sb.append("\n [" + sendDstVec[i] + "]").append(" in ")
						.append(gtm.getGlobalStateManager().getStateOf(i).toSmallString()).append("");
			}
		}
		sb.append("\n-------------");

		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof VectorPair))
			return false;
		VectorPair other = ((VectorPair) obj);
		return other.sendDstVec.equals(this.sendDstVec) && //
				other.sendSrcVec.equals(this.sendSrcVec);
	}

	public int getArity() {
		int sum = 0;
		for (int i = 0; i < sendDstVec.length; i++) {
			sum += sendDstVec[i];
		}
		return sum;
	}

}
