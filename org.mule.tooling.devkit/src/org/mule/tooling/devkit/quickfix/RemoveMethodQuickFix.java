package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class RemoveMethodQuickFix extends QuickFix {

	RemoveMethodQuickFix(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	@Override
	protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
		ASTRewrite rewrite = null;
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				errorMarkerStart);

		unit.accept(visitor);

		if (visitor.getNode() != null) {
			rewrite = ASTRewrite.create(unit.getAST());
			rewrite.remove(visitor.getNode(), null);
		}
		return rewrite;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE);
	}
}
