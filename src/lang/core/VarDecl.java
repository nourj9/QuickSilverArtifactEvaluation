package lang.core;

import lang.expr.ExprConstant;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.Type;

public class VarDecl extends ChooseNode {
	private Type type;
	private String name;
	private ExprConstant init;

	public VarDecl(Type type, String name) {
		this(type, name, null);
	}

	public VarDecl(Type type, String name, ExprConstant init) {
		this.type = type;
		this.name = name;
		this.init = init;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Expression getInitValue() {
		return init;
	}

	public ExprVar asExprVar() {
		return new ExprVar(name, type);
	}

	@Override
	public String toString() {
		return type + " " + name + (init == null ? "" : " := " + init);
	}

}
