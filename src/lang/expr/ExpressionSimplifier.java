package lang.expr;

import lang.type.TypeBool;

public class ExpressionSimplifier {

	public static Expression simplify(Expression expr) {
		if (expr instanceof ExprOp) {
			ExprOp exprOp = (ExprOp) expr;

			Expression simArg1 = simplify(exprOp.getArg1());
			Expression simArg2 = null;
			if (exprOp.getArg2() != null) {
				simArg2 = simplify(exprOp.getArg2());
			}
			//System.out.println();
			switch (exprOp.getOp()) {

			case ADD:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					int res = Integer.valueOf(const1.getVal()) + Integer.valueOf(const2.getVal());
					return new ExprConstant(String.valueOf(res), exprOp.getType());
				}
				break;
			case MUL:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					int res = Integer.valueOf(const1.getVal()) * Integer.valueOf(const2.getVal());
					return new ExprConstant(String.valueOf(res), exprOp.getType());
				}
				break;
			case SUB:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					int res = Integer.valueOf(const1.getVal()) - Integer.valueOf(const2.getVal());
					return new ExprConstant(String.valueOf(res), exprOp.getType());
				}
				break;
			case AND:
				// take advantage of logic there
				return ExpressionBuilder.makeConjunction(simArg1, simArg2);
			case OR:
				// take advantage of logic there
				return ExpressionBuilder.makeDisjunction(simArg1, simArg2);
			case EQ:
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					if (simArg1.equals(simArg2)) {
						return TypeBool.T().makeTrue();
					} else {
						return TypeBool.T().makeFalse();
					}
				}
				break;
			case NEQ:
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					if (!simArg1.equals(simArg2)) {
						return TypeBool.T().makeTrue();
					} else {
						return TypeBool.T().makeFalse();
					}
				}
				break;
			case NOT:
				if (simArg1.equals(TypeBool.T().makeTrue())) {
					return TypeBool.T().makeFalse();
				} else if (simArg1.equals(TypeBool.T().makeFalse())) {
					return TypeBool.T().makeTrue();
				}
				break;
			case GE:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					if (Integer.valueOf(const1.getVal()) >= Integer.valueOf(const2.getVal())) {
						return TypeBool.T().makeTrue();
					} else {
						return TypeBool.T().makeFalse();
					}
				}
				break;
			case GT:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					if (Integer.valueOf(const1.getVal()) > Integer.valueOf(const2.getVal())) {
						return TypeBool.T().makeTrue();
					} else {
						return TypeBool.T().makeFalse();
					}
				}
				break;
			case LE:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					if (Integer.valueOf(const1.getVal()) <= Integer.valueOf(const2.getVal())) {
						return TypeBool.T().makeTrue();
					} else {
						return TypeBool.T().makeFalse();
					}
				}
				break;
			case LT:
				// try to see if the ops are actually constants, then do the operation.
				if (simArg1 instanceof ExprConstant && simArg2 instanceof ExprConstant) {
					ExprConstant const1 = (ExprConstant) simArg1;
					ExprConstant const2 = (ExprConstant) simArg2;
					if (Integer.valueOf(const1.getVal()) < Integer.valueOf(const2.getVal())) {
						return TypeBool.T().makeTrue();
					} else {
						return TypeBool.T().makeFalse();
					}
				}
			default:
				break;
			}
			//return partial progress
			return new ExprOp(exprOp.getOp(), simArg1, simArg2);
			
		}else {
			return expr;
		}
	}

}
