package antlr.parsing;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import lang.core.Protocol;

public class MyParser {

	public static Protocol parse(CharStream input) {
		MercuryDSLLexer lexer = new MercuryDSLLexer(input);
		MercuryDSLParser parser = new MercuryDSLParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		ParseTree tree = null;
		try {
			tree = parser.program();
		} catch (ParseCancellationException ex) {
			System.err.println("Please correct the following parsing error: ");
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
		MercuryVisitor visitor = new MercuryVisitor();
		Protocol p = (Protocol) visitor.visit(tree);
		return p;

//		MercuryPassOneVisitor firstVisitor = new MercuryPassOneVisitor();
//		Protocol p = (Protocol) firstVisitor.visit(tree);
//		MercuryPassTwoVisitor secondVisitor = new MercuryPassTwoVisitor(p);
//		p = (Protocol) firstVisitor.visit(tree);
//		return p;
	}

}
