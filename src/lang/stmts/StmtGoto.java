package lang.stmts;

import Holes.Hole;
import lang.core.Location;
import lang.core.Protocol;
import lang.expr.ExprHole;
import lang.expr.ExprLoc;
import lang.expr.Expression;

public class StmtGoto extends Statement {
	private Expression targetLocExpr;
	private Protocol p;

	public StmtGoto(Expression targetLocExpr, Protocol p) {
		this.p = p;
		this.targetLocExpr = targetLocExpr;
	}

	// for convenience
	public StmtGoto(String locName, Protocol p) {
		this.p = p;
		this.targetLocExpr = new ExprLoc(locName);
	}

	@Override
	public StmtGoto clone() {
		return new StmtGoto(targetLocExpr, p);
	}

	public String getTargetLocName() {
		String locName = "WHAA?";
		if (targetLocExpr instanceof ExprHole) {
			Hole hole = ((ExprHole) targetLocExpr).getHole();
			if (hole.isCompletted()) {
				if (hole.getCompletion() instanceof ExprLoc) {
					ExprLoc completion = (ExprLoc) hole.getCompletion();
					locName = completion.getTargetLocName();
				} else {
					System.err.println("Error: location hole completted by a non-ExprLoc.");
					Thread.dumpStack();
					System.exit(-1);
				}
			} else {
				System.err.println("Error: trying to get location from an incompletted location hole.");
				Thread.dumpStack();
				System.exit(-1);
			}
		} else if (targetLocExpr instanceof ExprLoc) {
			ExprLoc targetLoc = (ExprLoc) targetLocExpr;
			locName = targetLoc.getTargetLocName();
		} else {
			System.err.println("Error: uncregonized location expression.");
			System.exit(-1);
		}

		return locName;
	}

	public Expression getTargetLocExpr() {
		return targetLocExpr;
	}

	public Location getTargetLoc() {
		String locName = getTargetLocName();

		if (!p.getLocationMap().containsKey(locName)) {
			System.err.println("The statement " + this + " uses a non-exsitent location!");
			Thread.dumpStack();
			System.exit(0);
		}

		return p.getLocationMap().get(locName);
	}

	public void setTargetLoc(Location newTarget) {
		if (targetLocExpr instanceof ExprHole) {
			System.err.println("Warning: calling setTargetLoc on hole loc..!");
		}
		targetLocExpr = new ExprLoc(newTarget.getName());
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
	}

	public String toString() {
		return "goto " + targetLocExpr + "\n";
	}

	public boolean hasHoleTarget() {
		return targetLocExpr instanceof ExprHole;
	}
}