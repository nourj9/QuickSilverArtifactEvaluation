package lang.stmts;

import lang.core.Action;
import lang.expr.ExprConstant;
import lang.expr.Expression;

public class StmtSend extends Statement {
	private Action act;
	private Expression to;

	public StmtSend(Action act, Expression to) {
		super();
		this.act = act;
		this.to = to;
	}

	public Action getAct() {
		return act;
	}

	public Expression getTo() {
		return to;
	}

	@Override
	public StmtSend clone() {
		return new StmtSend(act, to == null ? to : to.clone());
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
		if (found(to, toReplace)) {
			to = replacement;
		} else {
			to.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
	}

	@Override
	public String toString() {
		return "Send(" + act + ", " + to + ")\n";
	}

}