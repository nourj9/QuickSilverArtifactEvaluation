package lang.expr;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import lang.type.TypeBool;

public class ExpressionBuilder {

	public static Expression makeConjunction(Expression arg1, Expression arg2) {

		// if they are the same, return either
		if (arg1.equals(arg2)) {
			return arg1;
		}

		// either is false? return false
		if (arg1.equals(TypeBool.T().makeFalse()) || arg2.equals(TypeBool.T().makeFalse())) {
			return TypeBool.T().makeFalse();
		}

		// if either is true, return the other
		if (arg1.equals(TypeBool.T().makeTrue())) {
			return arg2;
		}
		if (arg2.equals(TypeBool.T().makeTrue())) {
			return arg1;
		}

		// otherwise build it
		return new ExprOp(ExprOp.Op.AND, arg1, arg2);
	}

	public static Expression makeDisjunction(Expression arg1, Expression arg2) {

		// if they are the same, return either
		if (arg1.equals(arg2)) {
			return arg1;
		}

		// either is true? return true
		if (arg1.equals(TypeBool.T().makeTrue()) || arg2.equals(TypeBool.T().makeTrue())) {
			return TypeBool.T().makeTrue();
		}

		// if either is false, return the other
		if (arg1.equals(TypeBool.T().makeFalse())) {
			return arg2;
		}
		if (arg2.equals(TypeBool.T().makeFalse())) {
			return arg1;
		}

		// otherwise build it
		return new ExprOp(ExprOp.Op.OR, arg1, arg2);
	}

	public static Expression makeConjunction(LinkedHashSet<Expression> conjuncts) {
		
		Expression conjunction = TypeBool.T().makeTrue();
		for (Expression conjunct : conjuncts) {
			conjunction = makeConjunction(conjunction, conjunct);
		}
		return conjunction;
	}

	public static Expression makeDisjunction(LinkedHashSet<Expression> disjuncts) {
		Expression disjunction = TypeBool.T().makeFalse();
		for (Expression disjunct : disjuncts) {
			disjunction = makeDisjunction(disjunction, disjunct);
		}
		return disjunction;
	}
}
