package semantics.core;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import Core.KinaraCodeGenerator;
import Core.Options;
import lang.core.*;
import lang.events.*;
import lang.expr.*;
import lang.handler.Handler;
import lang.handler.HandlerCrash;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerSymbolic;
import lang.handler.HandlerValueCons;
import lang.type.*;
import semantics.analysis.StructureSummarizer;

public class SemanticsGenerator {
	private Protocol p;
	private Options options;
	// private StructureSummarizer summerizer;

	public SemanticsGenerator(Protocol p, StructureSummarizer summarizer, Options options) {
		this.p = p;
		this.options = options;
		// this.summerizer = summarizer;

		// this takes care of things like BAC and negotations steps etc.

		summarizer.removeUnusedVars();

		// Internal self-loops are OK for safety but will report liveness/deadlock
		// problems. We will keep them here, and ignore them when merging phases.
		// summarizer.removeInternalSelfLoops();

		if (options.summerizer_removeNegotationStepInGeneral) {
			summarizer.removeNegotationSteps();
		}
		// take care of the value being received, essentially, by accounting for RECVAL.
		summarizer.unrollRecVal();
		// SemanticsGenerator.addPassiveSelfLoopsOnGeneratedStates(p);

		// prepareForSemanticsGeneration();

	}

	public ProcessSemantics createSemantics() throws Exception {

		HashMap<String, LocalState> states = new HashMap<String, LocalState>();
		ArrayList<LocalTransition> transitions = new ArrayList<LocalTransition>();

		VariableValuation currSigma = new VariableValuation();
		ArrayList<VariableValuation> swapvar;
		ArrayList<VariableValuation> createdSet = new ArrayList<VariableValuation>();
		ArrayList<VariableValuation> workingSet = new ArrayList<VariableValuation>();
		workingSet.add(currSigma);
		for (VarDecl vd : p.getNonSetVariables()) {
			Type t = vd.getType();
			for (int i = workingSet.size(); i-- > 0; workingSet.remove(i)) {
				currSigma = workingSet.get(i);

				createdSet.addAll(expandStateSpace(currSigma, vd.getName(), t));
			}

			swapvar = createdSet;
			createdSet = workingSet;
			workingSet = swapvar;
		}

		for (Location loc : p.getLocations()) {

			// skip here, we will create one state for it later.
			if (loc == p.CRASH) {
				continue;
			}

			for (VariableValuation valuation : workingSet) {
				LocalState currState = new LocalState(loc, valuation);
				states.put(currState.toString(), currState);
			}
		}

		LocalState crashState = null;

		if (options.includeProcessFailureModeling || options.includeProcessFailureModeling_inSemGen) {
			crashState = new LocalState(p.CRASH);
			// add crashed state manually now
			states.put(crashState.toString(), crashState);
		}

		for (LocalState src : states.values()) {
			for (Location loc : p.getLocations()) {
				if (src.getLoc() != loc) {
					continue;
				}
				for (Handler h : loc.getHandlers()) {
					if (h instanceof HandlerPredicated) {
						HandlerPredicated hp = (HandlerPredicated) h;
						if (src.getSigma().evalGuard(hp.getPredicate())) {
							Location destLoc = hp.getTargetLoc();
							VariableValuation destValuation = src.getSigma().evalUpdate(hp.getNonSetUpdates());
							LocalState dest = states.get(LocalState.asString(destLoc, destValuation));
							LocalTransition tr;

							if (dest == null) {
								System.err.println("dest state does not exist in the current semantics!");
								System.err.println("handler: " + hp);
								System.err.println("destValuation: " + destValuation);
								// Thread.dumpStack();
								// System.exit(-1);
								continue; // TODO: try to do this ps building dynamically to avoid this.
							}

							if (hp.isReceive()) {
								Action receiveAction = ((EventAction) hp.getEvent()).getAction();
								if (receiveAction.isBr()) {

									if (!receiveAction.isEnv()) {
										tr = LocalTransition.make(src, dest, // source and dest
												LocalAction.ActionType.BROADCAST_RECV, // type
												receiveAction.getName(), // label
												hp, // handler
												receiveAction.isEnv(), // env
												false // no proposal
										);
										transitions.add(tr);
									} else {
										// if this action is an env receive action, transform it to a non-env negotation
										// step with the exception that the crashing processes do not get the send
										// transition!
										tr = LocalTransition.make(src, dest, // source and dest
												LocalAction.ActionType.BROADCAST_RECV, // type
												receiveAction.getName(), // label
												hp, // handler
												false, // env
												false // no proposal
										);
										transitions.add(tr);

//										if (!src.equals(crashState)) { // crash states do not get the send!
										if (!src.equals(dest)) { // crash states do not get the send, or they're
																// passive, i.e., self loops
											tr = LocalTransition.make(src, dest, // source and dest
													LocalAction.ActionType.BROADCAST_SEND, // type
													receiveAction.getName(), // label
													hp, // handler
													false, // env
													false // no proposal
											);
											transitions.add(tr);
										}

									}

									// add the a self loop for the crash state
									//
									//
									if (options.includeProcessFailureModeling_inSemGen) {
										// OUTDATED
										LocalTransition crashTr = LocalTransition.make(crashState, crashState, // source
																												// and
																												// dest
												LocalAction.ActionType.BROADCAST_RECV, // type
												receiveAction.getName(), // label
												null, // handler
												receiveAction.isEnv(), // env
												false // no proposal
										);
										transitions.add(crashTr);
									}
									//
									//

								} else {
									tr = LocalTransition.make(src, dest, // source and dest
											LocalAction.ActionType.PAIRWISE_RECV, // type
											receiveAction.getName(), // label
											hp, // handler
											receiveAction.isEnv(), // env
											false // no proposal
									);
									transitions.add(tr);
								}

							} else if (hp.isSend()) {
								Action sendAction = hp.getSendAction();
								if (hp.getSendAction().isBr()) {
									tr = LocalTransition.make(src, dest, // source and dest
											LocalAction.ActionType.BROADCAST_SEND, // type
											sendAction.getName(), // label
											hp, // handler
											sendAction.isEnv(), // env
											false // no proposal
									);
									transitions.add(tr);
								} else {
									tr = LocalTransition.make(src, dest, // source and dest
											LocalAction.ActionType.PAIRWISE_SEND, // type
											sendAction.getName(), // label
											hp, // handler
											sendAction.isEnv(), // env
											false // no proposal
									);
									transitions.add(tr);
								}

							} else if (hp.isPassive()) {

								EventPassive eventPassive = (EventPassive) hp.getEvent();
								String chInst = eventPassive.getChID();
								Location destLoseLoc = hp.getTargetLoc();
								LocalState destLoseState = states.get(LocalState.asString(destLoseLoc, src.getSigma()));

								// check what type is it.
								if (p.getPartitionInstMap().containsKey(chInst)) {
									tr = LocalTransition.make(src, destLoseState, // source and dest
											LocalAction.ActionType.PARTITION_CONS_LOSE, // type
											chInst, // label
											h, // handler
											false, // env
											false // no proposal
									);

									transitions.add(tr);

								} else if (p.getValconsInstMap().containsKey(chInst)) {
									// TODO here, we are essentially ignoring the win var value and just moving.
									// should we give access to the body?
									tr = LocalTransition.make(src, destLoseState, // source and dest
											LocalAction.ActionType.VALUE_CONS, // type
											chInst, // label
											h, // handler
											false, // env
											false // proposal
									);
									transitions.add(tr);
								} else {
									tr = null;
									System.err
											.println("unrecognised agreement event used in passive handler: " + chInst);
									Thread.dumpStack();
									System.exit(-1);
								}
							} else {
								tr = LocalTransition.make(src, dest, // source and dest
										LocalAction.ActionType.INTERNAL, // type
										"", // label
										hp, // handler
										false, // env
										false // no proposal
								);
								transitions.add(tr);
							}

						}
					} else if (h instanceof HandlerSymbolic) {
						HandlerSymbolic hs = (HandlerSymbolic) h;
						LocalTransition tr = null;

						if (hs.getEvent() instanceof EventPartitionCons) {
							EventPartitionCons epc = (EventPartitionCons) hs.getEvent();
							tr = LocalTransition.make(src, src, // source and "dest" (it's the same)
									LocalAction.ActionType.PARTITION_CONS_LOSE, // type
									epc.getChID(), // label
									h, // handler
									false, // env
									false // no proposal
							);
							transitions.add(tr);
						} else if (hs.getEvent() instanceof EventValueCons) {
							EventValueCons evc = (EventValueCons) hs.getEvent();
							tr = LocalTransition.make(src, src, // source and "dest" (it's the same)
									LocalAction.ActionType.VALUE_CONS, // type
									evc.getChID(), // label
									h, // handler
									false, // env
									false // no proposal
							);
							transitions.add(tr);
						}

					} else if (h instanceof HandlerPartitionCons) {

						// fill in: this only changes locations, so it should be easy
						// Edit: we allowed updates in here now.
						HandlerPartitionCons hpc = (HandlerPartitionCons) h;

						String label = hpc.getEvent().getChID();

						Location destWinLoc = hpc.getWinLoc();
						Location destLoseLoc = hpc.getLoseLoc();

						// Edit, new code.
						VariableValuation destWinValuation = src.getSigma()
								.evalUpdate(hpc.getWinBlock().getNonSetUpdates());
						VariableValuation destLoseValuation = src.getSigma()
								.evalUpdate(hpc.getLoseBlock().getNonSetUpdates());

						LocalState destWinState = states.get(LocalState.asString(destWinLoc, destWinValuation));
						LocalState destLoseState = states.get(LocalState.asString(destLoseLoc, destLoseValuation));

						// old code
//						LocalState destWinState = states.get(LocalState.asString(destWinLoc, src.getSigma()));
//						LocalState destLoseState = states.get(LocalState.asString(destLoseLoc, src.getSigma()));

						LocalTransition winTr = LocalTransition.make(src, destWinState, // source and dest
								LocalAction.ActionType.PARTITION_CONS_WIN, // type
								label, // label
								h, // handler
								false, // env
								false // no proposal
						);
						LocalTransition loseTr = LocalTransition.make(src, destLoseState, // source and dest
								LocalAction.ActionType.PARTITION_CONS_LOSE, // type
								label, // label
								h, // handler
								false, // env
								false // no proposal
						);

						transitions.add(winTr);
						transitions.add(loseTr);

						// add the a self loop for the crash state
						//
						//
						if (options.includeProcessFailureModeling_inSemGen) {

							LocalTransition crashSelfLoop = LocalTransition.make(crashState, crashState, // source
																											// and
																											// dest
									LocalAction.ActionType.PARTITION_CONS_LOSE, // type
									label, // label
									null, // handler
									false, // env
									false // no proposal
							);
							transitions.add(crashSelfLoop);
						}
						//
						//
						// add the crash tr
						//
						//
						if (options.includeProcessFailureModeling_inSemGen) {
							LocalTransition crashTr = LocalTransition.make(src, // source
									crashState, // dest
									LocalAction.ActionType.CRASH, // type
									"crash", // label
									null, // handler
									false, // env
									false // no proposal
							);
							transitions.add(crashTr);
						}
						//
						//

					} else if (h instanceof HandlerValueCons) {

						HandlerValueCons hvc = (HandlerValueCons) h;
						Location destLocation = hvc.getTargetLoc();
						String label = hvc.getEvent().getChID();
						boolean hasProposal = hvc.getEvent().hasPropVar();

						ExprVar winVar = p.getSpecialVars().get(label + "_" + KinaraCodeGenerator.winvar).asExprVar();
						Type t = winVar.getType();

						if (t instanceof TypeBool) {

							VariableValuation trueDestSigma = src.getSigma().clone();
							trueDestSigma.setValue(winVar.getName(),
									new ExprConstant(TypeBool.Constant.TRUE.toString(), t));

							VariableValuation falseDestSigma = src.getSigma().clone();
							falseDestSigma.setValue(winVar.getName(),
									new ExprConstant(TypeBool.Constant.FALSE.toString(), t));

							LocalState trueDestState = states.get(LocalState.asString(destLocation, trueDestSigma));
							LocalState falseDestState = states.get(LocalState.asString(destLocation, falseDestSigma));

							LocalTransition trueTr = LocalTransition.make(src, trueDestState, // source and dest
									LocalAction.ActionType.VALUE_CONS, // type
									label, // label
									h, // handler
									false, // env
									hasProposal // proposal
							);
							LocalTransition falseTr = LocalTransition.make(src, falseDestState, // source and dest
									LocalAction.ActionType.VALUE_CONS, // type
									label, // label
									h, // handler
									false, // env
									hasProposal // proposal
							);

							transitions.add(trueTr);
							transitions.add(falseTr);

						} else if (t instanceof TypeInt) {
							TypeInt tint = (TypeInt) t;
							if (!tint.getUpperBound().equalsIgnoreCase("N")) {

								int upperBound = Integer.valueOf(tint.getUpperBound());
								int lowerBound = Integer.valueOf(tint.getLowerBound());
								for (int j = lowerBound; j <= upperBound; j++) {

									VariableValuation destSigma = src.getSigma().clone();
									ExprConstant jConst = new ExprConstant(String.valueOf(j), t);
									destSigma.setValue(winVar.getName(), jConst);

									LocalState destState = states.get(LocalState.asString(destLocation, destSigma));

									if (destState == null) {
										System.err.println("dest state does not exist in the current semantics!");
										Thread.dumpStack();
										System.exit(-1);
									}

									LocalTransition tr = LocalTransition.make(src, destState, // source and dest
											LocalAction.ActionType.VALUE_CONS, // type
											label, // label
											h, // handler
											false, // env
											hasProposal // proposal
									);

									transitions.add(tr);

								}
							}
						}

						// add the a self loop for the crash state
						//
						//
						if (options.includeProcessFailureModeling_inSemGen) {

							LocalTransition crashTr = LocalTransition.make(crashState, crashState, // source
																									// and
																									// dest
									LocalAction.ActionType.VALUE_CONS, // type
									label, // label
									null, // handler
									false, // env
									false // no proposal
							);
							transitions.add(crashTr);
						}
						//
						//
					} else if (h instanceof HandlerCrash) {
						HandlerCrash hc = (HandlerCrash) h;
						if (src.getSigma().evalGuard(hc.getPredicate())) {
							LocalTransition crashTr = LocalTransition.make(src, // source
									crashState, // dest
									LocalAction.ActionType.CRASH, // type
									"crash", // label
									h, // handler
									false, // env
									false // no proposal
							);
							transitions.add(crashTr);
						}
					} else {
						System.err.println("This handler should have been removed during preprocessing:");
						System.err.println(h.toString());
						Thread.dumpStack();
						System.exit(1);
					}

				}
			}
		}

		// Connect stuff
		connectInAndOutTransitions(states, transitions);

		// System.out.println("xx: states before: " + states.size());
		// System.out.println("xx: transitions before: " + transitions.size());
		// System.out.println("xx:" + states);

		// Remove all states which cannot be reached from the initial location.
		String initialStateKey = getInitStateKey();
		// System.out.println("xx: initialState before: " + initialState.toString()+" :
		// "+initialState.hashCode());
		// to use the init state in states
		LocalState initialState = states.get(initialStateKey);

		// System.out.println("xx: initialState: " + initialState.toString());
		HashSet<LocalState> connectedStates = new HashSet<LocalState>();
		connectedStates.add(initialState);
		HashSet<String> toDelete = new HashSet<String>();

		Iterator<LocalState> phaseItr;
		boolean changeMade = true;
		CONNECTED_OUTER: while (changeMade) {
			changeMade = false;
			phaseItr = connectedStates.iterator();
			while (phaseItr.hasNext()) {
				LocalState s = phaseItr.next();

				for (LocalTransition t : transitions) {
					if (t.getSrc().equals(s) && !connectedStates.contains(t.getDest())) {
						connectedStates.add(t.getDest());
						changeMade = true;
						continue CONNECTED_OUTER;
					}
				}
			}
		}

		for (Entry<String, LocalState> entry : states.entrySet()) {
			if (!connectedStates.contains(entry.getValue())) {
				toDelete.add(entry.getKey());
			}
		}

		if (true) { // side-step deletion for debug purposes
			for (String key : toDelete) {
				states.remove(key);
			}

			for (Entry<String, LocalState> entry : states.entrySet()) {
				entry.getValue().getIncomingTransitions().removeIf(new Predicate<LocalTransition>() {

					@Override
					public boolean test(LocalTransition t) {
						return toDelete.contains(t.getSrc().toString()) || toDelete.contains(t.getDest().toString());
					}

				});
				entry.getValue().getOutgoingTransitions().removeIf(new Predicate<LocalTransition>() {

					@Override
					public boolean test(LocalTransition t) {
						// System.out.println("ttttt label " + t.getLabel());
						// System.out.println("ttttt src " + t.getSrc());
						// System.out.println("ttttt dst " + t.getDest());
						return toDelete.contains(t.getSrc().toString()) || toDelete.contains(t.getDest().toString());
					}

				});
			}

			// also delete transitions to/from deleted states.

			transitions.removeIf(new Predicate<LocalTransition>() {

				@Override
				public boolean test(LocalTransition tr) {
					return toDelete.contains(tr.getSrc().toString()) || toDelete.contains(tr.getDest().toString());
				}

			});

		}

		// System.out.println("xx: transitions after: " + transitions.size());
		// System.out.println("xx: states after: " + states.size());
		// delete edges to deleted states? check the current exception in KVS
		ProcessSemantics ps = new ProcessSemantics(p, initialState, crashState,
				new HashSet<LocalState>(states.values()), transitions, options);
//		// temp: add all self-loops of globally sync actions to the crashed state.
//		for (String label : ps.getGloballySyncLabels().keySet()) {
//			
//			ActionType type = ps.getReceiveTypeOfLabel(label);
//			boolean isEnvTr = ps.isEnvLabel(label);
//			LocalTransition tr = new LocalTransition(crashState, crashState, type, label, null, isEnvTr);
//			crashState.addIncomingTransition(tr);
//			crashState.addOutgoingTransition(tr);
//			ps.getTransitions().add(tr);
//			
//		}
		return ps;
	}

//	// to enable recursive calls.
//	private Expression computeStateExistanceDependency(LocalState ls, ProcessSemantics ps) {
//
//		// seen before? return condition and keep going
//		if (ls.isExistenceCondComputed()) {
//			return ls.getExistenceCondition();
//
//		} else { // otherwise, compute it.
//
//			// Concrete? no condition for this to exist
//			if (ls.isConcrete()) {
//				ls.setExistenceCondition(TypeBool.T().makeTrue());
//			} else {
//
//			}
//
//			// in both cases, return the computed condition.
//			return ls.getExistenceCondition();
//		}
//
//	}
	private String getInitStateKey() {
		Location initLoc = p.getInitLocation();
		VariableValuation initSigma = new VariableValuation();

		for (VarDecl vd : p.getNonSetVariables()) {
			initSigma.setValue(vd.getName(), vd.getInitValue());
		}

		// build this thing once.
		if (p.keyNames.isEmpty()) {
			initSigma.fillNames(p.keyNames);
		}

		return LocalState.asString(initLoc, initSigma);
	}

	public ArrayList<VariableValuation> expandStateSpace(VariableValuation sigma, String varName, Type t) {

		ArrayList<VariableValuation> createdSet = new ArrayList<VariableValuation>();
		if (t instanceof TypeBool) {

			// Create new sigma for true
			VariableValuation newTrue = sigma.clone();
			newTrue.setValue(varName, new ExprConstant(TypeBool.Constant.TRUE.toString(), t));
			createdSet.add(newTrue);

			// Create new sigma for false
			VariableValuation newFalse = sigma.clone();
			newFalse.setValue(varName, new ExprConstant(TypeBool.Constant.FALSE.toString(), t));
			createdSet.add(newFalse);

		} else if (t instanceof TypeInt) {
			TypeInt tint = (TypeInt) t;
			if (!tint.getUpperBound().equalsIgnoreCase("N")) {

				int upperBound = Integer.valueOf(tint.getUpperBound());
				int lowerBound = Integer.valueOf(tint.getLowerBound());
				for (int j = lowerBound; j <= upperBound; j++) {

					// Create new sigma for current int val
					VariableValuation newSigma = sigma.clone();
					ExprConstant jConst = new ExprConstant(String.valueOf(j), t);
					newSigma.setValue(varName, jConst);
					createdSet.add(newSigma);
				}
			}
		}
		return createdSet;
	}

	public void connectInAndOutTransitions(HashMap<String, LocalState> states, ArrayList<LocalTransition> transitions) {
		for (LocalState s : states.values()) {
			ArrayList<LocalTransition> outgoing = new ArrayList<LocalTransition>();
			ArrayList<LocalTransition> incoming = new ArrayList<LocalTransition>();
			for (LocalTransition t : transitions) {
				if (t.getSrc() == s) {
					outgoing.add(t);
				}
				if (t.getDest() == s) {
					incoming.add(t);
				}
			}
			s.setOutgoingTransitions(outgoing);
			s.setIncomingTransitions(incoming);
		}
	}

	// public void prepareForSemanticsGeneration() {}

}
