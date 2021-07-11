package lang.type;

import lang.expr.ExprConstant;

public class TypeInt extends Type {
	private String upperBound;
	private String lowerBound; // needed for cardinality.

	public static enum Constant {
		N, RECVAL;

		public String toString() {
			switch (this) {
			case N:
				return "N";
			default:
				return "Recval";
			}
		}
	};

	// public TypeInt(String bound) {
	// this.bound = bound;
	// this.lowerBound = "0";
	// }

	public TypeInt(String lowerBound, String upperBound) {
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	@Override
	public String toString() {
		if (lowerBound.equals("0")) {
			return "int[" + upperBound + "]";
		} else {
			return "int[" + lowerBound + " to " + upperBound + "]";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		return obj instanceof TypeInt;
	}

	@Override
	public ExprConstant getInitialValue() {
		return new ExprConstant(getLowerBound(), this);
	}

	public static Type Null() {
		return new TypeInt("-1", "-1"); // think of this as the null value of TypeInt
	}

	@Override
	public Object coerceToType(Object val) throws Exception {
		if(!(val instanceof Integer)) {
//			throw new TypeConstraintException("Type coercion to (bounded) Integer currently unsupported from type other Integer");
			throw new Exception("Type coercion to (bounded) Integer currently unsupported from type other Integer");
		}
		
		// Modular coercion
		int lower = Integer.parseInt(lowerBound);
		int upper = Integer.parseInt(upperBound);
		int domainSize = (upper - lower) + 1;
		Integer v = (Integer) val;
		//while(v < lower) {v += domainSize; } // TODO: potential performance issue; fix later.
		return Integer.valueOf(lower + ((((v-lower) % domainSize) + domainSize) % domainSize));
	}

}
