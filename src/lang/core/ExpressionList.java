package lang.core;

import java.util.List;

import lang.expr.Expression;

public class ExpressionList extends ChooseNode {
	private List<Expression> list;

	public ExpressionList(List<Expression> le) {
		this.list = le;
	}

	public List<Expression> getList() {
		return list;
	}

	public void setList(List<Expression> list) {
		this.list = list;
	}

}
