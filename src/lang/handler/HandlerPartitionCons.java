package lang.handler;

import lang.stmts.Statement;
import lang.stmts.StmtBlock;
import lang.stmts.StmtGoto;

import java.util.ArrayList;

import lang.core.Location;
import lang.events.EventPartitionCons;;

public class HandlerPartitionCons extends Handler {
	private EventPartitionCons event;
	private StmtBlock winBlock;
	private StmtBlock loseBlock;

	public HandlerPartitionCons(EventPartitionCons event, ReactionWinLose reaction) {
		this.event = event;
		this.winBlock = reaction.getWinBlock();
		this.loseBlock = reaction.getLoseBlock();
	}

	public HandlerPartitionCons(EventPartitionCons event) {
		this.event = event;
	}

	public void setEvent(EventPartitionCons event) {
		this.event = event;
	}

	public void setWinBlock(StmtBlock winBlock) {
		this.winBlock = winBlock;
	}

	public void setLoseBlock(StmtBlock loseBlock) {
		this.loseBlock = loseBlock;
	}

	public EventPartitionCons getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return " on " + event + "\n" + "win:\n" + winBlock + "lose:\n" + loseBlock;
	}

	public StmtBlock getWinBlock() {
		return winBlock;
	}

	public StmtBlock getLoseBlock() {
		return loseBlock;
	}

	// assumes normlaization
	public Location getWinLoc() {
		for (Statement stmt : winBlock.getStmts()) {
			if (stmt instanceof StmtGoto) {
				return ((StmtGoto) stmt).getTargetLoc();
			}
		}
		return null;
	}

	// assumes normlaization
	public Location getLoseLoc() {
		for (Statement stmt : loseBlock.getStmts()) {
			if (stmt instanceof StmtGoto) {
				return ((StmtGoto) stmt).getTargetLoc();
			}
		}
		return null;
	}

	public void setWinTargetLoc(Location loc) {
		for (Statement stmt : winBlock.getStmts()) {
			if (stmt instanceof StmtGoto) {
				((StmtGoto) stmt).setTargetLoc(loc);
			}
		}
	}

	public Location getWinTargetLoc() {
		for (Statement stmt : winBlock.getStmts()) {
			if (stmt instanceof StmtGoto) {
				return ((StmtGoto) stmt).getTargetLoc();
			}
		}
		return null;
	}

	public void setLoseTargetLoc(Location loc) {
		for (Statement stmt : loseBlock.getStmts()) {
			if (stmt instanceof StmtGoto) {
				((StmtGoto) stmt).setTargetLoc(loc);
			}
		}
	}

	public Location getLoseTargetLoc() {
		for (Statement stmt : loseBlock.getStmts()) {
			if (stmt instanceof StmtGoto) {
				return ((StmtGoto) stmt).getTargetLoc();
			}
		}
		return null;
	}

	@Override
	public ArrayList<StmtBlock> getBodies() {
		ArrayList<StmtBlock> bodies = new ArrayList<StmtBlock>();
		
		bodies.add(winBlock);
		bodies.add(loseBlock);
		
		return bodies;
	}
	
}
