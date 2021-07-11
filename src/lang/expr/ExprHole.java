package lang.expr;

import java.util.ArrayList;

import Holes.Hole;
import lang.type.Type;
import semantics.core.VariableValuation;
import synthesis.SynthFun;

public class ExprHole extends Expression {
	private Hole hole;

	public ExprHole(Hole hole) {
		this.hole = hole;
		this.type = hole.getType();
	}

	@Override
	public ExprHole clone() {
		return new ExprHole(hole);
	}

	public Hole getHole() {
		return hole;
	}

	public String toString() {
		if (hole.isCompletted()) {
			return "[" + hole.getId() + ": " + hole.getCompletion() + "]";
		} else {
			return "??" + "(" + hole.getId() + ")";
		}
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof ExprHole) && (((ExprHole) other).hole.getId() == this.hole.getId());
	}

	@Override
	public Object eval(VariableValuation variableValuation) {

		if (!hole.isCompletted()) {
			System.err.println("Error: trying to evaluate an incopmletted hole! (" + hole.getId() + ")");
			Thread.dumpStack();
			System.exit(-1);
		}

		return hole.getCompletion().eval(variableValuation);
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
		hole.getCompletion().findExprOneAndReplaceWithTwo(toReplace, replacement);
	}

	public Type getType() {
		return hole.getType();
	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
		// the fancy stuff. exprHole --> FunApp over concrete vals.

		SynthFun fun = hole.getSynthFun();
		ExprFunDecl funDecl = fun.getFunDecl();
		ArrayList<ExprVar> domain = fun.getDomain();

		// build an arg list wuth the domian values from sigma
		ArrayList<Expression> args = new ArrayList<Expression>();
		for (ExprVar var : domain) {
			args.add(new ExprConstant(String.valueOf(var.eval(variableValuation)), var.getType()));
		}
		ExprArgsList argsList = new ExprArgsList(args);

		return new ExprOp(ExprOp.Op.FUNAPP, funDecl, argsList);
	}

	@Override
	public int hashCode() {
		return hole.hashCode();
	}

}