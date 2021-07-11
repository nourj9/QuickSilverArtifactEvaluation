package lang.expr;

import lang.core.ChooseNode;
import lang.type.Type;
import semantics.core.VariableValuation;

public abstract class Expression extends ChooseNode {

	protected Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public abstract Object eval(VariableValuation variableValuation);

	/**
	 * The idea here is as follows: say we call this on the  
	 * expression ("x > y") && (("z == w") || (n+1 == 2)) 
	 * and sigma of (x = 1, y = 2, z = 3, w = 4, n = 5)
	 * such that "x > y" and "z == w", are holes h1(a,b) and h2(y,z,w) respectively. 
	 * 
	 * Then, this function will result in the following expression
	 * (h1(1,2)) && (h2(2,3,4)) || (1+1 == 2))
	 * 
	 *  the elevate part is what introduces the holes, and the partialEval
	 *  part is what plugs the values in.
	 * */
	public abstract Expression elevateHolesAndPartialEval(VariableValuation variableValuation);
	public abstract void findExprOneAndReplaceWithTwo(Expression toReplace, Expression replacement);
	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract Expression clone();
	public static boolean found(Expression expr, Expression toReplace) {
		return expr.equals(toReplace);
	}
		
}