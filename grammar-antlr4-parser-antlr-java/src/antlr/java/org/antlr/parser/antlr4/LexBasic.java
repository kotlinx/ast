// Generated from LexBasic.g4 by ANTLR 4.7.2
package org.antlr.parser.antlr4;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LexBasic extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Ws", "Hws", "Vws", "BlockComment", "DocComment", "LineComment", "EscSeq", 
			"EscAny", "UnicodeEsc", "DecimalNumeral", "HexDigit", "DecDigit", "BoolLiteral", 
			"CharLiteral", "SQuoteLiteral", "DQuoteLiteral", "USQuoteLiteral", "NameChar", 
			"NameStartChar", "Int", "Esc", "Colon", "DColon", "SQuote", "DQuote", 
			"LParen", "RParen", "LBrace", "RBrace", "LBrack", "RBrack", "RArrow", 
			"Lt", "Gt", "Equal", "Question", "Star", "Plus", "PlusAssign", "Underscore", 
			"Pipe", "Dollar", "Comma", "Semi", "Dot", "Range", "At", "Pound", "Tilde"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
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


	public LexBasic(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "LexBasic.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\2\u0130\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\3\2\3\2\5\2h\n\2"+
		"\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\7\5r\n\5\f\5\16\5u\13\5\3\5\3\5\3\5\5"+
		"\5z\n\5\3\6\3\6\3\6\3\6\3\6\7\6\u0081\n\6\f\6\16\6\u0084\13\6\3\6\3\6"+
		"\3\6\5\6\u0089\n\6\3\7\3\7\3\7\3\7\7\7\u008f\n\7\f\7\16\7\u0092\13\7\3"+
		"\b\3\b\3\b\3\b\3\b\5\b\u0099\n\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\5\n\u00a3"+
		"\n\n\5\n\u00a5\n\n\5\n\u00a7\n\n\5\n\u00a9\n\n\3\13\3\13\3\13\7\13\u00ae"+
		"\n\13\f\13\16\13\u00b1\13\13\5\13\u00b3\n\13\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00c2\n\16\3\17\3\17\3\17\5\17"+
		"\u00c7\n\17\3\17\3\17\3\20\3\20\3\20\7\20\u00ce\n\20\f\20\16\20\u00d1"+
		"\13\20\3\20\3\20\3\21\3\21\3\21\7\21\u00d8\n\21\f\21\16\21\u00db\13\21"+
		"\3\21\3\21\3\22\3\22\3\22\7\22\u00e2\n\22\f\22\16\22\u00e5\13\22\3\23"+
		"\3\23\3\23\3\23\5\23\u00eb\n\23\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26"+
		"\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&"+
		"\3&\3\'\3\'\3(\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3/\3\60"+
		"\3\60\3\61\3\61\3\62\3\62\4s\u0082\2\63\3\2\5\2\7\2\t\2\13\2\r\2\17\2"+
		"\21\2\23\2\25\2\27\2\31\2\33\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61\2"+
		"\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2"+
		"]\2_\2a\2c\2\3\2\r\4\2\13\13\"\"\4\2\f\f\16\17\4\2\f\f\17\17\n\2$$))^"+
		"^ddhhppttvv\3\2\63;\5\2\62;CHch\3\2\62;\6\2\f\f\17\17))^^\6\2\f\f\17\17"+
		"$$^^\5\2\u00b9\u00b9\u0302\u0371\u2041\u2042\17\2C\\c|\u00c2\u00d8\u00da"+
		"\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02"+
		"\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff\2\u0118\3g\3\2\2\2\5i\3\2\2"+
		"\2\7k\3\2\2\2\tm\3\2\2\2\13{\3\2\2\2\r\u008a\3\2\2\2\17\u0093\3\2\2\2"+
		"\21\u009a\3\2\2\2\23\u009d\3\2\2\2\25\u00b2\3\2\2\2\27\u00b4\3\2\2\2\31"+
		"\u00b6\3\2\2\2\33\u00c1\3\2\2\2\35\u00c3\3\2\2\2\37\u00ca\3\2\2\2!\u00d4"+
		"\3\2\2\2#\u00de\3\2\2\2%\u00ea\3\2\2\2\'\u00ec\3\2\2\2)\u00ee\3\2\2\2"+
		"+\u00f2\3\2\2\2-\u00f4\3\2\2\2/\u00f6\3\2\2\2\61\u00f9\3\2\2\2\63\u00fb"+
		"\3\2\2\2\65\u00fd\3\2\2\2\67\u00ff\3\2\2\29\u0101\3\2\2\2;\u0103\3\2\2"+
		"\2=\u0105\3\2\2\2?\u0107\3\2\2\2A\u0109\3\2\2\2C\u010c\3\2\2\2E\u010e"+
		"\3\2\2\2G\u0110\3\2\2\2I\u0112\3\2\2\2K\u0114\3\2\2\2M\u0116\3\2\2\2O"+
		"\u0118\3\2\2\2Q\u011b\3\2\2\2S\u011d\3\2\2\2U\u011f\3\2\2\2W\u0121\3\2"+
		"\2\2Y\u0123\3\2\2\2[\u0125\3\2\2\2]\u0127\3\2\2\2_\u012a\3\2\2\2a\u012c"+
		"\3\2\2\2c\u012e\3\2\2\2eh\5\5\3\2fh\5\7\4\2ge\3\2\2\2gf\3\2\2\2h\4\3\2"+
		"\2\2ij\t\2\2\2j\6\3\2\2\2kl\t\3\2\2l\b\3\2\2\2mn\7\61\2\2no\7,\2\2os\3"+
		"\2\2\2pr\13\2\2\2qp\3\2\2\2ru\3\2\2\2st\3\2\2\2sq\3\2\2\2ty\3\2\2\2us"+
		"\3\2\2\2vw\7,\2\2wz\7\61\2\2xz\7\2\2\3yv\3\2\2\2yx\3\2\2\2z\n\3\2\2\2"+
		"{|\7\61\2\2|}\7,\2\2}~\7,\2\2~\u0082\3\2\2\2\177\u0081\13\2\2\2\u0080"+
		"\177\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0083\3\2\2\2\u0082\u0080\3\2\2"+
		"\2\u0083\u0088\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7,\2\2\u0086\u0089"+
		"\7\61\2\2\u0087\u0089\7\2\2\3\u0088\u0085\3\2\2\2\u0088\u0087\3\2\2\2"+
		"\u0089\f\3\2\2\2\u008a\u008b\7\61\2\2\u008b\u008c\7\61\2\2\u008c\u0090"+
		"\3\2\2\2\u008d\u008f\n\4\2\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\16\3\2\2\2\u0092\u0090\3\2\2"+
		"\2\u0093\u0098\5+\26\2\u0094\u0099\t\5\2\2\u0095\u0099\5\23\n\2\u0096"+
		"\u0099\13\2\2\2\u0097\u0099\7\2\2\3\u0098\u0094\3\2\2\2\u0098\u0095\3"+
		"\2\2\2\u0098\u0096\3\2\2\2\u0098\u0097\3\2\2\2\u0099\20\3\2\2\2\u009a"+
		"\u009b\5+\26\2\u009b\u009c\13\2\2\2\u009c\22\3\2\2\2\u009d\u00a8\7w\2"+
		"\2\u009e\u00a6\5\27\f\2\u009f\u00a4\5\27\f\2\u00a0\u00a2\5\27\f\2\u00a1"+
		"\u00a3\5\27\f\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a5\3"+
		"\2\2\2\u00a4\u00a0\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6"+
		"\u009f\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8\u009e\3\2"+
		"\2\2\u00a8\u00a9\3\2\2\2\u00a9\24\3\2\2\2\u00aa\u00b3\7\62\2\2\u00ab\u00af"+
		"\t\6\2\2\u00ac\u00ae\5\31\r\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3\2\2\2"+
		"\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af"+
		"\3\2\2\2\u00b2\u00aa\3\2\2\2\u00b2\u00ab\3\2\2\2\u00b3\26\3\2\2\2\u00b4"+
		"\u00b5\t\7\2\2\u00b5\30\3\2\2\2\u00b6\u00b7\t\b\2\2\u00b7\32\3\2\2\2\u00b8"+
		"\u00b9\7v\2\2\u00b9\u00ba\7t\2\2\u00ba\u00bb\7w\2\2\u00bb\u00c2\7g\2\2"+
		"\u00bc\u00bd\7h\2\2\u00bd\u00be\7c\2\2\u00be\u00bf\7n\2\2\u00bf\u00c0"+
		"\7u\2\2\u00c0\u00c2\7g\2\2\u00c1\u00b8\3\2\2\2\u00c1\u00bc\3\2\2\2\u00c2"+
		"\34\3\2\2\2\u00c3\u00c6\5\61\31\2\u00c4\u00c7\5\17\b\2\u00c5\u00c7\n\t"+
		"\2\2\u00c6\u00c4\3\2\2\2\u00c6\u00c5\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8"+
		"\u00c9\5\61\31\2\u00c9\36\3\2\2\2\u00ca\u00cf\5\61\31\2\u00cb\u00ce\5"+
		"\17\b\2\u00cc\u00ce\n\t\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00cc\3\2\2\2\u00ce"+
		"\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d2\3\2"+
		"\2\2\u00d1\u00cf\3\2\2\2\u00d2\u00d3\5\61\31\2\u00d3 \3\2\2\2\u00d4\u00d9"+
		"\5\63\32\2\u00d5\u00d8\5\17\b\2\u00d6\u00d8\n\n\2\2\u00d7\u00d5\3\2\2"+
		"\2\u00d7\u00d6\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da"+
		"\3\2\2\2\u00da\u00dc\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00dd\5\63\32\2"+
		"\u00dd\"\3\2\2\2\u00de\u00e3\5\61\31\2\u00df\u00e2\5\17\b\2\u00e0\u00e2"+
		"\n\t\2\2\u00e1\u00df\3\2\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3"+
		"\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4$\3\2\2\2\u00e5\u00e3\3\2\2\2"+
		"\u00e6\u00eb\5\'\24\2\u00e7\u00eb\4\62;\2\u00e8\u00eb\5Q)\2\u00e9\u00eb"+
		"\t\13\2\2\u00ea\u00e6\3\2\2\2\u00ea\u00e7\3\2\2\2\u00ea\u00e8\3\2\2\2"+
		"\u00ea\u00e9\3\2\2\2\u00eb&\3\2\2\2\u00ec\u00ed\t\f\2\2\u00ed(\3\2\2\2"+
		"\u00ee\u00ef\7k\2\2\u00ef\u00f0\7p\2\2\u00f0\u00f1\7v\2\2\u00f1*\3\2\2"+
		"\2\u00f2\u00f3\7^\2\2\u00f3,\3\2\2\2\u00f4\u00f5\7<\2\2\u00f5.\3\2\2\2"+
		"\u00f6\u00f7\7<\2\2\u00f7\u00f8\7<\2\2\u00f8\60\3\2\2\2\u00f9\u00fa\7"+
		")\2\2\u00fa\62\3\2\2\2\u00fb\u00fc\7$\2\2\u00fc\64\3\2\2\2\u00fd\u00fe"+
		"\7*\2\2\u00fe\66\3\2\2\2\u00ff\u0100\7+\2\2\u01008\3\2\2\2\u0101\u0102"+
		"\7}\2\2\u0102:\3\2\2\2\u0103\u0104\7\177\2\2\u0104<\3\2\2\2\u0105\u0106"+
		"\7]\2\2\u0106>\3\2\2\2\u0107\u0108\7_\2\2\u0108@\3\2\2\2\u0109\u010a\7"+
		"/\2\2\u010a\u010b\7@\2\2\u010bB\3\2\2\2\u010c\u010d\7>\2\2\u010dD\3\2"+
		"\2\2\u010e\u010f\7@\2\2\u010fF\3\2\2\2\u0110\u0111\7?\2\2\u0111H\3\2\2"+
		"\2\u0112\u0113\7A\2\2\u0113J\3\2\2\2\u0114\u0115\7,\2\2\u0115L\3\2\2\2"+
		"\u0116\u0117\7-\2\2\u0117N\3\2\2\2\u0118\u0119\7-\2\2\u0119\u011a\7?\2"+
		"\2\u011aP\3\2\2\2\u011b\u011c\7a\2\2\u011cR\3\2\2\2\u011d\u011e\7~\2\2"+
		"\u011eT\3\2\2\2\u011f\u0120\7&\2\2\u0120V\3\2\2\2\u0121\u0122\7.\2\2\u0122"+
		"X\3\2\2\2\u0123\u0124\7=\2\2\u0124Z\3\2\2\2\u0125\u0126\7\60\2\2\u0126"+
		"\\\3\2\2\2\u0127\u0128\7\60\2\2\u0128\u0129\7\60\2\2\u0129^\3\2\2\2\u012a"+
		"\u012b\7B\2\2\u012b`\3\2\2\2\u012c\u012d\7%\2\2\u012db\3\2\2\2\u012e\u012f"+
		"\7\u0080\2\2\u012fd\3\2\2\2\31\2gsy\u0082\u0088\u0090\u0098\u00a2\u00a4"+
		"\u00a6\u00a8\u00af\u00b2\u00c1\u00c6\u00cd\u00cf\u00d7\u00d9\u00e1\u00e3"+
		"\u00ea\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}