package synthesis;

import lang.expr.ExprConstant;
import lang.expr.ExprOp;
import lang.expr.Expression;
import lang.type.TypeBool;

public class Constraint {
	protected Expression predicate;

	public Constraint(Expression predicate) {
		if (predicate instanceof ExprOp) {
			ExprOp predicateAsOpExpr = (ExprOp) predicate;
			if (predicateAsOpExpr.getType().equals(TypeBool.T())) {
				this.predicate = predicate;
			} else {
				System.err.println("Using the non-boolean Op expression " + predicate + " to create a constraint");
				Thread.dumpStack();
			}
			// true or false? ... sigh add it
		} else if (predicate instanceof ExprConstant && predicate.getType().equals(TypeBool.T())) {
			this.predicate = predicate;		
		} else {
			System.err.println("Using the non-op expression " + predicate + " to create a constraint");
		Thread.dumpStack();
	}

	}

	// it's safe at this point(?)
	public Expression getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return "constraint: " + predicate;
	}

	@Override
	public int hashCode() {
		return predicate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Constraint) {
			Constraint other = (Constraint) obj;
			return this.predicate.equals(other.predicate);
		}
		return false;
	}

}