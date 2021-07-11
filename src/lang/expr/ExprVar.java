package lang.expr;

import lang.core.VarDecl;
import lang.type.Type;
import semantics.core.VariableValuation;

public class ExprVar extends Expression {
	private String name;

	public ExprVar(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public ExprVar clone() {
		return new ExprVar(this.name, this.type);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public VarDecl asVarDecl() {
		return new VarDecl(type, name, type.getInitialValue());
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof ExprVar) && (((ExprVar) other).name.equals(this.name))
				&& (((ExprVar) other).type.equals(this.type));
	}

	@Override
	public Object eval(VariableValuation variableValuation) {
		return variableValuation.getValue(this.name);
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
		//make it into a constant.
		return new ExprConstant(String.valueOf(eval(variableValuation)), type);
	}

}