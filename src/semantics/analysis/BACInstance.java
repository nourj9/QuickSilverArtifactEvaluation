package semantics.analysis;

import java.util.HashSet;

import lang.core.Action;
import lang.core.Location;
import lang.expr.ExprConstant;
import lang.expr.ExprVar;
import lang.handler.Handler;
import lang.handler.HandlerPredicated;

public class BACInstance {

	Action brAction;
	Location sendSrcLoc;
	Location sendDstLoc;
	Location BACend;
	HashSet<Action> replyActions;
	Action negativeReplyAction;
	HashSet<Location> receiveSrcLocs;
	HashSet<Location> replyLocs;
	HashSet<Location> collectLocs;
	HashSet<ExprVar> varsToRemove;
	HashSet<Action> actsToRemove;
	HashSet<Location> receiveSrcLocsWithIncomingReplyEdge;
	HashSet<Location> receiveSrcLocsWithNoIncomingReplyEdge;
	HashSet<Location> replyLocsThatDoesNotLoopBackToRecLocs;
	HashSet<Location> targetOfRecWithNoReply;
	Location crashReplyLocToDeleteLater;
	HandlerPredicated crashReplyHandlerToDeleteLater;
	public Handler receiveSelfLoopOnCrash;

	public BACInstance(Action brAction, Location sendSrcLoc, Location sendDstLoc, Location BACend,
			HashSet<Action> replyActions, /* Action negativeReplyAction, */ HashSet<Location> receiveSrcLocs,
			HashSet<Location> replyLocs, HashSet<Location> collectLocs, HashSet<ExprVar> varsToRemove,
			HashSet<Action> actsToRemove, HashSet<Location> receiveSrcLocsWithIncomingReplyEdge,
			HashSet<Location> receiveSrcLocsWithNoIncomingReplyEdge,
			HashSet<Location> replyLocsThatDoesNotLoopBackToRecLocs, HashSet<Location> targetOfRecWithNoReply) {

		this.brAction = brAction;
		this.sendSrcLoc = sendSrcLoc;
		this.sendDstLoc = sendDstLoc;
		this.BACend = BACend;
		this.replyActions = replyActions;
		this.negativeReplyAction = decideNegativeReply(); // should be passed but for now this will do.
		this.receiveSrcLocs = receiveSrcLocs;
		this.replyLocs = replyLocs;
		this.collectLocs = collectLocs;
		this.varsToRemove = varsToRemove;
		this.actsToRemove = actsToRemove;
		this.receiveSrcLocsWithIncomingReplyEdge = receiveSrcLocsWithIncomingReplyEdge;
		this.receiveSrcLocsWithNoIncomingReplyEdge = receiveSrcLocsWithNoIncomingReplyEdge;
		this.replyLocsThatDoesNotLoopBackToRecLocs = replyLocsThatDoesNotLoopBackToRecLocs;
		this.targetOfRecWithNoReply = targetOfRecWithNoReply;
	}

	private Action decideNegativeReply() {
		Action sampleReply = replyActions.iterator().next();
		// TODO assumption: negative reply is the lower value.
		return new Action(sampleReply.getName(), sampleReply.isEnv(), sampleReply.getDomain(),
				sampleReply.getCommType(), sampleReply.getDomain().getInitialValue());
	}
}
