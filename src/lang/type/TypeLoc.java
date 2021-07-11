package lang.type;

import lang.expr.ExprConstant;

public class TypeLoc extends Type {
	private static TypeLoc instance = null;

	private TypeLoc() {
	}

	@Override
	public String toString() {
		return "Loc";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return obj instanceof TypeLoc;
	}

	public static TypeLoc T() {
		if (instance == null) {
			instance = new TypeLoc();
		}
		return instance;
	}

	@Override
	public ExprConstant getInitialValue() {
		System.err.println("Error: calling initial value of TypeLoc..");
		Thread.dumpStack();
		return null;
	}

	@Override
	public Object coerceToType(Object val) {
		throw new UnsupportedOperationException("Type coercion not yet implemented for Location types.");
	}

}
