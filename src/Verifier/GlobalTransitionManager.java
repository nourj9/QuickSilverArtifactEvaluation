package Verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import Holes.Hole;
import lang.core.Protocol;
import lang.expr.ExprConstant;
import lang.expr.ExprHole;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerValueCons;
import lang.stmts.Pair;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeInt;
import semantics.core.LocalAction;
import semantics.core.LocalState;
import semantics.core.LocalTransition;
import semantics.core.ProcessSemantics;
import semantics.core.VariableValuation;

public class GlobalTransitionManager {
	// private HashMap<String, int[][]> actionToRecMatrix = new HashMap<String,
	// int[][]>();
	// private HashMap<String, ArrayList<VectorPair>> actionToSendVectorPairs = new
	// HashMap<String, ArrayList<VectorPair>>();
	// stores the index of the states that can handle it, instead of the state
	// itself for efficiency.
	// private HashMap<String, HashSet<Integer>> actionToHandlingStates = new
	// HashMap<String, HashSet<Integer>>();

	private HashSet<Action> actions = new HashSet<Action>();

	private int s; // local state space size
	private Matrix IdMatrix;
	private ProcessSemantics ps;
	private GlobalStateManager gsm;
	private HashSet<GlobalTransition> allTransitions;
	private Protocol p;

	public GlobalTransitionManager(ModelChecker mc) {
		this.p = mc.getProtocol();
		this.ps = mc.getProcessSemantics();
		this.gsm = mc.getGlobalStateManager();
		this.s = mc.getLocalStateSpaceSize();
		this.IdMatrix = generateIDMatrix();
		this.allTransitions = new HashSet<GlobalTransition>();
		populateActionSetAndMaps();
	}

	private void populateActionSetAndMaps() {

		// System.out.println("internal: "+ ps.getInternalLabels().keySet());
		// System.out.println("pw: "+ ps.getPairwiseLabels().keySet());
		// System.out.println("bc: "+ ps.getBroadcastLabels().keySet());
		// System.out.println("pc: "+ ps.getPartitionConsLabels().keySet());
		// System.out.println("vc: "+ ps.getValueStoreLabels().keySet());
		// System.out.println("env: "+ ps.getEnvLabels().keySet());

		handleInternalActions();
		handlePairwiseActions();
		handleBroadcastActions();
		handlePartitionConsActions();
		handleValStoreActions();
		// handleEnvActions(); // is handled like th rest.
	}

	private void handleInternalActions() {
		int internalCounter = 0;
		// since we do not label internal transitions before this point, they all will
		// be using the same empty label "", and hence are in the same hashset.

		HashSet<LocalTransition> internalTrs = ps.getInternalLabels().get("" /* emtpy action */);

		// no internal trs? return
		if (internalTrs == null) {
			return;
		}

		// now, give each of them it's own action.
		for (LocalTransition tr : internalTrs) {

			// give internal transitions an action name
			String actionName = "inter_" + (internalCounter++);

			// build the "send" vectors:
			ArrayList<VectorPair> sndVectorPairs = new ArrayList<VectorPair>();
			sndVectorPairs.add(computeSenderVectorPairBasedOnTr(tr));

			// finally, mark *all* states as able to receive. This is not an issue as the
			// receive matrix is the ID matrix, this is just to make sure actionCanFire
			// works correctly.

			HashSet<Integer> handlingStates = new HashSet<Integer>();
			for (LocalState ls : ps.getStates()) {
				handlingStates.add(gsm.getIndexOf(ls));
			}

			Action action = Action.makeAction(this, actionName, Action.Type.MULTI, IdMatrix, sndVectorPairs,
					handlingStates);

			// populate things
			actions.add(action);

		}
	}

	private void handlePairwiseActions() {

		for (Entry<String, HashSet<LocalTransition>> entry : ps.getPairwiseLabels().entrySet()) {

			String actionName = entry.getKey();
			HashSet<LocalTransition> relatedTrs = entry.getValue();

			// since, for the same action x, we can have multiple places to send and
			// receive, e.g.:
			// tr1: s1 -- x! --> s2;
			// tr2: s3 -- x! --> s4;
			// tr3: s5 -- x? --> s6;
			// tr4: s7 -- x? --> s8;
			// we need a send vector-pair to simulate each scenario:
			// one where tr1 moves with t3
			// one where tr1 moves with t4
			// one where tr2 moves with t3
			// one where tr2 moves with t4

			// first, let us classify transitions to send and receive
			HashSet<LocalTransition> sendTrs = new HashSet<LocalTransition>();
			HashSet<LocalTransition> recTrs = new HashSet<LocalTransition>();

			for (LocalTransition tr : relatedTrs) {
				if (tr.getTransitionType().equals(LocalAction.ActionType.PAIRWISE_SEND)) {
					sendTrs.add(tr);
				} else if (tr.getTransitionType().equals(LocalAction.ActionType.PAIRWISE_RECV)) {
					recTrs.add(tr);
				} else {
					System.err.println("a pairwise action with odd related local transitions? hmm.");
					System.err.println("current action: " + actionName);
					System.err.println("relatedTrs: " + relatedTrs);
					System.err.println("problomatic tr: " + tr);
					Thread.dumpStack();
					System.exit(-1);
				}
			}

			ArrayList<VectorPair> sndVectorPairs = new ArrayList<VectorPair>();

			// now, we match each send with each receive and create a vector-pair.
			// note: since we may have receives for the env only, the send may be empty,
			// hence, the loops down there will be messed up. We handle that first.

			if (sendTrs.isEmpty()) { // the sender is the env
				for (LocalTransition recTr : recTrs) {

					// prepare the pair.
					int[] sendSrcVec = new int[s];
					int[] sendDstVec = new int[s];

					// handle the receive
					int recTrfromIndex = gsm.getIndexOf(recTr.getSrc());
					int recTrtoIndex = gsm.getIndexOf(recTr.getDest());
					sendSrcVec[recTrfromIndex] = sendSrcVec[recTrfromIndex] + 1;
					sendDstVec[recTrtoIndex] = sendDstVec[recTrtoIndex] + 1;

					sndVectorPairs.add(new VectorPair(sendSrcVec, sendDstVec, this));
				}
			} else if (recTrs.isEmpty()) { // the receiver is the env
				for (LocalTransition sendTr : sendTrs) {

					// prepare the pair.
					int[] sendSrcVec = new int[s];
					int[] sendDstVec = new int[s];

					// handle the receive
					int sndTrfromIndex = gsm.getIndexOf(sendTr.getSrc());
					int sndTrtoIndex = gsm.getIndexOf(sendTr.getDest());
					sendSrcVec[sndTrfromIndex] = sendSrcVec[sndTrfromIndex] + 1;
					sendDstVec[sndTrtoIndex] = sendDstVec[sndTrtoIndex] + 1;

					sndVectorPairs.add(new VectorPair(sendSrcVec, sendDstVec, this));
				}
			} else { // else, match and mix.

				for (LocalTransition sendTr : sendTrs) {
					for (LocalTransition recTr : recTrs) {

						// prepare the pair.
						int[] sendSrcVec = new int[s];
						int[] sendDstVec = new int[s];

						// handle the send
						int sndTrfromIndex = gsm.getIndexOf(sendTr.getSrc());
						int sndTrtoIndex = gsm.getIndexOf(sendTr.getDest());
						sendSrcVec[sndTrfromIndex] = sendSrcVec[sndTrfromIndex] + 1;
						sendDstVec[sndTrtoIndex] = sendDstVec[sndTrtoIndex] + 1;

						// handle the receive
						int recTrfromIndex = gsm.getIndexOf(recTr.getSrc());
						int recTrtoIndex = gsm.getIndexOf(recTr.getDest());
						sendSrcVec[recTrfromIndex] = sendSrcVec[recTrfromIndex] + 1;
						sendDstVec[recTrtoIndex] = sendDstVec[recTrtoIndex] + 1;

						sndVectorPairs.add(new VectorPair(sendSrcVec, sendDstVec, this));
					}
				}
			}

			HashSet<Integer> handlingStates = new HashSet<Integer>();
			for (LocalState ls : ps.getStates()) {
				handlingStates.add(gsm.getIndexOf(ls));
			}

			Action action = Action.makeAction(this, actionName, Action.Type.MULTI, IdMatrix, sndVectorPairs,
					handlingStates);

			// populate things
			actions.add(action);

		}

	}

	private void handleBroadcastActions() {

		for (Entry<String, HashSet<LocalTransition>> entry : ps.getBroadcastLabels().entrySet()) {

			String actionName = entry.getKey();
			HashSet<LocalTransition> relatedTrs = entry.getValue();

			// classify transitions based on their type and discharge the right method to
			// handle this.

			HashSet<LocalTransition> sendTrs = new HashSet<LocalTransition>();
			HashSet<LocalTransition> recTrs = new HashSet<LocalTransition>();

			for (LocalTransition tr : relatedTrs) {
				if (tr.getTransitionType().equals(LocalAction.ActionType.BROADCAST_SEND)) {
					sendTrs.add(tr);
				} else if (tr.getTransitionType().equals(LocalAction.ActionType.BROADCAST_RECV)) {
					recTrs.add(tr);
				} else {
					System.err.println("a broadcast action with odd related local transitions? hmm.");
					System.err.println("current action: " + actionName);
					System.err.println("relatedTrs: " + relatedTrs);
					System.err.println("problomatic tr: " + tr);
					Thread.dumpStack();
					System.exit(-1);
				}
			}

			// note that we need more than one sender vector-pair to model having the sender
			// originate from different local trs. For example we can have the following in
			// the semantics:
			// s1 --hi!!--> s2
			// s3 --hi!!--> s4
			// where hi is the an action. Hence, we need two vector-pairs. Both have the
			// same matrix, as the receivers move in the same way, but each of which has its
			// own send vector-pair

			HashSet<Integer> handlingStates = new HashSet<Integer>();
			Matrix recMatrix = computeRecMatrixAndEnabledness(recTrs, handlingStates);
			ArrayList<VectorPair> sndVectorPairs = new ArrayList<VectorPair>();

			if (!ps.isEnvLabel(actionName)) {
				// system broadcast
				for (LocalTransition sendTr : sendTrs) {
					sndVectorPairs.add(computeSenderVectorPairBasedOnTr(sendTr));
				}
			} else {
				// since this can be an environment broadcast, the send vector-pair is
				// essentially empty.
				sndVectorPairs.add(new VectorPair(new int[s], new int[s], this));
			}

			Action action = Action.makeAction(this, actionName, Action.Type.MULTI, recMatrix, sndVectorPairs,
					handlingStates);
			actions.add(action);

		}
	}

	private void handlePartitionConsActions() {

		for (Entry<String, HashSet<LocalTransition>> entry : ps.getPartitionConsLabels().entrySet()) {

			String actionName = entry.getKey();
			HashSet<LocalTransition> relatedTrs = entry.getValue();

			// classify transitions based on their type and discharge the right method to
			// handle this.

			ArrayList<LocalTransition> winTrs = new ArrayList<LocalTransition>(); // ArrayList for order
			HashSet<LocalTransition> loseTrs = new HashSet<LocalTransition>();

			for (LocalTransition tr : relatedTrs) {
				if (tr.getTransitionType().equals(LocalAction.ActionType.PARTITION_CONS_WIN)) {
					winTrs.add(tr);
				} else if (tr.getTransitionType().equals(LocalAction.ActionType.PARTITION_CONS_LOSE)) {
					loseTrs.add(tr);
				} else {
					System.err.println("a partition cons action with odd related local transitions? hmm.");
					System.err.println("current action: " + actionName);
					System.err.println("relatedTrs: " + relatedTrs);
					System.err.println("problomatic tr: " + tr);
					Thread.dumpStack();
					System.exit(-1);
				}
			}

			// get action arity
			int card = getCardinalityValue(actionName);

			// compute the receive matrix!
			HashSet<Integer> handlingStates = new HashSet<Integer>();
			Matrix recMatrix = computeRecMatrixAndEnabledness(loseTrs, handlingStates);

			ArrayList<VectorPair> sndVectors = new ArrayList<VectorPair>();

			// since this is a maximal thing, we need to create vector-pairs for
			// cardinalities from 1 to card
			for (int c = 1; c <= card; c++) {
				// now, since we can have multiple places from which a process can win, we need
				// to do combinations, for instance, say we can start partition cons from s1
				// (with tr1) ,s2 (with tr2)
				// and go to w1 and w2 upon winning, respectively. Additionally, let c = 3. Then
				// we need vectors to simulate:
				// 3 win from s1 and 0 win from s2,
				// 2 win from s1 and 1 win from s2,
				// 1 win from s1 and 2 win from s2, and
				// 0 win from s1 and 3 win from s2.
				// each scenario needs its own vector-pair.

				// get all combinations of shares for each winTr
				ArrayList<int[]> combinations = getCombinations(c, winTrs.size());
				// getCombinations(5,3) returns the following:
				// [5,0,0]
				// [4,1,0]
				// [3,1,1]
				// [2,2,1]
				// [2,1,1]
				// ...

				// now, use every split to create a vector-pair

				for (int[] split : combinations) {
					// prepare the pair.
					int[] sendSrcVec = new int[s];
					int[] sendDstVec = new int[s];

					for (int i = 0; i < split.length; i++) {

						// an optimization: no share? continue
						if (split[i] == 0) {
							continue;
						}

						// otherwise, compute the thing.
						// let share be [4,1,0]
						// then we have
						// 4 processes that move from the src of tr1 to the dest of tr1
						// 1 processes that move from the src of tr2 to the dest of tr2
						// 0 processes that move from the src of tr3 to the dest of tr3 (taken care of
						// by the optimization)

						LocalTransition currentTr = winTrs.get(i);

						int fromIndex = gsm.getIndexOf(currentTr.getSrc());
						int toIndex = gsm.getIndexOf(currentTr.getDest());

						sendSrcVec[fromIndex] = sendSrcVec[fromIndex] + split[i];
						sendDstVec[toIndex] = sendDstVec[toIndex] + split[i];
					}
					sndVectors.add(new VectorPair(sendSrcVec, sendDstVec, this));
				}
			}

			Action action = Action.makeAction(this, actionName, Action.Type.MAXIMAL, recMatrix, sndVectors,
					handlingStates);
			actions.add(action);

		}
	}

	/** this does the following:
	 * buckets is how many buckets to distribute over
	 * quote is the amount to distribute
	 * e.g. balls = 5 returns and boxes = 3: 
	 * [5,0,0]
	 * [4,1,0]
	 * [3,1,1]
	 * [2,2,1]
	 * [2,1,1]
	 * ...  
	*/

	private ArrayList<int[]> getCombinations(int balls, int boxes) {

		ArrayList<int[]> res = new ArrayList<int[]>();

		// base case: one box left? add all balls in it.
		if (boxes == 1) {
			int[] box = new int[boxes];
			box[0] = balls;
			res.add(box);
			return res;

			// otherwise, take your share, and recurse to get the suffixes
		} else {

			for (int myShare = 0; myShare <= balls; myShare++) {

				ArrayList<int[]> suffixes = getCombinations(balls - myShare, boxes - 1);

				for (int[] suffix : suffixes) {

					// prepare it
					int[] full = new int[boxes];

					// fill my share
					full[0] = myShare;

					// fill the rest
					for (int i = 0; i < suffix.length; i++) {
						full[i + 1] = suffix[i];
					}

					// add to the result.
					res.add(full);
				}
			}
			return res;
		}

	}

	/**
	 * trs of vc
	 * foreach v: valueDomain //LATER this can be more than one value if card > 1
	 *   create act_v
	 *   
	 *   compute act_v Matrix as follows:
	 *   let releventTrs = {}
	 *   foreach tr: trs of vc
	 *     let t be dst of tr
	 *     if t has vinwar = v then
	 *       add tr to releventTrs
	 * 	 call computeRecMatrix(releventTr)
	 *   
	 *   compute the vectors as follows:
	 *   let winningTrs = {}
	 *   foreach tr: trs of vc
	 *     let s be src of tr
	 *     let t be dst of tr
	 *     if s has propvar and propvar in v, and, t has vinwar = v then
	 *       add tr to winningTrs
	 *   
	 *   for each combination of size card
	 *      call computeVectorPairs for that combination.    
	 * 
	 * */
	// assumes card = 1
	private void handleValStoreActions() {

		for (Entry<String, HashSet<LocalTransition>> entry : ps.getValueStoreLabels().entrySet()) {

			String baseAction = entry.getKey();
			HashSet<LocalTransition> relatedTrs = entry.getValue();

			ExprVar winVar = p.getSpecialVars().get(baseAction + "_wval").asExprVar();
			ExprVar propVar = getProposalVar(baseAction);

			int card = getCardinalityValue(baseAction);
			if (card != 1) {
				System.err.println("Error: card > 1 is not supported for valueStore, yet.");
				System.exit(-1);
			}

			ArrayList<ExprConstant> propDom = getProposalVarDomain(baseAction);
			for (ExprConstant val : propDom) {
				// create an action to model this value winning:
				String actionForValName = baseAction + "_" + val.getVal();

				// get a hold of all the "winning" and "losing" transitions according to this
				// value
				ArrayList<LocalTransition> winTrs = new ArrayList<LocalTransition>();
				HashSet<LocalTransition> loseTrs = new HashSet<LocalTransition>();

				for (LocalTransition relatedTr : relatedTrs) {

					LocalState srcSt = relatedTr.getSrc();
					LocalState dstSt = relatedTr.getDest();

					VariableValuation srcSigma = srcSt.getSigma();
					VariableValuation dstSigma = dstSt.getSigma();

					// from the get-go, add all transitions with no proposal to the losing bucket.
					if (!relatedTr.hasProposal()) {
						loseTrs.add(relatedTr);
						continue;
					}

					// otherwise, using the normal classification

					// the destination winning value is the same as the current value?
					// i.e., wval = val
					// then you couldv'e gotten here be "losing" value cons.

					ExprOp winVarEqVal = new ExprOp(ExprOp.Op.EQ, winVar, val);

					if ((dstSigma.evalGuard(winVarEqVal))) {
						loseTrs.add(relatedTr);
					}

					// the proposal value is the same as the the wining value and the current value?
					// i.e.,propVar = val in srcSigma and winVar = val in DstSigma
					// then this trans can be a winning transition

					ExprOp propVarEqVal = new ExprOp(ExprOp.Op.EQ, propVar, val);

					if (srcSigma.evalGuard(propVarEqVal) && dstSigma.evalGuard(winVarEqVal)) {
						winTrs.add(relatedTr);
					}
				}

				// use the losing trs to build the matrix for this action.
				HashSet<Integer> handlingStates = new HashSet<Integer>();
				Matrix recMatrix = computeRecMatrixAndEnabledness(loseTrs, handlingStates);

				// now, let us compute the vectors.
				ArrayList<VectorPair> sndVectors = new ArrayList<VectorPair>();

				// get all combinations of shares for each winTr
				ArrayList<int[]> combinations = getCombinations(card, winTrs.size());
				// getCombinations(5,3) returns the following:
				// [5,0,0]
				// [4,1,0]
				// [3,1,1]
				// [2,2,1]
				// [2,1,1]
				// ...

				// now, use every split to create a vector-pair
				for (int[] split : combinations) {
					// prepare the pair.
					int[] sendSrcVec = new int[s];
					int[] sendDstVec = new int[s];

					for (int i = 0; i < split.length; i++) {

						// an optimization: no share? continue
						if (split[i] == 0) {
							continue;
						}

						// otherwise, compute the thing.
						// let share be [4,1,0]
						// then we have
						// 4 processes that move from the src of tr1 to the dest of tr1
						// 1 processes that move from the src of tr2 to the dest of tr2
						// 0 processes that move from the src of tr3 to the dest of tr3 (taken care of
						// by the optimization)

						LocalTransition currentTr = winTrs.get(i);

						int fromIndex = gsm.getIndexOf(currentTr.getSrc());
						int toIndex = gsm.getIndexOf(currentTr.getDest());

						sendSrcVec[fromIndex] = sendSrcVec[fromIndex] + split[i];
						sendDstVec[toIndex] = sendDstVec[toIndex] + split[i];
					}
					sndVectors.add(new VectorPair(sendSrcVec, sendDstVec, this));
				}

				Action actionForVal = Action.makeAction(this, actionForValName, Action.Type.MULTI, recMatrix,
						sndVectors, handlingStates);
				actions.add(actionForVal);

			} // end of value loop
		}
	}

	private ExprVar getProposalVar(String action) {

		// sanity check
		if (!p.getValconsInstMap().containsKey(action)) {
			System.err.println("Error: getProposalVar called on a non-value store action " + action);
			Thread.dumpStack();
			System.exit(-1);
		}

		HandlerValueCons sampleHandler = p.getValconsInstMap().get(action).iterator().next();
		return sampleHandler.getEvent().getProposalVar();

	}

	private ArrayList<ExprConstant> getProposalVarDomain(String action) {

		ArrayList<ExprConstant> vals = new ArrayList<ExprConstant>();

		// sanity check
		if (!p.getValconsInstMap().containsKey(action)) {
			System.err.println("Error: getProposalVarDomain called on a non-value store action " + action);
			Thread.dumpStack();
			System.exit(-1);
		}

		HandlerValueCons sampleHandler = p.getValconsInstMap().get(action).iterator().next();
		Type propType = sampleHandler.getEvent().getProposalVar().getType();

		// return values based on type
		if (propType instanceof TypeBool) {
			vals.add(new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T()));
			vals.add(new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T()));

		} else if (propType instanceof TypeInt) {
			TypeInt tint = (TypeInt) propType;
			if (!tint.getUpperBound().equalsIgnoreCase("N")) {
				int upperBound = Integer.valueOf(tint.getUpperBound());
				int lowerBound = Integer.valueOf(tint.getLowerBound());
				for (int j = lowerBound; j <= upperBound; j++) {
					vals.add(new ExprConstant(String.valueOf(j), propType));
				}
			}

		} else {
			System.err.println("Error: getProposalVarDomain called on an unkown type " + propType);
			Thread.dumpStack();
			System.exit(-1);
		}

		return vals;
	}

	private int getCardinalityValue(String action) {

		Expression cardValExpr = null;

		if (p.getPartitionInstMap().containsKey(action)) {
			// partition cons?
			HandlerPartitionCons sampleHandler = p.getPartitionInstMap().get(action).iterator().next();
			cardValExpr = sampleHandler.getEvent().getCardinality();
		} else if (p.getValconsInstMap().containsKey(action)) {
			// value cons?
			HandlerValueCons sampleHandler = p.getValconsInstMap().get(action).iterator().next();
			cardValExpr = sampleHandler.getEvent().getCardinality();
		} else {
			System.err.println("Error: getCardinalityValue called on a non-agreement action " + action);
			Thread.dumpStack();
			System.exit(-1);
		}

		ExprConstant cardVal = null;
		// uncompleted hole? we have a problem
		if (cardValExpr instanceof ExprHole) {
			Hole hole = ((ExprHole) cardValExpr).getHole();
			if (hole.isCompletted()) {
				if (hole.getCompletion() instanceof ExprConstant) {
					cardVal = (ExprConstant) hole.getCompletion();
				} else {
					System.err.println("Error: cardinality hole completted by a non-constant in getCardinalityValue.");
					Thread.dumpStack();
					System.exit(-1);
				}
			} else {
				System.err.println("Error: uncompletted cardinality hole in getCardinalityValue.");
				Thread.dumpStack();
				System.exit(-1);
			}
		} else if (cardValExpr instanceof ExprConstant) {
			cardVal = (ExprConstant) cardValExpr;
		} else {
			System.err.println("Error: uncregonized cardinality expression in getCardinalityValue.");
			Thread.dumpStack();
			System.exit(-1);
		}

		return Integer.valueOf(cardVal.getVal());
	}

	private Matrix computeRecMatrixAndEnabledness(HashSet<LocalTransition> recTrs, HashSet<Integer> handlingStates) {

		int[][] matrix = new int[s][s];
		// from --> to is equivalent to matrix[to][from]

		// set it to the identity matrix first
		for (int i = 0; i < matrix.length; i++) {
			matrix[i][i] = 1;
		}

		// change things that are not self loops
		for (LocalTransition tr : recTrs) {

			int fromIndex = gsm.getIndexOf(tr.getSrc());
			int toIndex = gsm.getIndexOf(tr.getDest());

			// remove the self-loop essentially
			matrix[fromIndex][fromIndex] = 0;

			// set the right colomn vector
			matrix[toIndex][fromIndex] = 1;

			// save "from" to states that can handle this action.
			handlingStates.add(fromIndex);
			// initializeAndAddIfNotThere(actionToHandlingStates, action, fromIndex);
		}

		return new Matrix(matrix, gsm);
	}

	private VectorPair computeSenderVectorPairBasedOnTr(LocalTransition tr) {
		int[] sendSrcVec = new int[s];
		int[] sendDstVec = new int[s];

		int fromIndex = gsm.getIndexOf(tr.getSrc());
		int toIndex = gsm.getIndexOf(tr.getDest());

		sendSrcVec[fromIndex] = sendSrcVec[fromIndex] + 1;
		sendDstVec[toIndex] = sendDstVec[toIndex] + 1;

		return new VectorPair(sendSrcVec, sendDstVec, this);
	}

	private Matrix generateIDMatrix() {
		int[][] idMat = new int[s][s];
		for (int i = 0; i < idMat.length; i++) {
			idMat[i][i] = 1;
		}

		return new Matrix(idMat, gsm);
	}

//	public HashMap<String, int[][]> getActionToRecMatrix() {
//		return actionToRecMatrix;
//	}
//
//	public HashMap<String, ArrayList<VectorPair>> getActionToSendVectorPairs() {
//		return actionToSendVectorPairs;
//	}

	public HashSet<Action> getActions() {
		return actions;
	}

	public Matrix getIdMatrix() {
		return IdMatrix;
	}

	public GlobalTransition makeGlobalTransition(Action action, VectorPair sendVecPair, GlobalState from,
			GlobalState to) {

		GlobalTransition tr = new GlobalTransition(this, action, sendVecPair, from, to);
		allTransitions.add(tr);

		// connect the transition to its states
		from.addOutgoingTransition(tr);
		to.addIncomingTransition(tr);

		return tr;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(">>>>>>> ");

		for (Action action : actions) {
			sb.append("action: " + action).append("\n");

		}
		return sb.toString();
	}

	/**
	 * next state = M.(q-v1) +v2?
	 * */
	public GlobalState getNextState(GlobalState curState, Matrix matrix, VectorPair pair) {

		int[] SendSrcVec = pair.sendSrcVec;
		int[] SendDstVec = pair.sendDstVec;
		int[] curValues = curState.getValues();
		int[] nextValues = new int[s];

		// (q-v1)
		int[] qMinusSendSrcVec = new int[s];

		for (int i = 0; i < s; i++) {
			qMinusSendSrcVec[i] = curValues[i] - SendSrcVec[i];
		}

		// M.(q-v1)
		int[] mByqMinusSendSrcVec = new int[s];
		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				mByqMinusSendSrcVec[i] = mByqMinusSendSrcVec[i] + (matrix.matrix[i][j] * qMinusSendSrcVec[j]);
			}
		}

		// M.(q-v1) + v2
		for (int i = 0; i < s; i++) {
			nextValues[i] = mByqMinusSendSrcVec[i] + SendDstVec[i];
		}

		return gsm.makeGlobalState(nextValues);
	}

	public ArrayList<Pair<GlobalState, VectorPair>> getNextStates(GlobalState curState, Action action) {
		Matrix matrix = action.getRecMatrix();
		ArrayList<VectorPair> vectorPairs = action.getSndVectorPairs();
		ArrayList<Pair<GlobalState, VectorPair>> nextStates = new ArrayList<Pair<GlobalState, VectorPair>>();

		switch (action.getType()) {
		case MAXIMAL:
			// maximize this.
			ArrayList<VectorPair> getFirablePairs = getFirableVectors(curState, action, vectorPairs);

			for (VectorPair pair : getFirablePairs) {
				if (actionIsEnabled(curState, action, pair.sendSrcVec)) {
					GlobalState nextState = getNextState(curState, matrix, pair);
					nextStates.add(new Pair<GlobalState, VectorPair>(nextState, pair));
				}
			}

			break;
		case MULTI:
			for (VectorPair pair : vectorPairs) {
				if (actionCanFire(curState, action, pair.sendSrcVec)
						&& actionIsEnabled(curState, action, pair.sendSrcVec)) {
					GlobalState nextState = getNextState(curState, matrix, pair);
					nextStates.add(new Pair<GlobalState, VectorPair>(nextState, pair));
				}
			}
			break;
		default:
			System.err.println("Error: recognized action: " + action);
			Thread.dumpStack();
			System.exit(-1);

		}

		return nextStates;

	}

	private ArrayList<VectorPair> getFirableVectors(GlobalState curState, Action action,
			ArrayList<VectorPair> vectorPairs) {

		// get anything that can fire.
		ArrayList<VectorPair> canFire = new ArrayList<VectorPair>();
		for (VectorPair vp : vectorPairs) {
			if (actionCanFire(curState, action, vp.sendSrcVec)) {
				canFire.add(vp);
			}
		}

		// get maximal number
		int max = 0;
		for (VectorPair vp : canFire) {
			int arity = vp.getArity();
			if (arity > max) {
				max = arity;
			}
		}

		// return anything that is maximal
		ArrayList<VectorPair> toRet = new ArrayList<VectorPair>();
		for (VectorPair vp : canFire) {
			if (vp.getArity() == max) {
				toRet.add(vp);
			}
		}
		return toRet;

	}

	// everyone can receive: every non-empty state has a receive out of it.
	private boolean actionIsEnabled(GlobalState curState, Action action, int[] sendSrcVec) {
		// the problem here is as follows:
		// due to the exclusive region analysis (maybe? yes, verified), we can have a
		// state, say
		// after_Decided_before_LeaderDone_1w where we know there is one process trying
		// to send, say, inform, to processes in wait, but since the
		// "statesHnadlingThisAction" do not include this state (there was no receives
		// from this state! because we did not need them!), hence, the transition
		// did not look enabled. solution:
		// [later]
		// 1- add the exclusive region knowledge to the actionToHandlingStates? i.e. any
		// state that "does not need to receive" is considered "ableToHndldle" this
		// action.
		// [implemented]
		// 2 - first, remove all the processes that will act as senders, then check if
		// the remaining process can handle the receive.

		int[] stateCounters = curState.getValues();

		// remove the senders
		int[] stateWithoutSenders = new int[s];
		for (int i = 0; i < s; i++) {
			// avoid negative counters.
			int diff = stateCounters[i] - sendSrcVec[i];
			stateWithoutSenders[i] = diff > 0 ? diff : 0;
		}

		// check if the rest can handle the receive.
		for (int stIndex = 0; stIndex < stateWithoutSenders.length; stIndex++) {
			if (stateWithoutSenders[stIndex] > 0 // has processes in it
					&& !action.getHandlingStates().contains(stIndex) // that cannot handle this action
			) {
				// System.out.println("xx: action " + action + " is not enabled in state: " +
				// curState);
				return false;
			}
		}
		return true;

	}

	// the source state has enough senders.
	private boolean actionCanFire(GlobalState curState, Action action, int[] sendSrcVec) {
		for (int i = 0; i < sendSrcVec.length; i++) {

			// if there are no senders expected from this state, move on
			// this also takes care of the env stuff! they always can fire.
			if (sendSrcVec[i] == 0) {
				continue;
			}

			if (curState.getValues()[i] < sendSrcVec[i]) {
				// the current state does not have enough senders to support this send vec.
				// System.out.println("xx: action " + action + " cannot fire in state: " +
				// curState);
				return false;
			}
		}
		return true;
	}

	public HashSet<GlobalTransition> getAllTransitions() {
		return allTransitions;
	}

	public GlobalStateManager getGlobalStateManager() {
		return gsm;
	}

//	private void initializeAndAddIfNotThere(HashMap<String, HashSet<Integer>> map, String key, int stIndex) {
//		if (!map.containsKey(key)) {
//			map.put(key, new HashSet<Integer>());
//		}
//		map.get(key).add(stIndex);
//	}

}
