package lang.handler;

import lang.expr.ExprConstant;
import lang.expr.Expression;
import lang.stmts.StmtBlock;
import lang.type.TypeBool;

public class ReactionPredicated extends Reaction {

	private StmtBlock block;
	private Expression predicate;

	public ReactionPredicated(StmtBlock block, Expression predicate) {
		this.block = block;
		this.predicate = predicate;
	}

	public ReactionPredicated(StmtBlock block) {
		this(block, new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T()));
	}

	public StmtBlock getBlock() {
		return block;
	}

	public Expression getPredicate() {
		return predicate;
	}

}
