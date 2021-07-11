package lang.expr;

import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeCard;
import lang.type.TypeID;
import lang.type.TypeInt;
import lang.type.TypeLoc;
import semantics.core.VariableValuation;

public class ExprOp extends Expression {

	public static enum Op {
		ADD, SUB, MUL, DIV, AND, OR, EQ, NEQ, LT, GT, LE, GE, NOT, FUNAPP, MOD
	};

	private Op op;
	private Expression arg1, arg2;

	public ExprOp(Op op, Expression arg1, Expression arg2) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;

		// some Type checking before we set the type
		this.type = checkType();
	}

	public ExprOp(Op op, Expression arg1) {
		this(op, arg1, null);
	}

	public Expression getArg1() {
		return arg1;
	}

	public Expression getArg2() {
		return arg2;
	}

	public Op getOp() {
		return op;
	}

	@Override
	public ExprOp clone() {
		if (arg2 == null) {
			return new ExprOp(op, arg1.clone());
		} else {
			return new ExprOp(op, arg1.clone(), arg2.clone());
		}
	}

	/** Returns true iff this expression is a comparison. */

	public boolean isFunApp() {
		return op == Op.FUNAPP;
	}

	public boolean isEqualityCheck() {
		return op == Op.EQ || op == Op.NEQ;
	}

	public boolean isComparison() {
		return isEqualityCheck() || op == Op.GT || op == Op.LT || op == Op.GE || op == Op.LE;
	}

	public boolean isArith() {
		return op == Op.ADD || op == Op.SUB || op == Op.MUL || op == Op.DIV || op == Op.MOD;
	}

	public boolean isBoolean() {
		return isEqualityCheck() || op == Op.AND || op == Op.OR || op == Op.NOT;
	}

	public String getOpString() {
		switch (op) {
		case ADD:
			return "+";
		case SUB:
			return "-";
		case MUL:
			return "*";
		case DIV:
			return "/";
		case MOD:
			return "%";
		case GT:
			return ">";
		case LT:
			return "<";
		case GE:
			return ">=";
		case LE:
			return "<=";
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
		case FUNAPP:
			return "FnApp";

		}
		return "The universe is broken";
	}

	public String toString() {

		if (op == Op.FUNAPP) {
			return arg1 + "(" + arg2 + ")";
		}

		if (arg2 != null) {
			return "(" + arg1 + " " + getOpString() + " " + arg2 + ")";
		} else {
			return "(" + getOpString() + " " + arg1 + ")";
		}
	}

	public Object eval(VariableValuation variableValuation) {
		switch (op) {
		case ADD:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString())
					+ Integer.valueOf(this.arg2.eval(variableValuation).toString());
		case SUB:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString())
					- Integer.valueOf(this.arg2.eval(variableValuation).toString());
		case MUL:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString())
					* Integer.valueOf(this.arg2.eval(variableValuation).toString());
		case DIV:
			return (Integer) (Integer.valueOf(this.arg1.eval(variableValuation).toString())
					/ Integer.valueOf(this.arg2.eval(variableValuation).toString()));
		case GT:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) > Integer
					.valueOf(this.arg2.eval(variableValuation).toString());
		case LT:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) < Integer
					.valueOf(this.arg2.eval(variableValuation).toString());
		case GE:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) >= Integer
					.valueOf(this.arg2.eval(variableValuation).toString());
		case LE:
			return Integer.valueOf(this.arg1.eval(variableValuation).toString()) <= Integer
					.valueOf(this.arg2.eval(variableValuation).toString());
		case EQ:
			return (this.arg1.eval(variableValuation).toString().equals(this.arg2.eval(variableValuation).toString()));
		case NEQ:
			return !(this.arg1.eval(variableValuation).toString().equals(this.arg2.eval(variableValuation).toString()));
		case NOT:
			return !Boolean.valueOf(this.arg1.eval(variableValuation).toString().toString());
		case AND:
			return Boolean.valueOf(this.arg1.eval(variableValuation).toString())
					&& Boolean.valueOf(this.arg2.eval(variableValuation).toString());
		case OR:
			return Boolean.valueOf(this.arg1.eval(variableValuation).toString())
					|| Boolean.valueOf(this.arg2.eval(variableValuation).toString());
		case FUNAPP:
			// this needs more attention
			System.err.println("Function Application is not supported yet.");
			Thread.dumpStack();
			return null;

		case MOD:
			// this needs more attention
			System.err.println("Mod is not supported yet.");
			Thread.dumpStack();
			return null;
		}
		return null;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ExprOp) {
			ExprOp o = (ExprOp) other;
			if (arg2 != null && o.arg2 != null) {
				return arg1.equals(o.arg1) && arg2.equals(o.arg2) && op.equals(o.op);
			} else if (arg2 == null && o.arg2 == null) {
				return arg1.equals(o.arg1) && op.equals(o.op);
			}
		}
		return false;
	}

	private Type checkType() {

		if (op == Op.FUNAPP) {
			if (arg1 == null || arg2 == null) {
				System.err.println("OpExpr " + this.toString() + " failed type checking!");
				Thread.dumpStack();
				System.exit(0);
			}

			ExprFunDecl funDef = null;
			ExprArgsList funArgs = null;
			if (arg1 instanceof ExprFunDecl) {
				funDef = (ExprFunDecl) arg1;
			} else {
				System.err.println(
						"FunApp expression with messed up args1 " + this.toString() + " failed type checking!");
				Thread.dumpStack();
				System.exit(0);
			}
			if (arg2 instanceof ExprArgsList) {
				funArgs = (ExprArgsList) arg2;
			} else {
				System.err.println(
						"FunApp expression with messed up args2 " + this.toString() + " failed type checking!");
				Thread.dumpStack();
				System.exit(0);
			}

			// are the arguments and the parameters of matching types?
			if (funDef.getParamTypes().size() != funArgs.getArgs().size()) {
				System.err.println("Incosistent param and args number in expression: " + this.toString()
						+ ". Type checking failed!");
				Thread.dumpStack();
				System.exit(0);
			}
			for (int i = 0; i < funArgs.getArgs().size(); i++) {

				if (!funArgs.getArgs().get(i).getType().equals(funDef.getParamTypes().get(i))) {
					System.err.println("Incosistent param and args types in expression: " + this.toString()
							+ ". Type checking failed!");
					Thread.dumpStack();
					System.exit(0);
				}
			}

			// all set? return the function type as a type
			return funDef.getType();

		}

		if (arg1 == null) {
			System.err.println("OpExpr " + this.toString() + " failed type checking!");
			Thread.dumpStack();
			System.exit(0);
		}

		if (arg2 != null) {
			// System.out.println(arg1.getType());
			// System.out.println(arg2.getType());
			if (arg2.type.equals(arg1.type)) {
				// IDs
				if (arg2.type instanceof TypeID && isEqualityCheck()) {
					return TypeBool.T();
				}

				// Locations
				if (arg2.type instanceof TypeLoc && isEqualityCheck()) {
					return TypeBool.T();
				}

				// Cardinalities
				if (arg2.type instanceof TypeCard) {
					if (isComparison()) {
						return TypeBool.T();
					}
					// add more as needed and allowed.
				}

				if (arg2.type instanceof TypeInt) {
					if (isArith()) {
						String b1u = ((TypeInt) arg1.getType()).getUpperBound();
						String b2u = ((TypeInt) arg2.getType()).getUpperBound();

						String upperBound;

						if (b1u.equalsIgnoreCase("n") || b2u.equalsIgnoreCase("n")) {
							upperBound = "n";
						} else {
							// otherwise get the maximum
							int b1up = Integer.valueOf(b1u);
							int b2up = Integer.valueOf(b2u);
							upperBound = String.valueOf(Math.max(b1up, b2up));
						}

						String b1l = ((TypeInt) arg1.getType()).getLowerBound();
						String b2l = ((TypeInt) arg2.getType()).getLowerBound();

						String lowerBound;

						if (b1u.equalsIgnoreCase("n") || b2u.equalsIgnoreCase("n")) {
							lowerBound = "n";
						} else {
							// otherwise get the maximum
							int b1lo = Integer.valueOf(b1l);
							int b2lo = Integer.valueOf(b2l);
							lowerBound = String.valueOf(Math.min(b1lo, b2lo));
						}

						return new TypeInt(lowerBound, upperBound);

					} else if (isComparison()) {
						return TypeBool.T();
					}
				}

				if (arg2.type instanceof TypeBool && isBoolean()) {
					return TypeBool.T();
				}
			}

			// Cardinality holes
			if ((arg1.type instanceof TypeCard && arg2.type instanceof TypeCard) || // both are card is ok too?
					(arg1.type instanceof TypeCard && arg2.type instanceof TypeInt) || //
					(arg2.type instanceof TypeCard && arg1.type instanceof TypeInt)) {
				if (isArith()) {
					return new TypeInt("1", "N");
				} else if (isComparison()) {
					return TypeBool.T();
				}
			}

		} else {
			if (op == Op.NOT) {
				return TypeBool.T();
			}
		}
		System.err.println("OpExpr " + this.toString() + " failed type checking");
		System.err.println("Op: " + op);
		System.err.println("arg1 " + arg1);
		System.err.println("arg1 type " + arg1.getType());

		if (arg2 != null) {
			System.err.println("arg2 " + arg2);
			System.err.println("arg2 type " + arg2.getType());
		}

		Thread.dumpStack();
		System.exit(0);
		return null;
	}

	@Override
	public void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement) {
		if (found(arg1, toReplace)) {
			arg1 = replacement;
		} else {
			arg1.findExprOneAndReplaceWithTwo(toReplace, replacement);
		}
		if (arg2 != null) {
			if (found(arg2, toReplace)) {
				arg2 = replacement;
			} else {
				arg2.findExprOneAndReplaceWithTwo(toReplace, replacement);
			}
		}
	}

	@Override
	public Expression elevateHolesAndPartialEval(VariableValuation variableValuation) {
		if (arg1 == null) {
			return new ExprOp(op, arg1.elevateHolesAndPartialEval(variableValuation));
		} else {
			return new ExprOp(op, arg1.elevateHolesAndPartialEval(variableValuation),
					arg2.elevateHolesAndPartialEval(variableValuation));
		}
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}