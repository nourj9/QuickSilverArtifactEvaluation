//package antlr.parsing;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
////import org.antlr.v4.runtime.tree.ParseTree;
//
//import Holes.Hole;
//import Holes.HoleManager;
//import antlr.parsing.MercuryDSLParser.*;
//import lang.core.*;
//import lang.events.*;
//import lang.expr.*;
//import lang.specs.AtLeastSpec;
//import lang.specs.AtMostSpec;
//import lang.specs.CompoundSpec;
//import lang.specs.Specification;
//import lang.specs.StateDesc;
//import lang.specs.StateDescList;
//import lang.stmts.*;
//import lang.type.*;
//
//public class MercuryPassTwoVisitor extends MercuryDSLBaseVisitor<ChooseNode> {
//
//	private Protocol p = new Protocol();
//	private HashSet<Integer> UserIDs = new HashSet<Integer>();
//	private HashMap<String, VarDecl> specialVars = new HashMap<String, VarDecl>();
//	private HashMap<String, VarDecl> userVars = new HashMap<String, VarDecl>();
//	private HashMap<String, Location> locationMap = new HashMap<String, Location>();
//	private HashSet<String> envInputs = new HashSet<String>();
//	private HashSet<String> envOutputs = new HashSet<String>();
//	private HashMap<String, Action> actionMap = new HashMap<String, Action>();
//	private HashMap<String, HashSet<HandlerPartitionCons>> partitionInstMap = new HashMap<String, HashSet<HandlerPartitionCons>>();
//	private HashMap<String, HashSet<HandlerValueCons>> valconsInstMap = new HashMap<String, HashSet<HandlerValueCons>>();
//
//	public MercuryPassTwoVisitor(Protocol p) {
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public ChooseNode visitProgram(ProgramContext ctx) {
//		
////		//TODO specs.
////		p.setName(name);
////		p.setLocations(locations);
////		p.setActions(actions);
////		p.setUserVars(userVars);
////		p.setSpecialVars(specialVars);
////		p.setActionMap(actionMap);
////		p.setLocationMap(locationMap);
////		p.setEnvInputs(envInputs);
////		p.setEnvOutputs(envOutputs);
////		p.setPartitionInstMap(partitionInstMap);
////		p.setValconsInstMap(valconsInstMap);
////		p.setSpecs(specs);
////		p.createAllVars();
//		
//		
//		//TODO 		p.createAllVars(); AT THE END
//		String name = ctx.ID().getText();
//		Specification specs = null;
//		ArrayList<Action> actions = new ArrayList<Action>();
//		ArrayList<Location> locations = new ArrayList<Location>();
//
//		if (ctx.var != null && !ctx.var.isEmpty()) {
//			for (VarContext vd : ctx.vars) {
//				VarDecl decl = (VarDecl) visit(vd);
//				userVars.put(decl.getName(), decl);
//			}
//		}
//
//		if (ctx.action != null && !ctx.action.isEmpty()) {
//			for (ActionContext a : ctx.actions) {
//				actions.add((Action) visit(a));
//			}
//		}
//
//		for (LocContext locConst : ctx.locs) {
//			Location loc = (Location) visit(locConst);
//			locations.add(loc);
//		}
//
//		if (ctx.spec() != null) {
//			specs = (Specification) visit(ctx.spec());
//		}
//		p.fillProtocol(name, locations, actions, userVars, specs, specialVars, actionMap, locationMap, envInputs,
//				envOutputs, partitionInstMap, valconsInstMap);
//
//// specs.setProtocol(p);
//		return p;
//	}
//
//	@Override
//	public ChooseNode visitSpecAtMost(MercuryDSLParser.SpecAtMostContext ctx) {
//		int threshold = Integer.valueOf(ctx.maxNum.getText());
//		StateDescList sdlist = (StateDescList) visit(ctx.stateDescList());
//		return new AtMostSpec(threshold, sdlist, p);
//	}
//
//	@Override
//	public ChooseNode visitSpecAtLeast(MercuryDSLParser.SpecAtLeastContext ctx) {
//		int threshold = Integer.valueOf(ctx.minNum.getText());
//		StateDescList sdlist = (StateDescList) visit(ctx.stateDescList());
//		return new AtLeastSpec(threshold, sdlist, p);
//	}
//
//	@Override
//	public ChooseNode visitSpecAnd(MercuryDSLParser.SpecAndContext ctx) {
//
//		Specification arg1 = (Specification) visit(ctx.l);
//		Specification arg2 = (Specification) visit(ctx.r);
//
//		return new CompoundSpec(CompoundSpec.Op.AND, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitSpecOr(MercuryDSLParser.SpecOrContext ctx) {
//		Specification arg1 = (Specification) visit(ctx.l);
//		Specification arg2 = (Specification) visit(ctx.r);
//
//		return new CompoundSpec(CompoundSpec.Op.OR, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitSpecParen(SpecParenContext ctx) {
//		return visit(ctx.spec());
//	}
//
//	@Override
//	public ChooseNode visitStateDesc(MercuryDSLParser.StateDescContext ctx) {
//
//		String locName = ctx.ID().getText();
//		if (ctx.bexp() != null) {
//			Expression predicate = (Expression) visit(ctx.bexp());
//			return new StateDesc(locName, predicate);
//		} else {
//			return new StateDesc(locName);
//		}
//	}
//
//	@Override
//	public ChooseNode visitStateDescList(MercuryDSLParser.StateDescListContext ctx) {
//
//		StateDescList list;
//		if (ctx.stateDescList() != null) {
//			list = (StateDescList) visit(ctx.stateDescList());
//		} else {
//			list = new StateDescList();
//		}
//
//		StateDesc sd = (StateDesc) visit(ctx.stateDesc());
//		list.add(sd);
//
//		return list;
//	}
//
//	@Override
//	public ChooseNode visitId_var(Id_varContext ctx) {
//		String varname = ctx.ID().getText();
//		return new VarDecl(TypeID.T(), varname, new ExprConstant(TypeID.Constant.UNDEF.toString(), TypeID.T()));
//	}
//
//	@Override
//	public ChooseNode visitInt_var(Int_varContext ctx) {
//		String varname = ctx.ID().getText();
//		ExprConstant lbound = (ExprConstant) visit(ctx.lbound);
//		ExprConstant ubound = (ExprConstant) visit(ctx.ubound);
//		TypeInt t = new TypeInt(lbound.getVal(), ubound.getVal());
//		if (ctx.initVal != null) {
//			String initValue = ctx.initVal.getText();
//			return new VarDecl(t, varname, new ExprConstant(initValue, t));
//		} else {
//			return new VarDecl(t, varname, new ExprConstant(t.getLowerBound(), t));
//		}
//
//	}
//
//	@Override
//	public ChooseNode visitSet_var(Set_varContext ctx) {
//		String varname = ctx.ID().getText();
//		return new VarDecl(TypeChooseSet.T(), varname,
//				new ExprConstant(TypeChooseSet.Constant.EMTPY.toString(), TypeChooseSet.T()));
//	}
//
//	@Override
//	public ChooseNode visitActionBr(ActionBrContext ctx) {
//		boolean isEnv = ctx.getText().startsWith("env");
//		String name = ctx.ID().getText();
//		Type domain = (Type) visit(ctx.domain());
//		Action act = new Action(name, isEnv, domain, Action.CommType.BROADCAST);
//		actionMap.put(name, act);
//		return act;
//	}
//
//	@Override
//	public ChooseNode visitActionRz(ActionRzContext ctx) {
//		boolean isEnv = ctx.getText().startsWith("env");
//		String name = ctx.ID().getText();
//		Type domain = (Type) visit(ctx.domain());
//		Action act = new Action(name, isEnv, domain, Action.CommType.PAIRWISE);
//		actionMap.put(name, act);
//		return act;
//	}
//
//	@Override
//	public ChooseNode visitDomainUnit(DomainUnitContext ctx) {
//		return TypeUnit.T();
//	}
//
//	@Override
//	public ChooseNode visitDomainIntwLowerBound(DomainIntwLowerBoundContext ctx) {
//		ExprConstant lbound = (ExprConstant) visit(ctx.lbound);
//		ExprConstant ubound = (ExprConstant) visit(ctx.ubound);
//		return new TypeInt(lbound.getVal(), ubound.getVal());
//	}
//
//	@Override
//	public ChooseNode visitLoc(LocContext ctx) {
//
//		String name = ctx.locName.getText();
//		ArrayList<Handler> handlers = new ArrayList<Handler>();
//		for (HandlerContext handler : ctx.handler()) {
//			handlers.add((Handler) visit(handler));
//		}
//		Location loc = new Location(name, handlers, p);
//		locationMap.put(name, loc);
//		return loc;
//	}
//
//	@Override
//	public ChooseNode visitHandlerNormal(HandlerNormalContext ctx) {
//
//		Event event = (Event) visit(ctx.event());
//		ChooseNode react;
//
//		if (event instanceof EventValueCons) {
//			EventValueCons vce = (EventValueCons) event;
//			String chInst = vce.getChID();
//
//			// First, put the handler in the map.
//			HandlerValueCons vch = new HandlerValueCons(vce, new StmtBlock());
//			add(valconsInstMap, chInst, vch);
//
//			// Then visit the reaction and populate the body.
//			react = visit(ctx.react());
////			if (react instanceof ReactionPredicated) {
////				System.out.println("Predicated handler with ValueCons is not allowed ");
////				System.out.println(event);
////				System.exit(1);
////				return null; // sigh
////			}
////			ReactionRegular reaction = (ReactionRegular) react;
//			ReactionPredicated reaction = (ReactionPredicated) react;
//			vch.setBody(reaction.getBlock());
//			return vch;
//		}
//
//		react = visit(ctx.react());
//		// if (react instanceof ReactionRegular) {
//		// ReactionRegular reaction = (ReactionRegular) react;
//		// return new HandlerRegular(event, reaction);
//		// } else
//		if (react instanceof ReactionPredicated) {
//			ReactionPredicated reaction = (ReactionPredicated) react;
//			return new HandlerPredicated(event, reaction);
//		} else if (react instanceof ReactionWinLose) {
//			ReactionWinLose reaction = (ReactionWinLose) react;
//			EventPartitionCons pce = (EventPartitionCons) event;
//			String chIst = pce.getChID();
//			HandlerPartitionCons pch = new HandlerPartitionCons(pce, reaction);
//			add(partitionInstMap, chIst, pch);
//			return pch;
//		} else if (react instanceof ReactionReply) {
//			ReactionReply reaction = (ReactionReply) react;
//
//			return new HandlerReply(event, reaction);
//		} else {
//			return null;
//		}
//	}
//
//	private <T extends Handler> void add(HashMap<String, HashSet<T>> map, String key, T element) {
//		if (!map.containsKey(key)) {
//			map.put(key, new HashSet<T>());
//		}
//		map.get(key).add(element);
//	}
//
//	@Override
//	public ChooseNode visitHandlerPassive(HandlerPassiveContext ctx) {
//		return new HandlerPassive((EventList) visit(ctx.eventlist()));
//	}
//
//	@Override
//	public ChooseNode visitReactPartCons(ReactPartConsContext ctx) {
//		StmtBlock winblock = new StmtBlock();
//		for (StmtContext stmt : ctx.win) {
//			winblock.addStmt((Statement) visit(stmt));
//		}
//
//		StmtBlock loseblock = new StmtBlock();
//		for (StmtContext stmt : ctx.lose) {
//			loseblock.addStmt((Statement) visit(stmt));
//		}
//
//		return new ReactionWinLose(winblock, loseblock);
//	}
//
//	@Override
//	public ChooseNode visitReactReply(ReactReplyContext ctx) {
//		return new ReactionReply((Action) visit(ctx.msg()));
//	}
//
//	@Override
//	public ChooseNode visitReactDo(ReactDoContext ctx) {
//		StmtBlock block = new StmtBlock();
//		for (StmtContext stmt : ctx.stmt()) {
//			block.addStmt((Statement) visit(stmt));
//		}
//
////		return new ReactionRegular(block);
//		return new ReactionPredicated(block);
//	}
//
//	@Override
//	public ChooseNode visitReactPredicate(ReactPredicateContext ctx) {
//		StmtBlock block = new StmtBlock();
//		for (StmtContext stmt : ctx.stmt()) {
//			block.addStmt((Statement) visit(stmt));
//		}
//
//		Expression predicate = (Expression) visit(ctx.bexp());
//
//		return new ReactionPredicated(block, predicate);
//	}
//
//	@Override
//	public ChooseNode visitEventlist(EventlistContext ctx) {
//		if (ctx.eventlist() == null) {
//			return new EventList(ctx.ID().getText());
//		} else {
//			EventList el = (EventList) visit(ctx.eventlist());
//			el.addEvent(ctx.ID().getText());
//			return el;
//		}
//
//	}
//
//	@Override
//	public ChooseNode visitEventValcons(EventValconsContext ctx) {
//		// String wv = ctx.winvar.getText();
//		// ExprVar winvar = new ExprVar(wv, symbolTable.get(wv).getType());
//		String pv = ctx.propvar.getText();
//		ExprVar propvar = null;
//
//		if (pv.equals("_")) {
//			propvar = new ExprVar(pv, TypeInt.Null());
//		} else {
//			// propvar = new ExprVar(pv, symbolTable.get(pv).getType());
//			if (!userVars.containsKey(pv)) {
//				System.err.println("variable " + pv + " not defined.");
//				System.exit(-1);
//			}
//			propvar = userVars.get(pv).asExprVar();
//		}
//
//		Expression part = (Expression) visit(ctx.chset);
//
//		if (part instanceof ExprVar) {
//			ExprVar varExp = (ExprVar) part;
//			if (varExp.getName().equals(TypeChooseSet.Constant.ALL.toString())) {
//				specialVars.put(varExp.getName(), varExp.asVarDecl());
//			}
//		}
//
//		Expression card = (Expression) visit(ctx.card);
//		String chID = ctx.chid.getText();
//		EventValueCons evc = new EventValueCons(propvar, chID, part, card);
//		return evc;
//	}
//
//	@Override
//	public ChooseNode visitEventPartCons(EventPartConsContext ctx) {
//		Expression part = (Expression) visit(ctx.chset);
//
//		if (part instanceof ExprVar) {
//			ExprVar varExp = (ExprVar) part;
//			if (varExp.getName().equals(TypeChooseSet.Constant.ALL.toString())) {
//				specialVars.put(varExp.getName(), varExp.asVarDecl());
//			}
//		}
//
//		Expression card = (Expression) visit(ctx.card);
//		EventPartitionCons epc = new EventPartitionCons(ctx.chid.getText(), part, card);
//		return epc;
//	}
//
//	@Override
//	public ChooseNode visitEventEpsilon(EventEpsilonContext ctx) {
//		return new EventEpsilon();
//	}
//
//	@Override
//	public ChooseNode visitEventMsg(EventMsgContext ctx) {
//		EventAction act = new EventAction((Action) visit(ctx.msg()));
//		// for Env events, save if it's input or output.
//
//		if (act.getAction() == null) {
//			System.err.println("Error: undefind action " + ctx.msg().getText());
//			System.exit(-1);
//		}
//
//		if (act.getAction().isEnv()) {
//			envInputs.add(act.getAction().getName());
//		}
//		return act;
//	}
//
//	@Override
//	public ChooseNode visitEventPassive(EventPassiveContext ctx) {
//		EventPassive passive = new EventPassive(ctx.ID().getText());
//		return passive;
//	}
//	
//	@Override
//	public ChooseNode visitStmt(StmtContext ctx) {
//		return visitChildren(ctx);
//	}
//
//	@Override
//	public ChooseNode visitUpdateAssignNexp(UpdateAssignNexpContext ctx) {
//		String name = ctx.ID().getText();
//		if (!userVars.containsKey(name)) {
//			System.err.println("variable " + name + " not defined.");
//			System.exit(-1);
//		}
//		ExprVar lhs = userVars.get(name).asExprVar();
//		Expression rhs = (Expression) visit(ctx.nexp());
//		return new StmtAssign(lhs, rhs);
//	}
//
//	@Override
//	public ChooseNode visitUpdateAssignIexp(UpdateAssignIexpContext ctx) {
//		ExprVar lhs = new ExprVar(ctx.ID().getText(), TypeID.T());
//		Expression rhs = (Expression) visit(ctx.iexp());
//		return new StmtAssign(lhs, rhs);
//	}
//
//	@Override
//	public ChooseNode visitUpdateAssignStexp(UpdateAssignStexpContext ctx) {
//		ExprVar lhs = new ExprVar(ctx.ID().getText(), TypeChooseSet.T());
//		Expression rhs = (Expression) visit(ctx.stexp());
//		return new StmtAssign(lhs, rhs);
//	}
//
//	@Override
//	public ChooseNode visitUpdateSetAdd(UpdateSetAddContext ctx) {
//		ExprVar thisSet = new ExprVar(ctx.ID().getText(), TypeChooseSet.T());
//		Expression elem = (Expression) visit(ctx.iexp());
//		return new StmtSetUpdate(thisSet, StmtSetUpdate.OpType.ADD, elem);
//	}
//
//	@Override
//	public ChooseNode visitUpdateSetRemAll(UpdateSetRemAllContext ctx) {
//		ExprVar thisSet = new ExprVar(ctx.ID().getText(), TypeChooseSet.T());
//		Expression elem = (Expression) visit(ctx.stexp());
//		return new StmtSetUpdate(thisSet, StmtSetUpdate.OpType.REMOVE, elem);
//	}
//
//	@Override
//	public ChooseNode visitUpdateSetAddAll(UpdateSetAddAllContext ctx) {
//		ExprVar thisSet = new ExprVar(ctx.ID().getText(), TypeChooseSet.T());
//		Expression elem = (Expression) visit(ctx.stexp());
//		return new StmtSetUpdate(thisSet, StmtSetUpdate.OpType.ADD, elem);
//	}
//
//	@Override
//	public ChooseNode visitUpdateSetRem(UpdateSetRemContext ctx) {
//		ExprVar thisSet = new ExprVar(ctx.ID().getText(), TypeChooseSet.T());
//		Expression elem = (Expression) visit(ctx.iexp());
//		return new StmtSetUpdate(thisSet, StmtSetUpdate.OpType.REMOVE, elem);
//	}
//
//	@Override
//	public ChooseNode visitCommSend(CommSendContext ctx) {
//		Action msg = (Action) visit(ctx.msg());
//		Expression to = (Expression) visit(ctx.iexp());
//
//		// keep track of inv outputs
//		if (msg.isEnv()) {
//			envOutputs.add(msg.getName());
//		}
//
//		return new StmtSend(msg, to);
//	}
//
//	@Override
//	public ChooseNode visitCommBroadcast(CommBroadcastContext ctx) {
//
//		Action msg = (Action) visit(ctx.msg());
//
//		// keep track of inv outputs
//		if (msg.isEnv()) {
//			envOutputs.add(msg.getName());
//		}
//
//		return new StmtBroadcast(msg);
//	}
//
//	@Override
//	public ChooseNode visitMsgId(MsgIdContext ctx) {
//		return actionMap.get(ctx.ID().getText());
//	}
//
//	@Override
//	public ChooseNode visitMsgIdVal(MsgIdValContext ctx) {
//		String name = ctx.ID().getText();
//		Expression value = (Expression) visit(ctx.nexp());
//		Action baseAction = actionMap.get(name);
//		return new Action(name, baseAction.isEnv(), baseAction.getDomain(), baseAction.getCommType(), value);
//	}
//
//	@Override
//	public ChooseNode visitControlIf(ControlIfContext ctx) {
//
//		Expression cond = (Expression) visit(ctx.cond);
//		StmtBlock ifbranch = new StmtBlock();
//		for (StmtContext stmt : ctx.ifbranch) {
//			ifbranch.addStmt((Statement) visit(stmt));
//		}
//
//		if (!ctx.elsebranch.isEmpty()) {
//			StmtBlock elsebranch = new StmtBlock();
//			for (StmtContext stmt : ctx.elsebranch) {
//				elsebranch.addStmt((Statement) visit(stmt));
//			}
//			return new StmtIfThen(cond, ifbranch, elsebranch);
//		} else {
//			return new StmtIfThen(cond, ifbranch, null);
//		}
//	}
//
//	@Override
//	public ChooseNode visitControlGoto(ControlGotoContext ctx) {
//		Expression target = (Expression) visit(ctx.targetLocation());
//		return new StmtGoto(target, p);
//	}
//
//	@Override
//	public ChooseNode visitIexpID(IexpIDContext ctx) {
//		return new ExprVar(ctx.ID().getText(), TypeID.T());
//	}
//
//	@Override
//	public ChooseNode visitIexpIdDotId(IexpIdDotIdContext ctx) {
//		String name = ctx.ID().getText();
//		// Make .sID a unary expression;
//		String varName = name + "_sID"; // changed to _sID because kinara has issues with .
//		ExprVar expVar = new ExprVar(varName, TypeID.T());
//		specialVars.put(varName, expVar.asVarDecl());
//		return expVar;
//	}
//
//	@Override
//	public ChooseNode visitIexpSelf(IexpSelfContext ctx) {
//		return new ExprConstant(ctx.getText(), TypeID.T());
//	}
//
//	@Override
//	public ChooseNode visitNexpMul(NexpMulContext ctx) {
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(ExprOp.Op.MUL, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitNexpParen(NexpParenContext ctx) {
//		return visit(ctx.nexp());
//	}
//
//	@Override
//	public ChooseNode visitNexpIDNormal(NexpIDNormalContext ctx) {
//		String name = ctx.ID().getText();
//		if (!userVars.containsKey(name)) {
//			System.err.println("variable " + name + " not defined.");
//			System.exit(-1);
//		}
//		return userVars.get(name).asExprVar();
//	}
//
//	@Override
//	public ChooseNode visitNexpIDVal(NexpIDValContext ctx) {
//		String name = ctx.ID().getText();
//		if (actionMap.get(name) != null && actionMap.get(name).getDomain() instanceof TypeUnit) {
//			System.out.println("Error: calling .val on an action of type unit!");
//			System.exit(0);
//		}
//
//		String varName = name + "_val";
//		ExprVar expVar = new ExprVar(varName, actionMap.get(name).getDomain());
//		specialVars.put(varName, expVar.asVarDecl());
//
//		return expVar;
//	}
//
//	@Override
//	public ChooseNode visitNexpIDWVal(NexpIDWValContext ctx) {
//		String name = ctx.ID().getText();
//
//		if (valconsInstMap.get(name) == null) {
//			System.err.println("Error: calling .desVal something other than valStore .chId(1)!");
//			Thread.dumpStack();
//		}
//
//		HashSet<HandlerValueCons> handlers = valconsInstMap.get(name);
//		if (handlers.isEmpty()) {
//			System.err.println("Error: calling .desVal something other than valStore .chId(2)!");
//			Thread.dumpStack();
//		}
//
//		HandlerValueCons sampleHandler = getHandlerWithNonEmptyProposal(handlers);
//
//		if (sampleHandler == null) {
//			System.err.println("Error: cannot find a non-empty proposal for type HandlerValueCons with ID " + name);
//			System.err.println("this can be solved with smarter parsing?");
//			Thread.dumpStack();
//		}
//
//		EventValueCons evc = sampleHandler.getEvent();
//		// might change when we allow other types.
//		TypeInt propValType = (TypeInt) evc.getProposalVar().getType();
//
//		ExprVar varExp = new ExprVar(name + "_wval", propValType);
//		specialVars.put(varExp.getName(), varExp.asVarDecl());
//		return varExp;
//	}
//
//	private HandlerValueCons getHandlerWithNonEmptyProposal(HashSet<HandlerValueCons> handlers) {
//		for (HandlerValueCons handler : handlers) {
//			if (handler.getEvent().hasPropVar()) {
//				return handler;
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public ChooseNode visitNexpDiv(NexpDivContext ctx) {
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(ExprOp.Op.DIV, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitNexpAdd(NexpAddContext ctx) {
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(ExprOp.Op.ADD, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitNexpSub(NexpSubContext ctx) {
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(ExprOp.Op.SUB, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitSetexpParen(SetexpParenContext ctx) {
//		return visit(ctx.stexp());
//	}
//
//	@Override
//	public ChooseNode visitSetexpId(SetexpIdContext ctx) {
//		return new ExprVar(ctx.ID().getText(), TypeChooseSet.T());
//	}
//
//	@Override
//	public ChooseNode visitSetexpAll(SetexpAllContext ctx) {
//		// return new ExprConstant(TypeChooseSet.Constant.ALL, TypeChooseSet.T());
//		String varName = TypeChooseSet.Constant.ALL.toString();
//		ExprVar varExp = new ExprVar(varName, TypeChooseSet.T());
//		// specialVars.put(varName, varExp);
//		return varExp;
//
//	}
//
//	@Override
//	public ChooseNode visitSetexpEmpty(SetexpEmptyContext ctx) {
//		return new ExprConstant(TypeChooseSet.Constant.EMTPY.toString(), TypeChooseSet.T());
//	}
//
//	@Override
//	public ChooseNode visitSetexpLosers(SetexpLosersContext ctx) {
//		// update symbol table in visitSetexpId
//
//		String varName = ctx.getText().replaceAll("\\.", "_");
//		ExprVar expVar = new ExprVar(varName, TypeChooseSet.T());
//		specialVars.put(varName, expVar.asVarDecl());
//		return expVar;
//	}
//
//	@Override
//	public ChooseNode visitSetexpWinners(SetexpWinnersContext ctx) {
//		// update symbol table in visitSetexpId
//		String varName = ctx.getText().replaceAll("\\.", "_");
//		ExprVar expVar = new ExprVar(varName, TypeChooseSet.T());
//		specialVars.put(varName, expVar.asVarDecl());
//		return expVar;
//
//	}
//
//	@Override
//	public ChooseNode visitBexpComp(BexpCompContext ctx) {
//		// System.err.println("xx: in visitBexpComp");
//		ExprOp.Op operation;
//		if (ctx.cmpop().getText().equals("==")) {
//			operation = ExprOp.Op.EQ;
//		} else if (ctx.cmpop().getText().equals("!=")) {
//			operation = ExprOp.Op.NEQ;
//		} else if (ctx.cmpop().getText().equals("<")) {
//			operation = ExprOp.Op.LT;
//		} else if (ctx.cmpop().getText().equals(">")) {
//			operation = ExprOp.Op.GT;
//		} else if (ctx.cmpop().getText().equals("<=")) {
//			operation = ExprOp.Op.LE;
//		} else {
//			operation = ExprOp.Op.GE;
//		}
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(operation, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitCardConst(CardConstContext ctx) {
//		String val = ctx.getText();
//		// return new ExprConstant(val, new TypeInt("1", "N"));
//		return new ExprConstant(val, new TypeInt("1", val));
//	}
//
//	@Override
//	public ChooseNode visitBexpFalse(BexpFalseContext ctx) {
//		return new ExprConstant(TypeBool.Constant.FALSE.toString(), TypeBool.T());
//	}
//
//	@Override
//	public ChooseNode visitBexpNot(BexpNotContext ctx) {
//		Expression arg = (Expression) visit(ctx.bexp());
//		return new ExprOp(ExprOp.Op.NOT, arg);
//	}
//
//	@Override
//	public ChooseNode visitBexpEqI(BexpEqIContext ctx) {
//		// System.err.println("xx: in visitBexpEqI");
//		ExprOp.Op operation;
//		if (ctx.eqop().getText().equals("==")) {
//			operation = ExprOp.Op.EQ;
//		} else {
//			operation = ExprOp.Op.NEQ;
//		}
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(operation, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitBexpEqB(BexpEqBContext ctx) {
//		// System.err.println("xx: in visitBexpEqB");
//
//		ExprOp.Op operation;
//		if (ctx.eqop().getText().equals("==")) {
//			operation = ExprOp.Op.EQ;
//		} else {
//			operation = ExprOp.Op.NEQ;
//		}
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(operation, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitBexpParen(BexpParenContext ctx) {
//		return visit(ctx.bexp());
//	}
//
//	@Override
//	public ChooseNode visitBexpAnd(BexpAndContext ctx) {
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(ExprOp.Op.AND, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitBexpTrue(BexpTrueContext ctx) {
//		return new ExprConstant(TypeBool.Constant.TRUE.toString(), TypeBool.T());
//	}
//
//	@Override
//	public ChooseNode visitBexpOr(BexpOrContext ctx) {
//		Expression arg1 = (Expression) visit(ctx.l);
//		Expression arg2 = (Expression) visit(ctx.r);
//		return new ExprOp(ExprOp.Op.OR, arg1, arg2);
//	}
//
//	@Override
//	public ChooseNode visitScalar(ScalarContext ctx) {
//		return new ExprConstant(ctx.getText(), new TypeInt(ctx.getText(), ctx.getText()));
//	}
//
//	@Override
//	public ChooseNode visitNexpHole(NexpHoleContext ctx) {
//		return new ExprHole((Hole) visit(ctx.h));
//	}
//
//	@Override
//	public ChooseNode visitBexpHole(BexpHoleContext ctx) {
//		return new ExprHole((Hole) visit(ctx.h));
//	}
//
//	@Override
//	public ChooseNode visitCardHole(CardHoleContext ctx) {
//		return new ExprHole((Hole) visit(ctx.h));
//	}
//
//	@Override
//	public ChooseNode visitLocHole(LocHoleContext ctx) {
//		return new ExprHole((Hole) visit(ctx.h));
//	}
//
//	@Override
//	public ChooseNode visitLocName(LocNameContext ctx) {
//		String locName = ctx.getText();
//		return new ExprLoc(locName);
//	}
//
//	@Override
//	public ChooseNode visitBhole(BholeContext ctx) {
//		// auto generated Ids are negative
//		if (ctx.id != null) {
//			int userId = Integer.valueOf(ctx.id.getText());
//			if (UserIDs.contains(userId)) {
//				// return the existing hole object
//				return HoleManager.getHole(userId);
//			} else {
//				UserIDs.add(userId);
//				return new Hole(userId, TypeBool.T(),userVars.values());
//			}
//		} else {
//			int id = Hole.generateID();
//			return new Hole(id, TypeBool.T(),userVars.values());
//		}
//	}
//
//	@Override
//	public ChooseNode visitIhole(IholeContext ctx) {
//		// auto generated Ids are negative
//		if (ctx.id != null) {
//			int userId = Integer.valueOf(ctx.id.getText());
//			if (UserIDs.contains(userId)) {
//				// return the existing hole object
//				return HoleManager.getHole(userId);
//			} else {
//				UserIDs.add(userId);
//				return new Hole(userId, TypeInt.Null(),userVars.values());
//			}
//		} else {
//			int id = Hole.generateID();
//			return new Hole(id, TypeInt.Null(),userVars.values());
//		}
//	}
//
//	public ChooseNode visitChole(CholeContext ctx) {
//		// auto generated Ids are negative
//		if (ctx.id != null) {
//			int userId = Integer.valueOf(ctx.id.getText());
//			if (UserIDs.contains(userId)) {
//				// return the existing hole object
//				return HoleManager.getHole(userId);
//			} else {
//				UserIDs.add(userId);
//				return new Hole(userId, TypeCard.T(),userVars.values());
//			}
//		} else {
//			int id = Hole.generateID();
//			return new Hole(id, TypeCard.T(),userVars.values());
//		}
//	}
//
//	public ChooseNode visitLhole(LholeContext ctx) {
//		// auto generated Ids are negative
//		if (ctx.id != null) {
//			int userId = Integer.valueOf(ctx.id.getText());
//			if (UserIDs.contains(userId)) {
//				// return the existing hole object
//				return HoleManager.getHole(userId);
//			} else {
//				UserIDs.add(userId);
//				return new Hole(userId, TypeLoc.T(),userVars.values());
//			}
//		} else {
//			int id = Hole.generateID();
//			return new Hole(id, TypeLoc.T(),userVars.values());
//		}
//	}
//
//}















