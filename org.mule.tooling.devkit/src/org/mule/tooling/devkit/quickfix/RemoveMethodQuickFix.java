package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.ASTUtils;

public class RemoveMethodQuickFix extends QuickFix {

	RemoveMethodQuickFix(String label,ConditionMarkerEvaluator evaluator) {
		super(label,evaluator);
	}
	
	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = ASTUtils.parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(charStart);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			ASTRewrite rewrite = ASTRewrite.create(parse.getAST());
			rewrite.remove(visitor.getNode(), null);
			applyChange(unit, rewrite);
		}
	}

	@Override
	public String getDescription() {
		return "You can either add datanse or remove this operation";
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE);
	}
}
