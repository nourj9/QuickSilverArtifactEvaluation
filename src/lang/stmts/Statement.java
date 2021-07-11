package lang.stmts;

import lang.core.ChooseNode;
import lang.expr.Expression;

public abstract class Statement extends ChooseNode {
	public abstract Statement clone();
	public abstract void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement);
	public static boolean found(Expression expr, Expression toReplace) {
		return expr.equals(toReplace);
	}
}
