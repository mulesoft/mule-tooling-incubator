package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocateAnnotationVisitor extends ASTVisitor {

	private ASTNode node;
	private final String annotation;
	private int chartStart;

	public LocateAnnotationVisitor(int chartStart, String annotation) {
		this.annotation = annotation;
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
		boolean visitChilds = (node.getName().getStartPosition() <= chartStart)
				&& (node.getName().getStartPosition() + node.getLength() > chartStart)
				&& getNode() == null;
		return visitChilds;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		return node.getName().getStartPosition() == chartStart;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		if (node.getTypeName().toString().equals(annotation)) {
			this.setNode(node);
		}
		return false;
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		if (node.getTypeName().toString().equals(annotation)) {
			this.setNode(node);
		}
		return false;
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		if (node.getTypeName().toString().equals(annotation)) {
			this.setNode(node);
		}
		return false;
	}

	public ASTNode getNode() {
		return node;
	}

	public void setNode(ASTNode node) {
		this.node = node;
	}
}
