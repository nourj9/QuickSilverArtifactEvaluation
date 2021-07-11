// Generated from CVC4Output.g4 by ANTLR 4.8

package synthesis.cvc4.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CVC4OutputParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, WS=12, Id=13, INTEGER=14, StringInsides=15;
	public static final int
		RULE_response = 0, RULE_general_response = 1, RULE_s_expr = 2, RULE_model_response = 3, 
		RULE_sorted_var = 4, RULE_sort = 5, RULE_term = 6, RULE_literal = 7, RULE_intConst = 8, 
		RULE_boolConst = 9, RULE_symbol = 10, RULE_string = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"response", "general_response", "s_expr", "model_response", "sorted_var", 
			"sort", "term", "literal", "intConst", "boolConst", "symbol", "string"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'unsat'", "'unknown'", "'memout'", "'('", "'error'", "')'", "'define-fun'", 
			"'N'", "'true'", "'false'", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"WS", "Id", "INTEGER", "StringInsides"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "CVC4Output.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CVC4OutputParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ResponseContext extends ParserRuleContext {
		public General_responseContext general_response() {
			return getRuleContext(General_responseContext.class,0);
		}
		public TerminalNode EOF() { return getToken(CVC4OutputParser.EOF, 0); }
		public ResponseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_response; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterResponse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitResponse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitResponse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResponseContext response() throws RecognitionException {
		ResponseContext _localctx = new ResponseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_response);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			general_response();
			setState(25);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class General_responseContext extends ParserRuleContext {
		public General_responseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_response; }
	 
		public General_responseContext() { }
		public void copyFrom(General_responseContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ResErrorContext extends General_responseContext {
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public ResErrorContext(General_responseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterResError(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitResError(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitResError(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ResModelsContext extends General_responseContext {
		public List<Model_responseContext> model_response() {
			return getRuleContexts(Model_responseContext.class);
		}
		public Model_responseContext model_response(int i) {
			return getRuleContext(Model_responseContext.class,i);
		}
		public ResModelsContext(General_responseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterResModels(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitResModels(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitResModels(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ResSuperErrorContext extends General_responseContext {
		public S_exprContext s_expr() {
			return getRuleContext(S_exprContext.class,0);
		}
		public ResSuperErrorContext(General_responseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterResSuperError(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitResSuperError(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitResSuperError(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ResUnkownContext extends General_responseContext {
		public ResUnkownContext(General_responseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterResUnkown(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitResUnkown(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitResUnkown(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ResMemoutContext extends General_responseContext {
		public ResMemoutContext(General_responseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterResMemout(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitResMemout(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitResMemout(this);
			else return visitor.visitChildren(this);
		}
	}

	public final General_responseContext general_response() throws RecognitionException {
		General_responseContext _localctx = new General_responseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_general_response);
		int _la;
		try {
			setState(42);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new ResModelsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(27);
				match(T__0);
				setState(31);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(28);
					model_response();
					}
					}
					setState(33);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new ResUnkownContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(34);
				match(T__1);
				}
				break;
			case 3:
				_localctx = new ResMemoutContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(35);
				match(T__2);
				}
				break;
			case 4:
				_localctx = new ResErrorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(36);
				match(T__3);
				setState(37);
				match(T__4);
				setState(38);
				string();
				setState(39);
				match(T__5);
				}
				break;
			case 5:
				_localctx = new ResSuperErrorContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(41);
				s_expr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class S_exprContext extends ParserRuleContext {
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public List<S_exprContext> s_expr() {
			return getRuleContexts(S_exprContext.class);
		}
		public S_exprContext s_expr(int i) {
			return getRuleContext(S_exprContext.class,i);
		}
		public S_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_s_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterS_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitS_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitS_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final S_exprContext s_expr() throws RecognitionException {
		S_exprContext _localctx = new S_exprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_s_expr);
		int _la;
		try {
			setState(53);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Id:
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				symbol();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(45);
				match(T__3);
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3 || _la==Id) {
					{
					{
					setState(46);
					s_expr();
					}
					}
					setState(51);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(52);
				match(T__5);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Model_responseContext extends ParserRuleContext {
		public SymbolContext funName;
		public TermContext funBody;
		public SortContext sort() {
			return getRuleContext(SortContext.class,0);
		}
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public List<Sorted_varContext> sorted_var() {
			return getRuleContexts(Sorted_varContext.class);
		}
		public Sorted_varContext sorted_var(int i) {
			return getRuleContext(Sorted_varContext.class,i);
		}
		public Model_responseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model_response; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterModel_response(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitModel_response(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitModel_response(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Model_responseContext model_response() throws RecognitionException {
		Model_responseContext _localctx = new Model_responseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_model_response);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(T__3);
			setState(56);
			match(T__6);
			setState(57);
			((Model_responseContext)_localctx).funName = symbol();
			setState(58);
			match(T__3);
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(59);
				sorted_var();
				}
				}
				setState(64);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(65);
			match(T__5);
			setState(66);
			sort();
			setState(67);
			((Model_responseContext)_localctx).funBody = term();
			setState(68);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sorted_varContext extends ParserRuleContext {
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public SortContext sort() {
			return getRuleContext(SortContext.class,0);
		}
		public Sorted_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sorted_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterSorted_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitSorted_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitSorted_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sorted_varContext sorted_var() throws RecognitionException {
		Sorted_varContext _localctx = new Sorted_varContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_sorted_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(T__3);
			setState(71);
			symbol();
			setState(72);
			sort();
			setState(73);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SortContext extends ParserRuleContext {
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public SortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterSort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitSort(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitSort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SortContext sort() throws RecognitionException {
		SortContext _localctx = new SortContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_sort);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			symbol();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
	 
		public TermContext() { }
		public void copyFrom(TermContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TermSymbolContext extends TermContext {
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public TermSymbolContext(TermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterTermSymbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitTermSymbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitTermSymbol(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TermGeneralContext extends TermContext {
		public SymbolContext op;
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public TermGeneralContext(TermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterTermGeneral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitTermGeneral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitTermGeneral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TermLiteralContext extends TermContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TermLiteralContext(TermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterTermLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitTermLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitTermLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_term);
		int _la;
		try {
			setState(88);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				_localctx = new TermGeneralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(77);
				match(T__3);
				setState(78);
				((TermGeneralContext)_localctx).op = symbol();
				setState(80); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(79);
					term();
					}
					}
					setState(82); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << Id) | (1L << INTEGER))) != 0) );
				setState(84);
				match(T__5);
				}
				break;
			case T__7:
			case T__8:
			case T__9:
			case INTEGER:
				_localctx = new TermLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(86);
				literal();
				}
				break;
			case Id:
				_localctx = new TermSymbolContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(87);
				symbol();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	 
		public LiteralContext() { }
		public void copyFrom(LiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LitIntContext extends LiteralContext {
		public IntConstContext intConst() {
			return getRuleContext(IntConstContext.class,0);
		}
		public LitIntContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterLitInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitLitInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitLitInt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LitBoolContext extends LiteralContext {
		public BoolConstContext boolConst() {
			return getRuleContext(BoolConstContext.class,0);
		}
		public LitBoolContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterLitBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitLitBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitLitBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LitNContext extends LiteralContext {
		public LitNContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterLitN(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitLitN(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitLitN(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_literal);
		try {
			setState(93);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INTEGER:
				_localctx = new LitIntContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				intConst();
				}
				break;
			case T__8:
			case T__9:
				_localctx = new LitBoolContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				boolConst();
				}
				break;
			case T__7:
				_localctx = new LitNContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(92);
				match(T__7);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntConstContext extends ParserRuleContext {
		public TerminalNode INTEGER() { return getToken(CVC4OutputParser.INTEGER, 0); }
		public IntConstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intConst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterIntConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitIntConst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitIntConst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntConstContext intConst() throws RecognitionException {
		IntConstContext _localctx = new IntConstContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_intConst);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(INTEGER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolConstContext extends ParserRuleContext {
		public BoolConstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolConst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterBoolConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitBoolConst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitBoolConst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolConstContext boolConst() throws RecognitionException {
		BoolConstContext _localctx = new BoolConstContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_boolConst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			_la = _input.LA(1);
			if ( !(_la==T__8 || _la==T__9) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SymbolContext extends ParserRuleContext {
		public TerminalNode Id() { return getToken(CVC4OutputParser.Id, 0); }
		public SymbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_symbol; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterSymbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitSymbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitSymbol(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SymbolContext symbol() throws RecognitionException {
		SymbolContext _localctx = new SymbolContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_symbol);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(Id);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringContext extends ParserRuleContext {
		public List<TerminalNode> StringInsides() { return getTokens(CVC4OutputParser.StringInsides); }
		public TerminalNode StringInsides(int i) {
			return getToken(CVC4OutputParser.StringInsides, i);
		}
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CVC4OutputListener ) ((CVC4OutputListener)listener).exitString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CVC4OutputVisitor ) return ((CVC4OutputVisitor<? extends T>)visitor).visitString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_string);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__10);
			setState(103); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(102);
				match(StringInsides);
				}
				}
				setState(105); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==StringInsides );
			setState(107);
			match(T__10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\21p\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\3\2\3\2\3\2\3\3\3\3\7\3 \n\3\f\3\16\3#\13\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\5\3-\n\3\3\4\3\4\3\4\7\4\62\n\4\f\4\16\4\65\13\4"+
		"\3\4\5\48\n\4\3\5\3\5\3\5\3\5\3\5\7\5?\n\5\f\5\16\5B\13\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\6\bS\n\b\r\b\16\bT\3"+
		"\b\3\b\3\b\3\b\5\b[\n\b\3\t\3\t\3\t\5\t`\n\t\3\n\3\n\3\13\3\13\3\f\3\f"+
		"\3\r\3\r\6\rj\n\r\r\r\16\rk\3\r\3\r\3\r\2\2\16\2\4\6\b\n\f\16\20\22\24"+
		"\26\30\2\3\3\2\13\f\2q\2\32\3\2\2\2\4,\3\2\2\2\6\67\3\2\2\2\b9\3\2\2\2"+
		"\nH\3\2\2\2\fM\3\2\2\2\16Z\3\2\2\2\20_\3\2\2\2\22a\3\2\2\2\24c\3\2\2\2"+
		"\26e\3\2\2\2\30g\3\2\2\2\32\33\5\4\3\2\33\34\7\2\2\3\34\3\3\2\2\2\35!"+
		"\7\3\2\2\36 \5\b\5\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\""+
		"-\3\2\2\2#!\3\2\2\2$-\7\4\2\2%-\7\5\2\2&\'\7\6\2\2\'(\7\7\2\2()\5\30\r"+
		"\2)*\7\b\2\2*-\3\2\2\2+-\5\6\4\2,\35\3\2\2\2,$\3\2\2\2,%\3\2\2\2,&\3\2"+
		"\2\2,+\3\2\2\2-\5\3\2\2\2.8\5\26\f\2/\63\7\6\2\2\60\62\5\6\4\2\61\60\3"+
		"\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\66\3\2\2\2\65\63\3"+
		"\2\2\2\668\7\b\2\2\67.\3\2\2\2\67/\3\2\2\28\7\3\2\2\29:\7\6\2\2:;\7\t"+
		"\2\2;<\5\26\f\2<@\7\6\2\2=?\5\n\6\2>=\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3"+
		"\2\2\2AC\3\2\2\2B@\3\2\2\2CD\7\b\2\2DE\5\f\7\2EF\5\16\b\2FG\7\b\2\2G\t"+
		"\3\2\2\2HI\7\6\2\2IJ\5\26\f\2JK\5\f\7\2KL\7\b\2\2L\13\3\2\2\2MN\5\26\f"+
		"\2N\r\3\2\2\2OP\7\6\2\2PR\5\26\f\2QS\5\16\b\2RQ\3\2\2\2ST\3\2\2\2TR\3"+
		"\2\2\2TU\3\2\2\2UV\3\2\2\2VW\7\b\2\2W[\3\2\2\2X[\5\20\t\2Y[\5\26\f\2Z"+
		"O\3\2\2\2ZX\3\2\2\2ZY\3\2\2\2[\17\3\2\2\2\\`\5\22\n\2]`\5\24\13\2^`\7"+
		"\n\2\2_\\\3\2\2\2_]\3\2\2\2_^\3\2\2\2`\21\3\2\2\2ab\7\20\2\2b\23\3\2\2"+
		"\2cd\t\2\2\2d\25\3\2\2\2ef\7\17\2\2f\27\3\2\2\2gi\7\r\2\2hj\7\21\2\2i"+
		"h\3\2\2\2jk\3\2\2\2ki\3\2\2\2kl\3\2\2\2lm\3\2\2\2mn\7\r\2\2n\31\3\2\2"+
		"\2\13!,\63\67@TZ_k";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}