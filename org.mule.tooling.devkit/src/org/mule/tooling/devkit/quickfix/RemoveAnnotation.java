package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.ASTUtils;

public class RemoveAnnotation extends QuickFix {

	private final String annotation;

	public RemoveAnnotation(String label, String annotation,ConditionMarkerEvaluator evaluator) {
		super(label,evaluator);
		this.annotation = annotation;

	}

	public String getLabel() {
		return label;
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = ASTUtils.parse(unit);
		LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(
				charStart, annotation);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			ASTRewrite rewrite = ASTRewrite.create(parse.getAST());
			rewrite.remove(visitor.getNode(), null);
			applyChange(unit, rewrite);
		}
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE);
	}
}