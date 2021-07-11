package semantics.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import Core.Options;
import Core.PreProcessing;
import lang.core.Action;
import lang.core.Location;
import lang.core.Protocol;
import lang.core.VarDecl;
import lang.events.EventAction;
import lang.events.EventEpsilon;
import lang.expr.ExprConstant;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.expr.Gatherer;
import lang.handler.Handler;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerReply;
import lang.handler.HandlerValueCons;
import lang.handler.ReactionReply;
import lang.stmts.Pair;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtBroadcast;
import lang.stmts.StmtGoto;
import lang.stmts.StmtSend;
import lang.stmts.StmtSetUpdate;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeChooseSet;
import lang.type.TypeID;
import lang.type.TypeInt;
import lang.type.TypeUnit;

public class StructureSummarizer {
	private Protocol p;
	private boolean addReceiveSelfLoops;
	private boolean removeNegotationStep;
	private Gatherer<ExprVar> varGatherer = new Gatherer<ExprVar>(ExprVar.class);
	private Options options;
	private ArrayList<BACInstance> BACInstances = new ArrayList<BACInstance>();

	public StructureSummarizer(boolean addReceiveSelfLoops, boolean removeNegotationStep, Protocol p, Options options) {
		this.p = p;
		this.addReceiveSelfLoops = addReceiveSelfLoops;
		this.removeNegotationStep = removeNegotationStep;
		this.options = options;

	}

	public void summarizeBAC() {
		HashSet<ExprVar> varsToRemove = new HashSet<ExprVar>();
		HashSet<Action> actsToRemove = new HashSet<Action>();

		HashSet<VarDecl> varsOverN = new HashSet<VarDecl>();

		for (VarDecl vd : p.getAllVariables()) {
			if (vd.getType() instanceof TypeInt
					&& ((TypeInt) vd.getType()).getUpperBound().equals(TypeInt.Constant.N.toString())) {
				varsOverN.add(vd);
			}
		}

		if (varsOverN.isEmpty()) {
			return;
		}

		// System.out.println("----------------------------------------------------------");
		// System.out.println("varsOverN: " + Arrays.toString(varsOverN.toArray()));
		// System.out.println("----------------------------------------------------------");

		HashSet<Action> brActions = new HashSet<Action>();
		for (Action action : p.getActions()) {
			if (action.isBr() && !action.isEnv()) {
				brActions.add(action);
			}
		}

		if (brActions.isEmpty()) {
			return;
		}

		// print("brActions", brActions);

		NEXTBRACTION: for (Action brAction : brActions) {

			HashSet<Location> receiveSrcLocs = new HashSet<Location>();
			HashSet<Location> receiveSrcLocsWithIncomingReplyEdge = new HashSet<Location>();
			HashSet<Location> receiveSrcLocsWithNoIncomingReplyEdge = new HashSet<Location>();
			HashSet<Location> replyLocsThatDoesNotLoopBackToRecLocs = new HashSet<Location>();
			HashSet<Location> replyLocs = new HashSet<Location>();

			HashSet<Location> targetOfRecWithNoReply = new HashSet<Location>();
			HashSet<Action> replyActions = new HashSet<Action>();
			Location sendSrcLoc = null;
			Location sendDstLoc = null;
			for (Location curLoc : p.getLocations()) {

				// TODO debug for now.
				if (curLoc.getName().equals("CRASH")) {
					continue;
				}

				for (Handler han : curLoc.getHandlers()) {
					if (han instanceof HandlerPredicated) {
						HandlerPredicated handler = (HandlerPredicated) han;
						if (handler.isReceive()) {
							Action action = ((EventAction) handler.getEvent()).getAction();
							if (action.equals(brAction)) { // a receive of brAction
								receiveSrcLocs.add(curLoc);
								Location targetLoc = handler.getTargetLoc();
								// also populates replyActions and replyLocs
								if (isReplyLocOfAction(curLoc, targetLoc, brAction, replyActions,
										receiveSrcLocsWithIncomingReplyEdge, replyLocsThatDoesNotLoopBackToRecLocs,
										replyLocs)) {
									// lol
								} else {
									targetOfRecWithNoReply.add(curLoc);
								}
							}
						} else if (handler.isSend()) {
							Action sAction = handler.getSendAction();
							if (sAction.equals(brAction)) {
								sendSrcLoc = curLoc;
								sendDstLoc = handler.getTargetLoc();

								ExprVar setVar = extractSet(handler);
								if (setVar != null)
									varsToRemove.add(setVar);
							}
						}
					}
				}
			}

			// get the location with a receive but with no reply edge looping back to it
			receiveSrcLocsWithNoIncomingReplyEdge.addAll(receiveSrcLocs);
			receiveSrcLocsWithNoIncomingReplyEdge.removeAll(receiveSrcLocsWithIncomingReplyEdge);

			if (!replyActions.isEmpty()) {

				// print("current braodcast action", brAction.getName());
				// print("replyActions", replyActions);
				// print("receiveSrcLocs", receiveSrcLocs);
				// print("receiveSrcLocsWithIncomingReplyEdge",
				// receiveSrcLocsWithIncomingReplyEdge);
				// print("receiveSrcLocsWithNoIncomingReplyEdge",
				// receiveSrcLocsWithNoIncomingReplyEdge);
				// print("replyLocs", replyLocs);
				// print("replyLocsThatDoesNotLoopBackToRecLocs",
				// replyLocsThatDoesNotLoopBackToRecLocs);
				// print("targetOfRecWithNoReply", targetOfRecWithNoReply);
				// print("sendSrcLoc", sendSrcLoc.getName());
				// print("sendDstLoc", sendDstLoc.getName());

			}

			if (targetOfRecWithNoReply.size() != 0) {
				continue NEXTBRACTION; // this is not a BAC br action
			}

			// now, we need to check if the sendDstLoc has the "reply receives"

			// collect --> save ==> decide ==> (Pick or Collect)

			HashSet<Location> collectLocs = new HashSet<Location>();
			collectLocs.add(sendDstLoc);

			Location BACend = null;
			for (Handler han : sendDstLoc.getHandlers()) {
				if (han instanceof HandlerPredicated) {
					HandlerPredicated handler = (HandlerPredicated) han;
					if (handler.isReceive()) {
						Action action = ((EventAction) handler.getEvent()).getAction();
						if (replyActions.contains(action)) { // a receive of a reply action
							Location nextLoc = handler.getTargetLoc();

							HashSet<Location> visited = new HashSet<Location>();
							ArrayList<Location> toVisit = new ArrayList<Location>();
							toVisit.add(nextLoc);
							boolean done = false;
							while (!toVisit.isEmpty() && (!nextLoc.equals(sendDstLoc)) && !done) {

								nextLoc = toVisit.remove(0);
								if (visited.contains(nextLoc)) {
									continue;
								}
								visited.add(nextLoc);
								collectLocs.add(nextLoc);

								// get all next locs using internal stuff
								for (Handler hx : nextLoc.getHandlers()) {
									if (hx instanceof HandlerPredicated) {
										HandlerPredicated ph = (HandlerPredicated) hx;
										if (ph.isInternal()) {
											toVisit.add(ph.getTargetLoc());
										} else {
											continue NEXTBRACTION; // this is not a BAC br action
										}
									}
								}

								if (nextLoc.getHandlers().size() == 2) {

									HandlerPredicated h1 = (HandlerPredicated) nextLoc.getHandlers().get(0);
									HandlerPredicated h2 = (HandlerPredicated) nextLoc.getHandlers().get(1);

									VarDecl vd = varsOverN.iterator().next();
									ExprVar var = new ExprVar(vd.getName(), vd.getType());

									boolean p1 = predicateContainsVar(var, h1.getPredicate());
									boolean p2 = predicateContainsVar(var, h2.getPredicate());

									if (p1 && p2) {
										if (!h1.getTargetLoc().equals(sendDstLoc)
												&& h2.getTargetLoc().equals(sendDstLoc)) {
											BACend = h1.getTargetLoc();
											varsToRemove.add(var);
											done = true;
										} else if (h1.getTargetLoc().equals(sendDstLoc)
												&& !h2.getTargetLoc().equals(sendDstLoc)) {
											BACend = h2.getTargetLoc();
											varsToRemove.add(var);
											done = true;
										} else {
											continue NEXTBRACTION; // this is not a BAC br action
										}
									}
								}

							}
						}
					}
				}
			}

			// print("collectLocs", collectLocs);
			// print("BACend", BACend.getName());

			// remove all reply and collect locations.
			// print("protocol locations before", p.getLocations());
			// print("specs before", p.getSpecs().toString());
			p.deleteLocationsAndCheckSpecs(replyLocs);
			p.deleteLocationsAndCheckSpecs(collectLocs);
			// print("protocol locations after", p.getLocations());
			// print("specs after", p.getSpecs().toString());

			// remove handlers from receiveSrcLocs to replyLocs
			for (Location recloc : receiveSrcLocs) {
				HashSet<Handler> handlersToDelete = new HashSet<Handler>();
				for (Handler handler : recloc.getHandlers()) {
					if (handler instanceof HandlerPredicated) {
						HandlerPredicated ph = (HandlerPredicated) handler;
						if (replyLocs.contains(ph.getTargetLoc())) {
							handlersToDelete.add(handler);
						}
					}
				}
				recloc.deleteHandlers(handlersToDelete);
			}

			// make "ask" goto "pick"
			HashSet<Handler> handlersToDelete = new HashSet<Handler>();
			for (Handler handler : sendSrcLoc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.isSend() && currentH.getTargetLoc().equals(sendDstLoc)
							&& currentH.getSendAction().equals(brAction)) {
						handlersToDelete.add(currentH);
					}
				}
			}
			sendSrcLoc.deleteHandlers(handlersToDelete);

			addSend(sendSrcLoc, brAction, BACend);

			// add the receive from "env" to "idle" if there is an and idle, otherwise, the
			// start is really "Ask"
			if (!receiveSrcLocsWithNoIncomingReplyEdge.isEmpty() && !replyLocsThatDoesNotLoopBackToRecLocs.isEmpty()) {
				Location BACstart = receiveSrcLocsWithNoIncomingReplyEdge.iterator().next();
				Location BACrecExit = replyLocsThatDoesNotLoopBackToRecLocs.iterator().next();
				addReceive(BACstart, brAction, BACrecExit);
			}
			// add the receive from "ask" to "pick"
			addReceive(sendSrcLoc, brAction, BACend);

			// System.out.println("PROTOCOL NOW:");
			// System.out.println(p.toString());
			actsToRemove.addAll(replyActions);

			// update June 15th remove pure negotiation steps: i.e.: s1 -- a!! --> s2 and s1
			// -- a?? --> s2 (with no other sends and receives!)
			boolean somethingRemoved = false;
			if (removeNegotationStep) {
				// essentially, no Idle state
				// System.out.println("xx: "+ receiveSrcLocsWithNoIncomingReplyEdge.isEmpty());
				// System.out.println("xx: "+ replyLocsThatDoesNotLoopBackToRecLocs.isEmpty());
				if (receiveSrcLocsWithNoIncomingReplyEdge.isEmpty()
						&& replyLocsThatDoesNotLoopBackToRecLocs.isEmpty()) {
					somethingRemoved = true;
					mergeLocations(sendSrcLoc, BACend);
				}
			}

			// System.err.println("xx: protocol after removal " + p);
			// update June 15th: add a self loop receive on states in
			// receiveSrcLocsWithIncomingReplyEdge except for the asking state,
			// as that has its own receive already.
			if (addReceiveSelfLoops && !somethingRemoved) {
				for (Location loc : receiveSrcLocsWithIncomingReplyEdge) {
					if (!loc.equals(sendSrcLoc)) {
						addReceive(loc, brAction, loc);
					}
				}
			}
			//
			//
			//
			//
			//
			//
			//
		} // end of loop over brActions

		cleanUp(varsToRemove, actsToRemove);
	}

	public ArrayList<BACInstance> detectBACs() {
		HashSet<ExprVar> varsToRemove = new HashSet<ExprVar>();
		HashSet<Action> actsToRemove = new HashSet<Action>();

		HashSet<VarDecl> varsOverN = new HashSet<VarDecl>();

		for (VarDecl vd : p.getAllVariables()) {
			if (vd.getType() instanceof TypeInt
					&& ((TypeInt) vd.getType()).getUpperBound().equals(TypeInt.Constant.N.toString())) {
				varsOverN.add(vd);
			}
		}

		if (varsOverN.isEmpty()) {
			return BACInstances;
		}

		// System.out.println("----------------------------------------------------------");
		// System.out.println("varsOverN: " + Arrays.toString(varsOverN.toArray()));
		// System.out.println("----------------------------------------------------------");

		HashSet<Action> brActions = new HashSet<Action>();
		for (Action action : p.getActions()) {
			if (action.isBr() && !action.isEnv()) {
				brActions.add(action);
			}
		}

		if (brActions.isEmpty()) {
			return BACInstances;
		}

		// print("brActions", brActions);

		NEXTBRACTION: for (Action brAction : brActions) {

			HashSet<Location> receiveSrcLocs = new HashSet<Location>();
			HashSet<Location> receiveSrcLocsWithIncomingReplyEdge = new HashSet<Location>();
			HashSet<Location> receiveSrcLocsWithNoIncomingReplyEdge = new HashSet<Location>();
			HashSet<Location> replyLocsThatDoesNotLoopBackToRecLocs = new HashSet<Location>();
			HashSet<Location> replyLocs = new HashSet<Location>();

			HashSet<Location> targetOfRecWithNoReply = new HashSet<Location>();
			HashSet<Action> replyActions = new HashSet<Action>();
			// Action negativeReplyAction = null;

			Location sendSrcLoc = null;
			Location sendDstLoc = null;
			for (Location curLoc : p.getLocations()) {

				// TODO debug for now.
				if (curLoc.equals(p.CRASH)) {
					continue;
				}

				for (Handler han : curLoc.getHandlers()) {
					if (han instanceof HandlerPredicated) {
						HandlerPredicated handler = (HandlerPredicated) han;
						if (handler.isReceive()) {
							Action action = ((EventAction) handler.getEvent()).getAction();
							if (action.equals(brAction)) { // a receive of brAction
								receiveSrcLocs.add(curLoc);
								Location targetLoc = handler.getTargetLoc();
								// also populates replyActions and replyLocs
								if (isReplyLocOfAction(curLoc, targetLoc, brAction, replyActions,
										receiveSrcLocsWithIncomingReplyEdge, replyLocsThatDoesNotLoopBackToRecLocs,
										replyLocs)) {
									// lol
								} else {
									targetOfRecWithNoReply.add(curLoc);
								}
							}
						} else if (handler.isSend()) {
							Action sAction = handler.getSendAction();
							if (sAction.equals(brAction)) {
								sendSrcLoc = curLoc;
								sendDstLoc = handler.getTargetLoc();

								ExprVar setVar = extractSet(handler);
								if (setVar != null)
									varsToRemove.add(setVar);
							}
						}
					}
				}
			}

			// get the location with a receive but with no reply edge looping back to it
			receiveSrcLocsWithNoIncomingReplyEdge.addAll(receiveSrcLocs);
			receiveSrcLocsWithNoIncomingReplyEdge.removeAll(receiveSrcLocsWithIncomingReplyEdge);

			if (!replyActions.isEmpty()) {

				// print("current braodcast action", brAction.getName());
				// print("replyActions", replyActions);
				// print("receiveSrcLocs", receiveSrcLocs);
				// print("receiveSrcLocsWithIncomingReplyEdge",
				// receiveSrcLocsWithIncomingReplyEdge);
				// print("receiveSrcLocsWithNoIncomingReplyEdge",
				// receiveSrcLocsWithNoIncomingReplyEdge);
				// print("replyLocs", replyLocs);
				// print("replyLocsThatDoesNotLoopBackToRecLocs",
				// replyLocsThatDoesNotLoopBackToRecLocs);
				// print("targetOfRecWithNoReply", targetOfRecWithNoReply);
				// print("sendSrcLoc", sendSrcLoc.getName());
				// print("sendDstLoc", sendDstLoc.getName());

			}

			if (targetOfRecWithNoReply.size() != 0) {
				continue NEXTBRACTION; // this is not a BAC br action
			}

			// now, we need to check if the sendDstLoc has the "reply receives"

			// collect --> save ==> decide ==> (Pick or Collect)

			HashSet<Location> collectLocs = new HashSet<Location>();
			collectLocs.add(sendDstLoc);

			Location BACend = null;
			for (Handler han : sendDstLoc.getHandlers()) {
				if (han instanceof HandlerPredicated) {
					HandlerPredicated handler = (HandlerPredicated) han;
					if (handler.isReceive()) {
						Action action = ((EventAction) handler.getEvent()).getAction();
						if (replyActions.contains(action)) { // a receive of a reply action
							Location nextLoc = handler.getTargetLoc();

							HashSet<Location> visited = new HashSet<Location>();
							ArrayList<Location> toVisit = new ArrayList<Location>();
							toVisit.add(nextLoc);
							boolean done = false;
							while (!toVisit.isEmpty() && (!nextLoc.equals(sendDstLoc)) && !done) {

								nextLoc = toVisit.remove(0);
								if (visited.contains(nextLoc)) {
									continue;
								}
								visited.add(nextLoc);
								collectLocs.add(nextLoc);

								// get all next locs using internal stuff
								for (Handler hx : nextLoc.getHandlers()) {
									if (hx instanceof HandlerPredicated) {
										HandlerPredicated ph = (HandlerPredicated) hx;
										if (ph.isInternal()) {
											toVisit.add(ph.getTargetLoc());
										} else {
											continue NEXTBRACTION; // this is not a BAC br action
										}
									}
								}

								if (nextLoc.getNonCrashHandlers().size() == 2) {

									HandlerPredicated h1 = (HandlerPredicated) nextLoc.getNonCrashHandlers().get(0);
									HandlerPredicated h2 = (HandlerPredicated) nextLoc.getNonCrashHandlers().get(1);

									VarDecl vd = varsOverN.iterator().next();
									ExprVar var = new ExprVar(vd.getName(), vd.getType());

									boolean p1 = predicateContainsVar(var, h1.getPredicate());
									boolean p2 = predicateContainsVar(var, h2.getPredicate());

									if (p1 && p2) {
										if (!h1.getTargetLoc().equals(sendDstLoc)
												&& h2.getTargetLoc().equals(sendDstLoc)) {
											BACend = h1.getTargetLoc();
											varsToRemove.add(var);
											done = true;
										} else if (h1.getTargetLoc().equals(sendDstLoc)
												&& !h2.getTargetLoc().equals(sendDstLoc)) {
											BACend = h2.getTargetLoc();
											varsToRemove.add(var);
											done = true;
										} else {
											continue NEXTBRACTION; // this is not a BAC br action
										}
									}
								}

							}
						}
					}
				}
			}

			BACInstance bac = new BACInstance(brAction, sendSrcLoc, sendDstLoc, BACend,
					replyActions/* , negativeReplyAction */, receiveSrcLocs, replyLocs, collectLocs, varsToRemove,
					actsToRemove, receiveSrcLocsWithIncomingReplyEdge, receiveSrcLocsWithNoIncomingReplyEdge,
					replyLocsThatDoesNotLoopBackToRecLocs, targetOfRecWithNoReply);

			BACInstances.add(bac);

			// removeBACInstance(bac);

		} // end of loop over brActions
			// cleanUp(varsToRemove, actsToRemove);
		return BACInstances;
	}

	private void removeBACInstance(BACInstance bac) {
		// print("collectLocs", collectLocs);
		// print("BACend", BACend.getName());

		// remove all reply and collect locations.
		// print("protocol locations before", p.getLocations());
		// print("specs before", p.getSpecs().toString());
		p.deleteLocationsAndCheckSpecs(bac.replyLocs);
		p.deleteLocationsAndCheckSpecs(bac.collectLocs);
		// print("protocol locations after", p.getLocations());
		// print("specs after", p.getSpecs().toString());

		// remove handlers from receiveSrcLocs to replyLocs
		for (Location recloc : bac.receiveSrcLocs) {
			HashSet<Handler> handlersToDelete = new HashSet<Handler>();
			for (Handler handler : recloc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated ph = (HandlerPredicated) handler;
					if (bac.replyLocs.contains(ph.getTargetLoc())) {
						handlersToDelete.add(handler);
					}
				}
			}
			recloc.deleteHandlers(handlersToDelete);
		}

		// make "ask" goto "pick"
		HashSet<Handler> handlersToDelete = new HashSet<Handler>();
		for (Handler handler : bac.sendSrcLoc.getHandlers()) {
			if (handler instanceof HandlerPredicated) {
				HandlerPredicated currentH = (HandlerPredicated) handler;
				if (currentH.isSend() && currentH.getTargetLoc().equals(bac.sendDstLoc)
						&& currentH.getSendAction().equals(bac.brAction)) {
					handlersToDelete.add(currentH);
				}
			}
		}
		bac.sendSrcLoc.deleteHandlers(handlersToDelete);

		addSend(bac.sendSrcLoc, bac.brAction, bac.BACend);

		// add the receive from "env" to "idle" if there is an and idle, otherwise, the
		// start is really "Ask"
		if (!bac.receiveSrcLocsWithNoIncomingReplyEdge.isEmpty()
				&& !bac.replyLocsThatDoesNotLoopBackToRecLocs.isEmpty()) {
			Location BACstart = bac.receiveSrcLocsWithNoIncomingReplyEdge.iterator().next();
			Location BACrecExit = bac.replyLocsThatDoesNotLoopBackToRecLocs.iterator().next();
			addReceive(BACstart, bac.brAction, BACrecExit);
		}
		// add the receive from "ask" to "pick"
		addReceive(bac.sendSrcLoc, bac.brAction, bac.BACend);

		// System.out.println("PROTOCOL NOW:");
		// System.out.println(p.toString());
		bac.actsToRemove.addAll(bac.replyActions);

		// update June 15th remove pure negotiation steps: i.e.: s1 -- a!! --> s2 and s1
		// -- a?? --> s2 (with no other sends and receives!)
		boolean somethingRemoved = false;
		if (removeNegotationStep) {
			// essentially, no Idle state
			// System.out.println("xx: "+ receiveSrcLocsWithNoIncomingReplyEdge.isEmpty());
			// System.out.println("xx: "+ replyLocsThatDoesNotLoopBackToRecLocs.isEmpty());
			if (bac.receiveSrcLocsWithNoIncomingReplyEdge.isEmpty()
					&& bac.replyLocsThatDoesNotLoopBackToRecLocs.isEmpty()) {
				somethingRemoved = true;
				mergeLocations(bac.sendSrcLoc, bac.BACend);
			}
		}

		// System.err.println("xx: protocol after removal " + p);
		// update June 15th: add a self loop receive on states in
		// receiveSrcLocsWithIncomingReplyEdge except for the asking state,
		// as that has its own receive already.
		if (addReceiveSelfLoops && !somethingRemoved) {
			for (Location loc : bac.receiveSrcLocsWithIncomingReplyEdge) {
				if (!loc.equals(bac.sendSrcLoc)) {
					addReceive(loc, bac.brAction, loc);
				}
			}
		}
		//
		//
		//
		//
		//
		//
		//

	}

	public void postBACleanUp() {
		for (BACInstance bac : BACInstances) {
			cleanUp(bac.varsToRemove, bac.actsToRemove);
		}
	}

	private ExprVar extractSet(HandlerPredicated handler) {

		for (Statement stmt : handler.getBody().getStmts()) {
			if (stmt instanceof StmtSetUpdate) {
				StmtSetUpdate su = (StmtSetUpdate) stmt;
				if (su.getOp() == StmtSetUpdate.OpType.ADD) {
					if (su.getInp() instanceof ExprConstant
							&& ((ExprConstant) su.getInp()).getVal().equals(TypeID.Constant.SELF.toString())) {
						return su.getSet();
					}
				}
			}
		}

		return null;
	}

	private void addSend(Location from, Action brAction, Location to) {
		StmtGoto gotoStmt = new StmtGoto(to.getName(), p);
		StmtBroadcast brStmt = new StmtBroadcast(brAction);
		StmtBlock body = new StmtBlock();
		body.addStmt(brStmt);
		body.addStmt(gotoStmt);

		HandlerPredicated recHandler = new HandlerPredicated(new EventEpsilon(), body);
		from.addHandler(recHandler);
	}

	private void cleanUp(HashSet<ExprVar> varsToRemove, HashSet<Action> actsToRemove) {
		// System.err.println("varsToRemove" + varsToRemove);
		// System.err.println("actsToRemove" + actsToRemove);

		p.getActions().removeAll(actsToRemove);

		for (ExprVar var : varsToRemove) {

			// Delete statements if they used removable user variables.
			if (p.getUserVars().containsKey(var.getName())) {
				deleteStatementsRelatedToVar(var);
			}

			// Delete the variable either way
			p.deleteVariable(var);

		}
	}

	private void deleteStatementsRelatedToVar(ExprVar var) {
		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {

				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;

					if (varGatherer.find(currentH.getPredicate()).contains(var)) {
						System.out.println("BAC var used in predicate!: " + var);
						System.exit(0);
					}
				}

				for (StmtBlock body : handler.getBodies()) {
					body.getStmts().removeIf(new Predicate<Statement>() {

						@Override
						public boolean test(Statement stmt) {
							if (stmt instanceof StmtAssign) {
								StmtAssign s = (StmtAssign) stmt;
								if (varGatherer.find(s.getLHS()).contains(var)
										|| varGatherer.find(s.getRHS()).contains(var)) {
									// System.err.println("xx: delete: "+s);
									return true;
								}
							} else if (stmt instanceof StmtSend) {
								StmtSend s = (StmtSend) stmt;
								if (varGatherer.find(s.getTo()).contains(var)
										|| varGatherer.find(s.getAct().getValue()).contains(var)) {
									// System.err.println("xx: delete: "+s);
									return true;
								}
							} else if (stmt instanceof StmtSetUpdate) {
								StmtSetUpdate s = (StmtSetUpdate) stmt;
								if (varGatherer.find(s.getSet()).contains(var)
										|| varGatherer.find(s.getInp()).contains(var)) {
									// System.err.println("xx: delete: "+s);
									return true;
								}
							}
							return false;
						}
					});
				}
			}
		}

	}

	private void addReceive(Location From, Action brAction, Location To) {
		StmtGoto gotoStmt = new StmtGoto(To.getName(), p);
		StmtBlock body = new StmtBlock(gotoStmt);
		HandlerPredicated recHandler = new HandlerPredicated(new EventAction(brAction), body);
		From.addHandler(recHandler);
	}

	private boolean predicateContainsVar(ExprVar var, Expression predicate) {
		return varGatherer.find(predicate).contains(var);
	}

	/**
	 * returns true (and updates replayActions) if the following holds: 1. there is
	 * only one edge (i.e. one handler) out of this location. 2. it is a send edge
	 * that leads back to curLoc
	 * 
	 * @param replyLocs
	 * 
	 */
	private boolean isReplyLocOfAction(Location curLoc, Location targetLoc, Action brAction,
			HashSet<Action> replyActions, HashSet<Location> replySelfLoopLocs, HashSet<Location> replyExitLocs,
			HashSet<Location> replyLocs) {
		// System.out.println("==========================================================");
		// System.out.println("curLoc: " + curLoc.getName());
		// System.out.println("----------------------------------------------------------");
		// System.out.println("targetLoc: " + targetLoc.getName());
		// System.out.println("----------------------------------------------------------");

		if (targetLoc.getNonCrashHandlers().size() != 1)
			return false;

		Handler han = targetLoc.getNonCrashHandlers().get(0);
		
		if (!(han instanceof HandlerPredicated))
			return false;

		HandlerPredicated targetH = (HandlerPredicated) han;

		if (targetH.isSend()) {
			Location nextLoc = targetH.getTargetLoc();
			// System.out.println("nextLoc: " + nextLoc.getName());
			// System.out.println("----------------------------------------------------------");

			if (nextLoc.equals(curLoc)) {
				replySelfLoopLocs.add(nextLoc);
			} else {
				replyExitLocs.add(nextLoc);
			}
			replyActions.add(targetH.getSendAction());
			replyLocs.add(targetLoc);
			return true;
		}

		return false;
	}

	public void removeNegotationSteps() {
		if (removeNegotationStep) {
			/**
			 * 
			 * LATER: might change this when the preprocessing "unrolls" the broadcasts with
			 * values.
			 * 
			 * 
			 * 1- Identify negotiation steps as follows: a. every send from s1 to s2, has a
			 * receive with it. b. the remaining receives are all self loops. [TODO: every
			 * other state has a self-loop] 2- Remove things: a. remove all the self loops.
			 * b. merge s1 and s2 as follows: delete s1 and replace all its mentions with s2
			 */
			HashSet<Action> brActions = new HashSet<Action>();
			for (Action action : p.getActions()) {
				if (action.isBr() && !action.isEnv()) {

					// LATER change later
					if (!action.getDomain().equals(TypeUnit.T())) {
						continue;
					}

					brActions.add(action);
				}
			}

			if (brActions.isEmpty()) {
				return;
			}

			// print("brActions", brActions);
			ArrayList<NegotiationInfo> nInfo = new ArrayList<NegotiationInfo>();
			NEXTACTION: for (Action brAction : brActions) {

				for (Location curLoc : p.getLocations()) {
					for (Handler han : curLoc.getHandlers()) {
						if (han instanceof HandlerPredicated) {
							HandlerPredicated handler = (HandlerPredicated) han;
							if (handler.isSend()) {
								// System.out.println(curLoc);
								// System.out.println(handler);
								Action sAction = handler.getSendAction();
								if (sAction.equals(brAction)) {

									// send source and destination
									Location sendSrcLoc = curLoc;
									Location sendDstLoc = handler.getTargetLoc();

									// have parallel receive?
									HandlerPredicated recHandler = getReceiveHandlersBetweenLocationsOnAction(
											sendSrcLoc, brAction, sendDstLoc);
									if (recHandler != null) {
										// make sure the rest of the receives are self-loops
										if (restOfReceivesAreSelfLoops(brAction, recHandler)) {
											// add things for cleanup
											nInfo.add(new NegotiationInfo(sendSrcLoc, sendDstLoc, brAction));
										}
									} else {
										// can't do anything about this action.
										continue NEXTACTION;
									}
								}
							}
						}
					}
				}

			}
			// CLEANUP
			for (NegotiationInfo i : nInfo) {
				// remove all self-loops
				removeAllselfLoopsOnAction(i.brAction);
				// remove action
				p.getActions().remove(i.brAction);

				if (i.src.equals(i.dst)) {
					// then it's a self-loop really, just delete the remaining send
					i.src.getHandlers().removeIf(new Predicate<Handler>() {

						@Override
						public boolean test(Handler h) {
							if (h instanceof HandlerPredicated) {
								HandlerPredicated currentH = (HandlerPredicated) h;
								if (currentH.isSend() && currentH.getSendAction().equals(i.brAction)
										&& currentH.getTargetLoc().equals(i.dst)) {
									return true;
								}
							}
							return false;
						}
					});
				} else {
					// merge s1, s2
					mergeLocations(i.src, i.dst);
				}
			}
			// System.err.println("xx: " + p);
		}
	}

	private void mergeLocationsWithoutDelectionOfLocs(Location fromLoc, Location toLoc) {
		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {

				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.getTargetLoc().equals(fromLoc)) {
						currentH.setTargetLoc(toLoc);
					}
				} else if (handler instanceof HandlerPartitionCons) {
					HandlerPartitionCons currentH = (HandlerPartitionCons) handler;
					if (currentH.getWinTargetLoc().equals(fromLoc)) {
						currentH.setWinTargetLoc(toLoc);
					}
					if (currentH.getLoseTargetLoc().equals(fromLoc)) {
						currentH.setLoseTargetLoc(toLoc);
					}
				} else if (handler instanceof HandlerValueCons) {
					HandlerValueCons currentH = (HandlerValueCons) handler;
					if (currentH.getTargetLoc().equals(fromLoc)) {
						currentH.setTargetLoc(toLoc);
					}
				}
			}
		}
	}

	private void mergeLocations(Location fromLoc, Location toLoc) {

		// 1- delete from.
		p.getLocations().remove(fromLoc);
		// 2- make sure everyone points to toLoc instead of fromLoc.
		mergeLocationsWithoutDelectionOfLocs(fromLoc, toLoc);
	}

//	private static void print(String label, HashSet<Action> acts) {
//		System.out.println("----------------------------------------------------------");
//		System.out.println(label + ": ");
//		for (Action act : acts) {
//			System.out.print(act.getName() + "  ");
//		}
//		System.out.println();
//	}
//
//	private static void print(String label, Collection<Location> locations) {
//		System.out.println("----------------------------------------------------------");
//		System.out.println(label + ": ");
//		for (Location loc : locations) {
//			System.out.print(loc.getName() + "  ");
//		}
//		System.out.println();
//	}

//	private static void print(String label, String msg) {
//		System.out.println("----------------------------------------------------------");
//		System.out.println(label + ": " + msg);
//		System.out.println();
//	}

	private static HandlerPredicated getReceiveHandlersBetweenLocationsOnAction(Location from, Action brAction,
			Location to) {
		for (Handler handler : from.getHandlers()) {
			if (handler instanceof HandlerPredicated) {
				HandlerPredicated currentH = (HandlerPredicated) handler;
				if (currentH.isReceive() && currentH.getTargetLoc().equals(to)) {
					Action recAction = ((EventAction) currentH.getEvent()).getAction();
					if (recAction.equals(brAction)) {
						return currentH;
					}
				}

			}
		}
		return null;

	}

	private boolean restOfReceivesAreSelfLoops(Action brAction, HandlerPredicated recHandler) {

		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;

					if (currentH.equals(recHandler)) // skip the edge acompanying the sender
						continue;

					if (currentH.isReceive()) {
						Action recAction = ((EventAction) currentH.getEvent()).getAction();
						if (recAction.equals(brAction)) {
							if (!currentH.getTargetLoc().equals(loc)) { // not a self-loop?
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	private void removeAllselfLoopsOnAction(Action brAction) {

		for (Location loc : p.getLocations()) {
			HashSet<Handler> handlersToDelete = new HashSet<Handler>();
			for (Handler handler : loc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.isReceive()) {
						Action recAction = ((EventAction) currentH.getEvent()).getAction();
						if (recAction.equals(brAction)) {
							if (currentH.getTargetLoc().equals(loc)) { // self-loop
								handlersToDelete.add(currentH);
							}
						}
					}
				}
			}
			loc.deleteHandlers(handlersToDelete);
		}

	}

	public void removeUnusedVars() {

		new PreProcessing(p, options).removeUnusedVars();

		if (options.summerizer_removeSetsAndIDsInSummarizer) {
			// additionally, remove all vars of type ID or Set (of IDs)
			HashSet<ExprVar> toRemove = new HashSet<ExprVar>();

			// Remove regular variables of type ID and Set
			for (VarDecl vd : p.getAllVariables()) {
				if (vd.getType() instanceof TypeID || vd.getType() instanceof TypeChooseSet) {
					toRemove.add(vd.asExprVar());
				}
			}

			for (ExprVar var : toRemove) {
				// Delete statements if they used removable user variables.
				if (p.getUserVars().containsKey(var.getName())) {
					deleteStatementsRelatedToVar(var);
				}
				// Delete the variable either way
				p.deleteVariable(var);
			}
		}
//		 System.out.println("SpecialVars after: " +
//		 Arrays.toString(p.getSpecialVars().values().toArray()));
//		 System.out.println("UserVars after: " +
//		 Arrays.toString(p.getUserVars().values().toArray()));
	}

	public void unrollRecVal() {
		ExprConstant recval;
		for (Location loc : p.getLocations()) {
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			for (Handler handler : loc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.isReceive()) {
						EventAction event = (EventAction) currentH.getEvent();
						Type actType = event.getAction().getDomain();
						recval = new ExprConstant(TypeInt.Constant.RECVAL.toString(), actType);
						recval.setAssosiatedAction(event.getAction());

						if (new Gatherer<ExprConstant>(ExprConstant.class).find(currentH).contains(recval)) {
							// First create the unrolled handlers
							if (actType instanceof TypeBool) {
								// Create new handler for true
								StmtBlock trueBody = currentH.getBody().clone();
								ExprConstant TrueCosnt = new ExprConstant(TypeBool.Constant.TRUE.toString(),
										TypeBool.T());
								trueBody.findExprOneAndReplaceWithTwo(recval, TrueCosnt);
								HandlerPredicated replaceH1 = new HandlerPredicated(currentH.getEvent(), trueBody,
										currentH.getPredicate());
								replacements.add(new Pair<Handler, Handler>(currentH, replaceH1));

								// Create new handler for false
								StmtBlock falseBody = currentH.getBody().clone();
								ExprConstant FalseCosnt = new ExprConstant(TypeBool.Constant.FALSE.toString(),
										TypeBool.T());
								falseBody.findExprOneAndReplaceWithTwo(recval, FalseCosnt);
								HandlerPredicated replaceH2 = new HandlerPredicated(currentH.getEvent(), falseBody,
										currentH.getPredicate());
								replacements.add(new Pair<Handler, Handler>(currentH, replaceH2));

							} else if (actType instanceof TypeInt) {
								TypeInt tint = (TypeInt) actType;
								if (!tint.getUpperBound().equalsIgnoreCase("N")) {

									int upperBound = Integer.valueOf(tint.getUpperBound());
									int lowerBound = Integer.valueOf(tint.getLowerBound());
									for (int j = lowerBound; j <= upperBound; j++) {
										StmtBlock newBody = currentH.getBody().clone();
										// System.out.println("xx: body before: " + newBody);
										ExprConstant JConst = new ExprConstant(String.valueOf(j), actType);
										newBody.findExprOneAndReplaceWithTwo(recval, JConst);
										// System.out.println("xx: body after: " + newBody);
										HandlerPredicated replaceH = new HandlerPredicated(currentH.getEvent(), newBody,
												currentH.getPredicate());
										replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
									}
								}
							}
						}
					}
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}

	}

	public void removeInternalSelfLoops() {
		for (Location loc : p.getLocations()) {
			HashSet<Handler> handlersToDelete = new HashSet<Handler>();
			for (Handler handler : loc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated ph = (HandlerPredicated) handler;
					if (ph.isInternal() && loc.equals(ph.getTargetLoc())) {
						handlersToDelete.add(handler);
					}
				}
			}
			loc.deleteHandlers(handlersToDelete);

		}
	}

	public void removeBACs() {
		for (BACInstance bac : BACInstances) {
			removeBACInstance(bac);
		}
	}

	public boolean hasBACs() {
		return !BACInstances.isEmpty();
	}

	public void enableNegativeReplyFromCrashLocs() {

		if (!p.hasCrashes()) {
			return;
		}

		for (BACInstance bac : BACInstances) {

			// get a hold of the br action and the negative reply
			Action brAction = bac.brAction;
			Action negativereply = bac.negativeReplyAction;

			// delete the self-loop handler from the current location and replace it with a
			// reply handler.
			Handler toReplace = null;
			for (Handler h : p.CRASH.getHandlers()) {
				if (h instanceof HandlerPredicated) {
					HandlerPredicated hp = (HandlerPredicated) h;
					if (hp.isReceive() && ((EventAction) hp.getEvent()).getAction().equals(brAction)) {
						toReplace = hp;
						break;
					}
				}
			}

			bac.receiveSelfLoopOnCrash = toReplace;

			// HandlerReply hr = new HandlerReply(new EventAction(brAction), new
			// ReactionReply(negativereply));
			// p.CRASH.addHandler(hr);
			// new PreProcessing(p, options).desugarReplyHandlers();

			// create the new location
			StmtGoto gstmt = new StmtGoto(p.CRASH.getName(), p);
			ExprVar to = new ExprVar(brAction + "_sID", TypeID.T());
			StmtSend sstmt = new StmtSend(negativereply, to);
			StmtBlock block = new StmtBlock(sstmt, gstmt);

			HandlerPredicated hp = new HandlerPredicated(new EventEpsilon(), block);
			String newLocName = p.CRASH.getName() + "_negRep";
			Location repLoc = new Location(newLocName, p.CRASH, p);
			repLoc.addHandler(hp);
			p.getTargetCrashLocNames().add(newLocName); //for code generation in kinara
			// create a replacement for the old handler
			StmtBlock replaceGoto = new StmtBlock(new StmtGoto(newLocName, p));
			HandlerPredicated replaceH = new HandlerPredicated(new EventAction(brAction), replaceGoto);

			p.CRASH.replaceHandler(toReplace, replaceH);

			// add all the new locations:
			p.addLocation(repLoc);

			// add a reference of stuff to delete later for convenience.
			bac.crashReplyLocToDeleteLater = repLoc;
			bac.crashReplyHandlerToDeleteLater = replaceH;
		}
	}

	public void removecrashReplyLogicAndRestoreSelfLoopOnCrash() {

		if (!p.hasCrashes()) {
			return;
		}

		for (BACInstance bac : BACInstances) {
			// remove things
			p.deleteLocation(bac.crashReplyLocToDeleteLater);
			p.CRASH.deleteHandler(bac.crashReplyHandlerToDeleteLater);

			// restore self-loop
			p.CRASH.addHandler(bac.receiveSelfLoopOnCrash);
		}

	}
}

class NegotiationInfo {
	Location src;
	Location dst;
	Action brAction;

	public NegotiationInfo(Location src, Location dst, Action brAction) {
		super();
		this.src = src;
		this.dst = dst;
		this.brAction = brAction;
	}
}
