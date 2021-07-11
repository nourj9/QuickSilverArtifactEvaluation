package lang.type;

import lang.expr.ExprConstant;

public class TypeChooseSet extends Type {
	private static TypeChooseSet instance = null;

	public static enum Constant {
		EMTPY, ALL;

		public String toString() {
			switch (this) {
			case ALL:
				return "All";
			default:
				return "Empty";
			}
		}
	};

	private TypeChooseSet() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return obj instanceof TypeChooseSet;
	}

	@Override
	public String toString() {
		return "set";
	}

	public static TypeChooseSet T() {
		if (instance == null) {
			instance = new TypeChooseSet();
		}
		return instance;
	}

	@Override
	public ExprConstant getInitialValue() {
		return new ExprConstant(TypeChooseSet.Constant.EMTPY.toString(), TypeChooseSet.T());
	}

	@Override
	public Object coerceToType(Object val) {
		//throw new UnsupportedOperationException("Type coercion not yet implemented for Participant Set types.");
		return val;
	}

}
