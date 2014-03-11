package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocateAnnotationVisitor extends ASTVisitor {

	private MarkerAnnotation node;
	private int chartStart;

	public LocateAnnotationVisitor(int chartStart) {
		this.chartStart = chartStart;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(FieldDeclaration node) {
		int currentChartStart=-1;
		List<VariableDeclarationFragment> fragments=node.fragments();
		for(VariableDeclarationFragment obj: fragments){
			currentChartStart=obj.getStartPosition();
		}
		return currentChartStart==chartStart;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		return node.getName().getStartPosition()==chartStart;
	}
	@Override
	public boolean visit(MarkerAnnotation node) {
		if (node.getTypeName().toString().equals("Optional")) {
			this.setNode(node);
		}
		return false;
	}

	public MarkerAnnotation getNode() {
		return node;
	}

	public void setNode(MarkerAnnotation node) {
		this.node = node;
	}
}
