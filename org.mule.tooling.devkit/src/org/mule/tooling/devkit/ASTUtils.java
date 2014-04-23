package org.mule.tooling.devkit;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTUtils {
	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file. It uses the AST API that handles JLS3.
	 * 
	 * @param unit
	 *            Java element to be used to generate a compilation unit.
	 * 
	 * @return Compilation unit to be processed.
	 */

	public static CompilationUnit parse(ICompilationUnit unit) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
