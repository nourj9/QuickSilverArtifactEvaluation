package Core;

import java.io.File;
import java.io.IOException;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import antlr.parsing.MercuryDSLLexer;
import antlr.parsing.MercuryDSLParser;
import antlr.parsing.MercuryVisitor;
import lang.core.*;

public class Utils {

	public static void parseAllBencmarks() throws IOException {
		File[] benckmakrks = new File("benchmarks/").listFiles();
		for (File bm : benckmakrks) {
			// System.out.println("Current BM: " + bm.getPath());
			// CharStream input = CharStreams.fromFileName("benchmarks/smoke_detector.ch");
			CharStream input = CharStreams.fromFileName(bm.getAbsolutePath());
			MercuryDSLLexer lexer = new MercuryDSLLexer(input);
			MercuryDSLParser parser = new MercuryDSLParser(new CommonTokenStream(lexer));
			ParseTree tree = parser.program();
			MercuryVisitor visitor = new MercuryVisitor();
			@SuppressWarnings("unused")
			Protocol p = (Protocol) visitor.visit(tree);

			// System.out.println(p.toString());
		}
	}

//	public static void checkAllBencmarksForNewStuff() throws IOException {
//		File[] benckmakrks = new File("benchmarks/").listFiles();
//		for (File bm : benckmakrks) {
//			// System.out.println("Current BM: " + bm.getPath());
//			CharStream input = CharStreams.fromFileName(bm.getAbsolutePath());
//			MercuryDSLLexer lexer = new MercuryDSLLexer(input);
//			MercuryDSLParser parser = new MercuryDSLParser(new CommonTokenStream(lexer));
//			ParseTree tree = parser.program();
//			MercuryVisitor visitor = new MercuryVisitor();
//			Protocol p = (Protocol) visitor.visit(tree);
//			// make this fixed-point
//			System.out.println(p.getName() + " Before ########################");
//			System.out.println(p.toString());
//			PreProcessing.desugarReplyHandler(p);
//			PreProcessing.desugarPassiveHandler(p);
//			PreProcessing.addImplicitElseBlocks(p);
//			PreProcessing.addImplicitGotos(p);
//			PreProcessing.eleminateDeadCode(p);
//			PreProcessing.pushNonIfIntoIf(p);
//			PreProcessing.eleminateDeadCode(p);
//			PreProcessing.moveLastIfToNewLoc(p);
//			PreProcessing.addImplicitGotos(p);
//			PreProcessing.eleminateDeadCode(p);
//			PreProcessing.pushNonIfIntoIf(p);
//			PreProcessing.eleminateDeadCode(p);
//			PreProcessing.winLoseWithOnlyGotos(p);
//			PreProcessing.valConsOnlyGotots(p);
//			PreProcessing.handleNestedIfs(p);
//			PreProcessing.seperateMultipleSends(p);
//			PreProcessing.seperateSendsFromReceives(p);
//			PreProcessing.flattenConditionals(p);
//			PreProcessing.PreCodeGenerationSanityChecks(p);
//
//			System.out.println(p.getName() + " After ########################");
//			System.out.println(p.toString());
//		}
//	}
}
