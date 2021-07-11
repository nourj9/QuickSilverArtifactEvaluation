package lang.stmts;

import lang.expr.ExprVar;
import lang.expr.Expression;

public class StmtSetIndexedAssign extends Statement {

	private ExprVar set;
	private Expression index;
	private Expression inp;

	public void setSet(Expression index) {
		this.index = index;
	}

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

	public Expression getIndex() {
		return index;
	}

	public StmtSetIndexedAssign(ExprVar set, Expression index, Expression inp) {
		super();
		this.set = set;
		this.inp = inp;
		this.index = index;
	}

	@Override
	public StmtSetIndexedAssign clone() {
		return new StmtSetIndexedAssign(set.clone(), index.clone(), inp.clone());
	}

	@Override
	public String toString() {
		return set.getName() + "[" + index + "]" + " = " + inp + "\n";
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

		if (found(inp, toReplace)) {
			inp = replacement;
		} else {
			inp.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}

		if (found(index, toReplace)) {
			index = replacement;
		} else {
			index.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
	}

}