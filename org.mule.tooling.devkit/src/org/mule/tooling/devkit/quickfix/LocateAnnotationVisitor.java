package org.mule.tooling.devkit.quickfix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;

public class LocateAnnotationVisitor extends ASTVisitor {

    private ASTNode node;
    private final String annotation;
    private int chartStart;
    private ICompilationUnit cu;

    public LocateAnnotationVisitor(int chartStart, String annotation, ICompilationUnit cu) {
        this.annotation = annotation;
        this.chartStart = chartStart;
        this.cu = cu;
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
        String typeName = node.getTypeName().toString();
        if (isFullyQualified(typeName)) {
            if (annotation.equals(typeName)) {
                this.setNode(node);
            }
        } else {
            IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(new IJavaElement[] { cu.getJavaProject() });
            SimpleName nameNode = null;
            TypeNameMatch[] matches = findAllTypes(typeName, searchScope, nameNode, null);
            if (matches.length == 0) {
                // Not the best approach to check name of the class
                if (annotation.endsWith("." + typeName)) {
                    this.setNode(node);
                }
            } else if (matches.length != 1) {// only add import if we have a single match
                return false;
            } else if (matches.length == 1) {
                TypeNameMatch match = matches[0];
                if (match.getTypeQualifiedName().equals(annotation)) {
                    this.setNode(node);
                }
            }
        }
        return false;
    }

    private boolean isFullyQualified(String typeName) {
        return typeName.contains(".");
    }

    private TypeNameMatch[] findAllTypes(String string, IJavaSearchScope searchScope, SimpleName nameNode, Object object) {
        List<TypeNameMatch> collection = new ArrayList<TypeNameMatch>();
        try {
            new SearchEngine().searchAllTypeNames(null, 0, annotation.toCharArray(), SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE,
                    IJavaSearchConstants.ANNOTATION_TYPE, searchScope, new TypeNameMatchCollector(collection), IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return collection.toArray(new TypeNameMatch[0]);
    }
}
