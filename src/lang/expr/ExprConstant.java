package lang.expr;
import lang.core.Action;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeChooseSet;
import lang.type.TypeID;
import lang.type.TypeInt;
import lang.type.TypeUnit;
import semantics.core.VariableValuation;

public class ExprConstant extends Expression {
	private String val;
	private Action assosiatedAction = null; // not the cleanest way but oh well.

	public ExprConstant(String val, Type type) {
		this.val = val;
		this.type = type;
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof ExprConstant && this.type.equals(((ExprConstant) other).type) && this.val.toString().equals(((ExprConstant) other).val.toString()));
	}


	public int hashCode() {
		return val.toString().hashCode();
	}

	
	public String getVal() {
		return val;
	}

	@Override
	public ExprConstant clone() {
		return new ExprConstant(val, type);
	}

	@Override
	public String toString() {
		return val;
	}

	@Override
	public Object eval(VariableValuation variableValuation) {

		if (type instanceof TypeInt) {
			return Integer.valueOf(val);
		} else if (type instanceof TypeBool) {
			return Boolean.valueOf(val);
		} else if (type instanceof TypeChooseSet) {
			return val;
		} else if (type instanceof TypeID) {
			return val;
		} else if (type instanceof TypeUnit) {
			return val;
		} else {
			return val;
		}

	}

	public Action getAssosiatedAction() {
		return assosiatedAction;
	}

	public void setAssosiatedAction(Action assosiatedAction) {
		this.assosiatedAction = assosiatedAction;
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {

	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
		return this;
	}

}