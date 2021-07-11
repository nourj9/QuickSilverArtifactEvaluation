package lang.stmts;

import lang.expr.Expression;

public class StmtAssign extends Statement {
	private Expression lhs, rhs;

	public StmtAssign(Expression lhs, Expression rhs) {
		if(!lhs.getType().equals(rhs.getType())){
			System.out.println("Trying to create an assign statement with mismatching types: ");
			System.out.println("lhs = "+lhs + "("+lhs.getType()+")");
			System.out.println("lhs = "+rhs + "("+rhs.getType()+")");
			System.exit(-1);
		}
		this.lhs = lhs;
		this.rhs = rhs;
		this.lhs.setParent(this);
		this.rhs.setParent(this);
	}

	@Override
	public StmtAssign clone() {
		return new StmtAssign(lhs.clone(), rhs.clone());
	}

	public Expression getLHS() {
		return lhs;
	}

	public Expression getRHS() {
		return rhs;
	}

	public String toString() {
		return "   " + lhs + " := " + rhs + "\n";
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
		//if (found(lhs, toReplace)) {
		//	lhs = replacement;
		//}
		if (found(rhs, toReplace)) {
			rhs = replacement;
		}else{
			rhs.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
	}

}