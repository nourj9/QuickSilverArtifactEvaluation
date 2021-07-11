package lang.type;

import lang.expr.ExprConstant;

public class TypeUnit extends Type {

	private static TypeUnit instance = null;

	private TypeUnit() {
	}

	@Override
	public String toString() {
		return "Unit";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return obj instanceof TypeUnit;
	}
	public static TypeUnit T() {
		if (instance == null) {
			instance = new TypeUnit();
		}
		return instance;
	}
	@Override
	public ExprConstant getInitialValue() {
		System.err.println("Error: calling initial value of TypeUnit..");
		Thread.dumpStack();
		return null;
	}

	@Override
	public Object coerceToType(Object val) {
		throw new UnsupportedOperationException("Type coercion not supported for Unit types.");
	}

}
