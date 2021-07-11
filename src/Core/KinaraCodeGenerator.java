package Core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import Holes.Hole;

import lang.core.*;
import lang.events.EventAction;
import lang.events.EventEpsilon;
import lang.events.EventPartitionCons;
import lang.events.EventValueCons;
import lang.expr.ExprConstant;
import lang.expr.ExprHole;
import lang.expr.ExprOp;
import lang.expr.ExprOp.Op;
import lang.handler.Handler;
import lang.handler.HandlerCrash;
import lang.handler.HandlerPartitionCons;
import lang.handler.HandlerPredicated;
import lang.handler.HandlerSymbolic;
import lang.handler.HandlerValueCons;
import lang.expr.ExprVar;
import lang.expr.Expression;
import lang.specs.AtLeastSpec;
import lang.specs.AtMostSpec;
import lang.specs.CompoundSpec;
import lang.specs.SafetySpecification;
import lang.specs.StateDesc;
import lang.specs.StateDescList;
import lang.stmts.Statement;
import lang.stmts.StmtAssign;
import lang.stmts.StmtBlock;
import lang.stmts.StmtBroadcast;
import lang.stmts.StmtGoto;
import lang.stmts.StmtIfThen;
import lang.stmts.StmtSend;
import lang.stmts.StmtSetIndexedAssign;
import lang.stmts.StmtSetUpdate;
import lang.type.Type;
import lang.type.TypeBool;
import lang.type.TypeChooseSet;
import lang.type.TypeID;
import lang.type.TypeInt;
import lang.type.TypeUnit;

public class KinaraCodeGenerator {

	static final String newline = "\n";
	static final String exp = "Exp";
	static final String auto = "\tauto ";
	static final String val = "val";
	static final String dotval = "DotVal";
	static final String tab = "\t";
	static final String N = "NumProcesss";
	static final String card = "cardinality";
	public static final String winvar = "wval";
	static final String all = "SetAll";
	static final int INTERNAL = 1;
	static final int SEND = 2;
	static final int RECEIVE = 3;
	private Protocol p;
	private boolean addSetsVector = true;
	private boolean addCrashStates = true;
	private HashMap<String, Integer> consName2ChIdMap = new HashMap<String, Integer>();
	HashMap<ExprVar, ExprConstant> cardVarToInitState = new HashMap<ExprVar, ExprConstant>();

	private Options options;

	public KinaraCodeGenerator(Options options) {
		this.options = options;
	}

	public StringBuilder prefix() {
		StringBuilder b = new StringBuilder();
		b.append("#include \"../../../src/uflts/LabelledTS.hpp\"\n");
		b.append("#include \"../../../src/uflts/LTSEFSM.hpp\"\n");
		b.append("#include \"../../../src/uflts/LTSChannelEFSM.hpp\"\n");
		b.append("#include \"../../../src/uflts/LTSAssign.hpp\"\n");
		b.append("#include \"../../../src/uflts/LTSTransitions.hpp\"\n");
		b.append("#include \"../../../src/mc/LTSChecker.hpp\"\n");
		b.append("#include \"../../../src/mc/OmegaAutomaton.hpp\"\n");
		b.append("#include \"../../../src/mc/Trace.hpp\"\n");
		b.append("#include \"../../../src/utils/LogManager.hpp\"\n");
		b.append("#include \"ChooseSynthOptions.hpp\"\n");
		b.append("\n");
		b.append("using namespace ESMC;\n");
		b.append("using namespace LTS;\n");
		b.append("using namespace Exprs;\n");
		b.append("using namespace MC;\n");
		b.append("using namespace Synth;\n");
		b.append("using namespace Analyses;\n");
		b.append("using namespace Logging;\n");
		b.append("\n");
		b.append("int main(int argc, char* argv[]){\n");
		b.append("\tif(argc == 1){\n");
		b.append("\t\tcout << \"Please specify # processes! \" <<endl;\n");
		b.append("\t\treturn -1;\n");
		b.append("\t}\n");
		b.append("\t// Types\n");
		b.append("\n");

		b.append("\tLogging::LogManager::Initialize();\n");
		b.append("\tLogging::LogManager::EnableLogOption(\"Solver.Traces\");\n");
		if (options.codeGen_generateDebugInfoInKinara) {
			b.append("\tLogging::LogManager::EnableLogOption(\"Checker.AQSDetailed\");\n");
			b.append("\tLogging::LogManager::EnableLogOption(\"Nour.ChooseGuardedCommands\");\n");
			b.append("\tLogging::LogManager::EnableLogOption(\"Nour.ErrorStates\");\n");
			b.append("\tLogging::LogManager::EnableLogOption(\"Nour.ErrorStates\");\n");
			b.append("\tLogging::LogManager::EnableLogOption(\"ESMC.All\");\n");
		}
		b.append("\tauto TheLTS = new LabelledTS();\n");
		b.append("\tu32 NumProcesss = atoi(argv[1]);\n");
		b.append("\tauto ProcessIDType = TheLTS->MakeSymmType(\"ProcessIDType\", NumProcesss);\n");
		// b.append("\tauto CounterType = TheLTS->MakeRangeType(0, NumProcesss);\n");
		// b.append("\tauto BoolType = TheLTS->MakeBoolType();\n");
		// b.append("\tauto CardinalityRange = TheLTS->MakeRangeType(1,
		// NumProcesss);\n");
		// b.append("\tauto ArrayType = TheLTS->MakeArrayType(ProcessIDType,
		// BoolType);\n");
		b.append("\tauto PID = TheLTS->MakeVar(\"ProcessID\", ProcessIDType);\n");
		b.append("\tauto PID1 = TheLTS->MakeVar(\"ProcessID1\", ProcessIDType);\n");
		b.append("\tvector<ExpT> Params = { PID };\n");
		b.append("\tauto TrueExp = TheLTS->MakeTrue();\n");
		b.append("\tauto FalseExp = TheLTS->MakeFalse();\n");
		b.append("\tauto FAType = TheLTS->MakeFieldAccessType();\n");
		b.append("\tauto PidNEQPidP1 = TheLTS->MakeOp(LTSOps::OpNOT,TheLTS->MakeOp(LTSOps::OpEQ, PID, PID1));\n");
		b.append("\tauto UndefID = TheLTS->MakeVal(\"clear\", ProcessIDType);\n");
		// b.append("\tExpT Guard = ExpT::NullPtr;\n");
		// b.append("\tvector<LTSAssignRef> Updates;\n");
		b.append("\n");
		b.append("\t// Messages / Channels\n");
		return b;
	}

	public StringBuilder messageTypes() {
		StringBuilder b = new StringBuilder();
		for (Entry<String, Action> e : p.getActionMap().entrySet()) {
			Action act = e.getValue();
			String actName = act.getName();
			// set act name to nameMsg if needed.
			// System.out.println(act.printDeclaration());
			// Fix this if-else nest: do env and not env, then br and not br
			if (act.isUnitDom()) {
				if (act.isEnv()) {
					if (act.isBr()) {
						b.append(auto);
						b.append(actName);
						b.append(" = TheLTS->MakeMsgType(\"");
						b.append(actName);
						b.append("\", { }, false);");
					} else {
						b.append(auto);
						b.append(actName);
						b.append(" = TheLTS->MakeMsgTypes(Params, TrueExp, \"");
						b.append(actName);
						b.append("\", { }, false);");
					}
				} else {
					if (act.isBr()) {
						b.append(auto);
						b.append(actName);
						b.append(" = TheLTS->MakeMsgTypes(Params, TrueExp, \"");
						b.append(actName);
						b.append("\", { }, false);");

					} else {

						b.append(tab);
						b.append(auto);
						b.append(actName);
						b.append(" = TheLTS->MakeMsgTypes({ PID, PID1 }, PidNEQPidP1, \"");
						b.append(actName);
						b.append("\", { },false);");
					}

				}
			} else if (act.isIntDom()) {
				if (act.isBr()) {
					//
					//
					//
					//
					//
					//
					//
					//
					//
					b.append(tab);
					b.append("vector<pair<string, TypeRef>> ");
					b.append(actName).append("Fields;");
					b.append(newline);
					b.append(tab);
					b.append(actName).append("Fields.push_back(make_pair(\"" + val + "\",");
					b.append(getKinaraType(act.getDomain()) + "));");
					b.append(newline);
					b.append(auto);
					b.append(actName);
					b.append(" = TheLTS->MakeMsgTypes(Params, TrueExp, \"");
					b.append(actName);
					b.append("\", ");
					b.append(actName);
					b.append("Fields , false);");
					//
					//
					//
					//
					//
					//
					//
					//
					//
				} else {
					b.append(tab);
					b.append("vector<pair<string, TypeRef>> ");
					b.append(actName).append("Fields;");
					b.append(newline);
					b.append(tab);
					b.append(actName).append("Fields.push_back(make_pair(\"" + val + "\",");
					b.append(getKinaraType(act.getDomain()) + "));");
					b.append(newline);
					if (act.isEnv()) {
						// no need for PidNEQPidP1 stuff
						b.append(auto);
						b.append(actName);
						b.append(" = TheLTS->MakeMsgTypes(Params, TrueExp, \"");
						b.append(actName);
						b.append("\", ");
						b.append(actName);
						b.append("Fields , false);");
						// auto ReturnMsg = TheLTS->MakeMsgTypes(Params, TrueExp, "ReturnMsg",
						// ReturnMsgFields , false);

					} else {
						b.append(auto);
						b.append(actName);
						b.append(" = TheLTS->MakeMsgTypes({ PID, PID1 }, PidNEQPidP1, \"");
						b.append(actName);
						b.append("\", ");
						b.append(actName);
						b.append("Fields , false);");
						// auto Reply = TheLTS->MakeMsgTypes( { PID, PID1 }, PidNEQPidP1, "Reply",
						// ReplyFields, false);
					}
				}
			} else {
				System.out.println("UNKNOWN ACTION DOMAIN");
			}
			// create exp:
			b.append(newline);
			b.append(auto);
			b.append(act.getName()).append(exp);
			b.append(" = TheLTS->MakeVar(\"");
			b.append(act.getName());
			b.append("\", ");
			b.append(act.getName());
			b.append(");");
			b.append(newline);
			if (act.isIntDom()) {
				// create exp for the value.
				b.append(auto);
				b.append(act.getName()).append(dotval);
				b.append(" = TheLTS->MakeOp(LTSOps::OpField,");
				b.append(act.getName()).append(exp);
				b.append(" , TheLTS->MakeVar(\"" + val + "\", FAType));");
				b.append(newline);

			}
			b.append(newline);
		}
		b.append("\tTheLTS->FreezeMsgs();");
		b.append(newline);
		return b;
	}

	public StringBuilder specs() {
		StringBuilder b = new StringBuilder();
		SafetySpecification specs = p.getSafetySpecs();

		if (specs == null) // no specs provided
			return b;
		// b.append("xx: InputSpecs: " + p.getSpecs() + "\n");

		// to keep things sequential, get the maximum number of bounded vars we need.
		HashSet<Integer> allThresholds = specs.getThresholds();
		HashSet<ExprVar> allVars = specs.getAllVarsInPreds();

		String preInfo = generateNeededVarsAndTypes(allThresholds, allVars, p);
		b.append(preInfo);

		String finalSpec = getKinaraCode_Spec(specs);

		b.append("\tauto Invariant = " + finalSpec + ";\n");

		if (options.codeGen_generateDebugInfoInKinara) {
			b.append("\tcout << \"Invariant before adding:\" << endl;\n");
			b.append("\tcout << Invariant->ToString() << endl;\n");
			b.append("\tcout << endl;\n");
		}

		b.append("\tTheLTS->AddInvariant(Invariant);\n");

		if (options.codeGen_generateDebugInfoInKinara) {
			b.append("\tcout << \"Invariant after adding:\" << endl;\n");
			b.append("\tcout << TheLTS->GetInvariant()->ToString() << endl;\n");
		}

		return b;
	}
	// StateDesc dummy = new StateDesc("Potato", new ExprOp(ExprOp.Op.AND, new
	// ExprConstant(TypeBool.Constant.FALSE, TypeBool.T()), new ExprVar("var",
	// TypeBool.T())));
	// System.out.println(getKinaraCode_Spec(dummy, 7));
	// System.exit(6);

	public String getKinaraCode_Spec(SafetySpecification spec) {

		if (spec instanceof AtMostSpec) {
			AtMostSpec ams = (AtMostSpec) spec;

			ArrayList<String> conjuncts = new ArrayList<String>();
			for (int i = 0; i < ams.getThreshold() + 1; i++) {
				StateDescList sdl = ams.getSdlist();
				String Pi_sdl = getKinaraCode_Spec(sdl, i);
				conjuncts.add(Pi_sdl);
			}

			String conjunction = makeConjunction(conjuncts);
			String consequent = "TheLTS->MakeOp(LTSOps::OpNOT," + conjunction + ")";
			String forall;
			if (ams.getThreshold() == 0) {
				forall = makeForall(ams.getThreshold() + 1, consequent);
			} else {
				String antecedent = makeAntecedent(ams.getThreshold() + 1);
				forall = makeForall(ams.getThreshold() + 1, antecedent, consequent);
			}
			return forall;
		} else if (spec instanceof AtLeastSpec) {
			return "AtLeastSpec not supported yet";
		} else if (spec instanceof CompoundSpec) {
			CompoundSpec cs = (CompoundSpec) spec;
			String arg1code = getKinaraCode_Spec(cs.getArg1());
			String arg2code = getKinaraCode_Spec(cs.getArg2());
			switch (cs.getOpCode()) {
			case AND:
				return "TheLTS->MakeOp(LTSOps::OpAND, " + arg1code + ", " + arg2code + ")";
			case OR:
				return "TheLTS->MakeOp(LTSOps::OpOR, " + arg1code + ", " + arg2code + ")";
			default:
				return "hmm?";
			}

		} else {
			return "unknown spec type in getKinaraCode_Spec";
		}
		// might need to call simplify
	}

	private String makeAntecedent(int bound) {
		// pairwise not equals
		ArrayList<String> conjuncts = new ArrayList<String>();
		for (int i = 0; i < bound; i++) {
			for (int j = i + 1; j < bound; j++) {
				conjuncts.add(
						"TheLTS->MakeOp(LTSOps::OpNOT, TheLTS->MakeOp(LTSOps::OpEQ, P" + i + "var, P" + j + "var))");
			}
		}
		return makeConjunction(conjuncts);
	}

	private String makeForall(int bound, String antecedent, String consequent) {
		return "TheLTS->MakeForAll(QVarTypes" + bound + ", TheLTS->MakeOp(LTSOps::OpIMPLIES, " + antecedent + ", "
				+ consequent + "))";
	}

	private String makeForall(int bound, String consequent) {
		return "TheLTS->MakeForAll(QVarTypes" + bound + ", " + consequent + ")";
	}

	private String makeConjunction(ArrayList<String> conjuncts) {
		if (conjuncts.size() == 0) {
			return "hm733?";
		} else if (conjuncts.size() == 1) {
			return conjuncts.get(0);
		} else {
			String current = "TheLTS->MakeOp(LTSOps::OpAND, " + conjuncts.get(0) + ", " + conjuncts.get(1) + ")";
			for (int j = 2; j < conjuncts.size(); j++) {
				current = "TheLTS->MakeOp(LTSOps::OpAND, " + current + ", " + conjuncts.get(j) + ")";
			}
			return current;
		}
	}

	private String getKinaraCode_Spec(StateDescList sdl, int i) {

		ArrayList<String> disjuncts = new ArrayList<String>();
		for (StateDesc sd : sdl.getList()) {
			disjuncts.add(getKinaraCode_Spec(sd, i));
		}

		String disjunction = makeDisjunction(disjuncts);

		return disjunction;
	}

	private String makeDisjunction(ArrayList<String> disjuncts) {
		if (disjuncts.size() == 0) {
			return "hm744?";
		} else if (disjuncts.size() == 1) {
			return disjuncts.get(0);
		} else {
			String current = "TheLTS->MakeOp(LTSOps::OpOR, " + disjuncts.get(0) + ", " + disjuncts.get(1) + ")";
			for (int j = 2; j < disjuncts.size(); j++) {
				current = "TheLTS->MakeOp(LTSOps::OpOR, " + current + ", " + disjuncts.get(j) + ")";
			}
			return current;
		}
	}

	private String generateNeededVarsAndTypes(HashSet<Integer> allThresholds, HashSet<ExprVar> allVars, Protocol p) {
		StringBuilder b = new StringBuilder();
		int maxThreshold = Collections.max(allThresholds);
		b.append("\tauto ProcessType = TheLTS->GetEFSMType(\"" + p.getName() + "\");\n\n");

		for (Integer th : allThresholds) {
			b.append("\t" + generateQvars(th + 1) + "\n");
		}
		b.append(newline);

		for (int i = 0; i < maxThreshold + 1; i++) {
			b.append("\tauto P" + i + "var = TheLTS->MakeBoundVar(" + i + ", ProcessIDType);\n");
		}
		b.append(newline);

		for (int i = 0; i < maxThreshold + 1; i++) {
			b.append("\tauto P" + i + " = TheLTS->MakeOp(LTSOps::OpIndex,TheLTS->MakeVar(\"" + p.getName()
					+ "\", ProcessType),P" + i + "var);\n");
		}
		b.append(newline);

		for (int i = 0; i < maxThreshold + 1; i++) {
			b.append("\tauto P" + i + "_state = TheLTS->MakeOp(LTSOps::OpField, P" + i
					+ ",TheLTS->MakeVar(\"state\", FAType));\n");
		}
		b.append(newline);

		for (ExprVar ev : allVars) {
			for (int i = 0; i < maxThreshold + 1; i++) {
				b.append("\tauto P" + i + "_" + ev.getName() + " = TheLTS->MakeOp(LTSOps::OpField, P" + i
						+ ",TheLTS->MakeVar(\"" + ev.getName() + "\", FAType));\n");
			}
		}
		b.append(newline);

		return b.toString();
	}

	private String getKinaraCode_Spec(StateDesc sd, int i) {
		StringBuilder b = new StringBuilder();
		String Pi_stateEqSomething = "TheLTS->MakeOp(LTSOps::OpEQ, P" + i + "_state, TheLTS->MakeVal(\""
				+ sd.getLocName() + "\", P" + i + "_state->GetType()))";

		if (sd.getPredicate().equals(new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T()))) {
			b.append(Pi_stateEqSomething);
			return b.toString();

		} else {
			String Pi_pred = getKinaraCode_Spec(sd.getPredicate(), i);
			b.append("TheLTS->MakeOp(LTSOps::OpAND, " + Pi_stateEqSomething + ", " + Pi_pred + ")");
			return b.toString();
		}

	}

	private String getKinaraCode_Spec(Expression expr, int i) {

		if (expr instanceof ExprConstant) {
			return getKinaraCode(expr); // constants are the same.
		} else if (expr instanceof ExprVar) {
			ExprVar exprVar = (ExprVar) expr;
			return "P" + i + "_" + exprVar.getName(); // should be already there.
		} else if (expr instanceof ExprOp) {
			ExprOp exprOp = (ExprOp) expr;

			switch (exprOp.getOp()) { // pretty much the same.
			case NEQ:
				ExprOp eq = new ExprOp(ExprOp.Op.EQ, exprOp.getArg1(), exprOp.getArg2());
				String arg = getKinaraCode_Spec(eq, i);
				return "TheLTS->MakeOp(LTSOps::OpNOT ," + arg + ")";

			case NOT: // because there is no arg2
				String argx = getKinaraCode_Spec(exprOp.getArg1(), i);
				return "TheLTS->MakeOp(LTSOps::OpNOT ," + argx + ")";
			default:
				String opCode = toKinaraOpString(exprOp.getOp());
				String arg1 = getKinaraCode_Spec(exprOp.getArg1(), i);
				String arg2 = getKinaraCode_Spec(exprOp.getArg2(), i);
				return "TheLTS->MakeOp(" + opCode + "," + arg1 + "," + arg2 + ")";
			}

		} else {
			System.out.println("what the heck is this expression(in specs)?" + expr);
			return "what the heck is this expression?";
		}

	}

	private String generateQvars(Integer bound) {
		StringBuilder b = new StringBuilder();

		b.append("vector<TypeRef> QVarTypes" + bound + " = {");
		// vector<TypeRef> QVarTypes3 = { ProcessIDType , ProcessIDType , ProcessIDType
		// };

		for (int i = 0; i < bound; i++) {
			b.append("ProcessIDType");
			if (i < bound - 1)
				b.append(", ");
		}
		b.append("};");
		return b.toString();
	}

	/**	
	 * This is the simple Environment process we agreed on: sends the receives and receives the sends from the same state
	*/
	public StringBuilder env() {

		StringBuilder b = new StringBuilder();

		b.append("\tauto Env = TheLTS->MakeGenEFSM(\"Env\",{}, TrueExp, LTSFairnessType::Strong);\n");

		// states
		b.append("\tEnv->AddState(\"InitialState\");\n");
		b.append("\tEnv->FreezeStates();\n");
		b.append("\tEnv->FreezeVars();\n");
		// messages
		// inputs w.r.t. processes are outputs to the env.
		for (String msgName : p.getEnvInputs()) {
			Action action = p.getActionMap().get(msgName);
			if (action.isBr()) {
				b.append("\tEnv->AddOutputMsg(" + msgName + ");\n");
			} else {
				b.append("\tEnv->AddOutputMsgs(Params,TrueExp, " + msgName + ",Params);\n");
			}
		}
		// outputs w.r.t. processes are inputs to the env.
		for (String msgName : p.getEnvOutputs()) {
			b.append("\tEnv->AddInputMsgs(Params,TrueExp, " + msgName + ",Params);\n");
		}

		// input transitions
		for (String msgName : p.getEnvOutputs() /* this is ok */) {
			b.append("\tEnv->AddInputTransitions(Params, TrueExp, \"InitialState\", \"InitialState\", TrueExp, { }, \""
					+ msgName + "\", " + msgName + ", Params);\n");
		}

		// outputs transitions: if it has a field, send all possible values.
		for (String msgName : p.getEnvInputs() /* this is ok */) {
			Action action = p.getActionMap().get(msgName);
			if (action.getDomain() instanceof TypeUnit) {
				if (action.isBr()) {
					b.append("\tEnv->AddOutputTransition(\"InitialState\", \"InitialState\", TrueExp, { } , \""
							+ msgName + "\", " + msgName + ", {});\n");
				} else {
					b.append(
							"\tEnv->AddOutputTransitions(Params, TrueExp, \"InitialState\", \"InitialState\", TrueExp, { } , \""
									+ msgName + "\", " + msgName + ", Params);\n");
				}

			} else if (action.getDomain() instanceof TypeInt) {

				TypeInt actDom = (TypeInt) action.getDomain();
				if (!actDom.getUpperBound().equalsIgnoreCase("N")) {

					int upperBound = Integer.valueOf(actDom.getUpperBound());
					int lowerBound = Integer.valueOf(actDom.getLowerBound());

					for (int i = lowerBound; i <= upperBound; i++) {
						ExprConstant RECVAL = new ExprConstant(TypeInt.Constant.RECVAL.toString(), action.getDomain());
						RECVAL.setAssosiatedAction(action);
						ExprConstant val = new ExprConstant(i + "", actDom);
						StmtAssign update = new StmtAssign(RECVAL, val);
						String updateStmt = getKinaraCode(update);
						if (action.isBr()) {
							// LATER broadcast output with value to env
						} else {
							b.append(
									"\tEnv->AddOutputTransitions(Params, TrueExp, \"InitialState\", \"InitialState\", TrueExp, {"
											+ updateStmt + "}, \"" + msgName + "\", " + msgName + ", Params);\n");
						}

					}

				} else {
					System.out.println("Env action domain cannot be N: " + action);
					System.exit(0);
				}

			} else {
				System.out.println("Env action domain not supported: " + action);
				System.exit(0);
			}
		}

		// ClientFSM->AddOutputTransitions(Params, TrueExp, "InitialState",
		// "InitialState", TrueExp, {new LTSAssignSimple(PutMsgDotVal, _ProposalOne)},
		// "PutMsg", PutMsg, Params);
		// ClientFSM->AddOutputTransitions(Params, TrueExp, "InitialState",
		// "InitialState", TrueExp, {new LTSAssignSimple(PutMsgDotVal, _ProposalTwo)},
		// "PutMsg", PutMsg, Params);

		b.append("\tEnv->Freeze();\n");
		b.append("\tTheLTS->FreezeAutomata();\n");

		return b;
	}

	public StringBuilder initState() {
		StringBuilder b = new StringBuilder();
		b.append("\tvector<LTSAssignRef> InitUpdates;\n");
		b.append("\tauto ProcessVar = TheLTS->MakeOp(LTSOps::OpIndex,TheLTS->MakeVar(\"" + p.getName()
				+ "\", TheLTS->GetEFSMType(\"" + p.getName() + "\")),PID);\n");

		// set initial state
		b.append(
				"\tauto ProcessDotState = TheLTS->MakeOp(LTSOps::OpField, ProcessVar,TheLTS->MakeVar(\"state\", FAType));\n");
		b.append("\tInitUpdates.push_back(new LTSAssignParam(Params, TrueExp, ProcessDotState, TheLTS->MakeVal(\""
				+ p.getInitLocation().getName() + "\", ProcessDotState->GetType())));\n");
		// p.getSymbolTable();
		// loop over all variables (user defined and special ones), if int or id, so it
		// using "clear", otherwise use the
		// set param thingy

		for (VarDecl vd : p.getAllVariables()) {

			// if(cardVarToInitState.containsKey(vd.asExprVar())) {
			// System.out.println(vd);
			// }

			if (vd.getType() instanceof TypeInt) {
				String iv = vd.getInitValue().toString();
				ExprConstant initValExp = new ExprConstant(iv, vd.getType());
				String initVal = getKinaraCode(initValExp);
				b.append(
						"\tInitUpdates.push_back(new LTSAssignParam(Params, TrueExp, TheLTS->MakeOp(LTSOps::OpField, ProcessVar,TheLTS->MakeVar(\""
								+ vd.getName() + "\", FAType)), " + initVal + "));\n");

			} else if (vd.getType() instanceof TypeID) {
				ExprConstant initValExp = new ExprConstant(TypeID.Constant.UNDEF.toString(), vd.getType());
				String initVal = getKinaraCode(initValExp);
				b.append(
						"\tInitUpdates.push_back(new LTSAssignParam(Params, TrueExp, TheLTS->MakeOp(LTSOps::OpField, ProcessVar,TheLTS->MakeVar(\""
								+ vd.getName() + "\", FAType)), " + initVal + "));\n");

			} else if (vd.getType() instanceof TypeChooseSet) {

				if (vd.getName().equals(TypeChooseSet.Constant.ALL.toString())) {
					ExprConstant initValExp = new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
					String initVal = getKinaraCode(initValExp);
					b.append(
							"\tInitUpdates.push_back(new LTSAssignParam( { PID, PID1 }, TrueExp, TheLTS->MakeOp(LTSOps::OpIndex, TheLTS->MakeOp(LTSOps::OpField, ProcessVar,TheLTS->MakeVar(\""
									+ vd.getName() + "\", FAType)), PID1), " + initVal + "));\n");

				} else if (vd.getName().equals(TypeChooseSet.Constant.EMTPY.toString())) { // shouldn't happen but oh
																							// well
					ExprConstant initValExp = new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
					String initVal = getKinaraCode(initValExp);
					b.append(
							"\tInitUpdates.push_back(new LTSAssignParam( { PID, PID1 }, TrueExp, TheLTS->MakeOp(LTSOps::OpIndex, TheLTS->MakeOp(LTSOps::OpField, ProcessVar,TheLTS->MakeVar(\""
									+ vd.getName() + "\", FAType)), PID1), " + initVal + "));\n");
				} else {

					ExprConstant initValExp = new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
					String initVal = getKinaraCode(initValExp);
					b.append(
							"\tInitUpdates.push_back(new LTSAssignParam( { PID, PID1 }, TrueExp, TheLTS->MakeOp(LTSOps::OpIndex, TheLTS->MakeOp(LTSOps::OpField, ProcessVar,TheLTS->MakeVar(\""
									+ vd.getName() + "\", FAType)), PID1), " + initVal + "));\n");
				}
			} else {
				System.out.println("not sure what to do about this variable's inital value: " + vd);
				System.exit(0);
			}
		}

		// env stuff is ok
		b.append("\t//Env init updates\n");
		b.append("\tauto EnvType = TheLTS->GetEFSMType(\"Env\");\n");
		b.append("\tauto EnvVar = TheLTS->MakeVar(\"Env\", EnvType);\n");
		b.append("\tauto EnvDotState = TheLTS->MakeOp(LTSOps::OpField, EnvVar, TheLTS->MakeVar(\"state\", FAType));\n");
		b.append(
				"\tInitUpdates.push_back(new LTSAssignSimple(EnvDotState, TheLTS->MakeVal(\"InitialState\", EnvDotState->GetType())));\n");

		return b;

	}

	public StringBuilder process() {

		StringBuilder b = new StringBuilder();
		b.append(newline);
		b.append(tab);
		b.append("EFSMBase* ProcessEFSM = TheLTS->MakeGenEFSM(\"" + p.getName()
				+ "\", Params, TrueExp, LTSFairnessType::Strong);");
		b.append(newline);
		b.append(newline);

		// states
		generateStates(b);
		dprint(b, "States generated");

		// messages
		generateMessages(b);
		dprint(b, "Messages generated");

		// vars
		generateVariables(b);
		dprint(b, "Vars generated");

		// transitions
		generateTransitions(b);
		dprint(b, "Transitions generated");

		b.append(newline);
		b.append(tab);
		b.append("ProcessEFSM->Freeze();");
		b.append(newline);
		b.append(newline);

		return b;
	}

	private void dprint(StringBuilder b, String msg) {
		if (options.codeGen_generateDebugInfoInKinara) {
			b.append("\tcout << \"" + msg + "\" << endl;\n");
		}
	}

	private void generateMessages(StringBuilder b) {
		// first, take care of the Env messages:
		for (String msgName : p.getEnvInputs()) {
			Action action = p.getActionMap().get(msgName);
			if (action.isBr()) { // no params needed
				b.append(tab);
				b.append("ProcessEFSM->AddInputMsg(" + msgName + ");");
				b.append(newline);
			} else {
				b.append(tab);
				b.append("ProcessEFSM->AddInputMsg(" + msgName + ",Params);");
				b.append(newline);
			}
		}
		for (String msgName : p.getEnvOutputs()) {
			b.append(tab);
			b.append("ProcessEFSM->AddOutputMsg(" + msgName + ",Params);");
			b.append(newline);
		}

		// now, handle the normal messages:
		for (Entry<String, Action> e : p.getActionMap().entrySet()) {
			Action act = e.getValue();
			String actName = act.getName();

			if (!act.isEnv()) {
				if (act.isBr()) {
					// for broadcasts: the output is 1 and the inputs are n-1
					b.append("\tProcessEFSM->AddOutputMsg(" + actName + ", Params);\n");
					b.append("\tProcessEFSM->AddInputMsgs({ PID1 }, PidNEQPidP1, " + actName + ", { PID1 });\n");
				} else {
					// for pairwise: the output and inputs are n - 1
					b.append("\tProcessEFSM->AddOutputMsgs( { PID1 }, PidNEQPidP1, " + actName + ", { PID, PID1 });\n");
					b.append("\tProcessEFSM->AddInputMsgs( { PID1 }, PidNEQPidP1, " + actName + ", { PID1, PID }); \n");
				}
			}
		}
		b.append(newline);
	}

	private void generateTransitions(StringBuilder b) {
		for (Location loc : p.getLocations()) {
			for (Handler handler : loc.getHandlers()) {

				if (options.codeGen_generateDebugInfoInKinara) {
					dprint(b, "Generating code for handler: ");
					dprint(b, handler.toString().replaceAll("\n", " "));
				}

				if (handler instanceof HandlerPredicated) {
					HandlerPredicated h = (HandlerPredicated) handler;
					generatePredicatedTransition(loc, h, b);
				} else if (handler instanceof HandlerPartitionCons) {
					HandlerPartitionCons h = (HandlerPartitionCons) handler;
					generatePartitionConsTransitions(loc, h, b);
				} else if (handler instanceof HandlerValueCons) {
					HandlerValueCons h = (HandlerValueCons) handler;
					generateValueConsTransition(loc, h, b);
				} else if (handler instanceof HandlerCrash) {
					HandlerCrash h = (HandlerCrash) handler;
					generateCrashTransition(loc, h, b);
				} else if (handler instanceof HandlerSymbolic) {
					// do nothing
				} else {
					System.out.println("This handler should have been eleminated by preprocessing: " + handler);
					System.exit(1);
				}
			}
		}
	}

	private void generateCrashTransition(Location currentLoc, HandlerCrash handler, StringBuilder b) {

		// how it looks on kinara side
		// vector<string> setsToUpdate = { "All" };
		// ProcessEFSM->AddCrashTransition("Candidate", "CRASH", TheLTS->MakeTrue() ,
		// setsToUpdate);

		String targetLoc = handler.getTargetLoc().getName();
		Expression guardExpr = handler.getPredicate();

		// collect the sets and add the vector definition if not added before
		addSetVectorIfNotThere(b);
		addCrashLocsIfNotThere(b);

		String guard = getKinaraCode(guardExpr);
		b.append(tab);
		b.append("ProcessEFSM->AddCrashTransition(\"" + currentLoc.getName() + "\", \"" + targetLoc + "\", " + guard
				+ ", " + "setsToUpdate" + ", " + "crashLocs" + ");");
		b.append(newline);
	}

	private void addSetVectorIfNotThere(StringBuilder b) {

		if (addSetsVector) {
			addSetsVector = false;

			ArrayList<String> setVars = new ArrayList<String>();

			for (VarDecl var : p.getAllVariables()) {
				if (var.getType().equals(TypeChooseSet.T())) {
					setVars.add(var.getName());
				}
			}
			b.append(tab);
			b.append("vector<string> setsToUpdate = {");
			boolean first = true;
			for (String setName : setVars) {
				if (first) {
					b.append("\"" + setName + "\"");
					first = false;
				} else {
					b.append(" ,\"" + setName + "\"");
				}
			}
			b.append("};");
			b.append(newline);
		}

	}

	private void addCrashLocsIfNotThere(StringBuilder b) {

		if (addCrashStates) {
			addCrashStates = false;

			ArrayList<String> crashLocs = p.getTargetCrashLocNames();
			b.append(tab);
			b.append("vector<string> crashLocs = {");
			boolean first = true;
			for (String crashLoc : crashLocs) {
				if (first) {
					b.append("\"" + crashLoc + "\"");
					first = false;
				} else {
					b.append(" ,\"" + crashLoc + "\"");
				}
			}
			b.append("};");
			b.append(newline);
		}
	}

	private void generatePredicatedTransition(Location currentLoc, HandlerPredicated handler, StringBuilder b) {
		// extract info
		String targetLoc = "Unkown?!";
		Statement communicationStmt = null;
		int transitionType = -1;
		Action action = null;
		Expression guard = handler.getPredicate();
		ArrayList<Statement> updates = new ArrayList<Statement>();
		//
		for (Statement stmt : handler.getBody().getStmts()) {
			if (stmt instanceof StmtGoto) {
				targetLoc = ((StmtGoto) stmt).getTargetLocName();
			} else if (stmt instanceof StmtSend) {
				communicationStmt = stmt;
				action = ((StmtSend) stmt).getAct();
			} else if (stmt instanceof StmtBroadcast) {
				communicationStmt = stmt;
				action = ((StmtBroadcast) stmt).getAct();
			} else {
				updates.add(stmt);
			}

		}

		if (handler.getEvent() instanceof EventEpsilon && handler.getBody().getNumOfSnds() == 0) {
			transitionType = INTERNAL;
		} else if (handler.getEvent() instanceof EventEpsilon && handler.getBody().getNumOfSnds() == 1) {
			transitionType = SEND;
		} else if (handler.getEvent() instanceof EventAction) {
			transitionType = RECEIVE;
			action = ((EventAction) (handler.getEvent())).getAction();
			if (!action.isEnv()) { // the ID has no process ID :)
				// for receives where we actually use the "sid", add an assign statement to save
				// that to the body!
				String varName = action.getName() + "_sID";
				if (p.getSpecialVars().containsKey(varName)) {
					ExprVar var = p.getSpecialVars().get(varName).asExprVar();
					ExprConstant constOther = new ExprConstant(TypeID.Constant.OTHER.toString(), TypeID.T());
					constOther.setAssosiatedAction(action);
					StmtAssign stmtAssign = new StmtAssign(var, constOther);
					updates.add(0, stmtAssign);
				}
			}
		} else {
			System.out.println("Something went wrong... (457)");
			System.out.println(handler);
			System.exit(0);
		}

		switch (transitionType) {
		case INTERNAL:
			generateInternalTransition(currentLoc.getName(), guard, updates, targetLoc, b);
			break;
		case SEND:
			// System.out.println("================================================================");
			// System.out.println(handler);
			// System.out.println("----------------------------------------------------------------");
			// System.out.println("predicated transition details:");
			// System.out.println("guard: " + guard);
			// System.out.println("action: " + action);
			// System.out.println("transition type: " + transitionType + "(1:i ,2:s ,3:r)");
			// System.out.println("updates: " + Arrays.toString(updates.toArray()));
			// System.out.println("comm stmt: " + communicationStmt);
			// System.out.println("target loc: " + targetLoc);
			// System.out.println("================================================================");
			generateSendTransition(currentLoc.getName(), guard, updates, action, communicationStmt, targetLoc, b);
			break;
		case RECEIVE:
			// System.out.println("================================================================");
			// System.out.println(handler);
			// System.out.println("----------------------------------------------------------------");
			// System.out.println("predicated transition details:");
			// System.out.println("guard: " + guard);
			// System.out.println("action: " + action);
			// System.out.println("transition type: " + transitionType + "(1:i ,2:s ,3:r)");
			// System.out.println("updates: " + Arrays.toString(updates.toArray()));
			// System.out.println("comm stmt: " + communicationStmt);
			// System.out.println("target loc: " + targetLoc);
			// System.out.println("================================================================");

			generateReceiveTransition(currentLoc.getName(), guard, updates, action, targetLoc, b);
			break;

		default:
			break;
		}
	}

	private void generateReceiveTransition(String currentLoc, Expression guardExp, ArrayList<Statement> updatesList,
			Action action, String targetLoc, StringBuilder b) {
		String updates = generateUpdates(updatesList);
		String guard = getKinaraCode(guardExp);
		String actionName = action.getName();
		b.append(tab);
		if (action.isEnv()) {
			if (action.isBr()) { // no params
				// if (p.getEnvInputs().contains(actionName)) {
				b.append("ProcessEFSM->AddInputTransition(\"" + currentLoc + "\", \"" + targetLoc + "\", " + guard
						+ ", " + updates + ", \"" + actionName + "\", " + actionName + ", {});");
				// }
			} else {
				// if (p.getEnvInputs().contains(actionName)) {
				b.append("ProcessEFSM->AddInputTransition(\"" + currentLoc + "\", \"" + targetLoc + "\", " + guard
						+ ", " + updates + ", \"" + actionName + "\", " + actionName + ", Params);");
				// }
			}
		} else {
			if (action.isBr()) {
				b.append("ProcessEFSM->AddInputTransitions({PID1},PidNEQPidP1,\"" + currentLoc + "\", \"" + targetLoc
						+ "\", " + guard + ", " + updates + ", \"" + actionName + "\", " + actionName + ", {PID1});");
			} else {
				b.append("ProcessEFSM->AddInputTransitions({PID1},PidNEQPidP1,\"" + currentLoc + "\", \"" + targetLoc
						+ "\", " + guard + ", " + updates + ", \"" + actionName + "\", " + actionName
						+ ", {PID1,PID});");
			}
		}
		b.append(newline);
	}

	private void generateSendTransition(String currentLoc, Expression guardExp, ArrayList<Statement> updatesList,
			Action action, Statement communicationStmt, String targetLoc, StringBuilder b) {
		String actionName = action.getName();
		b.append(tab);
		if (action.isEnv()) {
			if (p.getEnvOutputs().contains(actionName)) {
				if (action.isBr()) {
					String guard = getKinaraCode(guardExp);
					String updates = generateUpdates(updatesList);
					b.append("ProcessEFSM->AddOutputTransition(\"" + currentLoc + "\", \"" + targetLoc + "\", " + guard
							+ ", " + updates + ", \"" + actionName + "\", " + actionName + ", Params);");
				} else {
					// Add value update, if the value exists and the type of the action isn't unit
					StmtSend sstmt = (StmtSend) communicationStmt;
					Expression sentVal = sstmt.getAct().getValue();
					if (sentVal != null && !(action.getDomain() instanceof TypeUnit)) { // no update needed if unit or
																						// no value sent
						ExprConstant RECVAL = new ExprConstant(TypeInt.Constant.RECVAL.toString(), action.getDomain());
						RECVAL.setAssosiatedAction(action);
						// System.out.println("xx: action: "+action);
						// System.out.println("xx: RECVAL: "+RECVAL);
						// System.out.println("xx: sentVal: "+sentVal);
						StmtAssign actNameDotVal_equals_sentVal = new StmtAssign(RECVAL, sentVal);
						updatesList.add(actNameDotVal_equals_sentVal);
					}
					// Done
					String guard = getKinaraCode(guardExp);
					String updates = generateUpdates(updatesList);
					b.append("ProcessEFSM->AddOutputTransition(\"" + currentLoc + "\", \"" + targetLoc + "\", " + guard
							+ ", " + updates + ", \"" + actionName + "\", " + actionName + ", Params);");

				}
			}
		} else {
			if (action.isBr()) {
				String guard = getKinaraCode(guardExp);
				String updates = generateUpdates(updatesList);
				b.append("ProcessEFSM->AddOutputTransition(\"" + currentLoc + "\", \"" + targetLoc + "\", " + guard
						+ ", " + updates + ", \"" + actionName + "\", " + actionName + ", Params);");
			} else {
				// System.out.println("communicationStmtcommunicationStmtcommunicationStmtcommunicationStmt");
				// System.out.println("communicationStmtcommunicationStmtcommunicationStmtcommunicationStmt");
				// System.out.println("communicationStmtcommunicationStmtcommunicationStmtcommunicationStmt");
				// System.out.println(communicationStmt);

				// in addition to the normal guard, we need to add a guard to send to the right
				// process
				StmtSend sstmt = (StmtSend) communicationStmt;
				Expression targetID = sstmt.getTo();
				if (targetID != null) { // sending to a specific ID
					ExprConstant otherConst = new ExprConstant(TypeID.Constant.OTHER.toString(), TypeID.T());
					ExprOp storedIDEqualsPID1 = new ExprOp(ExprOp.Op.EQ, targetID, otherConst);
					guardExp = new ExprOp(ExprOp.Op.AND, guardExp, storedIDEqualsPID1);

					// System.out.println("sstmt " + sstmt);
					// System.out.println("targetID " + targetID);
					// System.out.println("otherConst " + otherConst);
					// System.out.println("storedIDEqualsPID1 " + storedIDEqualsPID1);

					// add the actnameDotVal = sentValue and actname.SID = UNDEF updates!
					// System.out.println("action.getName() " + action.getName());
					Expression sentVal = sstmt.getAct().getValue();
					if (sentVal != null && !(action.getDomain() instanceof TypeUnit)) { // no update needed if unit or
																						// no
																						// value sent

						ExprConstant RECVAL = new ExprConstant(TypeInt.Constant.RECVAL.toString(), action.getDomain());
						RECVAL.setAssosiatedAction(action);
						// System.out.println("RECVAL " + RECVAL);
						// ExprVar actNameSId = p.getSpecialVars().get(action.getName() +
						// "_sID");
						// System.out.println("actName " + action.getName());
						// System.out.println("actNameSId " + actNameSId);
						// ExprConstant undef = new ExprConstant(TypeID.Constant.UNDEF.toString(),
						// TypeID.T());
						// System.out.println("undef " + undef);
						// System.out.println("sentVal " + sentVal);
						StmtAssign actNameDotVal_equals_sentVal = new StmtAssign(RECVAL, sentVal);
						// StmtAssign actNameSId_equals_undef = new StmtAssign(actNameSId, undef);
						updatesList.add(actNameDotVal_equals_sentVal);
						// updatesList.add(actNameSId_equals_undef);
					}
					// Done
					String guard = getKinaraCode(guardExp);
					String updates = generateUpdates(updatesList);
					b.append("ProcessEFSM->AddOutputTransitions({PID1},PidNEQPidP1,\"" + currentLoc + "\", \""
							+ targetLoc + "\", " + guard + ", " + updates + ", \"" + actionName + "\", " + actionName
							+ ", {PID, PID1});");
				} else { // send to anyone ready to receive
					guardExp = TypeBool.T().makeTrue();
					String guard = getKinaraCode(guardExp);
					String updates = generateUpdates(updatesList);
					b.append("ProcessEFSM->AddOutputTransitions({PID1},PidNEQPidP1,\"" + currentLoc + "\", \""
							+ targetLoc + "\", " + guard + ", " + updates + ", \"" + actionName + "\", " + actionName
							+ ", {PID, PID1});");
				}

			}
		}
		b.append(newline);

	}

	private void generateInternalTransition(String currentLoc, Expression guardExp, ArrayList<Statement> updatesList,
			String targetLoc, StringBuilder b) {
		/**ProcessEFSM->AddInternalTransition("CollectRepliesState", "ChooseState", Guard, Updates);*/

		String updates = generateUpdates(updatesList);
		String guard = getKinaraCode(guardExp);
		b.append(tab);
		b.append("ProcessEFSM->AddInternalTransition(\"" + currentLoc + "\", \"" + targetLoc + "\", " + guard + ", "
				+ updates + ");");
		b.append(newline);
	}

	private String generateUpdates(ArrayList<Statement> updatesList) {
		StringBuilder b = new StringBuilder();
		b.append("{ ");
		for (int i = 0; i < updatesList.size(); i++) {
			Statement stmt = updatesList.get(i);
			b.append(getKinaraCode(stmt));
			if (i < updatesList.size() - 1) {
				b.append(" , ");
			}
		}
		b.append(" }");
		return b.toString();
	}

	public String getKinaraCode(Statement stmt) {
		if (stmt instanceof StmtAssign) {
			StmtAssign s = (StmtAssign) stmt;
			String lhs = getKinaraCode(s.getLHS());
			String rhs;
			//if ((s.getLHS().toString().equals("stored")) && p.getName().startsWith("DistributedStore")) { // TODO quick hack
			//	rhs = getKinaraCode(getTransformedRHS(s));
			//} else {
				rhs = getKinaraCode(s.getRHS());
			//}

			if (s.getLHS().getType() instanceof TypeChooseSet) {
				if (s.getRHS() instanceof ExprVar) {
					// return "new LTSAssignParam({PID1}, TrueExp , TheLTS->MakeOp(LTSOps::OpIndex,
					// " + lhs + ", PID1), TheLTS->MakeTrue())";
					ExprVar svar = (ExprVar) s.getRHS();
					if (svar.getName().equals(TypeChooseSet.Constant.ALL.toString())) {
						return "new LTSAssignParam({PID1}, TrueExp , TheLTS->MakeOp(LTSOps::OpIndex, " + lhs
								+ ", PID1), TheLTS->MakeTrue())";
					} else if (p.getSpecialVars().containsKey(svar.getName())) {
						return "new LTSAssignParam({PID1}, TrueExp , TheLTS->MakeOp(LTSOps::OpIndex, " + lhs
								+ ", PID1), TheLTS->MakeOp(LTSOps::OpIndex, " + rhs + ", PID1))";
					}
					// forall i set[i] = wins[i]

				} else if (s.getRHS() instanceof ExprConstant
						&& ((ExprConstant) s.getRHS()).getVal().equals(TypeChooseSet.Constant.EMTPY.toString())) {
					return "new LTSAssignParam({PID1}, TrueExp , TheLTS->MakeOp(LTSOps::OpIndex, " + lhs
							+ ", PID1), TheLTS->MakeFalse())";
				}
			} else {
				return "new LTSAssignSimple(" + lhs + ", " + rhs + ")";
			}
		} else if (stmt instanceof StmtSetUpdate) {
			/**
			 * Set.add(Reply.sID)
			 * becomes set.ReplysID = true
			 * */

			StmtSetUpdate s = (StmtSetUpdate) stmt;
			String set = getKinaraCode(s.getSet());
			String inp = getKinaraCode(s.getInp());
			String indexExp = "TheLTS->MakeOp(LTSOps::OpIndex, " + set + ", " + inp + ")";
			if (s.getOp().equals(StmtSetUpdate.OpType.ADD)) {
				return "new LTSAssignSimple(" + indexExp + ", TheLTS->MakeTrue())";
			} else {
				return "new LTSAssignSimple(" + indexExp + ", TheLTS->MakeFalse())";
			}
		} else if (stmt instanceof StmtSetIndexedAssign) {
			StmtSetIndexedAssign stmtSetAssign = (StmtSetIndexedAssign) stmt;
			// System.out.println("stmt: " + stmt);
			String indexCode = getKinaraCode(stmtSetAssign.getIndex());
			String setCode = getKinaraCode(stmtSetAssign.getSet());
			// String inputCode = getKinaraCode(stmtSetAssign.getInp());

			/**
			 * edit: since the set expects a bool, and the type we have is int, we need a small conversion.
			 * to do a minimal edit, we just need to do an ite statement as follows: 
			 * inputcode becomes: 
			 * (inputcode == 1)? true:false
			*/
			ExprConstant OneConst = new ExprConstant("1", new TypeInt("1", "1"));
			ExprConstant TrueCosnt = new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
			ExprConstant FalseCosnt = new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
			ExprOp inputEqOne = new ExprOp(ExprOp.Op.EQ, stmtSetAssign.getInp(), OneConst);

			String inputEqOneCode = getKinaraCode(inputEqOne);
			String trueCode = getKinaraCode(TrueCosnt);
			String falseCode = getKinaraCode(FalseCosnt);
			String iteCode = "TheLTS->MakeOp(LTSOps::OpITE," + inputEqOneCode + ", " + trueCode + ", " + falseCode
					+ ")";
			// System.out.println("new LTSAssignSimple(TheLTS->MakeOp(LTSOps::OpIndex, " +
			// setCode + ", " + indexCode + "), " + iteCode + ")");
			// System.exit(-1);
			return "new LTSAssignSimple(TheLTS->MakeOp(LTSOps::OpIndex, " + setCode + ", " + indexCode + "), " + iteCode
					+ ")";
		} else if (stmt instanceof StmtIfThen) {
			System.out.println("Warning, StmtIfThen inside an update, skipping: " + stmt);
		} else if (stmt instanceof StmtBlock) {
			System.out.println("Warning, StmtBlock inside an update, skipping: " + stmt);
		} else if (stmt instanceof StmtSend) {
			System.out.println("Warning, StmtSend inside an update, skipping: " + stmt);
		} else if (stmt instanceof StmtBroadcast) {
			System.out.println("Warning, StmtBroadcast inside an update, skipping: " + stmt);
		}
		return "huh?";
	}

	private Expression getTransformedRHS(StmtAssign assignStmt) {

		Expression oldRhs = assignStmt.getRHS();
		Expression lhs = assignStmt.getLHS();
		TypeInt lhsType = (TypeInt) lhs.getType();

		ExprConstant lower = new ExprConstant(lhsType.getLowerBound(), lhsType);

		String domainSizeVal = String
				.valueOf(1 + Integer.parseInt(lhsType.getUpperBound()) - Integer.parseInt(lhsType.getLowerBound()));

		ExprConstant domainSize = new ExprConstant(domainSizeVal, new TypeInt(domainSizeVal, domainSizeVal));

		// lower + ((((rhs - lower) % domainSize) + domainSize) %domainSize)

		/**
		 * +
		 *   lower
		 *   %
		 *     +
		 *       %
		 *         -
		 *           rhs
		 *           lower
		 *         domainSize
		 *       domainSize
		 *     domainSize
		 * */

		ExprOp rhsMinusLower = new ExprOp(Op.SUB, oldRhs, lower);
		ExprOp rhsMinusLowerModDomSize = new ExprOp(Op.MOD, rhsMinusLower, domainSize);
		ExprOp rhsMinusLowerModDomSizePlusDomsSize = new ExprOp(Op.ADD, rhsMinusLowerModDomSize, domainSize);
		ExprOp rhsMinusLowerModDomSizePlusDomsSizeModDomSize = new ExprOp(Op.MOD, rhsMinusLowerModDomSizePlusDomsSize,
				domainSize);
		ExprOp newRhs = new ExprOp(Op.ADD, lower, rhsMinusLowerModDomSizePlusDomsSizeModDomSize);

		return newRhs;
	}

	private void generateValueConsTransition(Location currentLoc, HandlerValueCons handler, StringBuilder b) {
		/** Choose state transitions
			ProcessEFSM->AddConsTransition(ChIns1, "AgreeState", "SettleState", SetVar1, CardVar1, LocalCopy, winvar1);
		*/
		EventValueCons vcEvent = handler.getEvent();
		String consName = vcEvent.getChID();
		String ChId = consName2ChId(consName);
		String setVar = getKinaraCode(vcEvent.getParticipants());
		String hasPropVar;
		String propVar;

		if (vcEvent.hasPropVar()) {
			hasPropVar = "true";
			propVar = getKinaraCode(vcEvent.getProposalVar());
		} else {
			hasPropVar = "false";
			propVar = getKinaraCode(extractNonEmptyPropVar(consName));
		}

		String cardVar = getKinaraCode(p.getSpecialVars().get(consName + "_" + card).asExprVar());
		String modelCrashes = "";
		String setsToUpdate = "";

		// check if we are crashing this action.
		if (p.getActionCrashs().contains(consName)) {
			modelCrashes = "true";
			addSetVectorIfNotThere(b); // creates the vector named setsToUpdate
			setsToUpdate = "setsToUpdate";
		} else {
			modelCrashes = "false";
			setsToUpdate = "{ }";
		}

		// for (Entry<String, ExprVar> e : p.getSpecialVars().entrySet()) {
		// System.out.println(e);
		// }
		// System.exit(0);
		String winVar = getKinaraCode(p.getSpecialVars().get(consName + "_" + winvar).asExprVar());
		String targetLoc = ((StmtGoto) (handler.getBody().getStmts().get(0))).getTargetLocName(); // banking on
																									// preProcessing,
																									// cast may fail
		b.append("\tProcessEFSM->AddConsTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + targetLoc
				+ "\", " + setVar + ", " + cardVar + ", " + propVar + ", " + hasPropVar + ", " + winVar + ", "
				+ modelCrashes + ", " + setsToUpdate + ");\n");
	}

	private ExprVar extractNonEmptyPropVar(String consInst) {
		for (HandlerValueCons hvc : p.getValconsInstMap().get(consInst)) {

			if (hvc.getEvent().hasPropVar()) {
				return hvc.getEvent().getProposalVar();
			}
		}
		// should never happen
		return null;
	}

	private void generatePartitionConsTransitions(Location currentLoc, HandlerPartitionCons handler, StringBuilder b) {

		EventPartitionCons pcEvent = handler.getEvent();
		String consName = pcEvent.getChID();
		String ChId = consName2ChId(consName);
		String setVar = getKinaraCode(pcEvent.getParticipants());

		String cardVar = getKinaraCode(p.getSpecialVars().get(consName + "_" + card).asExprVar());
		String winLoc = ((StmtGoto) (handler.getWinBlock().getStmts().get(0))).getTargetLocName(); // banking on
																									// preProcessing,
																									// cast may fail
		String loseLoc = ((StmtGoto) (handler.getLoseBlock().getStmts().get(0))).getTargetLocName(); // banking on
																										// preProcessing,
																										// cast may fail

		// System.out.println("xx:" +
		// Arrays.toString(p.getSpecialVars().entrySet().toArray()));

		String winsetName = consName + "_winS";
		String losesetName = consName + "_loseS";

		boolean winSetUsed = p.getSpecialVars().containsKey(winsetName);
		boolean loseSetUsed = p.getSpecialVars().containsKey(losesetName);

		if (winSetUsed && !loseSetUsed) {
			ExprVar winSvarExp = p.getSpecialVars().get(winsetName).asExprVar();
			String winSvar = getKinaraCode(winSvarExp);
			b.append("\tProcessEFSM->AddWinTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + winLoc
					+ "\", " + setVar + ", " + cardVar + ", " + winSvar + ");\n");
			b.append("\tProcessEFSM->AddLoseTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + loseLoc
					+ "\", " + setVar + ", " + cardVar + ", " + winSvar + ");\n");

		} else if (!winSetUsed && loseSetUsed) {
			System.out.println("Can't use lose set without winset, maybe change you edges? :)");
		} else if (winSetUsed && loseSetUsed) {
			ExprVar winSvarExp = p.getSpecialVars().get(winsetName).asExprVar();
			ExprVar loseSvarExp = p.getSpecialVars().get(losesetName).asExprVar();

			String winSvar = getKinaraCode(winSvarExp);
			String loseSvar = getKinaraCode(loseSvarExp);

			b.append("\tProcessEFSM->AddWinTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + winLoc
					+ "\", " + setVar + ", " + cardVar + ", " + winSvar + ", " + loseSvar + ");\n");
			b.append("\tProcessEFSM->AddLoseTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + loseLoc
					+ "\", " + setVar + ", " + cardVar + ", " + winSvar + ", " + loseSvar + ");\n");

		} else {
			b.append("\tProcessEFSM->AddWinTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + winLoc
					+ "\", " + setVar + ", " + cardVar + ");\n");
			b.append("\tProcessEFSM->AddLoseTransition(" + ChId + ", \"" + currentLoc.getName() + "\", \"" + loseLoc
					+ "\", " + setVar + ", " + cardVar + ");\n");

		}

	}

	public String getKinaraCode(Expression expr) {
		if (expr instanceof ExprConstant) {
			ExprConstant exprConst = (ExprConstant) expr;
			String val = exprConst.getVal().toString();
			Type type = exprConst.getType();

			// special treatment for boolean constants
			if (type instanceof TypeBool) {
				if (exprConst.getVal().equals(TypeBool.Constant.TRUE.toString())) {
					return "TheLTS->MakeTrue()";
				} else {
					return "TheLTS->MakeFalse()";
				}
			}

			// special treatment for N
			if (type instanceof TypeInt) {
				if (val.equalsIgnoreCase("N")) {
					return "TheLTS->MakeVal(to_string(" + N + ") ," + ltsRange(N) + ")";
				}

				if (val.equals(TypeInt.Constant.RECVAL.toString())) {
					return exprConst.getAssosiatedAction().getName() + dotval;
				}
			}

			// special treatment for N
			if (type instanceof TypeID) {
				if (val.equals(TypeID.Constant.SELF.toString())) {
					return "PID";
				}
				if (val.equals(TypeID.Constant.OTHER.toString())) {
					return "PID1";
				}
				if (val.equals(TypeID.Constant.UNDEF.toString())) {
					return "UndefID";
				}
			}

			return "TheLTS->MakeVal(\"" + val + "\", " + getKinaraType(type) + ")";
		} else if (expr instanceof ExprVar) {
			ExprVar exprVar = (ExprVar) expr;
			String varname = exprVar.getName();
			Type type = exprVar.getType();
			// System.out.println("===========================================");
			// System.out.println("===========================================");
			// System.out.println("===========================================");
			// System.out.println(exprVar);
			// System.out.println(type);
			// System.out.println("===========================================");
			// System.out.println("===========================================");
			// System.out.println("===========================================");
			return "TheLTS->MakeVar(\"" + varname + "\", " + getKinaraType(type) + ")";
		} else if (expr instanceof ExprOp) {
			ExprOp exprOp = (ExprOp) expr;

			switch (exprOp.getOp()) {
			case NEQ: // because kinara decided to support all the operation in the universe other
						// than this one..
				ExprOp eq = new ExprOp(ExprOp.Op.EQ, exprOp.getArg1(), exprOp.getArg2());
				String arg = getKinaraCode(eq);
				return "TheLTS->MakeOp(LTSOps::OpNOT ," + arg + ")";

			case NOT: // because there is no arg2
				String argx = getKinaraCode(exprOp.getArg1());
				return "TheLTS->MakeOp(LTSOps::OpNOT ," + argx + ")";
			default:
				String opCode = toKinaraOpString(exprOp.getOp());
				String arg1 = getKinaraCode(exprOp.getArg1());
				String arg2 = getKinaraCode(exprOp.getArg2());
				return "TheLTS->MakeOp(" + opCode + "," + arg1 + "," + arg2 + ")";
			}
		} else if (expr instanceof ExprHole) {
			ExprHole holeExpr = (ExprHole) expr;
			Hole hole = holeExpr.getHole();
			if (!hole.isCompletted()) {
				System.err.println("Error: trying to generate code for an incompleted hole! (" + hole.getId() + ")");
				// Thread.dumpStack();
				System.exit(-1);
			}
			Expression completionExpr = hole.getCompletion();
			return getKinaraCode(completionExpr);
		} else if (expr == null) {
			System.err.println("Warning: trying to generate code from a null expression, ignoring..");
			return "Problems..";
		} else {
			System.out.println("what the heck is this expression?" + expr);
			return "what the heck is this expression?";
		}
	}

	private String toKinaraOpString(Op op) {
		switch (op) {
		case ADD:
			return "LTSOps::OpADD";
		case SUB:
			return "LTSOps::OpSUB";
		case MUL:
			return "LTSOps::OpMUL";
		case DIV:
			return "LTSOps::OpDIV";
		case MOD:
			return "LTSOps::OpMOD";
		case GT:
			return "LTSOps::OpGT";
		case LT:
			return "LTSOps::OpLT";
		case GE:
			return "LTSOps::OpGE";
		case LE:
			return "LTSOps::OpLE";
		case EQ:
			return "LTSOps::OpEQ";
		case AND:
			return "LTSOps::OpAND";
		case OR:
			return "LTSOps::OpOR";
		case NEQ:
			return "The universe is broken, again";
		default:
			return "The universe is broken, again";
		}
	}

	private String getKinaraCode(ExprVar var) {
		return "TheLTS->MakeVar(\"" + var.getName() + "\", " + getKinaraType(var.getType()) + ")";
	}

	private String consName2ChId(String consName) {
		if (consName2ChIdMap.containsKey(consName)) {
			return consName2ChIdMap.get(consName) + "";
		} else {
			consName2ChIdMap.put(consName, consName2ChIdMap.size());
			return consName2ChIdMap.get(consName) + "";
		}
	}

	private void generateVariables(StringBuilder b) {
		// HashSet<String> varsCreated = new HashSet<String>();
		// create the variables the user gives
		for (VarDecl vd : p.getAllVariables()) {
			b.append(tab);
			b.append("ProcessEFSM->AddVariable(\"" + vd.getName() + "\", " + getKinaraType(vd.getType()) + ");");
			b.append(newline);
			// varsCreated.add(vd.getName());
		}

		// create "hidden" variables: Cardinalities, Implicit Sets, winning prop,
		// AskingID etc.
		for (Entry<String, HashSet<HandlerPartitionCons>> cn : p.getPartitionInstMap().entrySet()) {
			// System.out.println(cn); // temp
			String consName = cn.getKey();
			EventPartitionCons event = cn.getValue().iterator().next().getEvent();

			// generate cardinality variable for each consName
			// add those to the special vars for convenience
			String cardVname = consName + "_" + card;

			Expression cardValExpr = event.getCardinality();
			ExprConstant cardVal = null;
			// uncompleted hole? we have a problem
			if (cardValExpr instanceof ExprHole) {
				Hole hole = ((ExprHole) cardValExpr).getHole();
				if (hole.isCompletted()) {
					if (hole.getCompletion() instanceof ExprConstant) {
						cardVal = (ExprConstant) hole.getCompletion();
					} else {
						System.err.println("Error: cardinality hole completted by a non-constant in code generation.");
						System.exit(-1);
					}
				} else {
					System.err.println("Error: uncompletted cardinality hole in code generation.");
					System.exit(-1);
				}
			} else if (cardValExpr instanceof ExprConstant) {
				cardVal = (ExprConstant) cardValExpr;
			} else {
				System.err.println("Error: uncregonized cardinality expression in code generation.");
				System.exit(-1);
			}

			// give the variable the same domain as its value.
			VarDecl cardVarDecl = new VarDecl(cardVal.getType(), cardVname, cardVal);
//			p.getSpecialVars().put(cardVname, cardVar.asVarDecl());
			p.addSpecialVariable(cardVarDecl);
			// cardVarToInitState.put(cardVar, cardVal);

			b.append("ProcessEFSM->AddVariable(\"" + cardVarDecl.getName() + "\", "
					+ getKinaraType(cardVarDecl.getType()) + ");");

			b.append(newline);
		}

		for (Entry<String, HashSet<HandlerValueCons>> cn : p.getValconsInstMap().entrySet()) {
			// System.out.println(cn); // temp
			String consName = cn.getKey();
			EventValueCons event = cn.getValue().iterator().next().getEvent();

			// generate cardinality variable for each consName
			// add those to the special vars for convenience
			Expression cardValExpr = event.getCardinality();

			ExprConstant cardVal = null;
			// uncompleted hole? we have a problem
			if (cardValExpr instanceof ExprHole) {
				Hole hole = ((ExprHole) cardValExpr).getHole();
				if (hole.isCompletted()) {
					if (hole.getCompletion() instanceof ExprConstant) {
						cardVal = (ExprConstant) hole.getCompletion();
					} else {
						System.err.println("Error: cardinality hole completted by a non-constant in code generation.");
						System.exit(-1);
					}
				} else {
					System.err.println("Error: uncompletted cardinality hole in code generation.");
					System.exit(-1);
				}
			} else if (cardValExpr instanceof ExprConstant) {
				cardVal = (ExprConstant) cardValExpr;
			} else {
				System.err.println("Error: uncregonized cardinality expression in code generation.");
				System.exit(-1);
			}

			String cardVname = consName + "_" + card;
			VarDecl cardVarDecl = new VarDecl(cardVal.getType(), cardVname, cardVal);
			p.addSpecialVariable(cardVarDecl);

			// cardVarToInitState.put(cardVar, cardVal);

			b.append("ProcessEFSM->AddVariable(\"" + cardVarDecl.getName() + "\", "
					+ getKinaraType(cardVarDecl.getType()) + ");");

			b.append(newline);

			// LATER: we might need to create a special variable for no prop (_)
		}

		// freeze and done
		b.append(tab);
		b.append("ProcessEFSM->FreezeVars();");
		b.append(newline);
		b.append(newline);
	}

	// private static boolean eventHasAllAsParticipantSet(Event event) {
	//
	// if (event instanceof EventValueCons) {
	// EventValueCons evc = (EventValueCons) event;
	// return evc.participantsIsAll();
	// } else if (event instanceof EventPartitionCons) {
	// EventPartitionCons epc = (EventPartitionCons) event;
	// return epc.participantsIsAll();
	// }
	// System.out.println("shouldn't happen(123)");
	// return false;
	// }

	private String getKinaraType(Type type) {

		String s = "UNKOWN (or unit?!)";

		if (type instanceof TypeBool) {
			s = "TheLTS->MakeBoolType()";
		} else if (type instanceof TypeID) {
			// s = "TheLTS->MakeSymmType(\"ProcessIDType\", NumProcesss)";
			s = "ProcessIDType";
		} else if (type instanceof TypeChooseSet) {
			// s = "TheLTS->MakeArrayType(TheLTS->MakeSymmType(\"ProcessIDType\",
			// NumProcesss), TheLTS->MakeBoolType())";
			s = "TheLTS->MakeArrayType(ProcessIDType, TheLTS->MakeBoolType())";
		} else if (type instanceof TypeInt) {
			String upperBound = ((TypeInt) type).getUpperBound();
			String lowerBound = ((TypeInt) type).getLowerBound();
			if (upperBound.equalsIgnoreCase("N")) {
				s = "TheLTS->MakeRangeType(" + lowerBound + "," + N + ")"; // or call ltsrange
			} else {
				int ub = Integer.valueOf(upperBound);
				s = "TheLTS->MakeRangeType(" + lowerBound + "," + ub + ")";
			}
		}
		return s;
	}

	private void generateStates(StringBuilder b) {
		for (Location loc : p.getLocations()) {
			b.append(tab);
			b.append("ProcessEFSM->AddState(\"" + loc.getName() + "\");");
			b.append(newline);
		}
		b.append(tab);
		b.append("ProcessEFSM->FreezeStates();");
		b.append(newline);
		b.append(newline);

	}

	public StringBuilder suffix() {
		StringBuilder b = new StringBuilder();
		b.append("\t//init states\n");
		b.append("\tTheLTS->AddInitStates({new LTSInitState({ }, TheLTS->MakeTrue(), InitUpdates)});\n");
		b.append("\n\n");
		b.append("\t// Freezing the LTS\n");
		b.append("\n\n");
		b.append("\tTheLTS->Freeze();\n");
		b.append("\tauto Checker = new LTSChecker(TheLTS);\n");
		// if (debug) {
		// b.append("\tauto Alltrans = ProcessEFSM->GetSymbolicTransitions();\n");
		// b.append("\tcout << \"Symbolic Transitions:\" << endl;\n");
		// b.append("\tfor (LTSSymbTransRef trans : Alltrans) {\n");
		// b.append("\t\tcout << trans->ToString() << endl;\n");
		// b.append("\t\tcout << endl;\n");
		// b.append("\t}\n");
		// }

		if (options.codeGen_generateDebugInfoInKinara) {
			b.append("\tChecker->PrintChooseRelatedDataStrctures();\n");
			b.append("\tauto AllEFSMs = TheLTS->GetEFSMs([&] (const EFSMBase *) {return true;});\n");
			b.append("\tcout << \"a total of \" << AllEFSMs.size() << \" EFSMs were generated: \" << endl;\n");
			b.append("\tfor (auto EFSM : AllEFSMs) {\n");
			b.append("\t\t cout << EFSM->ToString() << endl;\n");
			b.append("\t\t cout << endl;\n");
			b.append("\t\tauto Alltrans = EFSM->GetSymbolicTransitions();\n");
			// b.append("\t\tcout << \"Symbolic Transitions:\" << endl;\n");
			// b.append("\t\tfor (LTSSymbTransRef trans : Alltrans) {\n");
			// b.append("\t\t\tcout << trans->ToString() << endl;\n");
			// b.append("\t\t\tcout << endl;\n");
			// b.append("\t\t}\n");
			b.append("\t}\n");

			b.append("\tcout << \"Guarded Commands:\" << endl;\n");
			b.append("\tauto const& GCmds = TheLTS->GetGuardedCmds();\n");
			b.append("\tfor (auto const& GCmd : GCmds) {\n");
			b.append("\t\tcout << GCmd->ToString() << endl;\n");
			b.append("\t}\n");

		}

		b.append("\t//auto Safe = Checker->BuildAQS();\n");
		b.append("\n\n");
		b.append("\tauto TheSolver = new Solver(Checker);\n");
		b.append("\tTheSolver->Solve();\n");
		b.append("\n\n");
		b.append("\tdelete Checker;\n");
		b.append("\tdelete TheSolver;\n");
		b.append("\n\n");
		b.append("}\n\n");
		return b;
	}

	private static String ltsRange(String n) {
		return " TheLTS->MakeRangeType(0, " + n + ")";
	}

	public void generateCode(Protocol p) throws IOException {
		this.p = p;
		StringBuilder code = new StringBuilder();
		code.append(prefix());
		code.append(messageTypes());
		code.append(process());
		code.append(env());
		code.append(specs());
		code.append(initState());
		code.append(suffix());

		File destFolder = new File(options.kinaraDestFolder);
		if (destFolder.exists()) {
			File bmFile = new File(destFolder + File.separator + "gen_" + p.getName() + ".cpp");
			// System.err.println(bmFile);
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(bmFile));
				writer.append(code);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}

}
