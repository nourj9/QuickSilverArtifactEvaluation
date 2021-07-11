// Generated from MercuryDSL.g4 by ANTLR 4.8

package antlr.parsing;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MercuryDSLParser}.
 */
public interface MercuryDSLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MercuryDSLParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MercuryDSLParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#crashes}.
	 * @param ctx the parse tree
	 */
	void enterCrashes(MercuryDSLParser.CrashesContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#crashes}.
	 * @param ctx the parse tree
	 */
	void exitCrashes(MercuryDSLParser.CrashesContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#spec}.
	 * @param ctx the parse tree
	 */
	void enterSpec(MercuryDSLParser.SpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#spec}.
	 * @param ctx the parse tree
	 */
	void exitSpec(MercuryDSLParser.SpecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code safetySpecAtMost}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void enterSafetySpecAtMost(MercuryDSLParser.SafetySpecAtMostContext ctx);
	/**
	 * Exit a parse tree produced by the {@code safetySpecAtMost}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void exitSafetySpecAtMost(MercuryDSLParser.SafetySpecAtMostContext ctx);
	/**
	 * Enter a parse tree produced by the {@code safetySpecParen}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void enterSafetySpecParen(MercuryDSLParser.SafetySpecParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code safetySpecParen}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void exitSafetySpecParen(MercuryDSLParser.SafetySpecParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code safetySpecAnd}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void enterSafetySpecAnd(MercuryDSLParser.SafetySpecAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code safetySpecAnd}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void exitSafetySpecAnd(MercuryDSLParser.SafetySpecAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code safetySpecAtLeast}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void enterSafetySpecAtLeast(MercuryDSLParser.SafetySpecAtLeastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code safetySpecAtLeast}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void exitSafetySpecAtLeast(MercuryDSLParser.SafetySpecAtLeastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code safetySpecOr}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void enterSafetySpecOr(MercuryDSLParser.SafetySpecOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code safetySpecOr}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 */
	void exitSafetySpecOr(MercuryDSLParser.SafetySpecOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#stateDescList}.
	 * @param ctx the parse tree
	 */
	void enterStateDescList(MercuryDSLParser.StateDescListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#stateDescList}.
	 * @param ctx the parse tree
	 */
	void exitStateDescList(MercuryDSLParser.StateDescListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#stateDesc}.
	 * @param ctx the parse tree
	 */
	void enterStateDesc(MercuryDSLParser.StateDescContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#stateDesc}.
	 * @param ctx the parse tree
	 */
	void exitStateDesc(MercuryDSLParser.StateDescContext ctx);
	/**
	 * Enter a parse tree produced by the {@code livenessSpecEventually}
	 * labeled alternative in {@link MercuryDSLParser#liveness_spec}.
	 * @param ctx the parse tree
	 */
	void enterLivenessSpecEventually(MercuryDSLParser.LivenessSpecEventuallyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code livenessSpecEventually}
	 * labeled alternative in {@link MercuryDSLParser#liveness_spec}.
	 * @param ctx the parse tree
	 */
	void exitLivenessSpecEventually(MercuryDSLParser.LivenessSpecEventuallyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code livenessSpecReactive}
	 * labeled alternative in {@link MercuryDSLParser#liveness_spec}.
	 * @param ctx the parse tree
	 */
	void enterLivenessSpecReactive(MercuryDSLParser.LivenessSpecReactiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code livenessSpecReactive}
	 * labeled alternative in {@link MercuryDSLParser#liveness_spec}.
	 * @param ctx the parse tree
	 */
	void exitLivenessSpecReactive(MercuryDSLParser.LivenessSpecReactiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code livpredSDlist}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 */
	void enterLivpredSDlist(MercuryDSLParser.LivpredSDlistContext ctx);
	/**
	 * Exit a parse tree produced by the {@code livpredSDlist}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 */
	void exitLivpredSDlist(MercuryDSLParser.LivpredSDlistContext ctx);
	/**
	 * Enter a parse tree produced by the {@code livpredReceived}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 */
	void enterLivpredReceived(MercuryDSLParser.LivpredReceivedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code livpredReceived}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 */
	void exitLivpredReceived(MercuryDSLParser.LivpredReceivedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code livpredSent}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 */
	void enterLivpredSent(MercuryDSLParser.LivpredSentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code livpredSent}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 */
	void exitLivpredSent(MercuryDSLParser.LivpredSentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#var}.
	 * @param ctx the parse tree
	 */
	void enterVar(MercuryDSLParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#var}.
	 * @param ctx the parse tree
	 */
	void exitVar(MercuryDSLParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#id_var}.
	 * @param ctx the parse tree
	 */
	void enterId_var(MercuryDSLParser.Id_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#id_var}.
	 * @param ctx the parse tree
	 */
	void exitId_var(MercuryDSLParser.Id_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#int_var}.
	 * @param ctx the parse tree
	 */
	void enterInt_var(MercuryDSLParser.Int_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#int_var}.
	 * @param ctx the parse tree
	 */
	void exitInt_var(MercuryDSLParser.Int_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#set_var}.
	 * @param ctx the parse tree
	 */
	void enterSet_var(MercuryDSLParser.Set_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#set_var}.
	 * @param ctx the parse tree
	 */
	void exitSet_var(MercuryDSLParser.Set_varContext ctx);
	/**
	 * Enter a parse tree produced by the {@code actionBr}
	 * labeled alternative in {@link MercuryDSLParser#action}.
	 * @param ctx the parse tree
	 */
	void enterActionBr(MercuryDSLParser.ActionBrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code actionBr}
	 * labeled alternative in {@link MercuryDSLParser#action}.
	 * @param ctx the parse tree
	 */
	void exitActionBr(MercuryDSLParser.ActionBrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code actionRz}
	 * labeled alternative in {@link MercuryDSLParser#action}.
	 * @param ctx the parse tree
	 */
	void enterActionRz(MercuryDSLParser.ActionRzContext ctx);
	/**
	 * Exit a parse tree produced by the {@code actionRz}
	 * labeled alternative in {@link MercuryDSLParser#action}.
	 * @param ctx the parse tree
	 */
	void exitActionRz(MercuryDSLParser.ActionRzContext ctx);
	/**
	 * Enter a parse tree produced by the {@code domainUnit}
	 * labeled alternative in {@link MercuryDSLParser#domain}.
	 * @param ctx the parse tree
	 */
	void enterDomainUnit(MercuryDSLParser.DomainUnitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code domainUnit}
	 * labeled alternative in {@link MercuryDSLParser#domain}.
	 * @param ctx the parse tree
	 */
	void exitDomainUnit(MercuryDSLParser.DomainUnitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code domainIntwLowerBound}
	 * labeled alternative in {@link MercuryDSLParser#domain}.
	 * @param ctx the parse tree
	 */
	void enterDomainIntwLowerBound(MercuryDSLParser.DomainIntwLowerBoundContext ctx);
	/**
	 * Exit a parse tree produced by the {@code domainIntwLowerBound}
	 * labeled alternative in {@link MercuryDSLParser#domain}.
	 * @param ctx the parse tree
	 */
	void exitDomainIntwLowerBound(MercuryDSLParser.DomainIntwLowerBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#loc}.
	 * @param ctx the parse tree
	 */
	void enterLoc(MercuryDSLParser.LocContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#loc}.
	 * @param ctx the parse tree
	 */
	void exitLoc(MercuryDSLParser.LocContext ctx);
	/**
	 * Enter a parse tree produced by the {@code handlerNormal}
	 * labeled alternative in {@link MercuryDSLParser#handler}.
	 * @param ctx the parse tree
	 */
	void enterHandlerNormal(MercuryDSLParser.HandlerNormalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code handlerNormal}
	 * labeled alternative in {@link MercuryDSLParser#handler}.
	 * @param ctx the parse tree
	 */
	void exitHandlerNormal(MercuryDSLParser.HandlerNormalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code handlerPassive}
	 * labeled alternative in {@link MercuryDSLParser#handler}.
	 * @param ctx the parse tree
	 */
	void enterHandlerPassive(MercuryDSLParser.HandlerPassiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code handlerPassive}
	 * labeled alternative in {@link MercuryDSLParser#handler}.
	 * @param ctx the parse tree
	 */
	void exitHandlerPassive(MercuryDSLParser.HandlerPassiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code reactDo}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void enterReactDo(MercuryDSLParser.ReactDoContext ctx);
	/**
	 * Exit a parse tree produced by the {@code reactDo}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void exitReactDo(MercuryDSLParser.ReactDoContext ctx);
	/**
	 * Enter a parse tree produced by the {@code reactPredicate}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void enterReactPredicate(MercuryDSLParser.ReactPredicateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code reactPredicate}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void exitReactPredicate(MercuryDSLParser.ReactPredicateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code reactReply}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void enterReactReply(MercuryDSLParser.ReactReplyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code reactReply}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void exitReactReply(MercuryDSLParser.ReactReplyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code reactPartCons}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void enterReactPartCons(MercuryDSLParser.ReactPartConsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code reactPartCons}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 */
	void exitReactPartCons(MercuryDSLParser.ReactPartConsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierList(MercuryDSLParser.IdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierList(MercuryDSLParser.IdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eventValcons}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEventValcons(MercuryDSLParser.EventValconsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eventValcons}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEventValcons(MercuryDSLParser.EventValconsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eventPartCons}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEventPartCons(MercuryDSLParser.EventPartConsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eventPartCons}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEventPartCons(MercuryDSLParser.EventPartConsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eventEpsilon}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEventEpsilon(MercuryDSLParser.EventEpsilonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eventEpsilon}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEventEpsilon(MercuryDSLParser.EventEpsilonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eventMsg}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEventMsg(MercuryDSLParser.EventMsgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eventMsg}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEventMsg(MercuryDSLParser.EventMsgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eventPassive}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEventPassive(MercuryDSLParser.EventPassiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eventPassive}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEventPassive(MercuryDSLParser.EventPassiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code identifier}
	 * labeled alternative in {@link MercuryDSLParser#prop}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(MercuryDSLParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code identifier}
	 * labeled alternative in {@link MercuryDSLParser#prop}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(MercuryDSLParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code noprop}
	 * labeled alternative in {@link MercuryDSLParser#prop}.
	 * @param ctx the parse tree
	 */
	void enterNoprop(MercuryDSLParser.NopropContext ctx);
	/**
	 * Exit a parse tree produced by the {@code noprop}
	 * labeled alternative in {@link MercuryDSLParser#prop}.
	 * @param ctx the parse tree
	 */
	void exitNoprop(MercuryDSLParser.NopropContext ctx);
	/**
	 * Enter a parse tree produced by the {@code cardConst}
	 * labeled alternative in {@link MercuryDSLParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void enterCardConst(MercuryDSLParser.CardConstContext ctx);
	/**
	 * Exit a parse tree produced by the {@code cardConst}
	 * labeled alternative in {@link MercuryDSLParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void exitCardConst(MercuryDSLParser.CardConstContext ctx);
	/**
	 * Enter a parse tree produced by the {@code cardHole}
	 * labeled alternative in {@link MercuryDSLParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void enterCardHole(MercuryDSLParser.CardHoleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code cardHole}
	 * labeled alternative in {@link MercuryDSLParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void exitCardHole(MercuryDSLParser.CardHoleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(MercuryDSLParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(MercuryDSLParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateAssignNexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateAssignNexp(MercuryDSLParser.UpdateAssignNexpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateAssignNexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateAssignNexp(MercuryDSLParser.UpdateAssignNexpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateAssignIexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateAssignIexp(MercuryDSLParser.UpdateAssignIexpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateAssignIexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateAssignIexp(MercuryDSLParser.UpdateAssignIexpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateAssignStexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateAssignStexp(MercuryDSLParser.UpdateAssignStexpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateAssignStexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateAssignStexp(MercuryDSLParser.UpdateAssignStexpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateSetAdd}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateSetAdd(MercuryDSLParser.UpdateSetAddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateSetAdd}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateSetAdd(MercuryDSLParser.UpdateSetAddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateSetRem}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateSetRem(MercuryDSLParser.UpdateSetRemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateSetRem}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateSetRem(MercuryDSLParser.UpdateSetRemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateSetAddAll}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateSetAddAll(MercuryDSLParser.UpdateSetAddAllContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateSetAddAll}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateSetAddAll(MercuryDSLParser.UpdateSetAddAllContext ctx);
	/**
	 * Enter a parse tree produced by the {@code updateSetRemAll}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void enterUpdateSetRemAll(MercuryDSLParser.UpdateSetRemAllContext ctx);
	/**
	 * Exit a parse tree produced by the {@code updateSetRemAll}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 */
	void exitUpdateSetRemAll(MercuryDSLParser.UpdateSetRemAllContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commSend}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 */
	void enterCommSend(MercuryDSLParser.CommSendContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commSend}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 */
	void exitCommSend(MercuryDSLParser.CommSendContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commSendWithoutID}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 */
	void enterCommSendWithoutID(MercuryDSLParser.CommSendWithoutIDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commSendWithoutID}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 */
	void exitCommSendWithoutID(MercuryDSLParser.CommSendWithoutIDContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commBroadcast}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 */
	void enterCommBroadcast(MercuryDSLParser.CommBroadcastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commBroadcast}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 */
	void exitCommBroadcast(MercuryDSLParser.CommBroadcastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code msgId}
	 * labeled alternative in {@link MercuryDSLParser#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsgId(MercuryDSLParser.MsgIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code msgId}
	 * labeled alternative in {@link MercuryDSLParser#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsgId(MercuryDSLParser.MsgIdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code msgIdVal}
	 * labeled alternative in {@link MercuryDSLParser#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsgIdVal(MercuryDSLParser.MsgIdValContext ctx);
	/**
	 * Exit a parse tree produced by the {@code msgIdVal}
	 * labeled alternative in {@link MercuryDSLParser#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsgIdVal(MercuryDSLParser.MsgIdValContext ctx);
	/**
	 * Enter a parse tree produced by the {@code controlIf}
	 * labeled alternative in {@link MercuryDSLParser#controlStmt}.
	 * @param ctx the parse tree
	 */
	void enterControlIf(MercuryDSLParser.ControlIfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code controlIf}
	 * labeled alternative in {@link MercuryDSLParser#controlStmt}.
	 * @param ctx the parse tree
	 */
	void exitControlIf(MercuryDSLParser.ControlIfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code controlGoto}
	 * labeled alternative in {@link MercuryDSLParser#controlStmt}.
	 * @param ctx the parse tree
	 */
	void enterControlGoto(MercuryDSLParser.ControlGotoContext ctx);
	/**
	 * Exit a parse tree produced by the {@code controlGoto}
	 * labeled alternative in {@link MercuryDSLParser#controlStmt}.
	 * @param ctx the parse tree
	 */
	void exitControlGoto(MercuryDSLParser.ControlGotoContext ctx);
	/**
	 * Enter a parse tree produced by the {@code locName}
	 * labeled alternative in {@link MercuryDSLParser#targetLocation}.
	 * @param ctx the parse tree
	 */
	void enterLocName(MercuryDSLParser.LocNameContext ctx);
	/**
	 * Exit a parse tree produced by the {@code locName}
	 * labeled alternative in {@link MercuryDSLParser#targetLocation}.
	 * @param ctx the parse tree
	 */
	void exitLocName(MercuryDSLParser.LocNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code locHole}
	 * labeled alternative in {@link MercuryDSLParser#targetLocation}.
	 * @param ctx the parse tree
	 */
	void enterLocHole(MercuryDSLParser.LocHoleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code locHole}
	 * labeled alternative in {@link MercuryDSLParser#targetLocation}.
	 * @param ctx the parse tree
	 */
	void exitLocHole(MercuryDSLParser.LocHoleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iexpID}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 */
	void enterIexpID(MercuryDSLParser.IexpIDContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iexpID}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 */
	void exitIexpID(MercuryDSLParser.IexpIDContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iexpIdDotId}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 */
	void enterIexpIdDotId(MercuryDSLParser.IexpIdDotIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iexpIdDotId}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 */
	void exitIexpIdDotId(MercuryDSLParser.IexpIdDotIdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iexpSelf}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 */
	void enterIexpSelf(MercuryDSLParser.IexpSelfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iexpSelf}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 */
	void exitIexpSelf(MercuryDSLParser.IexpSelfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpScalar}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpScalar(MercuryDSLParser.NexpScalarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpScalar}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpScalar(MercuryDSLParser.NexpScalarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpMul}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpMul(MercuryDSLParser.NexpMulContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpMul}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpMul(MercuryDSLParser.NexpMulContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpParen}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpParen(MercuryDSLParser.NexpParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpParen}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpParen(MercuryDSLParser.NexpParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpIDNormal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpIDNormal(MercuryDSLParser.NexpIDNormalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpIDNormal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpIDNormal(MercuryDSLParser.NexpIDNormalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpHole}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpHole(MercuryDSLParser.NexpHoleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpHole}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpHole(MercuryDSLParser.NexpHoleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpDiv}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpDiv(MercuryDSLParser.NexpDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpDiv}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpDiv(MercuryDSLParser.NexpDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpAdd}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpAdd(MercuryDSLParser.NexpAddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpAdd}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpAdd(MercuryDSLParser.NexpAddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpSub}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpSub(MercuryDSLParser.NexpSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpSub}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpSub(MercuryDSLParser.NexpSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpIDVal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpIDVal(MercuryDSLParser.NexpIDValContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpIDVal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpIDVal(MercuryDSLParser.NexpIDValContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nexpIDWVal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void enterNexpIDWVal(MercuryDSLParser.NexpIDWValContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nexpIDWVal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 */
	void exitNexpIDWVal(MercuryDSLParser.NexpIDWValContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setexpParen}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void enterSetexpParen(MercuryDSLParser.SetexpParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setexpParen}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void exitSetexpParen(MercuryDSLParser.SetexpParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setexpId}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void enterSetexpId(MercuryDSLParser.SetexpIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setexpId}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void exitSetexpId(MercuryDSLParser.SetexpIdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setexpAll}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void enterSetexpAll(MercuryDSLParser.SetexpAllContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setexpAll}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void exitSetexpAll(MercuryDSLParser.SetexpAllContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setexpEmpty}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void enterSetexpEmpty(MercuryDSLParser.SetexpEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setexpEmpty}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void exitSetexpEmpty(MercuryDSLParser.SetexpEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setexpWinners}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void enterSetexpWinners(MercuryDSLParser.SetexpWinnersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setexpWinners}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void exitSetexpWinners(MercuryDSLParser.SetexpWinnersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setexpLosers}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void enterSetexpLosers(MercuryDSLParser.SetexpLosersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setexpLosers}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 */
	void exitSetexpLosers(MercuryDSLParser.SetexpLosersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpEqB}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpEqB(MercuryDSLParser.BexpEqBContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpEqB}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpEqB(MercuryDSLParser.BexpEqBContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpHole}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpHole(MercuryDSLParser.BexpHoleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpHole}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpHole(MercuryDSLParser.BexpHoleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpComp}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpComp(MercuryDSLParser.BexpCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpComp}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpComp(MercuryDSLParser.BexpCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpFalse}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpFalse(MercuryDSLParser.BexpFalseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpFalse}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpFalse(MercuryDSLParser.BexpFalseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpNot}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpNot(MercuryDSLParser.BexpNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpNot}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpNot(MercuryDSLParser.BexpNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpParen}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpParen(MercuryDSLParser.BexpParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpParen}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpParen(MercuryDSLParser.BexpParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpAnd}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpAnd(MercuryDSLParser.BexpAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpAnd}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpAnd(MercuryDSLParser.BexpAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpTrue}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpTrue(MercuryDSLParser.BexpTrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpTrue}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpTrue(MercuryDSLParser.BexpTrueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpEqI}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpEqI(MercuryDSLParser.BexpEqIContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpEqI}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpEqI(MercuryDSLParser.BexpEqIContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bexpOr}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void enterBexpOr(MercuryDSLParser.BexpOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bexpOr}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 */
	void exitBexpOr(MercuryDSLParser.BexpOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#cmpop}.
	 * @param ctx the parse tree
	 */
	void enterCmpop(MercuryDSLParser.CmpopContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#cmpop}.
	 * @param ctx the parse tree
	 */
	void exitCmpop(MercuryDSLParser.CmpopContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#eqop}.
	 * @param ctx the parse tree
	 */
	void enterEqop(MercuryDSLParser.EqopContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#eqop}.
	 * @param ctx the parse tree
	 */
	void exitEqop(MercuryDSLParser.EqopContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#scalar}.
	 * @param ctx the parse tree
	 */
	void enterScalar(MercuryDSLParser.ScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#scalar}.
	 * @param ctx the parse tree
	 */
	void exitScalar(MercuryDSLParser.ScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#ihole}.
	 * @param ctx the parse tree
	 */
	void enterIhole(MercuryDSLParser.IholeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#ihole}.
	 * @param ctx the parse tree
	 */
	void exitIhole(MercuryDSLParser.IholeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#bhole}.
	 * @param ctx the parse tree
	 */
	void enterBhole(MercuryDSLParser.BholeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#bhole}.
	 * @param ctx the parse tree
	 */
	void exitBhole(MercuryDSLParser.BholeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#chole}.
	 * @param ctx the parse tree
	 */
	void enterChole(MercuryDSLParser.CholeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#chole}.
	 * @param ctx the parse tree
	 */
	void exitChole(MercuryDSLParser.CholeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MercuryDSLParser#lhole}.
	 * @param ctx the parse tree
	 */
	void enterLhole(MercuryDSLParser.LholeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MercuryDSLParser#lhole}.
	 * @param ctx the parse tree
	 */
	void exitLhole(MercuryDSLParser.LholeContext ctx);
}