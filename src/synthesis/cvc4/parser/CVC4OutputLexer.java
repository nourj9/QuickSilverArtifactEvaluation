// Generated from CVC4Output.g4 by ANTLR 4.8

package synthesis.cvc4.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CVC4OutputLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, WS=12, Id=13, INTEGER=14, StringInsides=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "WS", "Id", "INTEGER", "StringInsides", "PrintableCharNoDquote", 
			"EscapedSpace", "WhiteSpaceChar", "Sym", "Digit"
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


	public CVC4OutputLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CVC4Output.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\21\u008c\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\6\rf"+
		"\n\r\r\r\16\rg\3\r\3\r\3\16\3\16\3\16\7\16o\n\16\f\16\16\16r\13\16\3\17"+
		"\5\17u\n\17\3\17\6\17x\n\17\r\17\16\17y\3\20\3\20\5\20~\n\20\3\21\3\21"+
		"\5\21\u0082\n\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\2\2\26\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\2#\2%\2\'\2)\2\3\2\6\5\2\13\f\17\17\"\"\3\2\62;\5\2\"#%\u0080\u0082"+
		"\1\n\2##&(,-/\61>\\`ac|\u0080\u0080\2\u008d\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\3+\3\2\2\2\5\61\3\2\2\2\79\3\2\2\2\t@\3\2\2"+
		"\2\13B\3\2\2\2\rH\3\2\2\2\17J\3\2\2\2\21U\3\2\2\2\23W\3\2\2\2\25\\\3\2"+
		"\2\2\27b\3\2\2\2\31e\3\2\2\2\33k\3\2\2\2\35t\3\2\2\2\37}\3\2\2\2!\u0081"+
		"\3\2\2\2#\u0083\3\2\2\2%\u0086\3\2\2\2\'\u0088\3\2\2\2)\u008a\3\2\2\2"+
		"+,\7w\2\2,-\7p\2\2-.\7u\2\2./\7c\2\2/\60\7v\2\2\60\4\3\2\2\2\61\62\7w"+
		"\2\2\62\63\7p\2\2\63\64\7m\2\2\64\65\7p\2\2\65\66\7q\2\2\66\67\7y\2\2"+
		"\678\7p\2\28\6\3\2\2\29:\7o\2\2:;\7g\2\2;<\7o\2\2<=\7q\2\2=>\7w\2\2>?"+
		"\7v\2\2?\b\3\2\2\2@A\7*\2\2A\n\3\2\2\2BC\7g\2\2CD\7t\2\2DE\7t\2\2EF\7"+
		"q\2\2FG\7t\2\2G\f\3\2\2\2HI\7+\2\2I\16\3\2\2\2JK\7f\2\2KL\7g\2\2LM\7h"+
		"\2\2MN\7k\2\2NO\7p\2\2OP\7g\2\2PQ\7/\2\2QR\7h\2\2RS\7w\2\2ST\7p\2\2T\20"+
		"\3\2\2\2UV\7P\2\2V\22\3\2\2\2WX\7v\2\2XY\7t\2\2YZ\7w\2\2Z[\7g\2\2[\24"+
		"\3\2\2\2\\]\7h\2\2]^\7c\2\2^_\7n\2\2_`\7u\2\2`a\7g\2\2a\26\3\2\2\2bc\7"+
		"$\2\2c\30\3\2\2\2df\t\2\2\2ed\3\2\2\2fg\3\2\2\2ge\3\2\2\2gh\3\2\2\2hi"+
		"\3\2\2\2ij\b\r\2\2j\32\3\2\2\2kp\5\'\24\2lo\5)\25\2mo\5\'\24\2nl\3\2\2"+
		"\2nm\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2q\34\3\2\2\2rp\3\2\2\2su\7/"+
		"\2\2ts\3\2\2\2tu\3\2\2\2uw\3\2\2\2vx\t\3\2\2wv\3\2\2\2xy\3\2\2\2yw\3\2"+
		"\2\2yz\3\2\2\2z\36\3\2\2\2{~\5!\21\2|~\5%\23\2}{\3\2\2\2}|\3\2\2\2~ \3"+
		"\2\2\2\177\u0082\t\4\2\2\u0080\u0082\5#\22\2\u0081\177\3\2\2\2\u0081\u0080"+
		"\3\2\2\2\u0082\"\3\2\2\2\u0083\u0084\7$\2\2\u0084\u0085\7$\2\2\u0085$"+
		"\3\2\2\2\u0086\u0087\t\2\2\2\u0087&\3\2\2\2\u0088\u0089\t\5\2\2\u0089"+
		"(\3\2\2\2\u008a\u008b\t\3\2\2\u008b*\3\2\2\2\n\2gnpty}\u0081\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}