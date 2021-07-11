package lang.type;

import lang.core.ChooseNode;
import lang.expr.ExprConstant;

public abstract class Type extends ChooseNode {

	public abstract ExprConstant getInitialValue();

	public abstract Object coerceToType(Object val) throws Exception;	
}
