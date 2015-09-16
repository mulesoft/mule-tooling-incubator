package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocateAnnotationVisitor extends ASTVisitor {

    private ASTNode node;
    private List<QualifiedName> annotations;
    private int chartStart;

    public LocateAnnotationVisitor(int chartStart, QualifiedName annotation) {
        this.annotations = new ArrayList<QualifiedName>();
        annotations.add(annotation);
        this.chartStart = chartStart;
    }

    @SuppressWarnings("unchecked")
    public boolean visit(FieldDeclaration node) {
        int currentChartStart = -1;
        List<VariableDeclarationFragment> fragments = node.fragments();
        for (VariableDeclarationFragment obj : fragments) {
            currentChartStart = obj.getStartPosition();
            if (chartStart == currentChartStart) {
                return true;
            }
        }
        return currentChartStart == chartStart;
    }

    public boolean visit(MethodDeclaration node) {
        boolean visitChilds = (node.getName().getStartPosition() <= chartStart) && (node.getName().getStartPosition() + node.getLength() > chartStart) && getNode() == null;
        return visitChilds;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        return node.getName().getStartPosition() == chartStart;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        return doVisit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        return doVisit(node);
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        return doVisit(node);
    }

    public ASTNode getNode() {
        return node;
    }

    public void setNode(ASTNode node) {
        this.node = node;
    }

    protected boolean doVisit(Annotation node) {
        for (QualifiedName annotationName : annotations) {
            if (node.getTypeName().isQualifiedName()) {
                if (node.getTypeName().getFullyQualifiedName().equals(annotationName.getFullyQualifiedName())) {
                    this.setNode(node);
                }
            } else if (node.getTypeName() != null && node.getTypeName().resolveTypeBinding() != null && node.getTypeName().resolveTypeBinding().getBinaryName() != null
                    && node.getTypeName().resolveTypeBinding().getBinaryName().equals(annotationName.getFullyQualifiedName())) {
                this.setNode(node);
            }
        }
        return false;
    }

    public LocateAnnotationVisitor addAnnotation(QualifiedName annotation) {
        annotations.add(annotation);
        return this;
    }
}
