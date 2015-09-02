package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class AddAnnotationQuickFix extends QuickFix {

    private final String newAnnotation;

    public AddAnnotationQuickFix(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
        this.newAnnotation = "Default";
    }

    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(errorMarkerStart);

        unit.accept(visitor);

        if (visitor.getNode() != null) {
            AST ast = unit.getAST();

            rewrite = ASTRewrite.create(ast);
            FieldDeclaration field = (FieldDeclaration) visitor.getNode();
            NormalAnnotation replacement = ast.newNormalAnnotation();
            replacement.setTypeName(ast.newName(newAnnotation));
            MemberValuePair valuePair = ast.newMemberValuePair();

            StringLiteral literal = ast.newStringLiteral();
            literal.setLiteralValue("${value}");
            valuePair.setValue(literal);
            ListRewrite values = rewrite.getListRewrite(replacement, NormalAnnotation.VALUES_PROPERTY);
            values.insertFirst(literal, null);

            ListRewrite annotations = rewrite.getListRewrite(field, FieldDeclaration.MODIFIERS2_PROPERTY);
            annotations.insertFirst(replacement, null);
            addImportIfRequired(unit, rewrite, "org.mule.api.annotations.param.Default");

        }
        return rewrite;
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
    }
}