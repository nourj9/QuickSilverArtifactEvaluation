package synthesis;

import lang.expr.ExprConstant;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.type.TypeBool;
import synthesis.cvc4.CVC4StringTranslator;

public class TestMain {

	public static void main(String[] args) {

		ExprOp arg1 = new ExprOp(ExprOp.Op.AND, new ExprVar("x", TypeBool.T()), new ExprConstant(TypeBool.Constant.FALSE+"", TypeBool.T()));
		ExprOp arg2 = new ExprOp(ExprOp.Op.OR, new ExprVar("Y", TypeBool.T()), new ExprConstant(TypeBool.Constant.TRUE+"", TypeBool.T()));
		ExprOp predicate = new ExprOp(ExprOp.Op.AND, arg1, arg2);
		Constraint c = new Constraint(predicate);
		System.out.println(CVC4StringTranslator.instance().translate(c));
	}

}