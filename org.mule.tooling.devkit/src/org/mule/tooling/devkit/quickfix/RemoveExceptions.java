package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class RemoveExceptions extends QuickFix {

    RemoveExceptions(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
    }

    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(errorMarkerStart);

        unit.accept(visitor);

        if (visitor.getNode() != null) {
            rewrite = ASTRewrite.create(unit.getAST());
            MethodDeclaration method = (MethodDeclaration) visitor.getNode();

            for (Object element : method.thrownExceptions()) {
                ASTNode node = (ASTNode) element;
                rewrite.remove(node, null);
            }
            addImportIfRequired(unit, rewrite, "org.mule.api.ConnectionException");

            ListRewrite exceptions = rewrite.getListRewrite(method, MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY);

            exceptions.insertAt(rewrite.getAST().newName("ConnectionException"), 0, null);
        }
        return rewrite;
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_MULTI_FIX);
    }
}
