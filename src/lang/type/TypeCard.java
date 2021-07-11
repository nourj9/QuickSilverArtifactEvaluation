package lang.type;

import lang.expr.ExprConstant;

public class TypeCard extends Type {
	private static TypeCard instance = null;

	private TypeCard() {
	}

	@Override
	public String toString() {
		return "TypeCard";
	}

	public static TypeCard T() {
		if (instance == null) {
			instance = new TypeCard();
		}
		return instance;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return obj instanceof TypeCard;
	}

	@Override
	public ExprConstant getInitialValue() {
		System.err.println("Not Implemented");
		Thread.dumpStack();
		return null;
	}

	@Override
	public Object coerceToType(Object val) {
		throw new UnsupportedOperationException("Type coercion not yet implemented for Cardinality types.");
	}

}
