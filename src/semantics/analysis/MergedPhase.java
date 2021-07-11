package semantics.analysis;

import java.util.HashSet;

import semantics.core.LocalState;
import semantics.core.LocalTransition;
import utils.MyStringBuilder;

public class MergedPhase extends Phase {
	private HashSet<Phase> enclosedPhases;
	private LocalTransition mergerTrans;

	public MergedPhase(PhaseAnalysis pa, HashSet<Phase> enclosedPhases, LocalTransition mergerTrans) {
		super(pa, Phase.T.merged);
		this.enclosedPhases = enclosedPhases;
		this.mergerTrans = mergerTrans;

		// populate the states too
		this.states = new HashSet<LocalState>();
		for (Phase corePhase : enclosedPhases) {
			this.states.addAll(corePhase.states);
		}
	}

	@Override
	public String toString() {
		MyStringBuilder sb = new MyStringBuilder();

		sb.appn("Merged Phase{ \n merger = " + mergerTrans);

		sb.app(" states(" + states.size() + ") = ");
		for (LocalState ls : states) {
			sb.app(ls.toSmallString());
		}
		sb.appn();

		sb.appn(" parts(" + enclosedPhases.size() + ") = ");
		for (Phase cp : enclosedPhases) {
			sb.appn("  " + cp);
		}

		sb.app("}");
		return sb.toString();
	}

	public HashSet<Phase> getEnclosedPhases() {
		return enclosedPhases;
	}

	public LocalTransition getMergerTr() {
		return mergerTrans;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj == null) {
//			return false;
//		}
//
//		if (obj == this) {
//			return true;
//		}
//
//		if (obj instanceof MergedPhase) {
//			MergedPhase other = (MergedPhase) obj;
//			return this.states.equals(other.states) //
//					&& this.mergerTrans.equals(other.mergerTrans)//
//					&& this.enclosedPhases.equals(other.enclosedPhases);
//		}
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//		return toString().hashCode();
//	}

}
