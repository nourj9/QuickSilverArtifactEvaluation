package lang.type;

import lang.expr.ExprConstant;

public class TypeID extends Type {
	private static TypeID instance = null;

	private TypeID() {
	}

	public static enum Constant {
		UNDEF, SELF, OTHER;

		public String toString() {
			switch (this) {
			case SELF:
				return "self";
			case OTHER:
				return "other";
			default:
				return "undef";
			}
		}
	};

	@Override
	public String toString() {
		return "ID";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return obj instanceof TypeID;
	}

	public static TypeID T() {
		if (instance == null) {
			instance = new TypeID();
		}
		return instance;
	}

	@Override
	public ExprConstant getInitialValue() {
		return new ExprConstant(TypeID.Constant.UNDEF.toString(), TypeID.T());
	}

	@Override
	public Object coerceToType(Object val) {
		throw new UnsupportedOperationException("Type coercion not yet implemented for Process ID types.");
	}

}
