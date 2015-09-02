package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.treeview.model.ModelUtils;

@SuppressWarnings("restriction")
public class ChangeAnnotationQuickFix extends QuickFix {

    private final QualifiedName annotation;
    private final QualifiedName newAnnotation;

    public ChangeAnnotationQuickFix(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
        this.annotation = ModelUtils.MODULE_ANNOTATION;
        this.newAnnotation = ModelUtils.CONNECTOR_ANNOTATION;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(errorMarkerStart, annotation);

        unit.accept(visitor);

        if (visitor.getNode() != null) {
            AST ast = unit.getAST();

            rewrite = ASTRewrite.create(ast);
            NormalAnnotation replacement = ast.newNormalAnnotation();
            Annotation annotation = (Annotation) visitor.getNode();
            if (annotation.isNormalAnnotation()) {
                NormalAnnotation original = (NormalAnnotation) annotation;
                replacement.setTypeName(ast.newName(newAnnotation.getName().toString()));

                replacement.values().addAll(ASTNode.copySubtrees(replacement.getAST(), (List) original.values()));
                rewrite.replace(visitor.getNode(), replacement, null);

                addImportIfRequired(unit, rewrite, newAnnotation.getFullyQualifiedName());

            }
        }
        return rewrite;
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
    }
}