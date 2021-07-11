package lang.expr;

import java.util.ArrayList;

import lang.type.Type;
import semantics.core.VariableValuation;

public class ExprFunDecl extends Expression {
	private String fnName;
	ArrayList<Type> paramTypes;

	public ExprFunDecl(String fnName, ArrayList<Type> paramTypes, Type returnType) {
		this.fnName = fnName;
		this.paramTypes = paramTypes;
		this.type = returnType;
	}

	@Override
	public ExprFunDecl clone() {
		return new ExprFunDecl(this.fnName, this.paramTypes, this.type);
	}

	public String getFunName() {
		return fnName;
	}

	public ArrayList<Type> getParamTypes() {
		return paramTypes;
	}

	public String toString() {
		return fnName + "(" + paramTypes.toString() + ") : " + type;
	}

	public int hashCode() {
		return fnName.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof ExprFunDecl) && (((ExprFunDecl) other).fnName.equals(this.fnName))
				&& (((ExprFunDecl) other).paramTypes.equals(this.paramTypes))
				&& (((ExprFunDecl) other).type.equals(this.type));
	}

	@Override
	public Object eval(VariableValuation variableValuation) {
		// LATER: this needs more attention. Edit: this is ok, do not try to eval this.
		System.err.println("Needs implementing");
		Thread.dumpStack();
		return null;
		// return variableValuation.getValue(this.name);
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
		return this;
	}

}