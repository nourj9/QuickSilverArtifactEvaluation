package lang.events;

import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.TypeChooseSet;

public class EventPartitionCons extends Event {

	/***************************
	 * Result Enum *
	 ***************************/
	public enum Result {
		WIN, LOSE
	};

	/***************************
	 * Partition Event Members *
	 ***************************/
	private static final String LOSESET = "loseS";
	private static final String WINSET = "winS";
	private ExprVar winset;
	private ExprVar loseset;
	private String chID; // This is the thing in the angle brackets
	private Expression participants;
	private Expression cardinality;

	public EventPartitionCons(String chID, Expression participants, Expression cardinality) {

		this(new ExprVar(chID + "." + WINSET, TypeChooseSet.T()), new ExprVar(chID + "." + LOSESET, TypeChooseSet.T()), chID, participants, cardinality);
	}

	private EventPartitionCons(ExprVar winset, ExprVar loseset, String chID, Expression participants, Expression cardinality) {
		this.winset = winset;
		this.loseset = loseset;
		this.chID = chID;
		this.participants = participants;
		this.cardinality = cardinality;
	}

	public ExprVar getWinset() {
		return winset;
	}

	public ExprVar getLoseset() {
		return loseset;
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
		return "PartitionCons<" + chID + ">(" + participants + "," + cardinality + ")";
	}

	@Override
	public Event clone() {
		return new EventPartitionCons(winset.clone(), loseset.clone(), chID, participants.clone(), cardinality.clone());
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
