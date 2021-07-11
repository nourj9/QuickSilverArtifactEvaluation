package Core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import lang.core.*;
import lang.core.Action.CommType;
import lang.events.Event;
import lang.events.EventAction;
import lang.events.EventEpsilon;
import lang.events.EventList;
import lang.events.EventPassive;
import lang.events.EventValueCons;
import lang.expr.ExprConstant;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.expr.Gatherer;
import lang.handler.Handler;
import lang.handler.HandlerCrash;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPassive;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerReply;
import lang.handler.HandlerSymbolic;
import lang.handler.HandlerValueCons;
import lang.handler.ReactionReply;
import lang.handler.ReactionWinLose;
import lang.stmts.Pair;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtBroadcast;
import lang.stmts.StmtGoto;
import lang.stmts.StmtIfThen;
import lang.stmts.StmtSend;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeID;
import lang.type.TypeInt;
import lang.type.TypeUnit;
import semantics.core.LocalAction;
import semantics.core.LocalState;
import semantics.core.LocalTransition;

public class PreProcessing {

	private Protocol p;
	private Options options;
	int uniqueLocNameIdx = 0;

	public PreProcessing(Protocol p, Options options) {
		this.p = p;
		this.options = options;
	}

	public void desugarReplyHandlers() {
		ArrayList<Location> locs = p.getLocations();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();
		// ArrayList<Location> toAdd = new ArrayList<Location>();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			// int i = 0;
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerReply) {
					HandlerReply currentH = (HandlerReply) handler;
					/**
					 * change on event reply msg to:
					 *  on event do 
					 *    goto loc_repi
					 * 
					 * location loc_repi
					 *  on _ do
					 * 	 rend(event.action.sID, msg)
					 *   goto loc 
					 */
					// create the new location
					StmtGoto gstmt = new StmtGoto(loc.getName(), p);

					Action actevent = ((EventAction) currentH.getEvent()).getAction();
					ExprVar to = new ExprVar(actevent + "_sID", TypeID.T());

					// add that to the sepcial variables, too. Since it's the same as if the user
					// wrote it.
					if (!p.getSpecialVars().containsKey(to.getName())) {
						// p.getSpecialVars().put(to.getName(), to.asVarDecl());
						p.addSpecialVariable(to.asVarDecl());
					}
					StmtSend sstmt = new StmtSend(currentH.getAction(), to);

					StmtBlock block = new StmtBlock(sstmt, gstmt);

					HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), block);
					// HandlerRegular regh = new HandlerRegular(new EventEpsilon(), block);
					String newLocName = loc.getName() + "_rep_" + (++uniqueLocNameIdx);
					Location repLoc = new Location(newLocName, loc, p);
					repLoc.addHandler(regh);
					addIfNotThere(locsToAdd, loc, repLoc);

					// create a replacement for the old handler
					StmtBlock replaceGoto = new StmtBlock(new StmtGoto(newLocName, p));
					HandlerPredicated replaceH = new HandlerPredicated(currentH.getEvent(), replaceGoto);

					replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
		// add all the new locations:
		p.addLocations(locsToAdd);
	}

	private void addIfNotThere(HashMap<Location, ArrayList<Location>> locsToAdd, Location loc, Location repLoc) {
		if (!locsToAdd.containsKey(loc)) {
			locsToAdd.put(loc, new ArrayList<>());
		}
		locsToAdd.get(loc).add(repLoc);
	}

	public void desugarPassiveHandler() {

		ArrayList<Location> locs = p.getLocations();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerPassive) {
					HandlerPassive currentH = (HandlerPassive) handler;
					/**
					 * passive ev1, ev2, ... to:
					 *  on evi do 
					 *    goto loc
					 */
					EventList passiveEventList = currentH.getEventList();
					loc.addToPassiveEventList(passiveEventList);
					for (String actionName : passiveEventList.getEventlist()) {
						StmtGoto gstmt = new StmtGoto(loc.getName(), p);
						Event event;
						if (p.getActionMap().containsKey(actionName)) {
							// normal action?
							event = new EventAction(p.getActionMap().get(actionName));
							HandlerPredicated replaceH = new HandlerPredicated(event, new StmtBlock(gstmt));
							replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
						} else if (p.getValconsInstMap().containsKey(actionName)) {
							EventValueCons sampleEvent = p.getValconsInstMap().get(actionName).iterator().next()
									.getEvent();
							// create a value cons handler with an empty event.
							ExprVar emptyProp = new ExprVar("_", TypeInt.Null());

							event = new EventValueCons(emptyProp, sampleEvent.getChID(), sampleEvent.getParticipants(),
									sampleEvent.getCardinality());

							HandlerSymbolic replaceH = new HandlerSymbolic(event, gstmt);
							replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
						} else if (p.getPartitionInstMap().containsKey(actionName)) {
							event = p.getPartitionInstMap().get(actionName).iterator().next().getEvent();

							HandlerSymbolic replaceH = new HandlerSymbolic(event, gstmt);
							replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
						} else {
							System.out.println(p.getActionMap());
							System.out.println("Unknown event in passive: " + actionName + "\n in location " + loc);
							System.exit(0);
						}
					}
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
	}

	public void addImplicitGotos() {

		ArrayList<Location> locs = p.getLocations();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				for (StmtBlock body : handler.getBodies()) {
					if (!body.endsWithGoto()) {
						StmtGoto gstmt = new StmtGoto(loc.getName(), p);
						body.addImplicitGoto(gstmt);
					}
				}
			}
		}
	}

	public void addImplicitElseBlocks() {

		ArrayList<Location> locs = p.getLocations();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				for (StmtBlock body : handler.getBodies()) {
					if (body.getNumOfConds() != 0) {
						body.addImplicitElseBlocks();
					}
				}
			}
		}
	}

	public void eleminateDeadCode() {

		ArrayList<Location> locs = p.getLocations();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				for (StmtBlock body : handler.getBodies()) {
					body.eleminateDeadCode();
				}
			}
		}
	}

	public void pushNonIfIntoIf() {

		ArrayList<Location> locs = p.getLocations();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				for (StmtBlock body : handler.getBodies()) {
					body.pushNonIfIntoIf();
				}
			}
		}
	}

	// going quick and dirty
	public void moveLastIfToNewLoc() {

		ArrayList<Location> locs = p.getLocations();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();
		// ArrayList<Location> locsToAdd = new ArrayList<Location>();

		for (Location loc : locs) {
			ArrayList<Handler> handlers = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			// int i = 0;
			for (Handler handler : handlers) {
//				if (handler instanceof HandlerRegular) {
//					HandlerRegular currentH = (HandlerRegular) handler;
//					StmtBlock block = currentH.getBody();
//					if (block.endsWithIfandHasNonEmptyPrefix()) {
//						Pair<StmtBlock, StmtBlock> parts = block.Split(block.getStmts().size() - 1);
//						// System.out.println("f"+ parts.getFirst());
//						// System.out.println("s"+ parts.getSecond());
//						/**
//						* from:
//						* location loc
//						*  on event do
//						*   part1
//						*	 part2 \\witn if
//						* 
//						* to:
//						* location loc
//						*  part1
//						*  goto loc_i_des
//						*  
//						* location loc_i_des
//						* on _ do
//						* part2
//						*/
//						// create the new location
//						HandlerRegular regh = new HandlerRegular(new EventEpsilon(), parts.getSecond());
//						String newLocName = loc.getName() + "_des_" + (++i);
//						Location desLoc = new Location(newLocName, loc, p);
//						desLoc.addHandler(regh);
//						addIfNotThere(locsToAdd, loc, desLoc);
//
//						// create a replacement for the old handler
//						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
//						StmtBlock replacementHBody = parts.getFirst();
//						replacementHBody.addStmt(replaceGoto);
//						HandlerRegular replaceH = new HandlerRegular(currentH.getEvent(), replacementHBody);
//						replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
//
//					}
//				} else 
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					StmtBlock block = currentH.getBody();
					if (block.endsWithIfandHasNonEmptyPrefix()) {
						Pair<StmtBlock, StmtBlock> parts = block.Split(block.getStmts().size() - 1);
						// System.out.println("f"+ parts.getFirst());
						// System.out.println("s"+ parts.getSecond());
						/**
						* from:
						* location loc
						*  on event do
						*   part1
						*	 part2 \\witn if
						* 
						* to:
						* location loc
						*  part1
						*  goto loc_i_des
						*  
						* location loc_i_des
						* on _ do
						* part2
						*/
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
						String newLocName = loc.getName() + "_des_" + (++uniqueLocNameIdx);
						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old handler
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						StmtBlock replacementHBody = parts.getFirst();
						replacementHBody.addStmt(replaceGoto);
						HandlerPredicated replaceH = new HandlerPredicated(currentH.getEvent(), replacementHBody,
								currentH.getPredicate());
						replacements.add(new Pair<Handler, Handler>(currentH, replaceH));

					}
				} else if (handler instanceof HandlerValueCons) {
					HandlerValueCons currentH = (HandlerValueCons) handler;
					StmtBlock block = currentH.getBody();
					if (block.endsWithIfandHasNonEmptyPrefix()) {
						Pair<StmtBlock, StmtBlock> parts = block.Split(block.getStmts().size() - 1);
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
						String newLocName = loc.getName() + "_des_" + (++uniqueLocNameIdx);
						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old handler
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						StmtBlock replacementHBody = parts.getFirst();
						replacementHBody.addStmt(replaceGoto);
						HandlerValueCons replaceH = new HandlerValueCons(currentH.getEvent(), replacementHBody);
						replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
					}
				} else if (handler instanceof HandlerPartitionCons) {
					HandlerPartitionCons currentH = (HandlerPartitionCons) handler;
					StmtBlock wblock = currentH.getWinBlock();
					StmtBlock resultingWinBlock = currentH.getWinBlock();
					StmtBlock resultingLoseBlock = currentH.getLoseBlock();
					boolean somethingChanged = false;
					if (wblock.endsWithIf()) {
						Pair<StmtBlock, StmtBlock> parts = wblock.Split(wblock.getStmts().size() - 1);
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
						String newLocName = loc.getName() + "_desw_" + (++uniqueLocNameIdx);
						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old win block
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						StmtBlock replacementHBody = parts.getFirst();
						replacementHBody.addStmt(replaceGoto);
						resultingWinBlock = replacementHBody;
						somethingChanged = true;
					}
					StmtBlock lblock = currentH.getLoseBlock();
					if (lblock.endsWithIf()) {
						Pair<StmtBlock, StmtBlock> parts = lblock.Split(lblock.getStmts().size() - 1);
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
						String newLocName = loc.getName() + "_desl_" + (++uniqueLocNameIdx);
						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old win block
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						StmtBlock replacementHBody = parts.getFirst();
						replacementHBody.addStmt(replaceGoto);
						resultingLoseBlock = replacementHBody;
						somethingChanged = true;
					}

					if (somethingChanged) {
						HandlerPartitionCons replaceH = new HandlerPartitionCons(currentH.getEvent(),
								new ReactionWinLose(resultingWinBlock, resultingLoseBlock));
						replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
					}

				} else if (handler instanceof HandlerSymbolic) {
					// do nothing for now

				} else if (handler instanceof HandlerCrash) {
					// do nothing for now

				} else {
					System.out.println("this handler should have been eleminated by preprcessing(5): " + handler);
					System.out.println(handler.getClass());
					System.exit(0);
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
		// add all the new locations:
		p.addLocationsAndAdjustSpecs(locsToAdd);

	}

	public void handleNestedIfs() {
		// LATER Auto-generated method stub

	}

	public void seperateMultipleSends() {

		ArrayList<Location> locs = p.getLocations();
		// ArrayList<Location> locsToAdd = new ArrayList<Location>();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			// int i = 0;
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerPredicated) { // receive or _ handler
					HandlerPredicated currentH = (HandlerPredicated) handler;
					// System.out.println("HANDLER " + handler);
					if (currentH.getEvent() instanceof EventEpsilon) { // _ handler
						StmtBlock block = currentH.getBody();
						if (block.getNumOfSnds() >= 2) { // two or more send
							int afterSendIndex = block.getFirstSendIndex() + 1;
							Pair<StmtBlock, StmtBlock> parts = block.Split(afterSendIndex);
							/**
							* from:
							* location loc
							* on event do
							* stmt1
							* send2
							* send3
							*
							* to:
							* location loc
							* stmt1
							* send2
							* goto loc_i_multiS
							*
							* location loc_i_multiS
							* on _ do
							* send3
							*/
							// create the new location
							HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
							String newLocName = loc.getName() + "_multiS_" + (++uniqueLocNameIdx);
							Location desLoc = new Location(newLocName, loc, p);
							desLoc.addHandler(regh);
							addIfNotThere(locsToAdd, loc, desLoc);

							// create a replacement for the old handler
							StmtGoto replaceGoto = new StmtGoto(newLocName, p);
							StmtBlock replacementHBody = parts.getFirst();
							replacementHBody.addStmt(replaceGoto);
							HandlerPredicated replaceH = new HandlerPredicated(currentH.getEvent(), replacementHBody,
									currentH.getPredicate());
							replacements.add(new Pair<Handler, Handler>(currentH, replaceH));

						}
					}
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
		// add all the new locations:
		p.addLocationsAndAdjustSpecs(locsToAdd);

	}

	public void winLoseWithOnlyGotos() {
		ArrayList<Location> locs = p.getLocations();
		// ArrayList<Location> locsToAdd = new ArrayList<Location>();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();

			for (Handler handler : hanlders) {

				// int i = 0;
				if (handler instanceof HandlerPartitionCons) {
					HandlerPartitionCons currentH = (HandlerPartitionCons) handler;
					// HandlerPartitionCons replaceH = new
					// HandlerPartitionCons(currentH.getEvent());
					StmtBlock cwblock = currentH.getWinBlock();
					StmtBlock clblock = currentH.getLoseBlock();
					if (!cwblock.hasOnlyAGoto()) {
						/**
						* from:
						*   body[stmt1, goto1]
						* 
						* to:
						*  goto loc_i_later_w
						* location loc_i_later_w
						*  on _ do
						*   body
						*/
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), cwblock);

						// if this became a problem, just use the old implementation. This is just a
						// naming thing after all.
						Location originalWinDest = cwblock.getLastGotoStmt().getTargetLoc();
						String newLocName = "after_" + loc.getName() + "_before_" + originalWinDest.getName() + "_"
								+ (++uniqueLocNameIdx) + "w";
						// String newLocName = loc.getName() + "_later_" + (++i) + "w";

						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old block
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						currentH.setWinBlock(new StmtBlock(replaceGoto));
					}
					if (!clblock.hasOnlyAGoto()) {
						/**
						* from:
						*   body[stmt1, goto1]
						* 
						* to:
						*  goto loc_i_later_l
						* location loc_i_later_l
						*  on _ do
						*   body
						*/
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), clblock);

						// if this became a problem, just use the old implementation. This is just a
						// naming thing after all.
						Location originalLoseDest = clblock.getLastGotoStmt().getTargetLoc();
						String newLocName = "after_" + loc.getName() + "_before_" + originalLoseDest.getName() + "_"
								+ (++uniqueLocNameIdx) + "l";
						// String newLocName = loc.getName() + "_later_" + (++i) + "l";

						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old block
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						currentH.setLoseBlock(new StmtBlock(replaceGoto));
					}
				}
			}
		}
		// can also make the new locations part of the post-location, and adjust specs.
		p.addLocations(locsToAdd);

	}

	public void flattenConditionals() {

		ArrayList<Location> locs = p.getLocations();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			for (Handler handler : hanlders) {
//				if (handler instanceof HandlerRegular) {
//					HandlerRegular currentH = (HandlerRegular) handler;
//					StmtBlock block = currentH.getBody();
//					if (block.getNumOfConds() == 0) { // "basic" handler
//						HandlerPredicated replaceH = new HandlerPredicated(currentH); // make it predicated
//						replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
//					} else if (block.isTopLevelIf()) {
//						StmtIfThen ifelsestmt = (StmtIfThen) block.getStmts().get(0);
//						HandlerPredicated replaceH1 = new HandlerPredicated(currentH.getEvent(), ifelsestmt.getThen(),
//								ifelsestmt.getCond());
//						HandlerPredicated replaceH2 = new HandlerPredicated(currentH.getEvent(), ifelsestmt.getElse(),
//								ifelsestmt.getNegatedCond());
//						replacements.add(new Pair<Handler, Handler>(currentH, replaceH1));
//						replacements.add(new Pair<Handler, Handler>(currentH, replaceH2));
//
//					} else {
//						System.out.println("Odd, this handler is not \"basic\" nor top-level if.." + handler);
//						System.exit(1);
//					}
//				} else 
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					StmtBlock block = currentH.getBody();
					if (block.getNumOfConds() == 0) { // "basic" handler
						// do nothing
					} else if (block.isTopLevelIf()) {
						StmtIfThen ifelsestmt = (StmtIfThen) block.getStmts().get(0);
						// newPred1 = oldPred ^ ifCond
						// newPred2 = oldPred ^ elseCond
						ExprOp newIfCond = new ExprOp(ExprOp.Op.AND, ifelsestmt.getCond(), currentH.getPredicate());
						ExprOp newElseCond = new ExprOp(ExprOp.Op.AND, ifelsestmt.getNegatedCond(),
								currentH.getPredicate());
						HandlerPredicated replaceH1 = new HandlerPredicated(currentH.getEvent(), ifelsestmt.getThen(),
								newIfCond);
						HandlerPredicated replaceH2 = new HandlerPredicated(currentH.getEvent(), ifelsestmt.getElse(),
								newElseCond);
						replacements.add(new Pair<Handler, Handler>(currentH, replaceH1));
						replacements.add(new Pair<Handler, Handler>(currentH, replaceH2));

					} else {
						System.out.println("Odd, this handler is not \"basic\" nor top-level if.." + handler);
						System.exit(1);
					}
				} else if (handler instanceof HandlerValueCons) {
					HandlerValueCons currentH = (HandlerValueCons) handler;
					StmtBlock block = currentH.getBody();
					block.pushNonIfIntoIf();
				} else if (handler instanceof HandlerPartitionCons) {
					HandlerPartitionCons currentH = (HandlerPartitionCons) handler;
					StmtBlock wblock = currentH.getWinBlock();
					wblock.pushNonIfIntoIf();
					StmtBlock lblock = currentH.getLoseBlock();
					lblock.pushNonIfIntoIf();
				} else if (handler instanceof HandlerSymbolic) {
					// do nothing for now

				} else if (handler instanceof HandlerCrash) {
					// do nothing for now

				} else {
					System.out.println("this handler should have been eleminated by preprcessing(6): " + handler);
					System.out.println(handler.getClass());
					System.exit(0);
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
	}

	public void valConsOnlyGotos() {
		ArrayList<Location> locs = p.getLocations();
		// ArrayList<Location> locsToAdd = new ArrayList<Location>();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();

			// int i = 0;

			for (Handler handler : hanlders) {

				if (handler instanceof HandlerValueCons) {
					HandlerValueCons currentH = (HandlerValueCons) handler;
					StmtBlock block = currentH.getBody();
					if (!block.hasOnlyAGoto()) {
						/**
						* from:
						*   body[stmt1, goto1]
						* 
						* to:
						*  goto loc_i_later
						* location loc_i_later
						*  on _ do
						*   body
						*/
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), block);

						// if this became a problem, just use the old implementation. This is just a
						// naming thing after all.
						Location originalVCDest = block.getLastGotoStmt().getTargetLoc();
						String newLocName = "after_" + loc.getName() + "_before_" + originalVCDest.getName() + "_"
								+ (++uniqueLocNameIdx);
						// String newLocName = loc.getName() + "_later_" + (++i);

						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old block
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						currentH.setBody(new StmtBlock(replaceGoto));
					}
				}
			}
		}
		// can also make the new locations part of the post-location, and adjust specs.
		p.addLocations(locsToAdd);

	}

	/** DONE: this call should, in fact, be over the predicated handlers, after the if statements have been fixed,
	 * the reason it is not there, is that the previous functions do not know how to handle PredicatedHandlers,
	 * which is essentially the same as normal handlers, this should be revised as follows:
	 * - desugar everything
	 * - change all regular handlers to predicated ones
	 * - rewrite all the functions to expect that
	 */

	public void seperateSendsFromReceives() {

		ArrayList<Location> locs = p.getLocations();
		// ArrayList<Location> locsToAdd = new ArrayList<Location>();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			// int i = 0;
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerPredicated) { // receive or _ handler
					HandlerPredicated currentH = (HandlerPredicated) handler;
					// System.out.println("HANDLER " + handler);
					if (currentH.getEvent() instanceof EventAction) { // receive handler
						StmtBlock block = currentH.getBody();
						if (block.getNumOfSnds() >= 1) { // one or more send
							int sendIndex = block.getFirstSendIndex();
							Pair<StmtBlock, StmtBlock> parts = block.Split(sendIndex);
							/**
							* from:
							* location loc
							* on event do
							* part1
							* part2 // starts with send
							*
							* to:
							* location loc
							* part1
							* goto loc_i_des
							*
							* location loc_i_des
							* on _ do
							* part2
							*/
							// create the new location
							HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
							String newLocName = loc.getName() + "_comm_" + (++uniqueLocNameIdx);
							Location desLoc = new Location(newLocName, loc, p);
							desLoc.addHandler(regh);
							addIfNotThere(locsToAdd, loc, desLoc);

							// create a replacement for the old handler
							StmtGoto replaceGoto = new StmtGoto(newLocName, p);
							StmtBlock replacementHBody = parts.getFirst();
							replacementHBody.addStmt(replaceGoto);
							HandlerPredicated replaceH = new HandlerPredicated(currentH.getEvent(), replacementHBody,
									currentH.getPredicate());
							replacements.add(new Pair<Handler, Handler>(currentH, replaceH));

						}
					}
					// while this not really a receive thing, we cannot send and do partition cons
					// at the same time, so it is the same concept.
				} else if (handler instanceof HandlerPartitionCons) {
					HandlerPartitionCons currentH = (HandlerPartitionCons) handler;

					StmtBlock wbody = currentH.getWinBlock();
					if (wbody.getNumOfSnds() >= 1) { // one or more send
						int sendIndex = wbody.getFirstSendIndex();
						Pair<StmtBlock, StmtBlock> parts = wbody.Split(sendIndex);
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
						String newLocName = loc.getName() + "_commw_" + (++uniqueLocNameIdx);
						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old body
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						StmtBlock replacementHBody = parts.getFirst();
						replacementHBody.addStmt(replaceGoto);
						currentH.setWinBlock(replacementHBody);
					}

					StmtBlock lbody = currentH.getWinBlock();

					if (lbody.getNumOfSnds() >= 1) { // one or more send
						int sendIndex = lbody.getFirstSendIndex();
						Pair<StmtBlock, StmtBlock> parts = lbody.Split(sendIndex);
						// create the new location
						HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(), parts.getSecond());
						String newLocName = loc.getName() + "_comml_" + (++uniqueLocNameIdx);
						Location desLoc = new Location(newLocName, loc, p);
						desLoc.addHandler(regh);
						addIfNotThere(locsToAdd, loc, desLoc);

						// create a replacement for the old body
						StmtGoto replaceGoto = new StmtGoto(newLocName, p);
						StmtBlock replacementHBody = parts.getFirst();
						replacementHBody.addStmt(replaceGoto);
						currentH.setLoseBlock(replacementHBody);
					}
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
		// add all the new locations:
		p.addLocationsAndAdjustSpecs(locsToAdd);
	}

	public void PreCodeGenerationSanityChecks() {
		// LATER Complete as needed
		// 1. all regular, passive, and reply handlers are eliminated.
		// 2. every goto (concrete) target is an actual location
		// 3. every variable expr uses a defined or special variable.

		for (Location loc : p.getLocations()) {
			for (Handler h : loc.getHandlers()) {
				if (h instanceof HandlerPassive || h instanceof HandlerReply) {
					System.err.println("Error: In location " + loc.getName()
							+ " this handler should have been eleminated by PreProcessing: \n" + h);
					System.exit(0);
				} else if (h instanceof HandlerPredicated) {
					HandlerPredicated handler = (HandlerPredicated) h;

					Pair<Boolean, String> results = handler.checkNormalization();

					if (!results.getFirst()) {
						System.err.println(results.getSecond());
						System.exit(0);
					}

					StmtGoto gotoStmt = handler.getBody().getLastGotoStmt();
					if (!gotoStmt.hasHoleTarget()) { // not a hole
						gotoStmt.getTargetLoc(); // this will trigger an error interally.
					}

				} else if (h instanceof HandlerValueCons) {
					HandlerValueCons handler = (HandlerValueCons) h;
					if (!handler.getBody().hasOnlyAGoto()) {
						System.err.println("Error: In location " + loc.getName()
								+ " this ValueCons handler has statements other than a Goto: \n" + handler);
						System.exit(0);
					}
					StmtGoto gotoStmt = handler.getBody().getLastGotoStmt();
					if (!gotoStmt.hasHoleTarget()) { // not a hole
						gotoStmt.getTargetLoc(); // this will trigger an error interally.
					}

				} else if (h instanceof HandlerPartitionCons) {
					HandlerPartitionCons handler = (HandlerPartitionCons) h;
// 					no longer needed
//					if (!handler.getWinBlock().hasOnlyAGoto()) {
//						System.err.println("Error: In location " + loc.getName()
//								+ " this PartitionCons win handler has statements other than a Goto: \n" + handler);
//						System.exit(0);
//					}
					StmtGoto wgotoStmt = handler.getWinBlock().getLastGotoStmt();
					if (!wgotoStmt.hasHoleTarget()) { // not a hole
						wgotoStmt.getTargetLoc(); // this will trigger an error interally.
					}
// 					no longer needed
//					if (!handler.getLoseBlock().hasOnlyAGoto()) {
//						System.err.println("Error: In location " + loc.getName()
//								+ " this PartitionCons lose handler has statements other than a Goto: \n" + handler);
//						System.exit(0);
//					}
					StmtGoto lgotoStmt = handler.getLoseBlock().getLastGotoStmt();
					if (!lgotoStmt.hasHoleTarget()) { // not a hole
						lgotoStmt.getTargetLoc(); // this will trigger an error interally.
					}
				}
			}
		}

		// every variable expr uses a defined or special variable.
		// get all the variables used in the program.
		HashSet<ExprVar> remainingVars = new HashSet<ExprVar>();
		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {
//				remainingVars.addAll(new NewGatherer<ExprVar>().FindExprT(handler));
				remainingVars.addAll(new Gatherer<ExprVar>(ExprVar.class).find(handler));
			}
		}

		for (ExprVar var : remainingVars) {
			if (p.getUserVars().containsKey(var.getName()))
				continue;
			if (p.getSpecialVars().containsKey(var.getName()))
				continue;
			if (var.getType().equals(TypeInt.Null())) {
				//System.out.println("null var: " + var);
				continue;
			}

			System.err.println("Error: variable " + var.getName() + " is used but not defined");
			Thread.dumpStack();
			System.exit(0);

		}

	}

	/**
	 * New: we need to do the OLD stuff on before we go to the predicated handlers
	 * example: =================================================================
	location Collect
	on recv(Reply) do
	if(Reply.val == 1){
	Set.add(Reply.sID)
	   totalReplies := totalReplies + 1
	goto Collect_des_1
	}
	else{
	goto Collect_des_1
	}
	=================================================================
	
	location Collect
	on recv(Reply) do
	Reply.val = RECVAL
	goto fancyloc1
	
	loc fancyloc1
	on _ where do
	if(Reply.val == 1){
	Set.add(Reply.sID)
	   totalReplies := totalReplies + 1
	goto Collect_des_1
	}
	else{
	goto Collect_des_1
	}
	 * 
	 * 
	 * 
	 * OLD:The idea here is that, we need to ensure that the following is not allowed:
	 * 
	 * on recv(Reply) where (Reply.val == 1) do
	 *   totalReplies := totalReplies + 1
	 *   goto Collect_des_1
	 *   
	 * because the predicate refers to the value received in the same handler. We transform this to:
	 * 
	 * on recv(Reply) where (TRUE) do
	 *   Reply.val = new ExprConst("RecVal",new TypeInt()); 
	 *   goto fancy_loc
	 * 
	 * loc fancy_loc
	 *   on _ where (Reply.val == 1)
	 *   totalReplies := totalReplies + 1
	 *   goto Collect_des_1
	 *   
	 * This is a similar Idea to the OTHER for the ID..
	 * Something to think about: if we do this on the "top-level" handlers, we can save a handler:
	 * right now, we go as follows: 
	 * if(v=1) -1- else -2- 
	 * becomes
	 * on - where (v=1) -1-
	 * on - where (v!=1) -2-
	 * then those become two handlers, so a total of 4
	 * 
	 * Alternatively,
	 * if(v=1) -1- else -2- 
	 * becomes
	 * handler (a) to save ReVal
	 * if (RecVal) -1- else -2- 
	 * then we have handler (a)
	 * then (b) (c) for the if statement
	 */

	public void handleConditionOverValOfAction() {
		ArrayList<Location> locs = p.getLocations();
		// ArrayList<Location> locsToAdd = new ArrayList<Location>();
		HashMap<Location, ArrayList<Location>> locsToAdd = new HashMap<Location, ArrayList<Location>>();

		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			// int i = 0;
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerPredicated) { // receive or _ handler
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.getEvent() instanceof EventAction) { // receive handler
						Action action = ((EventAction) currentH.getEvent()).getAction();
						if (action.getDomain() instanceof TypeInt) { // skip this for unit actions
							StmtBlock block = currentH.getBody();
							if (block.isTopLevelIf()) {
								Expression cond = ((StmtIfThen) block.getStmts().get(0)).getCond();
								ExprVar actNameDotVal = new ExprVar(action.getName() + "_val", action.getDomain());
								if (conditionContains(cond, actNameDotVal)) {

									// /**
									// * from:
									// * location loc
									// * on event do
									// * if (cond [with event.val variable]) stmt1 else stmt2
									// *
									// *
									// * to:
									// * location loc
									// * reply.val = RECVAL
									// * goto loc_i_saveVal
									// *
									// * location loc_i_saveVal
									// * on _ do
									// * if (cond) stmt1 else stmt2
									// */
									// create the new location
									HandlerPredicated regh = new HandlerPredicated(new EventEpsilon(),
											currentH.getBody());
									String newLocName = loc.getName() + "_saveVal_" + (++uniqueLocNameIdx);
									Location desLoc = new Location(newLocName, loc, p);
									desLoc.addHandler(regh);
									addIfNotThere(locsToAdd, loc, desLoc);

									// create a replacement for the old handler
									StmtBlock replacementHBody = new StmtBlock();
									// System.out.println(currentH);
									// System.out.println(actNameDotVal);
									TypeInt typeAsInt = (TypeInt) actNameDotVal.getType();
									ExprConstant recValConst = new ExprConstant(TypeInt.Constant.RECVAL.toString(),
											typeAsInt);
									recValConst.setAssosiatedAction(action);
									StmtAssign assignStmt = new StmtAssign(actNameDotVal, recValConst);
									StmtGoto replaceGoto = new StmtGoto(newLocName, p);
									replacementHBody.addStmt(assignStmt);
									replacementHBody.addStmt(replaceGoto);
									HandlerPredicated replaceH = new HandlerPredicated(currentH.getEvent(),
											replacementHBody);
									replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
								}
							}
						}
					}
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}
		// add all the new locations:
		p.addLocations(locsToAdd);

	}

	private boolean conditionContains(Expression cond, ExprVar expr) {
		HashSet<ExprVar> gatheredExpressions = new Gatherer<ExprVar>(ExprVar.class).find(cond);
		return gatheredExpressions.contains(expr);
	}

	/**
	 * Here, the idea is that, if we are in a receive handler and we want sID, we can use it directly here, and otherwise
	 * use actname.sID variable.
	 * BEFORE
		  on smoke do
		      set.add(smoke.sID) <--sid on the same action
		      goto senloc
		
		AFTER
		  on smoke do
		      set.add(OTHER)  
		      goto senloc
	
	 * */
	public void handleReferenceToSidInSameHandler() {

		ArrayList<Location> locs = p.getLocations();
		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerPredicated) { // receive or _ handler
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.getEvent() instanceof EventAction) { // receive handler
						Action action = ((EventAction) currentH.getEvent()).getAction();
						String varName = action.getName() + "_sID";
						ExprVar actNameDotSid = new ExprVar(varName, TypeID.T());
						ExprConstant constOther = new ExprConstant(TypeID.Constant.OTHER.toString(), TypeID.T());
						constOther.setAssosiatedAction(action);
						if (p.getSpecialVars().containsKey(varName)) { // this is an optimization
							finaExprOneAndReplaceWithTwo(currentH, actNameDotSid, constOther);
						}
					}
				}
			}
		}
	}

	/**
	 * this function skips lhs of statements*/
	private void finaExprOneAndReplaceWithTwo(HandlerPredicated handler, Expression toReplace, Expression replacement) {
		// handle the predicate
		handler.getPredicate().findExprOneAndReplaceWithTwo(toReplace, replacement);
		// handle the body
		handler.getBody().findExprOneAndReplaceWithTwo(toReplace, replacement);
	}

	public void handleReferenceToDotValInSameHandler() {

		ArrayList<Location> locs = p.getLocations();
		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerPredicated) { // receive or _ handler
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.getEvent() instanceof EventAction) { // receive handler
						Action action = ((EventAction) currentH.getEvent()).getAction();
						if (action.getDomain() instanceof TypeInt) { // skip this for unit actions
							String varName = action.getName() + "_val";
							TypeInt actDom = (TypeInt) action.getDomain();
							ExprVar actNameDotVal = new ExprVar(varName, actDom);
							ExprConstant constRecVal = new ExprConstant(TypeInt.Constant.RECVAL.toString(), actDom);
							constRecVal.setAssosiatedAction(action);
							if (p.getSpecialVars().containsKey(varName)) { // this is an optimization
								finaExprOneAndReplaceWithTwo(currentH, actNameDotVal, constRecVal);
							}
						}
					}
				}
			}
		}
	}

	public void removeUnusedVars() {
		// get all the variables used in the program.
		HashSet<ExprVar> remainingVars = new HashSet<ExprVar>();
		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {
				remainingVars.addAll(new Gatherer<ExprVar>(ExprVar.class).find(handler));
			}
		}

		// prepare things to delete: loop over the old vars and collect the missing ones
		HashSet<ExprVar> varsToDelete = new HashSet<ExprVar>();

		for (VarDecl currentVar : p.getAllVariables()) {
			if (!remainingVars.contains(currentVar.asExprVar())) {
				varsToDelete.add(currentVar.asExprVar());
			}
		}

		// delete things
		p.deleteVariables(varsToDelete);

	}

	/**
	 * The idea is to capture the following: 
	 * on recv(Reply) do 
	 *   if (Reply.payld == 1){ 
	 *       participants.add(Reply.sID);
	 *   }
	 *   ....
	 *   
	 * so we can change it to something more low level:
	 * participants[Reply.sID] = Reply.payld
	 * */
	public void optimizeSetAddition() {
		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.getEvent() instanceof EventAction) { // receive handler
						EventAction action = (EventAction) currentH.getEvent();

						if (action.getAction().getDomain().equals(new TypeInt("0", "1"))) { // has a binary domain?
							StmtBlock block = currentH.getBody();
							block.optimizeSetAddition(action, p);
						}
					}
				}
			}
		}
	}

	private void addWinVarReset() {
		ArrayList<Location> locs = p.getLocations();
		for (Location loc : locs) {
			ArrayList<Handler> hanlders = loc.getHandlers();
			for (Handler handler : hanlders) {
				if (handler instanceof HandlerValueCons) {
					HandlerValueCons currentH = (HandlerValueCons) handler;
					StmtBlock block = currentH.getBody();

					// get a hold of winvar.
					ExprVar winVar = p.getSpecialVars().get(currentH.getEvent().getChID() + "_wval").asExprVar();

					block.addWinVarReset(winVar);
				}
			}
		}
	}

	/** this turns 
	 * br hi : int [1,2] 
	 * to
	 * br bi_1 :unit
	 * br bi_2 :unit
	 * and changes the uses accordingly:
	 * First,
	 * change 
	 * on rec(hi) do
	 *  x = hi.payld
	 * to 
	 * on rec(hi_1) do
	 *  x = 1
	 * on rec(hi_2) do
	 *  x = 2
	 *  Then, change
	 *  br (hi,y)
	 *  to
	 *  if(y = 1)
	 *    br(hi, 1)
	 *  else
	 *    br(hi, 2)
	 */
	private void flattenBroadcasts() {

		// to avoid concurrent modification.
		ArrayList<Pair<Action, Collection<Action>>> replacements = new ArrayList<Pair<Action, Collection<Action>>>();

		// loop over the actions and handle
		for (Action action : p.getActions()) {
			if (action.isBr() && !action.getDomain().equals(TypeUnit.T())) { // non-unit broadcast

				// get a hold of the domain values
				ArrayList<ExprConstant> vals = getDomainValues(p, action.getDomain());

				// create a new action for each value. Keep a map for convenience.
				HashMap<ExprConstant, Action> valToActMap = new HashMap<ExprConstant, Action>();
				for (ExprConstant val : vals) {

					Action actForVal = new Action(action.getName() + "_" + val.getVal(), action.isEnv(), TypeUnit.T(),
							action.getCommType());

					valToActMap.put(val, actForVal);
				}
				// first, handle the receives
				flattenReceives(p, action, valToActMap);

				// then, handle the sends
				flattenSends(p, action, valToActMap);

				// finally, cleanup old things, add new actions.
				replacements.add(new Pair<Action, Collection<Action>>(action, valToActMap.values()));
			}
		}

		p.replaceActions(replacements);
	}

	private void flattenSends(Protocol p, Action action, HashMap<ExprConstant, Action> valToActMap) {
		for (Location loc : p.getLocations()) {

			for (Handler handler : loc.getHandlers()) {
				for (StmtBlock body : handler.getBodies()) {
					// get all br sends over the original action
					ArrayList<StmtBroadcast> sendStmts = body.getBroadcastsOverAction(action);

					// replace them with the appropriate thing.
					for (StmtBroadcast stmtBroadcast : sendStmts) {
						Statement replacement = buildFlattenedSendCommand(p, stmtBroadcast, action, valToActMap);
						body.replaceStmt(stmtBroadcast, replacement);
					}
				}
			}
		}
	}

	/** 
	 * takes br(hi,1) and returns br(hi_1)
	 * takes br(hi,msg) and returns
	 * if(msg == 1) then
	 *   br(hi_1)
	 * else
	 *   br(hi_2)
	 *
	 * in general, it'll take care of the domain of msg.
	 */

	private Statement buildFlattenedSendCommand(Protocol p, StmtBroadcast originalBr, Action action,
			HashMap<ExprConstant, Action> valToActMap) {

		Expression valueBeingSentExp = originalBr.getAct().getValue();

		if (valueBeingSentExp instanceof ExprConstant) {

			// build a new statement using the right action.
			return new StmtBroadcast(valToActMap.get((ExprConstant) valueBeingSentExp));

		} else if (valueBeingSentExp instanceof ExprVar) {

			// build a nested if statement.
			ExprVar msg = (ExprVar) valueBeingSentExp;
			ArrayList<ExprConstant> domVals = getDomainValues(p, msg.getType());

			// can we get an empty dom? we shouldn't.

			// build it recursively
			return buildIfNest(msg, domVals, valToActMap);
		} else {
			// should not happen, at least for now.
			System.err.println("Error: Not sure how to handle this, a missing case?");
			Thread.dumpStack();
			System.exit(-1);
			return null;
		}
	}

	// start index is just to reuse the domVal.
	private Statement buildIfNest(ExprVar msg, ArrayList<ExprConstant> domVals,
			HashMap<ExprConstant, Action> valToActMap) {

		// base case.
		if (domVals.size() == 1) {
			ExprConstant val = domVals.remove(0);
			return new StmtBroadcast(valToActMap.get((ExprConstant) val));

		} else {
			ExprConstant val = domVals.remove(0);

			ExprOp cond = new ExprOp(ExprOp.Op.EQ, msg, val);
			StmtBlock thenbranch = new StmtBlock(new StmtBroadcast(valToActMap.get(val)));
			StmtBlock elsebranch = new StmtBlock(buildIfNest(msg, domVals, valToActMap));

			StmtIfThen ifStmt = new StmtIfThen(cond, thenbranch, elsebranch);
			return ifStmt;
		}
	}

	public void flattenReceives(Protocol p, Action action, HashMap<ExprConstant, Action> valToActMap) {

		// will need it later.
		ExprVar actNameDotVal = new ExprVar(action.getName() + "_val", action.getDomain());

		for (Location loc : p.getLocations()) {
			ArrayList<Pair<Handler, Handler>> replacements = new ArrayList<Pair<Handler, Handler>>();
			for (Handler handler : loc.getHandlers()) {
				if (handler instanceof HandlerPredicated) {
					HandlerPredicated currentH = (HandlerPredicated) handler;
					if (currentH.isReceive()) {
						EventAction event = (EventAction) currentH.getEvent();
						Action recAction = event.getAction();

						// action of interest
						if (action.equals(recAction)) {

							// replicate the current handler for each value.
							for (ExprConstant val : valToActMap.keySet()) {

								// Create new handler using the val instead of the .payld thing.
								StmtBlock newBody = currentH.getBody().clone();
								newBody.findExprOneAndReplaceWithTwo(actNameDotVal, val);

								// new event, based on the val, too.
								EventAction valEvent = new EventAction(valToActMap.get(val));

								HandlerPredicated replaceH = new HandlerPredicated(valEvent, newBody,
										currentH.getPredicate());
								replacements.add(new Pair<Handler, Handler>(currentH, replaceH));
							}
						}
					}
				}
			}
			// apply replacements!
			loc.replaceHandlers(replacements);
		}

	}

	private ArrayList<ExprConstant> getDomainValues(Protocol p, Type type) {

		ArrayList<ExprConstant> vals = new ArrayList<ExprConstant>();

		// return values based on type
		if (type instanceof TypeBool) {
			vals.add(new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T()));
			vals.add(new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T()));

		} else if (type instanceof TypeInt) {
			TypeInt tint = (TypeInt) type;
			if (!tint.getUpperBound().equalsIgnoreCase("N")) {
				int upperBound = Integer.valueOf(tint.getUpperBound());
				int lowerBound = Integer.valueOf(tint.getLowerBound());
				for (int j = lowerBound; j <= upperBound; j++) {
					vals.add(new ExprConstant(String.valueOf(j), type));
				}
			}

		} else {
			System.err.println("Error: getDomainValues called on an unkown type " + type);
			Thread.dumpStack();
			System.exit(-1);
		}

		return vals;
	}

	public void preprocess() {

		// LATER make this fixed-point

		// if (options.includeProcessFailureModeling) {
		addCrashLocation();
		// }

		addWinVarReset();
		desugarReplyHandlers();
		desugarPassiveHandler();
		flattenBroadcasts();
		optimizeSetAddition();
		addImplicitElseBlocks();
		addImplicitGotos();
		winLoseWithOnlyGotos(); // TODO: remove and make sure susequent code goes
		// inside wam hanlders.
		valConsOnlyGotos(); // ok
		eleminateDeadCode(); // ok
		for (int i = 0; i < 4; ++i) { // TODO: change this to a fixed point.
			pushNonIfIntoIf(); // ok
			eleminateDeadCode(); // ok
			moveLastIfToNewLoc(); //
			addImplicitGotos(); //
			eleminateDeadCode(); // ok
		}
		pushNonIfIntoIf(); // ok
		eleminateDeadCode(); // ok
		handleNestedIfs(); // ok
		seperateSendsFromReceives(); // modified.
		handleConditionOverValOfAction(); // probably ok
		flattenConditionals(); // probably ok
		flattenConditionals(); // probably ok
		flattenConditionals(); // probably ok
		flattenConditionals(); // probably ok
		seperateSendsFromReceives(); // modified.
		seperateMultipleSends(); // probably ok, double check..
		handleReferenceToSidInSameHandler(); // TODO uncomment! probably ok
		handleReferenceToDotValInSameHandler(); // TODO: this will probably not be needed.
		removeUnusedVars();

		// if (options.includeProcessFailureModeling) {
		addCrashHandlers();
		// }

		// addPassiveSelfLoopsOnGeneratedStates(p);
		PreCodeGenerationSanityChecks();
	}

	private void addCrashHandlers() {

		if (!p.hasCrashes()) {
			return;
		}

		// first, handle the location crashes.
		if (p.hasLocationCrashes()) {

			HashSet<String> crashableLocations = p.getLocationCrashs();

			for (Location loc : p.getLocations()) {

				// skip the crash location itself
				if (loc == p.CRASH) {
					continue;
				}

				if (crashableLocations.contains(loc.getName())) {
					HandlerCrash crashHandlder = new HandlerCrash(loc.getName() + "Crash", TypeBool.T().makeTrue(),
							null, p);
					loc.addHandler(crashHandlder);
				}
			}
		}

		if (p.hasActionCrashes()) {

			HashSet<String> crashableActions = p.getActionCrashs();

			for (Location loc : p.getLocations()) {

				// skip the crash location itself
				if (loc == p.CRASH) {
					continue;
				}

				ArrayList<Handler> handlersToAdd = new ArrayList<Handler>();

				// locate handlers with crashable actions.
				for (Handler handler : loc.getHandlers()) {

					// if the location has already crashed, then this is really just do the value
					// cons ones, as the modeling for the others is subsumed if the location
					// itself crashed.

					if (p.getLocationCrashs().contains(loc.getName()) && !(handler instanceof HandlerValueCons)) {
						continue;
					}

					if (handler instanceof HandlerPredicated) {

						HandlerPredicated hp = (HandlerPredicated) handler;

						if (hp.isReceive()) {
							Action receiveAction = ((EventAction) hp.getEvent()).getAction();
							if (crashableActions.contains(receiveAction.getName())) {
								if (receiveAction.isBr()) { // broadcast receive
									HandlerCrash crashHandlder = new HandlerCrash(receiveAction.getName(),
											hp.getPredicate(), hp, p);
									handlersToAdd.add(crashHandlder);
								} else { // pairwise receive
									// nothing for now.
								}
							}

						} else if (hp.isSend()) {
							Action sendAction = hp.getSendAction();
							if (crashableActions.contains(sendAction.getName())) {
								if (sendAction.isBr()) { // broadcast send
									HandlerCrash crashHandlder = new HandlerCrash(sendAction.getName(),
											hp.getPredicate(), hp, p);
									handlersToAdd.add(crashHandlder);
								} else { // pairwise send
									// nothing for now.
								}
							}
						} else if (hp.isPassive()) { // we added this to have people move on passive consensus.
							// nothing for now.
						} else { // internal
							// nothing for now.
						}

					} else if (handler instanceof HandlerPartitionCons) {
						HandlerPartitionCons currentH = (HandlerPartitionCons) handler;
						if (crashableActions.contains(currentH.getEvent().getChID())) {
							HandlerCrash crashHandlder = new HandlerCrash(currentH.getEvent().getChID(),
									TypeBool.T().makeTrue(), currentH, p);
							handlersToAdd.add(crashHandlder);
						}
					} else if (handler instanceof HandlerValueCons) {
						HandlerValueCons currentH = (HandlerValueCons) handler;
						if (crashableActions.contains(currentH.getEvent().getChID())) {
							HandlerCrash crashHandlder = new HandlerCrash(currentH.getEvent().getChID(),
									TypeBool.T().makeTrue(), currentH, p);
							handlersToAdd.add(crashHandlder);
						}
					} else if (handler instanceof HandlerSymbolic) {
						// nothing for now
					} else if (handler instanceof HandlerPassive) {
						HandlerPassive currentH = (HandlerPassive) handler;

						for (String event : currentH.getEventList().getEventlist()) {
							if (p.getActionMap().containsKey(event) && p.getActionMap().get(event).isBr()) { // only do
																												// this
																												// for
																												// broadcasts.
								HandlerCrash crashHandlder = new HandlerCrash(event, TypeBool.T().makeTrue(), currentH,
										p);
								handlersToAdd.add(crashHandlder);
							}
						}

					} else if (handler instanceof HandlerReply) {
						// nothing for now
					} else {
						// eh
					}
				}

				// add new handlers, if any.
				for (Handler h : handlersToAdd) {
					loc.addHandler(h);
				}
			}
		}

	}

	private void addCrashLocation() {
		if (p.hasCrashes()) {

			// make this passive on every globally-sync action in the system.
			EventList list = new EventList();

			// start with broadcasts
			for (Action action : p.getActions()) {
				if (action.isBr()) {
					list.addEvent(action.getName());
				}
			}

			// add partitionCons
			for (String pcInst : p.getPartitionInstMap().keySet()) {
				list.addEvent(pcInst);

			}

			// add ValueCons
			for (String vcInst : p.getValconsInstMap().keySet()) {
				list.addEvent(vcInst);

			}

			p.CRASH.addHandler(new HandlerPassive(list));

			// Finally, we will add a self-loop on an environment broadcast-receive
			// transition so that when *all* processes have crashed, we do not get a
			// deadlock state, since they now can take this fake transition
			//
			// Note: adding an internal self-loop so the system is not "deadlocked" is a bad
			// idea because this will hide *all* deadlocks.

			/** TODO: not needed for kinara anymore.
			Action deadlockAvoider = new Action("deadlockAvoider", true, TypeUnit.T(), CommType.BROADCAST);
			p.addAction(deadlockAvoider);
			p.getEnvInputs().add(deadlockAvoider.getName());
			
			// self-loop receive
			HandlerPredicated hp = new HandlerPredicated(new EventAction(deadlockAvoider),
					new StmtBlock(new StmtGoto(p.CRASH.getName(), p)));
			
			p.CRASH.addHandler(hp);
			*/
			// add the location to the protocol.
			p.addLocation(p.CRASH);
			p.getTargetCrashLocNames().add(p.CRASH.getName()); //for code generation in kinara
		}
	}

}
