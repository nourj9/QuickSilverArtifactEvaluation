// Generated from MercuryDSL.g4 by ANTLR 4.8

package antlr.parsing;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MercuryDSLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MercuryDSLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MercuryDSLParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#crashes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCrashes(MercuryDSLParser.CrashesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpec(MercuryDSLParser.SpecContext ctx);
	/**
	 * Visit a parse tree produced by the {@code safetySpecAtMost}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSafetySpecAtMost(MercuryDSLParser.SafetySpecAtMostContext ctx);
	/**
	 * Visit a parse tree produced by the {@code safetySpecParen}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSafetySpecParen(MercuryDSLParser.SafetySpecParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code safetySpecAnd}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSafetySpecAnd(MercuryDSLParser.SafetySpecAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code safetySpecAtLeast}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSafetySpecAtLeast(MercuryDSLParser.SafetySpecAtLeastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code safetySpecOr}
	 * labeled alternative in {@link MercuryDSLParser#safety_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSafetySpecOr(MercuryDSLParser.SafetySpecOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#stateDescList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateDescList(MercuryDSLParser.StateDescListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#stateDesc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateDesc(MercuryDSLParser.StateDescContext ctx);
	/**
	 * Visit a parse tree produced by the {@code livenessSpecEventually}
	 * labeled alternative in {@link MercuryDSLParser#liveness_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLivenessSpecEventually(MercuryDSLParser.LivenessSpecEventuallyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code livenessSpecReactive}
	 * labeled alternative in {@link MercuryDSLParser#liveness_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLivenessSpecReactive(MercuryDSLParser.LivenessSpecReactiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code livpredSDlist}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLivpredSDlist(MercuryDSLParser.LivpredSDlistContext ctx);
	/**
	 * Visit a parse tree produced by the {@code livpredReceived}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLivpredReceived(MercuryDSLParser.LivpredReceivedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code livpredSent}
	 * labeled alternative in {@link MercuryDSLParser#livpred}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLivpredSent(MercuryDSLParser.LivpredSentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(MercuryDSLParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#id_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId_var(MercuryDSLParser.Id_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#int_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_var(MercuryDSLParser.Int_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#set_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet_var(MercuryDSLParser.Set_varContext ctx);
	/**
	 * Visit a parse tree produced by the {@code actionBr}
	 * labeled alternative in {@link MercuryDSLParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionBr(MercuryDSLParser.ActionBrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code actionRz}
	 * labeled alternative in {@link MercuryDSLParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionRz(MercuryDSLParser.ActionRzContext ctx);
	/**
	 * Visit a parse tree produced by the {@code domainUnit}
	 * labeled alternative in {@link MercuryDSLParser#domain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomainUnit(MercuryDSLParser.DomainUnitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code domainIntwLowerBound}
	 * labeled alternative in {@link MercuryDSLParser#domain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomainIntwLowerBound(MercuryDSLParser.DomainIntwLowerBoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#loc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoc(MercuryDSLParser.LocContext ctx);
	/**
	 * Visit a parse tree produced by the {@code handlerNormal}
	 * labeled alternative in {@link MercuryDSLParser#handler}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHandlerNormal(MercuryDSLParser.HandlerNormalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code handlerPassive}
	 * labeled alternative in {@link MercuryDSLParser#handler}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHandlerPassive(MercuryDSLParser.HandlerPassiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code reactDo}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReactDo(MercuryDSLParser.ReactDoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code reactPredicate}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReactPredicate(MercuryDSLParser.ReactPredicateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code reactReply}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReactReply(MercuryDSLParser.ReactReplyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code reactPartCons}
	 * labeled alternative in {@link MercuryDSLParser#react}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReactPartCons(MercuryDSLParser.ReactPartConsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#identifierList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierList(MercuryDSLParser.IdentifierListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eventValcons}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventValcons(MercuryDSLParser.EventValconsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eventPartCons}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventPartCons(MercuryDSLParser.EventPartConsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eventEpsilon}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventEpsilon(MercuryDSLParser.EventEpsilonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eventMsg}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventMsg(MercuryDSLParser.EventMsgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eventPassive}
	 * labeled alternative in {@link MercuryDSLParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventPassive(MercuryDSLParser.EventPassiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code identifier}
	 * labeled alternative in {@link MercuryDSLParser#prop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(MercuryDSLParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code noprop}
	 * labeled alternative in {@link MercuryDSLParser#prop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNoprop(MercuryDSLParser.NopropContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cardConst}
	 * labeled alternative in {@link MercuryDSLParser#cardinality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCardConst(MercuryDSLParser.CardConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cardHole}
	 * labeled alternative in {@link MercuryDSLParser#cardinality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCardHole(MercuryDSLParser.CardHoleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(MercuryDSLParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateAssignNexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateAssignNexp(MercuryDSLParser.UpdateAssignNexpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateAssignIexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateAssignIexp(MercuryDSLParser.UpdateAssignIexpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateAssignStexp}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateAssignStexp(MercuryDSLParser.UpdateAssignStexpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateSetAdd}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateSetAdd(MercuryDSLParser.UpdateSetAddContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateSetRem}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateSetRem(MercuryDSLParser.UpdateSetRemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateSetAddAll}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateSetAddAll(MercuryDSLParser.UpdateSetAddAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code updateSetRemAll}
	 * labeled alternative in {@link MercuryDSLParser#updateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateSetRemAll(MercuryDSLParser.UpdateSetRemAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commSend}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommSend(MercuryDSLParser.CommSendContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commSendWithoutID}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommSendWithoutID(MercuryDSLParser.CommSendWithoutIDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commBroadcast}
	 * labeled alternative in {@link MercuryDSLParser#sendStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommBroadcast(MercuryDSLParser.CommBroadcastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code msgId}
	 * labeled alternative in {@link MercuryDSLParser#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgId(MercuryDSLParser.MsgIdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code msgIdVal}
	 * labeled alternative in {@link MercuryDSLParser#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsgIdVal(MercuryDSLParser.MsgIdValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code controlIf}
	 * labeled alternative in {@link MercuryDSLParser#controlStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitControlIf(MercuryDSLParser.ControlIfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code controlGoto}
	 * labeled alternative in {@link MercuryDSLParser#controlStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitControlGoto(MercuryDSLParser.ControlGotoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code locName}
	 * labeled alternative in {@link MercuryDSLParser#targetLocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocName(MercuryDSLParser.LocNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code locHole}
	 * labeled alternative in {@link MercuryDSLParser#targetLocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocHole(MercuryDSLParser.LocHoleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code iexpID}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIexpID(MercuryDSLParser.IexpIDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code iexpIdDotId}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIexpIdDotId(MercuryDSLParser.IexpIdDotIdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code iexpSelf}
	 * labeled alternative in {@link MercuryDSLParser#iexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIexpSelf(MercuryDSLParser.IexpSelfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpScalar}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpScalar(MercuryDSLParser.NexpScalarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpMul}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpMul(MercuryDSLParser.NexpMulContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpParen}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpParen(MercuryDSLParser.NexpParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpIDNormal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpIDNormal(MercuryDSLParser.NexpIDNormalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpHole}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpHole(MercuryDSLParser.NexpHoleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpDiv}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpDiv(MercuryDSLParser.NexpDivContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpAdd}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpAdd(MercuryDSLParser.NexpAddContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpSub}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpSub(MercuryDSLParser.NexpSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpIDVal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpIDVal(MercuryDSLParser.NexpIDValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nexpIDWVal}
	 * labeled alternative in {@link MercuryDSLParser#nexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNexpIDWVal(MercuryDSLParser.NexpIDWValContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setexpParen}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetexpParen(MercuryDSLParser.SetexpParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setexpId}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetexpId(MercuryDSLParser.SetexpIdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setexpAll}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetexpAll(MercuryDSLParser.SetexpAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setexpEmpty}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetexpEmpty(MercuryDSLParser.SetexpEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setexpWinners}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetexpWinners(MercuryDSLParser.SetexpWinnersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setexpLosers}
	 * labeled alternative in {@link MercuryDSLParser#stexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetexpLosers(MercuryDSLParser.SetexpLosersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpEqB}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpEqB(MercuryDSLParser.BexpEqBContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpHole}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpHole(MercuryDSLParser.BexpHoleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpComp}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpComp(MercuryDSLParser.BexpCompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpFalse}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpFalse(MercuryDSLParser.BexpFalseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpNot}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpNot(MercuryDSLParser.BexpNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpParen}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpParen(MercuryDSLParser.BexpParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpAnd}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpAnd(MercuryDSLParser.BexpAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpTrue}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpTrue(MercuryDSLParser.BexpTrueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpEqI}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpEqI(MercuryDSLParser.BexpEqIContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bexpOr}
	 * labeled alternative in {@link MercuryDSLParser#bexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBexpOr(MercuryDSLParser.BexpOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#cmpop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmpop(MercuryDSLParser.CmpopContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#eqop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqop(MercuryDSLParser.EqopContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#scalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalar(MercuryDSLParser.ScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#ihole}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIhole(MercuryDSLParser.IholeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#bhole}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBhole(MercuryDSLParser.BholeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#chole}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChole(MercuryDSLParser.CholeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MercuryDSLParser#lhole}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLhole(MercuryDSLParser.LholeContext ctx);
}