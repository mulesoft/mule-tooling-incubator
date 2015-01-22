package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class RemoveModifier extends QuickFix {

    private ModifierKeyword modifier;

    public RemoveModifier(String label, ModifierKeyword modifier, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
        this.modifier = modifier;

    }

    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateModifierVisitor visitor = new LocateModifierVisitor(errorMarkerStart, modifier);

        unit.accept(visitor);

        if (visitor.getNode() != null) {
            rewrite = ASTRewrite.create(unit.getAST());
            rewrite.remove(visitor.getNode(), null);
        }
        return rewrite;
    }

    @Override
    public Image getImage() {
        return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
    }
}