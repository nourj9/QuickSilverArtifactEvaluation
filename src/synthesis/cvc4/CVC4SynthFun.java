package synthesis.cvc4;

import java.util.ArrayList;

import Holes.Hole;
import lang.core.Protocol;
import lang.expr.ExprArgsList;
import lang.expr.ExprFunDecl;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeCard;
import lang.type.TypeInt;
import lang.type.TypeLoc;
import synthesis.SynthFun;

public class CVC4SynthFun extends SynthFun {

	private static Protocol p;
	private String CVC4domain;
	private String CVC4retType;
	private String grammer;

	public CVC4SynthFun(Hole hole, Protocol p) {
		super("synthFun_" + hole.getId(), hole);

		// not the best way to do this, really.
		CVC4SynthFun.p = p;
		// domVars = getDomainVars();
		// this.funName = "synthFun_" + hole.getId(); set in super
		this.CVC4domain = decideCVC4Domain(domain);
		this.CVC4retType = decideCVCType(type);
		this.grammer = decideCVCGrammar();
	}

	public String getCVCGrammar() {
		return grammer;
	}

	public String getCVC4Domain() {
		return CVC4domain;
	}

	public String getCVCRetType() {
		return CVC4retType;
	}

	/**
	 * This defaults to LIA now but can do more later.
	 * */
	private String decideCVCGrammar() {
		Type type = hole.getType();
		if (type instanceof TypeInt) {
			return LIAIntGrammar();
		} else if (type instanceof TypeBool) {
			return LIABoolGrammar();
		} else if (type instanceof TypeCard) {
			// no grammar
			// return LIACardGrammar();
			return "";
		} else {
			return ""; // no special grammar for this
		}
	}

//	private String LIACardGrammar() {
//		StringBuilder grammar = new StringBuilder();
//		appn(grammar, "  (( y_cons Int ))");
//		appn(grammar, "  ");
//		appn(grammar, "  (");
//		appn(grammar, "    ( y_cons Int ");
//		appn(grammar, "      (");
//		appn(grammar, "        ( Constant Int )");
//		appn(grammar, "      )");
//		appn(grammar, "    )");
//		appn(grammar, "");
//		appn(grammar, "  )");
//		return grammar.toString();
//	}

	private String LIABoolGrammar() {
		StringBuilder grammar = new StringBuilder();
		appn(grammar, "  (( y_pred Bool ) ( y_term Int ) ( y_cons Int ) )");
		appn(grammar, "  (");
		appn(grammar, "    ( y_pred Bool ");
		appn(grammar, "      (");
		appn(grammar, "        ( Constant Bool)"); // revise
		appn(grammar, "        (= y_term y_term )");
		appn(grammar, "        (> y_term y_term )");
		appn(grammar, "        (>= y_term y_term )");
		appn(grammar, "        (< y_term y_term )");
		appn(grammar, "        (<= y_term y_term )");
		appn(grammar, "      )");
		appn(grammar, "    )");
		appn(grammar, "    ( y_term Int ");
		appn(grammar, "      ( ");
		appn(grammar, "        y_cons");
		appn(grammar, "        ( Variable Int)");
		appn(grammar, "        (- y_term )");
		appn(grammar, "        (+ y_term y_term )");
		appn(grammar, "        (- y_term y_term )");
		appn(grammar, "        (* y_cons y_term )");
		appn(grammar, "        (* y_term y_cons )");
		appn(grammar, "        (div y_term y_cons )");
		// appn(grammar," (mod y_term y_cons )");
		// appn(grammar," (abs y_term )");
		// appn(grammar," (ite y_pred y_term y_term )");
		appn(grammar, "      )");
		appn(grammar, "    )");
		appn(grammar, "    ( y_cons Int ");
		appn(grammar, "      (");
		appn(grammar, "        ( Constant Int )");
		appn(grammar, "      )");
		appn(grammar, "    )");
		appn(grammar, "  )");
		return grammar.toString();
	}

	private String LIAIntGrammar() {
		StringBuilder grammar = new StringBuilder();
		// appn(grammar, " (( y_term Int ) ( y_cons Int ) ( y_pred Bool ))");
		appn(grammar, "  (( y_term Int ) ( y_cons Int ))");
		appn(grammar, "  ");
		appn(grammar, "  (");
		appn(grammar, "    ( y_term Int ");
		appn(grammar, "      ( ");
		appn(grammar, "        y_cons");
		appn(grammar, "        ( Variable Int)");
		appn(grammar, "        (- y_term )");
		appn(grammar, "        (+ y_term y_term )");
		appn(grammar, "        (- y_term y_term )");
		appn(grammar, "        (* y_cons y_term )");
		appn(grammar, "        (* y_term y_cons )");
		appn(grammar, "        (div y_term y_cons )");
		// appn(grammar, " (mod y_term y_cons )");
		// appn(grammar, " (abs y_term )");
		// appn(grammar, " (ite y_pred y_term y_term )");
		appn(grammar, "      )");
		appn(grammar, "    )");
		appn(grammar, "");
		appn(grammar, "    ( y_cons Int ");
		appn(grammar, "      (");
		appn(grammar, "        ( Constant Int )");
		appn(grammar, "      )");
		appn(grammar, "    )");
		appn(grammar, "");
		// appn(grammar, " ( y_pred Bool ");
		// appn(grammar, " (");
		// appn(grammar," ( Constant Bool)"); //revise
		// appn(grammar, " (= y_term y_term )");
		// appn(grammar, " (> y_term y_term )");
		// appn(grammar, " (>= y_term y_term )");
		// appn(grammar, " (< y_term y_term )");
		// appn(grammar, " (<= y_term y_term )");
		// appn(grammar, " )");
		// appn(grammar, " )");
		appn(grammar, "  )");
		return grammar.toString();
	}

	/**
	 * for now, Int and Bool holes have the same domain,
	 * and cardinality and location holes have no domain.
	 * */
	private String decideCVC4Domain(ArrayList<ExprVar> domainVars) {
		Type t = hole.getType();
		if (t instanceof TypeInt || t instanceof TypeBool) {
			StringBuilder dom = new StringBuilder();
			app(dom, "(");
			for (ExprVar vd : domainVars) {
				app(dom, "(");
				apps(dom, vd.getName());
				app(dom, decideCVCType(vd.getType()));
				apps(dom, ")");
			}
			app(dom, ")");
			return dom.toString();
		} else if (t instanceof TypeLoc || t instanceof TypeCard) {
			return "( )"; // no domain for now
		} else {
			System.err.println(
					"Warning: unsupported return type " + t + " in  CVC4Synthesizer.getCVC4Domain, ignoring..");
			return "NoClue2";
		}
	}

	public static String decideCVCType(Type t) {
		if (t instanceof TypeInt) {
			return "Int";
		} else if (t instanceof TypeCard) {
			return "Int";
		} else if (t instanceof TypeBool) {
			return "Bool";
		} else if (t instanceof TypeLoc) {
			return "Loc";
		} else {
			System.err.println(
					"Warning: unsupported return type " + t + " in  CVC4Synthesizer.getCVCRetType, ignoring..");
			return "NoClue1";
		}
	}

	private void app(StringBuilder sb, String s) {
		sb.append(s);
	}

	private void appn(StringBuilder sb, String s) {
		sb.append(s).append("\n");
	}

	private void apps(StringBuilder sb, String s) {
		sb.append(s).append(" ");
	}

	// public static ArrayList<VarDecl> getdomVars() {
	// return domVars;
	// }

	public static Protocol getProtocol() {
		return p;
	}

	public Expression getCurrentCompletion() {
		return hole.getCompletion();
	}

	// here we will return the the args as variables. for instance, if fun(x:int,
	// y:int), we return <x,y>
	public ExprArgsList getArgsList() {
//		ArrayList<Expression> args = new ArrayList<Expression>();
//		for (VarDecl varDecl : hole.getDomain()) {
//			args.add(new ExprVar(varDecl.getName(), varDecl.getType()));
//		}
		return new ExprArgsList(new ArrayList<Expression>(hole.getDomain()));
	}

	public ExprOp makeFunEqCompExpression() {
		Expression completion = getCurrentCompletion();
		ExprFunDecl funDecl = getFunDecl();
		ExprArgsList funArgs = getArgsList();
		ExprOp funApp = new ExprOp(ExprOp.Op.FUNAPP, funDecl, funArgs);
		return new ExprOp(ExprOp.Op.EQ, funApp, completion);
	}
}
