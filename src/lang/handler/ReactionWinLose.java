package lang.handler;

import lang.stmts.StmtBlock;

public class ReactionWinLose extends Reaction {

	private StmtBlock winBlock;
	private StmtBlock loseBlock;

	public ReactionWinLose(StmtBlock winBlock, StmtBlock loseBlock) {
		this.winBlock = winBlock;
		this.loseBlock = loseBlock;
	}

	public StmtBlock getWinBlock() {
		return winBlock;
	}

	public StmtBlock getLoseBlock() {
		return loseBlock;
	}

}
