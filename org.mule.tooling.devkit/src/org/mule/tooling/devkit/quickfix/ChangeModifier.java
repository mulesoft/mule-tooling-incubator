package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ChangeModifier extends QuickFix {

    public ChangeModifier(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);

    }

    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateModifierVisitor visitor = new LocateModifierVisitor(errorMarkerStart, ModifierKeyword.PROTECTED_KEYWORD);

        unit.accept(visitor);

        // No protected modifier, try to find a private modifier
        if (visitor.getNode() == null) {
            visitor = new LocateModifierVisitor(errorMarkerStart, ModifierKeyword.PRIVATE_KEYWORD);
            unit.accept(visitor);
        }

        if (visitor.getNode() != null) {
            rewrite = ASTRewrite.create(unit.getAST());
            rewrite.replace(visitor.getNode(), unit.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
        } else {
            // No modifier
            // TODO check how to add a modifier to an existing class
        }
        return rewrite;
    }

    @Override
    public Image getImage() {
        return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
    }
}