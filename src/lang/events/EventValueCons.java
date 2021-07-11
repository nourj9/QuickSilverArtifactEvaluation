package lang.events;

import lang.expr.ExprVar;
import lang.expr.Expression;

public class EventValueCons extends Event {

	// private ExprVar winVar;
	private ExprVar proposalVar;
	private String chID; // This is the thing in the angle brackets
	private Expression participants;
	private Expression cardinality;

	// public EventValueCons(ExprVar winVar, ExprVar proposalVar, String label,
	// Expression participants, ExprConstant cardinality) {
	// this.winVar = winVar;
	// this.proposalVar = proposalVar;
	// this.label = label;
	// this.participants = participants;
	// this.cardinality = cardinality;
	// }

	public EventValueCons(ExprVar proposalVar, String chID, Expression participants, Expression cardinality) {
		this.proposalVar = proposalVar;
		this.chID = chID;
		this.participants = participants;
		this.cardinality = cardinality;
	}

	// public ExprVar getWinVar() {
	// return winVar;
	// }

	public ExprVar getProposalVar() {
		return proposalVar;
	}

	public String getChID() {
		return chID;
	}

	public Expression getParticipants() {
		return participants;
	}

	public Expression getCardinality() {
		return cardinality;
	}

	@Override
	public String toString() {
		return "ValueCons<" + chID + ">(" + participants + "," + cardinality + "," + proposalVar + ")";
	}

	public Event clone() {
		return new EventValueCons(proposalVar.clone(), chID, participants.clone(), cardinality.clone());
	}

	public boolean hasPropVar() {
		return !proposalVar.getName().equals("_");
	}

//	public boolean participantsIsAll() {
//
//		if (participants instanceof ExprConstant) {
//			ExprConstant p = (ExprConstant) participants;
//			return p.getVal().equals(TypeChooseSet.Constant.ALL);
//		}
//		
//		return false;
//	}
}
