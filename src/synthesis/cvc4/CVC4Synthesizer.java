package synthesis.cvc4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import Holes.Hole;
import Holes.HoleManager;
import antlr.parsing.ThrowingErrorListener;
import lang.core.Location;
import lang.core.Protocol;
import lang.expr.ExprArgsList;
import lang.expr.ExprConstant;
import lang.expr.ExprFunDecl;
import lang.expr.ExprOp;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeCard;
import lang.type.TypeChooseSet;
import lang.type.TypeID;
import lang.type.TypeInt;
import lang.type.TypeLoc;
import lang.type.TypeUnit;
import synthesis.Constraint;
import synthesis.Synthesizer;
import synthesis.cvc4.parser.CVC4OVisitor;
import synthesis.cvc4.parser.CVC4OutputLexer;
import synthesis.cvc4.parser.CVC4OutputParser;

public class CVC4Synthesizer extends Synthesizer {
	private StringBuilder _prefix = new StringBuilder();
	private StringBuilder _definitions = new StringBuilder();
	private StringBuilder _varDefs = new StringBuilder();
	private StringBuilder _constraints = new StringBuilder();
	private StringBuilder _suffix = new StringBuilder();
	private Protocol p;
	// one is probably not needed?
	HashMap<Hole, CVC4SynthFun> holeToSynFun = new HashMap<Hole, CVC4SynthFun>();
	HashSet<CVC4SynthFun> synthFuns = new HashSet<CVC4SynthFun>();
	HashMap<String, CVC4SynthFun> funNameToSynFun = new HashMap<String, CVC4SynthFun>();
	HashMap<Type, String> mercuryTpe2CVC4Type = new HashMap<Type, String>();
	private HashSet<ExprVar> declaredVars = new HashSet<ExprVar>();

	public CVC4Synthesizer(Protocol p) {
		this.p = p;
	}

	@Override
	public void initialize() {
		appn(_prefix, "(set-logic ALL)");
		generateNeededTypes();
		appn(_suffix, "(check-synth)"); // note! so it happens once.
	}

	private void generateNeededTypes() {
		for (Hole hole : HoleManager.getHoles().values()) {
			Type t = hole.getType();
			if (t instanceof TypeUnit) {
				// Do nothing for now
			} else if (t instanceof TypeInt) {
				// Do nothing for now
			} else if (t instanceof TypeCard) {
				// Do nothing for now
			} else if (t instanceof TypeBool) {
				// Do nothing for now
			} else if (t instanceof TypeChooseSet) {
				// Do nothing for now
			} else if (t instanceof TypeID) {
				// Do nothing for now
			} else if (t instanceof TypeLoc) {

				if (mercuryTpe2CVC4Type.containsKey(t)) {
					continue; // already generated
				}
				// Here, we need to create the Enum for the type thingy
				ArrayList<Location> locs = p.getLocations();
				// this is hard-coded for now, but can be made nicer later

				appn(_definitions, "(declare-datatypes");
				appn(_definitions, " ((Loc 0))");
				appn(_definitions, " ((");
				for (Location loc : locs) {
					// TODO revise? currently, we are excluding pre-processing generated locs.
					if (loc.isUserProvidedLocation()) {
						
						//if(loc.getName().startsWith("Colle")) {
						//	continue;
						//}
						
						appn(_definitions, "  (" + loc.getName() + ")");
					}
				}
				appn(_definitions, " ))");
				appn(_definitions, ")");

				mercuryTpe2CVC4Type.put(t, "Loc"); // later make Loc generic?

			} else {
				System.err.println("Warning: unkown Type in  CVC4Synthesizer.generateNeededTypes, ignoring..");
			}
		}
	}

	@Override
	public void generateSynthFuns() {

		for (Hole hole : HoleManager.getHoles().values()) {
			CVC4SynthFun fun = new CVC4SynthFun(hole, p);

			synthFuns.add(fun);
			holeToSynFun.put(hole, fun);
			// TODO for now, give the hole a back reference to the synthesis engine.
			// Ideally, the holes need to come back here for this.
			hole.setSynthsisizer(this);
			funNameToSynFun.put(fun.getFunName(), fun);

			String funName = fun.getFunName();
			String domain = fun.getCVC4Domain();
			String retType = fun.getCVCRetType();
			String grammer = fun.getCVCGrammar();

			apps(_definitions, "(synth-fun");
			apps(_definitions, funName);
			apps(_definitions, domain);
			appn(_definitions, retType);
			appn(_definitions, grammer);
			appn(_definitions, ")");

			// Finally, add all vars that are part of domains
			for (ExprVar var : fun.getDomain()) {

				// avoid double declarations
				if (declaredVars.contains(var)) {
					continue;
				}

				apps(_varDefs, "(declare-var");
				apps(_varDefs, var.getName());
				apps(_varDefs, CVC4SynthFun.decideCVCType(var.getType()));
				appn(_varDefs, ")");
				
				declaredVars.add(var);
				
			}

		}
	}

	@Override
	public void generateInitialConstraints() {
		for (CVC4SynthFun fun : synthFuns) {
			if (fun.getType() instanceof TypeCard) {
				ExprFunDecl funDecl = fun.getFunDecl();
				ExprArgsList funArgs = fun.getArgsList();
				ExprOp funApp = new ExprOp(ExprOp.Op.FUNAPP, funDecl, funArgs);

				ExprConstant zero = new ExprConstant("0", new TypeInt("0", "0"));
				ExprOp cardGTzero = new ExprOp(ExprOp.Op.GT, funApp, zero);
				addConstraint(new Constraint(cardGTzero));

				ExprConstant five = new ExprConstant("5", new TypeInt("5", "5"));
				ExprOp cardLTfive = new ExprOp(ExprOp.Op.LT, funApp, five);
				addConstraint(new Constraint(cardLTfive));
			}
		}
	}

	@Override
	public void flushCompletions() {
		for (Hole hole : HoleManager.getHoles().values()) {
			hole.clearCompletion();
		}
	}

	@Override
	public void completeHoles() {
		// System.out.print("- ");
		//dumpCode();
		File cvc4Code = writeCodeToFile();
		InputStream cvc4Output = callCVC4(cvc4Code);
		// BufferedReader cvc4Output = callCVC4(cvc4Code);
		// System.out.println(cvc4Code.getAbsolutePath());

		CharStream input = null;
		try {
			input = CharStreams.fromStream(cvc4Output);
			// input = CharStreams.fromFileName("benchmarks/cvc4output1.cvc4");
		} catch (IOException e) {
			e.printStackTrace();
		}

		CVC4OutputLexer lexer = new CVC4OutputLexer(input);
		CVC4OutputParser parser = new CVC4OutputParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		ParseTree tree = null;
		try {
			tree = parser.response();
		} catch (ParseCancellationException ex) {
			System.err.println("Please correct the following CVC4 output parsing error: ");
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
		CVC4OVisitor visitor = new CVC4OVisitor();
		visitor.pasreAndRaiseCompletions(funNameToSynFun, tree);

		// System.out.println("xx: holes after raising: ");
		// HoleManager.printHoles();

		// String line = null;
		// try {
		// while ((line = cvc4Output.readLine()) != null) {
		// System.out.println(line);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	private InputStream callCVC4(File cvc4Code) {
		BufferedReader error = null;
		// BufferedReader output = null;
		Process pr = null;
		try {
			Runtime rt = Runtime.getRuntime();
			pr = rt.exec("/usr/local/bin/cvc4 --lang sygus2 " + cvc4Code.getCanonicalPath());

			error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			// output = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			// int exitVal = pr.waitFor();
			// System.out.println("Exited with error code " + exitVal);
			String line = null;
			try {
				while ((line = error.readLine()) != null) {
					System.err.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return pr.getInputStream();
	}

	private File writeCodeToFile() {
		File destFolder = new File(".");
		File cvc4Query = null;

		if (destFolder.exists()) {
			cvc4Query = new File(destFolder + File.separator + "cvc4_" + p.getName() + ".cvc4");
			// System.err.println(bmFile);
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(cvc4Query));
				writer.append(_prefix);
				writer.append(_definitions.toString());
				writer.append(_varDefs.toString());
				writer.append(_constraints.toString());
				writer.append(_suffix.toString());
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cvc4Query;
	}

	public void dumpCode() {
		System.out.println("CVC input: ----------------");
		System.out.println(_prefix.toString());
		System.out.println(_definitions.toString());
		System.out.println(_varDefs.toString());
		System.out.println(_constraints.toString());
		System.out.println(_suffix.toString());
		System.out.println("---------------------------");
	}

	@Override
	public boolean addConstraint(Constraint c) {
		String code = CVC4StringTranslator.instance().translate(c);

		if (!constraints.contains(c)) {
			constraints.add(c);
			appn(_constraints, code);
			return true;
		}

		return false;
	}

	@Override
	public void removeConstraint(Constraint c) {
		System.err.println("need to add extra logic to remove it from the textual represntation");
		Thread.dumpStack();
		constraints.remove(c);

	}

	@Override
	public String toString() {
		return "CVC4Synthesizer";
	}

	// private void app(StringBuilder sb, String s) {
	// sb.append(s);
	// }

	private void appn(StringBuilder sb, String s) {
		sb.append(s).append("\n");
	}

	private void apps(StringBuilder sb, String s) {
		sb.append(s).append(" ");
	}

	@Override
	public void rejectCurrentCompletion() {
		ExprOp predicate = null;
		if (synthFuns.size() == 0) {
			return;
		} else if (synthFuns.size() == 1) {
			CVC4SynthFun fun = synthFuns.iterator().next();
			predicate = fun.makeFunEqCompExpression();
//			predicate = getFunEqComp(fun);
		} else {
			Iterator<CVC4SynthFun> funs = synthFuns.iterator();

			ExprOp funEqComp1 = funs.next().makeFunEqCompExpression();
			ExprOp funEqComp2 = funs.next().makeFunEqCompExpression();
//			ExprOp funEqComp1 = getFunEqComp(funs.next());
//			ExprOp funEqComp2 = getFunEqComp(funs.next());

			predicate = new ExprOp(ExprOp.Op.AND, funEqComp1, funEqComp2);

			while (funs.hasNext()) {
				CVC4SynthFun fun = funs.next();
				ExprOp funEqComp = fun.makeFunEqCompExpression();
//				ExprOp funEqComp = getFunEqComp(fun);
				predicate = new ExprOp(ExprOp.Op.AND, predicate, funEqComp);
			}
		}
		predicate = new ExprOp(ExprOp.Op.NOT, predicate);
		Constraint constraint = new Constraint(predicate);
		// System.out.println(constraint); // TODO remove later
		addConstraint(constraint);
	}

//	private ExprOp getFunEqComp(CVC4SynthFun fun) {
//		Expression completion = fun.getCurrentCompletion();
//		ExprFunDecl funDecl = fun.getFunDecl();
//		ExprArgsList funArgs = fun.getArgsList();
//		ExprOp funApp = new ExprOp(ExprOp.Op.FUNAPP, funDecl, funArgs);
//		return new ExprOp(ExprOp.Op.EQ, funApp, completion);
//	}

	// temp!
	public void rejectHoleMinusOneEqCand() {
		for (CVC4SynthFun fun : synthFuns) {
			if (fun.getHole().getId() == -1) {
				Expression completion = fun.getCurrentCompletion();
				ExprFunDecl funDecl = fun.getFunDecl();
				ExprArgsList funArgs = fun.getArgsList();
				ExprOp funApp = new ExprOp(ExprOp.Op.FUNAPP, funDecl, funArgs);
				ExprOp predicate = new ExprOp(ExprOp.Op.NEQ, funApp, completion);
				Constraint constraint = new Constraint(predicate);
				System.out.println(constraint); // TODO remove later
				addConstraint(constraint);
				break;
			}
		}
	}

	public int getNumOfConstraints() {
		return constraints.size();
	}

	public CVC4SynthFun getSynthFunForHole(Hole hole) {
		return holeToSynFun.get(hole);
	}

	/**
	 * 
	 * 
	
	(synth -fun g_NIA ((x Int )) Int
	
	(( y_term Int ) ( y_pred Bool ))
	
	(
	( y_term Int 
	  (
	    ( Constant Int )
	    ( Variable Int)
	    (- y_term )
	    (+ y_term y_term )
	    (- y_term y_term )
	    (* y_term y_term )
	    (div y_term y_term )
	    (mod y_term y_term )
	    (abs y_term )
	    (ite y_pred y_term y_term )
	  )
	)
	
	( y_pred Bool 
	  (
	    (= y_term y_term )
	    (> y_term y_term )
	    (>= y_term y_term )
	    (< y_term y_term )
	    (<= y_term y_term )
	  )
	)
	
	)
	 * */
}

// Expression completion = fun.getCurrentCompletion();
// System.out.println("xx: completion = " + completion);
// ExprFunDecl funDecl = fun.getFunDecl();
// System.out.println("xx: funDecl = " + funDecl);
// ExprArgsList funArgs = fun.getArgsList();
// System.out.println("xx: funArgs = " + funArgs);
// ExprOp funApp = new ExprOp(ExprOp.Op.FUNAPP, funDecl, funArgs);
// System.out.println("xx: funApp = " + funApp);
// ExprOp funNEqComp = new ExprOp(ExprOp.Op.NEQ, funApp, completion);
// System.out.println("xx: funNEqComp = " + funNEqComp);
// Constraint constraint = new Constraint(funNEqComp);
// System.out.println("xx: constraint = " + constraint);
// addConstraint(constraint);