package liveness.buchi;

public class LivenessExprOp extends LivenessExpression {


	public static enum Op {
		AND, OR, EQ, NEQ, NOT
	};

	private Op op;
	private LivenessExpression arg1, arg2;

	private LivenessExprOp(Op op, LivenessExpression arg1, LivenessExpression arg2) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	public LivenessExprOp(Op op, LivenessExpression arg1) {
		this(op, arg1, null);
	}

	public LivenessExpression getArg1() {
		return arg1;
	}

	public LivenessExpression getArg2() {
		return arg2;
	}

	public Op getOp() {
		return op;
	}

	public String getOpString() {
		switch (op) {
		case EQ:
			return "==";
		case NEQ:
			return "!=";
		case NOT:
			return "!";
		case AND:
			return "&&";
		case OR:
			return "||";
		}
		return "The universe is broken";
	}

	public String toString() {

		if (arg2 != null) {
			return "("+arg1 + " " + getOpString() + " " + arg2+")";
		} else {
			return "("+getOpString() + " " + arg1+")";
		}
	}

//	public Object eval(VariableValuation variableValuation) {
//		switch (op) {
//		case ADD:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString())
//					+ Integer.valueOf(this.arg2.eval(variableValuation).toString());
//		case SUB:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString())
//					- Integer.valueOf(this.arg2.eval(variableValuation).toString());
//		case MUL:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString())
//					* Integer.valueOf(this.arg2.eval(variableValuation).toString());
//		case DIV:
//			return (Integer) (Integer.valueOf(this.arg1.eval(variableValuation).toString())
//					/ Integer.valueOf(this.arg2.eval(variableValuation).toString()));
//		case GT:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) > Integer
//					.valueOf(this.arg2.eval(variableValuation).toString());
//		case LT:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) < Integer
//					.valueOf(this.arg2.eval(variableValuation).toString());
//		case GE:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) >= Integer
//					.valueOf(this.arg2.eval(variableValuation).toString());
//		case LE:
//			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) <= Integer
//					.valueOf(this.arg2.eval(variableValuation).toString());
//		case EQ:
//			return (this.arg1.eval(variableValuation).toString().equals(this.arg2.eval(variableValuation).toString()));
//		case NEQ:
//			return !(this.arg1.eval(variableValuation).toString().equals(this.arg2.eval(variableValuation).toString()));
//		case NOT:
//			return !Boolean.valueOf(this.arg1.eval(variableValuation).toString().toString());
//		case AND:
//			return Boolean.valueOf(this.arg1.eval(variableValuation).toString())
//					&& Boolean.valueOf(this.arg2.eval(variableValuation).toString());
//		case OR:
//			return Boolean.valueOf(this.arg1.eval(variableValuation).toString())
//					|| Boolean.valueOf(this.arg2.eval(variableValuation).toString());
//		case FUNAPP:
//			// this needs more attention
//			System.err.println("Function Application is not supported yet.");
//			Thread.dumpStack();
//			return null;
//		}
//		return null;
//	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LivenessExprOp) {
			LivenessExprOp o = (LivenessExprOp) other;
			if (arg2 != null && o.arg2 != null) {
				return arg1.equals(o.arg1) && arg2.equals(o.arg2) && op.equals(o.op);
			} else if (arg2 == null && o.arg2 == null) {
				return arg1.equals(o.arg1) && op.equals(o.op);
			}
		}
		return false;
	}

//	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
//		if (found(arg1, toReplace)) {
//			arg1 = replacement;
//		} else {
//			arg1.findExprOneAndReplaceWithTwo(toReplace, replacement);
//		}
//
//		if (found(arg2, toReplace)) {
//			arg2 = replacement;
//		} else {
//			arg2.findExprOneAndReplaceWithTwo(toReplace, replacement);
//		}
//	}
//
//	@Override
//	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
//		if (arg1 == null) {
//			return new ExprOp(op, arg1.elevateHolesAndPartialEval(variableValuation));
//		} else {
//			return new ExprOp(op, arg1.elevateHolesAndPartialEval(variableValuation),
//					arg2.elevateHolesAndPartialEval(variableValuation));
//		}
//	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}


}
