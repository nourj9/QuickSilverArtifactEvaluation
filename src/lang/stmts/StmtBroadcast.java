package lang.stmts;

import lang.core.Action;
import lang.expr.ExprConstant;
import lang.expr.Expression;

public class StmtBroadcast extends Statement {
	private Action act;

	public StmtBroadcast(Action act) {
		super();
		this.act = act;
	}

	public Action getAct() {
		return act;
	}

	@Override
	public StmtBroadcast clone() {
		return new StmtBroadcast(act);
	}

	@Override
	public String toString() {
		return "Broadcast(" + act + ")\n";
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
	}

}