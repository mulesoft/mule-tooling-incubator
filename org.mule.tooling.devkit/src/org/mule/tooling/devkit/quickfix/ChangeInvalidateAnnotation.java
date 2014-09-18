package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.treeview.model.ModelUtils;

@SuppressWarnings("restriction")
public class ChangeInvalidateAnnotation extends QuickFix {

    private final QualifiedName annotation;
    private final QualifiedName newAnnotation;

    public ChangeInvalidateAnnotation(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
        this.annotation = ModelUtils.createAnnotation(ModelUtils.ORG_MULE_API_ANNOTATIONS, "InvalidateConnectionOn");
        this.newAnnotation = ModelUtils.createAnnotation(ModelUtils.ORG_MULE_API_ANNOTATIONS, "ReconnectOn");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(errorMarkerStart, annotation);

        unit.accept(visitor);

        if (visitor.getNode() != null) {
            AST ast = unit.getAST();

            rewrite = ASTRewrite.create(ast);
            Annotation annotation = (Annotation) visitor.getNode();
            if (annotation.isNormalAnnotation()) {
                NormalAnnotation replacement = ast.newNormalAnnotation();
                NormalAnnotation original = (NormalAnnotation) annotation;
                MemberValuePair value2 = (MemberValuePair) original.values().get(0);
                Expression exp = value2.getValue();
                replacement.setTypeName(ast.newName(newAnnotation.getName().toString()));
                MemberValuePair memberValuePair = ast.newMemberValuePair();
                memberValuePair.setName(ast.newSimpleName("exceptions"));
                TypeLiteral value = ast.newTypeLiteral();

                SimpleType arrayType = ast.newSimpleType(ast.newName(((SimpleType) ((TypeLiteral) exp).getType()).getName().toString()));

                value.setType(arrayType);
                memberValuePair.setValue(value);
                replacement.values().add(memberValuePair);
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