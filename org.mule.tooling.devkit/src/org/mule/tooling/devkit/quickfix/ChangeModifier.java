package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ChangeModifier extends QuickFix {

	public ChangeModifier(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);

	}

	public String getLabel() {
		return label;
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = parse(unit);
		LocateModifierVisitor visitor = new LocateModifierVisitor(charStart,
				ModifierKeyword.PROTECTED_KEYWORD);

		parse.accept(visitor);

		if (visitor.getNode() == null) {
			visitor = new LocateModifierVisitor(charStart,
					ModifierKeyword.PRIVATE_KEYWORD);
			parse.accept(visitor);
		}

		ASTRewrite rewrite = ASTRewrite.create(parse.getAST());
		if (visitor.getNode() != null) {
			rewrite.replace(
					visitor.getNode(),
					parse.getAST().newModifier(
							Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
		} else {
			// No modifier
			//TODO check how to add a modifier to an existing class
		}
		unit.applyTextEdit(rewrite.rewriteAST(), null);
		unit.becomeWorkingCopy(null);
		unit.commitWorkingCopy(true, null);
		unit.discardWorkingCopy();
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE);
	}
}