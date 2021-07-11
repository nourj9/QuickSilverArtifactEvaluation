package lang.type;

import lang.expr.ExprConstant;

public class TypeBool extends Type {
	private static TypeBool instance = null;

	private TypeBool() {
	}

	public static enum Constant {
		TRUE, FALSE;

		public String toString() {
			switch (this) {
			case TRUE:
				return "True";
			default:
				return "False";
			}
		}
	};

	@Override
	public String toString() {
		return "bool";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}
		
		return obj instanceof TypeBool;
	}

	public static TypeBool T() {
		if (instance == null) {
			instance = new TypeBool();
		}
		return instance;
	}

	@Override
	public ExprConstant getInitialValue() {
		return new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
	}

	public ExprConstant makeTrue() {
		return new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
	}

	public ExprConstant makeFalse() {
		return new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
	}

	@Override
	public Object coerceToType(Object val) throws Exception {
		if(val instanceof Boolean) {
			return val;
		}
		if(val instanceof Integer) {
			return Boolean.valueOf(((Integer)val) != 0); // zero is false, otherwise true
		}
		throw new Exception("Type coercion to Boolean currently unsupported from type other than Boolean or Integer");
		//throw new Exception("Type coercion to Boolean currently unsupported from type other than Boolean or Integer");
	}
}
