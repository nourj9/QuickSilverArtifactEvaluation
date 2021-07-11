package lang.stmts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lang.core.Action;
import lang.core.Protocol;
import lang.events.EventAction;
import lang.expr.ExprConstant;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.TypeInt;

public class StmtBlock extends Statement {

	// extract meta-data from the body
	// 1) number of send statements in body
	// 2) how many conditionals
	// 3) whether the first stmt is a send statement

	private int numOfSnds = 0;
	private int numOfConds = 0;
	private int numOfCondsWithNoElseBranch = 0;
	private boolean firstStmtIsSend = false;
	private ArrayList<Statement> stmts;

	public StmtBlock(ArrayList<Statement> pStmts) {
		ArrayList<Statement> newStmts = new ArrayList<Statement>();
		for (Statement s : pStmts) {
			if (s != null) {

				// maybe we can be nice later.
				if (s instanceof StmtBlock) {
					System.out.println("No blocks inside blocks");
					System.exit(0);
				}

				Statement tmp = s.clone();
				tmp.setParent(this);
				newStmts.add(tmp);
			}
		}
		this.stmts = newStmts;

		for (int i = 0; i < stmts.size(); i++) {
			examineStmtAndUpdateStatus(stmts.get(i));
			// Statement s = stmts.get(i);
			// boolean isSend = (s instanceof StmtBroadcast || s instanceof StmtSend);
			//
			// if (isSend) {
			// firstStmtIsSend = (i == 0); // first statement
			// numOfSnds++;
			// }
			//
			// if (s instanceof StmtIfThen) {
			// StmtIfThen stmt = (StmtIfThen) s;
			// if (!stmt.hasElseBrach()) {
			// numOfCondsWithNoElseBranch++;
			// }
			// numOfConds++;
			// }
		}

	}

	public StmtBlock() {
		stmts = new ArrayList<Statement>();
	}

	public StmtBlock(StmtBlock sb) {
		stmts = sb.stmts;
		numOfConds = sb.numOfConds;
		numOfSnds = sb.numOfConds;
		firstStmtIsSend = sb.firstStmtIsSend;
		numOfCondsWithNoElseBranch = sb.numOfCondsWithNoElseBranch;
	}

	public StmtBlock(Statement stmt) {

		// maybe we can be nice later.
		if (stmt instanceof StmtBlock) {
			System.out.println("No blocks inside blocks");
			System.exit(0);
		}

		stmt.setParent(this);
		ArrayList<Statement> newStmts = new ArrayList<Statement>();
		newStmts.add(stmt);
		stmts = newStmts;
		examineStmtAndUpdateStatus(stmt);
	}

	public StmtBlock(Statement stmt1, Statement stmt2) {

		// maybe we can be nice later.
		if (stmt1 instanceof StmtBlock || stmt2 instanceof StmtBlock) {
			System.out.println("No blocks inside blocks");
			System.exit(0);
		}

		stmt1.setParent(this);
		stmt2.setParent(this);
		ArrayList<Statement> newStmts = new ArrayList<Statement>();
		newStmts.add(stmt1);
		newStmts.add(stmt2);
		stmts = newStmts;

		examineStmtAndUpdateStatus(stmt1);
		examineStmtAndUpdateStatus(stmt2);
	}

	private void examineStmtAndUpdateStatus(Statement stmt) {

		boolean isSend = (stmt instanceof StmtBroadcast || stmt instanceof StmtSend);
		if (isSend) {
			numOfSnds++;
		}

		if (stmt instanceof StmtIfThen) {
			StmtIfThen stmtif = (StmtIfThen) stmt;
			if (!stmtif.hasElseBrach()) {
				numOfCondsWithNoElseBranch++;
			}
			numOfConds++;
		}

		firstStmtIsSend = (stmts.get(0) instanceof StmtSend || stmts.get(0) instanceof StmtBroadcast);
	}

	public Pair<StmtBlock, StmtBlock> Split(int index) {
		StmtBlock firstBlock = new StmtBlock(new ArrayList<Statement>(stmts.subList(0, index)));
		StmtBlock secondBlock = new StmtBlock(new ArrayList<Statement>(stmts.subList(index, stmts.size())));
		return new Pair<StmtBlock, StmtBlock>(firstBlock, secondBlock);
	}

	@Override
	public StmtBlock clone() {
		return new StmtBlock(stmts);
	}

	public void addStmt(Statement stmt) {
		stmts.add(stmt);
		examineStmtAndUpdateStatus(stmt);
	}

	public int getNumOfSnds() {
		return numOfSnds;
	}

	public int getNumOfConds() {
		return numOfConds;
	}

	public int getnumOfCondsWithNoElseBranch() {
		return numOfCondsWithNoElseBranch;
	}

	public boolean isFirstStmtIsSend() {
		return firstStmtIsSend;
	}

	public String toString() {
		String result = "";
		for (Statement statement : stmts) {
			result = result + "    " + statement;
		}
		return result;
	}

	public List<Statement> getStmts() {
		return stmts;
	}

	public boolean endsWithGoto() {
		return stmts.get(stmts.size() - 1) instanceof StmtGoto;
	}

	public void addImplicitGoto(StmtGoto gstmt) {
		gstmt.setParent(this);
		// check null later
		stmts.add(gstmt);
	}

	public void addImplicitElseBlocks() {
		// System.out.println("addImplicitElseBlocks: "+this);
		for (Statement stmt : stmts) {
			if (stmt instanceof StmtIfThen) {
				StmtIfThen ifstmt = (StmtIfThen) stmt;
				if (!ifstmt.hasElseBrach()) {
					ifstmt.addElseBranch();
				}
				// recurse on the blocks in any case (maybe on top? haha)
				ifstmt.getThen().addImplicitElseBlocks();
				ifstmt.getElse().addImplicitElseBlocks();

			}
		}
		numOfCondsWithNoElseBranch = 0; // this is confidence lol
	}

	public void eleminateDeadCode() {
		// search for the first goto, remove anything after.
		boolean startRemoving = false;
		for (Iterator<Statement> iterator = stmts.iterator(); iterator.hasNext();) {
			Statement stmt = iterator.next();
			if (startRemoving) {
				iterator.remove();
			}
			if (stmt instanceof StmtGoto) {
				// System.out.println("start removing");
				startRemoving = true;
			}
			// recurse in
			if (stmt instanceof StmtIfThen) {
				StmtIfThen ifstmt = (StmtIfThen) stmt;
				ifstmt.getThen().eleminateDeadCode();
				ifstmt.getElse().eleminateDeadCode();

			}
		}
	}

	public void pushNonIfIntoIf() {
		// System.out.println("------- current block --------");
		// System.out.println(this);
		// System.out.println("---------------");
		int indexToSplit = -1;
		for (int i = stmts.size() - 1; i >= 0; i--) {
			Statement stmt = stmts.get(i);
			if (stmt instanceof StmtIfThen) {
				indexToSplit = i + 1;
				// System.out.println("index to split " + i);
				break;
			}
		}

		if (indexToSplit != -1) {
			// System.out.println("------- split on " + indexToSplit + " --------");
			Pair<StmtBlock, StmtBlock> parts = Split(indexToSplit);

			// System.out.println("------- part1 " + indexToSplit + " --------");
			// System.out.println(parts.getFirst());
			// System.out.println("------- part2 " + indexToSplit + " --------");
			// System.out.println(parts.getSecond());

			StmtIfThen currentIfStmt = (StmtIfThen) stmts.get(indexToSplit - 1);
			StmtBlock ifbranch = currentIfStmt.getThen();
			StmtBlock elsebranch = currentIfStmt.getElse();

			for (Statement st : parts.getSecond().getStmts()) {
				ifbranch.addStmt(st);
				elsebranch.addStmt(st);
				// System.out.println(stmts.contains(st));
			}
			// remove the merged statements
			for (int i = stmts.size() - 1; i >= indexToSplit; i--) {
				stmts.remove(i);
			}

		}

		for (Statement stmt : stmts) {
			if (stmt instanceof StmtIfThen) {
				// recurse in
				StmtIfThen ifstmt = (StmtIfThen) stmt;
				ifstmt.getThen().pushNonIfIntoIf();
				ifstmt.getElse().pushNonIfIntoIf();
				break;
			}
		}

	}

	public boolean endsWithIfandHasNonEmptyPrefix() {
		// System.out.println("------- current block --------");
		// System.out.println(this);
		// System.out.println("---------------");
		return (stmts.size() >= 2 && (stmts.get(stmts.size() - 1) instanceof StmtIfThen));
	}

	public boolean hasOnlyAGoto() {
		return (stmts.size() == 1 && (stmts.get(0) instanceof StmtGoto));
	}

	public Pair<StmtBlock, StmtBlock> seperateGotoAndRest() {
		StmtBlock gotoBlock = new StmtBlock();
		StmtBlock restOfItBlock = new StmtBlock();

		for (Statement s : stmts) {
			if (s instanceof StmtGoto) {
				gotoBlock.addStmt(s);
			} else {
				restOfItBlock.addStmt(s);
			}
		}
		return new Pair<StmtBlock, StmtBlock>(restOfItBlock, gotoBlock);
	}

	public boolean isTopLevelIf() {
		return numOfConds != 0 && stmts.size() == 1;
	}

	public int getFirstSendIndex() {
		for (Statement stmt : stmts) {
			if (stmt instanceof StmtBroadcast || stmt instanceof StmtSend) {
				return stmts.indexOf(stmt);
			}
		}
		return -1;
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

		for (Statement stmt : stmts) {
			stmt.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
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
	public void optimizeSetAddition(EventAction action, Protocol p) {

		if (numOfConds == 0) {
			return;
		}

		ArrayList<Pair<Integer /* index to replace */, Statement>> replacements = new ArrayList<Pair<Integer, Statement>>();
		for (Statement stmt : stmts) {
			if (stmt instanceof StmtIfThen) {
				StmtIfThen ifStmt = (StmtIfThen) stmt;

				if (ifStmt.getElse() != null) { // later we can remove this.
					continue;
				}

				// has the reply.payld == 1 thing?
				Expression condExpr = ifStmt.getCond();
				if (condExpr instanceof ExprOp) {

					ExprOp cond = (ExprOp) condExpr;

					ExprVar actNameDotVal = new ExprVar(action.getAction().getName() + "_val",
							action.getAction().getDomain());
					ExprConstant one = new ExprConstant("1", new TypeInt("1", "1"));

					if ((cond.getOp().equals(ExprOp.Op.EQ) && cond.getArg1().equals(actNameDotVal)
							&& cond.getArg2().equals(one)) // reply.payld == 1 ,or,
							|| (cond.getOp().equals(ExprOp.Op.EQ) && cond.getArg2().equals(actNameDotVal)
									&& cond.getArg1().equals(one))) { // 1 == reply.payld

						// now we check if the body is set.add(action.sID)
						StmtBlock ifblock = ifStmt.getThen();
						if (ifblock.getStmts().size() != 1) {
							continue;
						}
						Statement stmtInIf = ifblock.getStmts().get(0);
						if (!(stmtInIf instanceof StmtSetUpdate)) {
							continue;
						}

						StmtSetUpdate setUpStmt = (StmtSetUpdate) stmtInIf;

						// is it an add?
						if (!setUpStmt.getOp().equals(StmtSetUpdate.OpType.ADD)) {
							continue;
						}

						// is the input action.sID?
						ExprVar input = p.getSpecialVars().get(action.getAction().getName() + "_sID").asExprVar();
						if (!setUpStmt.getInp().equals(input)) {
							continue;
						}

						// Identification done, now create the replacement.

						StmtSetIndexedAssign replacementStmt = new StmtSetIndexedAssign(setUpStmt.getSet(), input,
								actNameDotVal);

						replacements.add(new Pair<Integer, Statement>(stmts.indexOf(ifStmt), replacementStmt));

						// adjust block info since one if stmt has been replaced.
						numOfConds--;
						numOfCondsWithNoElseBranch--;

						// System.out.println("xx: cond: " + cond);
						// System.out.println("xx: setUpStmt: " + setUpStmt);
						// System.out.println("xx: replacementStmt: " + replacementStmt);
						// System.out.println("xx: exit");
						// System.exit(-1);

					}
				}

			}
		}

		// apply replacements, if any.
		for (Pair<Integer, Statement> pair : replacements) {
			stmts.set(pair.getFirst(), pair.getSecond());
		}
	}

	public void addWinVarReset(ExprVar winVar) {
		StmtAssign restStmt = new StmtAssign(winVar, winVar.getType().getInitialValue());
		int insertIndex = stmts.size() - 1;

		while (stmts.get(insertIndex) instanceof StmtGoto) {
			insertIndex--;
		}

		stmts.add(insertIndex + 1, restStmt);
	}

	public StmtGoto getLastGotoStmt() {
		for (int i = stmts.size() - 1; i >= 0; i--) {
			if (stmts.get(i) instanceof StmtGoto) {
				return (StmtGoto) stmts.get(i);
			}
		}
		return null;
	}

	public ArrayList<StmtBroadcast> getBroadcastsOverAction(Action action) {
		ArrayList<StmtBroadcast> brs = new ArrayList<StmtBroadcast>();

		for (int i = stmts.size() - 1; i >= 0; i--) {
			if (stmts.get(i) instanceof StmtBroadcast) {
				StmtBroadcast br = (StmtBroadcast) stmts.get(i);
				if (br.getAct().getName().equals(action.getName())) {
					brs.add(br);
				}
			}
		}

		return brs;
	}

	public void replaceStmt(StmtBroadcast toReplace, Statement replacement) {

		stmts.set(stmts.indexOf(toReplace), replacement);

		examineStmtAndUpdateStatus(replacement);
	}

	public StmtBlock getNonSetUpdates() {
		StmtBlock updates = new StmtBlock();

		for (Statement s : stmts) {
			if (s instanceof StmtAssign) {
				updates.addStmt(s);
			}
		}
		return updates;
	}

	public boolean endsWithIf() {
		return (stmts.size() >= 1 && (stmts.get(stmts.size() - 1) instanceof StmtIfThen));
	}

}
