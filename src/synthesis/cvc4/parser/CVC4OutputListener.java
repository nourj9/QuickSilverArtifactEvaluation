// Generated from CVC4Output.g4 by ANTLR 4.8

package synthesis.cvc4.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CVC4OutputParser}.
 */
public interface CVC4OutputListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#response}.
	 * @param ctx the parse tree
	 */
	void enterResponse(CVC4OutputParser.ResponseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#response}.
	 * @param ctx the parse tree
	 */
	void exitResponse(CVC4OutputParser.ResponseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code resModels}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void enterResModels(CVC4OutputParser.ResModelsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code resModels}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void exitResModels(CVC4OutputParser.ResModelsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code resUnkown}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void enterResUnkown(CVC4OutputParser.ResUnkownContext ctx);
	/**
	 * Exit a parse tree produced by the {@code resUnkown}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void exitResUnkown(CVC4OutputParser.ResUnkownContext ctx);
	/**
	 * Enter a parse tree produced by the {@code resMemout}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void enterResMemout(CVC4OutputParser.ResMemoutContext ctx);
	/**
	 * Exit a parse tree produced by the {@code resMemout}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void exitResMemout(CVC4OutputParser.ResMemoutContext ctx);
	/**
	 * Enter a parse tree produced by the {@code resError}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void enterResError(CVC4OutputParser.ResErrorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code resError}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void exitResError(CVC4OutputParser.ResErrorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code resSuperError}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void enterResSuperError(CVC4OutputParser.ResSuperErrorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code resSuperError}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 */
	void exitResSuperError(CVC4OutputParser.ResSuperErrorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#s_expr}.
	 * @param ctx the parse tree
	 */
	void enterS_expr(CVC4OutputParser.S_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#s_expr}.
	 * @param ctx the parse tree
	 */
	void exitS_expr(CVC4OutputParser.S_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#model_response}.
	 * @param ctx the parse tree
	 */
	void enterModel_response(CVC4OutputParser.Model_responseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#model_response}.
	 * @param ctx the parse tree
	 */
	void exitModel_response(CVC4OutputParser.Model_responseContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#sorted_var}.
	 * @param ctx the parse tree
	 */
	void enterSorted_var(CVC4OutputParser.Sorted_varContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#sorted_var}.
	 * @param ctx the parse tree
	 */
	void exitSorted_var(CVC4OutputParser.Sorted_varContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#sort}.
	 * @param ctx the parse tree
	 */
	void enterSort(CVC4OutputParser.SortContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#sort}.
	 * @param ctx the parse tree
	 */
	void exitSort(CVC4OutputParser.SortContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termGeneral}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTermGeneral(CVC4OutputParser.TermGeneralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termGeneral}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTermGeneral(CVC4OutputParser.TermGeneralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termLiteral}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTermLiteral(CVC4OutputParser.TermLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termLiteral}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTermLiteral(CVC4OutputParser.TermLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termSymbol}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTermSymbol(CVC4OutputParser.TermSymbolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termSymbol}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTermSymbol(CVC4OutputParser.TermSymbolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code litInt}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLitInt(CVC4OutputParser.LitIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code litInt}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLitInt(CVC4OutputParser.LitIntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code litBool}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLitBool(CVC4OutputParser.LitBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code litBool}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLitBool(CVC4OutputParser.LitBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code litN}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLitN(CVC4OutputParser.LitNContext ctx);
	/**
	 * Exit a parse tree produced by the {@code litN}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLitN(CVC4OutputParser.LitNContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#intConst}.
	 * @param ctx the parse tree
	 */
	void enterIntConst(CVC4OutputParser.IntConstContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#intConst}.
	 * @param ctx the parse tree
	 */
	void exitIntConst(CVC4OutputParser.IntConstContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#boolConst}.
	 * @param ctx the parse tree
	 */
	void enterBoolConst(CVC4OutputParser.BoolConstContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#boolConst}.
	 * @param ctx the parse tree
	 */
	void exitBoolConst(CVC4OutputParser.BoolConstContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#symbol}.
	 * @param ctx the parse tree
	 */
	void enterSymbol(CVC4OutputParser.SymbolContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#symbol}.
	 * @param ctx the parse tree
	 */
	void exitSymbol(CVC4OutputParser.SymbolContext ctx);
	/**
	 * Enter a parse tree produced by {@link CVC4OutputParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(CVC4OutputParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link CVC4OutputParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(CVC4OutputParser.StringContext ctx);
}