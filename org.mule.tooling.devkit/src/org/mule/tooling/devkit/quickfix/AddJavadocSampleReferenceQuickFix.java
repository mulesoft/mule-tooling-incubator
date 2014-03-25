package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.devkit.generation.utils.NameUtils;

@SuppressWarnings("restriction")
public class AddJavadocSampleReferenceQuickFix extends QuickFix {

	AddJavadocSampleReferenceQuickFix(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				charStart);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = parse.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			MethodDeclaration method = (MethodDeclaration) visitor.getNode();

			ast = method.getAST();

			List parameters = method.parameters();
			String paramName = "";
			for (Object node : parameters) {
				VariableDeclaration item = (VariableDeclaration) node;
				if (item.getName().getStartPosition() == charStart) {
					paramName = item.getName().toString();
				}
			}

			Javadoc javadoc = method.getJavadoc();
			if (javadoc != null) {
				addJavadocForParam(ast, rewrite, method, paramName, javadoc);
			}

			unit.applyTextEdit(rewrite.rewriteAST(), null);
			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
			unit.discardWorkingCopy();
		}
	}

	private void addJavadocForParam(AST ast, ASTRewrite rewrite,
			MethodDeclaration method, String paramName, Javadoc javadoc) {

		TextElement comment = ast.newTextElement();
		comment.setText("\n{@sample.xml ../../../doc/${namespace}-connector.xml.sample ${namespace}:"
				+ NameUtils.uncamel(method.getName().toString()) + "}");

		ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc,
				Javadoc.TAGS_PROPERTY);
		tagsRewriter.insertAt(comment, 1, null);

	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

}
