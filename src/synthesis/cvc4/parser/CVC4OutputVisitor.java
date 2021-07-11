// Generated from CVC4Output.g4 by ANTLR 4.8

package synthesis.cvc4.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CVC4OutputParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CVC4OutputVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponse(CVC4OutputParser.ResponseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code resModels}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResModels(CVC4OutputParser.ResModelsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code resUnkown}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResUnkown(CVC4OutputParser.ResUnkownContext ctx);
	/**
	 * Visit a parse tree produced by the {@code resMemout}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResMemout(CVC4OutputParser.ResMemoutContext ctx);
	/**
	 * Visit a parse tree produced by the {@code resError}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResError(CVC4OutputParser.ResErrorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code resSuperError}
	 * labeled alternative in {@link CVC4OutputParser#general_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResSuperError(CVC4OutputParser.ResSuperErrorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#s_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitS_expr(CVC4OutputParser.S_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#model_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel_response(CVC4OutputParser.Model_responseContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#sorted_var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSorted_var(CVC4OutputParser.Sorted_varContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#sort}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSort(CVC4OutputParser.SortContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termGeneral}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermGeneral(CVC4OutputParser.TermGeneralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termLiteral}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermLiteral(CVC4OutputParser.TermLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termSymbol}
	 * labeled alternative in {@link CVC4OutputParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermSymbol(CVC4OutputParser.TermSymbolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code litInt}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLitInt(CVC4OutputParser.LitIntContext ctx);
	/**
	 * Visit a parse tree produced by the {@code litBool}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLitBool(CVC4OutputParser.LitBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code litN}
	 * labeled alternative in {@link CVC4OutputParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLitN(CVC4OutputParser.LitNContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#intConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntConst(CVC4OutputParser.IntConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#boolConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolConst(CVC4OutputParser.BoolConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#symbol}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol(CVC4OutputParser.SymbolContext ctx);
	/**
	 * Visit a parse tree produced by {@link CVC4OutputParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(CVC4OutputParser.StringContext ctx);
}