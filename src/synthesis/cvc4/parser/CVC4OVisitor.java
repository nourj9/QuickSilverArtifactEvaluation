package synthesis.cvc4.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTree;

import Holes.Hole;
import lang.core.Location;
import lang.core.VarDecl;
import lang.expr.ExprArgsList;
import lang.expr.ExprConstant;
import lang.expr.ExprFunDecl;
import lang.expr.ExprLoc;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeInt;
import synthesis.cvc4.CVC4SynthFun;
import synthesis.cvc4.parser.CVC4OutputParser.*;

public class CVC4OVisitor extends CVC4OutputBaseVisitor<Object> {

	private HashMap<String, CVC4SynthFun> funNameToSynFun;
	private CVC4SynthFun currentFunc = null;

	@Override
	public Object visit(ParseTree tree) {
		// System.out.println("before");
		return super.visit(tree);
		// System.out.println("after");
		// return null;
	}

	@Override
	public Object visitResponse(ResponseContext ctx) {
		return super.visitResponse(ctx);
	}

	@Override
	public Object visitResModels(ResModelsContext ctx) {
		return super.visitResModels(ctx);
	}

	@Override
	public Object visitResSuperError(ResSuperErrorContext ctx) {
		System.err.println("CVC4 error: " + ctx.getText());
		System.exit(-1);
		return null;
	}

	@Override
	public Object visitResUnkown(ResUnkownContext ctx) {
		System.err.println("CVC4 error: unknown ");
		System.exit(-1);
		return null;
	}

	@Override
	public Object visitResError(ResErrorContext ctx) {
		System.err.println("CVC4 error: " + ctx.string().getText());
		System.exit(-1);
		return null;
	}

	@Override
	public Object visitResMemout(ResMemoutContext ctx) {
		System.err.println("CVC4 error: memout ");
		System.exit(-1);
		return null;
	}

	@Override
	public Object visitModel_response(Model_responseContext ctx) {
		String funName = ctx.funName.getText();
		CVC4SynthFun fun = funNameToSynFun.getOrDefault(funName, null);
		if (fun != null) {
			Hole hole = fun.getHole();
			currentFunc = fun;
			Expression body = (Expression) visit(ctx.funBody);
			hole.setCompletion(body);
		} else {
			System.err.println(
					"CVC4 error: this should not happen, but somehow this function made to the solver with no corrosponding function to synthezise: "
							+ funName);
			System.exit(-1);
		}
		return null;
	}

	@Override
	public Object visitTermGeneral(TermGeneralContext ctx) {
		String op = ctx.op.getText();

		ArrayList<Expression> args = new ArrayList<Expression>();
		for (TermContext term : ctx.term()) {
			args.add((Expression) visit(term));
		}

		// special treatment for negative numbers
		if (op.equals("-") && args.size() == 1) {
			return new ExprConstant("-1", new TypeInt("-1", "-1"));
		}

		// take care of core binary expressions.
		if (args.size() == 2) {
			if (op.equals("+")) {
				return new ExprOp(ExprOp.Op.ADD, args.get(0), args.get(1));
			} else if (op.equals("-")) {
				return new ExprOp(ExprOp.Op.SUB, args.get(0), args.get(1));
			} else if (op.equals("*")) {
				return new ExprOp(ExprOp.Op.MUL, args.get(0), args.get(1));
			} else if (op.equals("div")) {
				return new ExprOp(ExprOp.Op.DIV, args.get(0), args.get(1));
			} else if (op.equals(">")) {
				return new ExprOp(ExprOp.Op.GT, args.get(0), args.get(1));
			} else if (op.equals("<")) {
				return new ExprOp(ExprOp.Op.LT, args.get(0), args.get(1));
			} else if (op.equals(">=")) {
				return new ExprOp(ExprOp.Op.GE, args.get(0), args.get(1));
			} else if (op.equals("<=")) {
				return new ExprOp(ExprOp.Op.LE, args.get(0), args.get(1));
			} else if (op.equals("=")) {
				return new ExprOp(ExprOp.Op.EQ, args.get(0), args.get(1));
			} else if (op.equals("and")) {
				return new ExprOp(ExprOp.Op.AND, args.get(0), args.get(1));
			} else if (op.equals("or")) {
				return new ExprOp(ExprOp.Op.OR, args.get(0), args.get(1));
			}
		}

		// our fancy unary expression
		if (op.equals("not") && args.size() == 1) {
			return new ExprOp(ExprOp.Op.NOT, args.get(0));
		}

		// check if it's a synthesis function!
		if (funNameToSynFun.containsKey(op)) {
			CVC4SynthFun fun = funNameToSynFun.get(op);
			return new ExprOp(ExprOp.Op.FUNAPP, //
					new ExprFunDecl(op, fun.getDomainTypes(), fun.getType()), //
					new ExprArgsList(args)//
			);
		}

		// otherwise, fail for now.
		System.err.println("CVC4 error: not sure about this operation/function: " + op);
		System.exit(-1);
		return null;

	}

	@Override
	public Object visitTermSymbol(TermSymbolContext ctx) {

		// Note: this can be made much smarter by doing it
		// it on a higher level and use the context of the function itself
		// and its return type etc.

		String var = ctx.getText();

		// is it a variable?
		// if the currentFunc is not null, search its domain
		if (currentFunc != null) {
			for (ExprVar v : currentFunc.getDomain()) {
				if (var.equals(v.getName())) {
					return v;
				}
			}
		}
		// is it a location?
		for (Location loc : CVC4SynthFun.getProtocol().getLocations()) {
			if (loc.getName().equals(var)) {
				return new ExprLoc(var);
			}
		}

		// otherwise, fail for now.
		System.err.println("CVC4 error: not sure about this term: " + var);
		System.err.println("this may change with more complicated constraints");
		System.exit(-1);
		return null;
	}

	@Override
	public Object visitTermLiteral(TermLiteralContext ctx) {
		return super.visitTermLiteral(ctx);
	}

	@Override
	public Object visitLitBool(LitBoolContext ctx) {

		String val = ctx.getText();
		if (val.equalsIgnoreCase("true")) {
			return new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
		} else {
			return new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
		}
	}

	@Override
	public Object visitLitInt(LitIntContext ctx) {
		String val = ctx.getText();
		return new ExprConstant(val, new TypeInt(val, val));
	}

	@Override
	public Object visitLitN(LitNContext ctx) {
		String val = ctx.getText();
		return new ExprConstant(val, new TypeInt(val, val));
	}

	public void pasreAndRaiseCompletions(HashMap<String, CVC4SynthFun> funNameToSynFun, ParseTree tree) {

		// to give access to other functions!
		this.funNameToSynFun = funNameToSynFun;
		visit(tree);
	}

}
