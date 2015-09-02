package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.mule.tooling.devkit.treeview.model.ModelUtils;

public class ChangeMinMuleVersion extends QuickFix {

    private final QualifiedName annotation;

    public ChangeMinMuleVersion(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
        this.annotation = ModelUtils.CONNECTOR_ANNOTATION;
    }

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
                NormalAnnotation value = (NormalAnnotation) annotation;
                for (Object member : value.values()) {
                    if (member instanceof MemberValuePair) {
                        MemberValuePair pair = (MemberValuePair) member;
                        if (pair.getName().toString().equals("minMuleVersion")) {
                            StringLiteral literal = ast.newStringLiteral();
                            literal.setLiteralValue("3.4");

                            rewrite.replace(pair.getValue(), literal, null);
                        }
                    }
                }
            }

        }
        return rewrite;
    }
}