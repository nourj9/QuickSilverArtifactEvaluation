package lang.stmts;

import lang.expr.ExprVar;
import lang.expr.Expression;

public class StmtSetUpdate extends Statement {

	public static enum OpType {
		ADD, REMOVE
	};

	private ExprVar set;
	private Expression inp;
	private OpType op;

	public void setSet(ExprVar set) {
		this.set = set;
	}

	public void setInp(Expression inp) {
		this.inp = inp;
	}

	public ExprVar getSet() {
		return set;
	}

	public Expression getInp() {
		return inp;
	}

	public OpType getOp() {
		return op;
	}

	public StmtSetUpdate(ExprVar set, OpType op, Expression inp) {
		super();
		this.set = set;
		this.inp = inp;
		this.op = op;
	}

	@Override
	public StmtSetUpdate clone() {
		return new StmtSetUpdate(set.clone(), op, inp.clone());
	}

	@Override
	public String toString() {
		return set.getName() + "." + (op == OpType.ADD ? "add" : "remove") + "(" + inp + ")\n";
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

		// if (found(set, toReplace)) {
		// set = (ExprVar) replacement;
		// }

		if (found(inp, toReplace)) {
			inp = replacement;
		} else {
			inp.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
	}

}