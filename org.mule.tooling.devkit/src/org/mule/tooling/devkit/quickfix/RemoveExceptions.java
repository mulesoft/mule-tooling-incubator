package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

public class RemoveExceptions extends QuickFix {

	RemoveExceptions(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				charStart);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			ASTRewrite rewrite = ASTRewrite.create(parse.getAST());
			MethodDeclaration method = (MethodDeclaration) visitor.getNode();

			for (Object element : method.thrownExceptions()) {
				ASTNode node = (ASTNode) element;
				rewrite.remove(node, null);
			}
			this.addImportIfRequired(parse, parse.getAST(), rewrite,
					"org.mule.api.ConnectionException");

			ListRewrite exceptions = rewrite.getListRewrite(method,
					MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY);

			exceptions.insertAt(rewrite.getAST().newName("ConnectionException"), 0, null);

			unit.applyTextEdit(rewrite.rewriteAST(), null);
			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
			unit.discardWorkingCopy();
		}
	}

	@Override
	public String getDescription() {
		return "You can either add datanse or remove this operation";
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_MULTI_FIX);
	}
}
