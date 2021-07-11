package lang.stmts;

import lang.expr.ExprOp;
import lang.expr.Expression;

public class StmtIfThen extends Statement {
	private Expression cond;
	private StmtBlock thenbranch = null, elsebranch = null;

	public StmtIfThen(Expression cond, StmtBlock thenbranch, StmtBlock elsebranch) {
		this.cond = cond;
		cond.setParent(this);

		this.thenbranch = thenbranch;
		thenbranch.setParent(this);

		this.elsebranch = elsebranch;
		if (elsebranch != null)
			elsebranch.setParent(this);
	}

	@Override
	public StmtIfThen clone() {
		if (elsebranch != null)
			return new StmtIfThen(cond.clone(), thenbranch.clone(), elsebranch.clone());
		else
			return new StmtIfThen(cond.clone(), thenbranch.clone(), null);
	}

	public Expression getCond() {
		return cond;
	}

	public Expression getNegatedCond() {
		ExprOp negCond = new ExprOp(ExprOp.Op.NOT, cond);
		return negCond;
	}

	public StmtBlock getThen() {
		return thenbranch;
	}

	public StmtBlock getElse() {
		return elsebranch;
	}

	public String toString() {
		String result = "if(" + cond + "){\n";
		result = result + thenbranch + "    }\n";
		if (elsebranch != null) {
			result += "    else{\n";
			result = result + elsebranch + "    }\n";
		}
		return result;
	}

	public boolean hasElseBrach() {
		return elsebranch != null;
	}

	public void addElseBranch() {
		elsebranch = new StmtBlock();
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

		if (found(cond, toReplace)) {
			cond = replacement;
		} else {
			cond.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
		thenbranch.findExprOneAndReplaceWithTwo(toReplace, replacement);
		elsebranch.findExprOneAndReplaceWithTwo(toReplace, replacement);
	}

}