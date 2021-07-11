package lang.expr;

import java.util.ArrayList;
import semantics.core.VariableValuation;

public class ExprArgsList extends Expression {
	private ArrayList<Expression> args;

	public ExprArgsList(ArrayList<Expression> args) {
		this.args = args;
	}

	public ArrayList<Expression> getArgs() {
		return args;
	}

	@Override
	public ExprArgsList clone() {
		return new ExprArgsList((ArrayList<Expression>) args.clone());
	}

	public String toString() {
		return "(" + args.toString() + ")";
	}

	public Object eval(VariableValuation variableValuation) {
		System.err.println("eval called on ExprArgList? really?");
		Thread.dumpStack();
		return null;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ExprArgsList) {
			ExprArgsList o = (ExprArgsList) other;
			return args.equals(o.args);
		}
		return false;
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
		for (Expression arg : args) {
			if (found(arg, toReplace)) {
				arg = replacement;
			} else {
				arg.findExprOneAndReplaceWithTwo(toReplace, replacement);
			}
		}
	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {

		ArrayList<Expression> newArgs = new ArrayList<Expression>();

		for (Expression arg : args) {
			newArgs.add(arg.elevateHolesAndPartialEval(variableValuation));
		}

		return new ExprArgsList(newArgs);
	}

	@Override
	public int hashCode() {
		return args.hashCode();
	}

}