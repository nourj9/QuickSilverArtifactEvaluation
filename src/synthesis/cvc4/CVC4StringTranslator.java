package synthesis.cvc4;

import lang.expr.ExprArgsList;
import lang.expr.ExprConstant;
import lang.expr.ExprFunDecl;
import lang.expr.ExprHole;
import lang.expr.ExprLoc;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.expr.ExprOp.Op;
import lang.type.TypeBool;
import synthesis.BackendTranslator;
import synthesis.Constraint;

public class CVC4StringTranslator extends BackendTranslator<String> {
	private static CVC4StringTranslator instance = null;
	
	private CVC4StringTranslator() {
	}
	public String translate(Constraint constraint) {
		return "(constraint " + generateCode(constraint.getPredicate()) + ")";
	}

	private static String generateCode(Expression expr) {
		if (expr instanceof ExprOp) {
			ExprOp opExp = (ExprOp) expr;
			Op op = opExp.getOp();
			String opStr = "";
			String arg1Str = "";
			String arg2Str = "";
			switch (op) {
			case NEQ:
				ExprOp eq = new ExprOp(ExprOp.Op.EQ, opExp.getArg1(), opExp.getArg2());
				ExprOp NotEq = new ExprOp(ExprOp.Op.NOT, eq);
				return generateCode(NotEq);
			case NOT:
				opStr = getCVC4OpCode(op);
				arg1Str = generateCode(opExp.getArg1());
				return "(" + opStr + " " + arg1Str + ")";
			case FUNAPP:
				ExprFunDecl funExp = (ExprFunDecl) opExp.getArg1();
				ExprArgsList argExp = (ExprArgsList) opExp.getArg2();
				opStr = funExp.getFunName();
				if (argExp.getArgs().isEmpty()) {
					return opStr; // function with no args
				} else {
					for (Expression arg : argExp.getArgs()) {
						arg1Str = arg1Str + generateCode(arg) + " ";
					}
					return "(" + opStr + " " + arg1Str + ")";
				}
			default:
				opStr = getCVC4OpCode(op);
				arg1Str = generateCode(opExp.getArg1());
				arg2Str = generateCode(opExp.getArg2());
				return "(" + opStr + " " + arg1Str + " " + arg2Str + ")";
			}

		} else if (expr instanceof ExprConstant) {
			ExprConstant e = (ExprConstant) expr;
			if(expr.getType().equals(TypeBool.T())) {
				return e.getVal().toLowerCase(); // to go from True to true, etc.
			}else {
				return e.getVal(); // otherwise, as is.				
			}
		} else if (expr instanceof ExprVar) {
			ExprVar e = (ExprVar) expr;
			return e.getName();
		} else if (expr instanceof ExprHole) {
			// revise?
			ExprHole e = (ExprHole) expr;
			return generateCode(e.getHole().getCompletion());
		} else if (expr instanceof ExprLoc) {
			ExprLoc e = (ExprLoc) expr;
			return e.getTargetLocName();
		}
		return null;
	}

	private static String getCVC4OpCode(Op op) {
		switch (op) {
		case ADD:
			return "+";
		case SUB:
			return "-";
		case MUL:
			return "*";
		case DIV:
			return "div";
		case GT:
			return ">";
		case LT:
			return "<";
		case GE:
			return ">=";
		case LE:
			return "<=";
		case EQ:
			return "=";
		case AND:
			return "and";
		case OR:
			return "or";
		case NOT:
			return "not";
		case NEQ:
		default:
			return "not supported";
		}
	}

	public static CVC4StringTranslator instance() {
		if(instance == null) {
			return new CVC4StringTranslator();
		}
		
		return instance;
	}
}
