package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocateFieldOrMethodVisitor extends ASTVisitor {

	private ASTNode node;
	private int chartStart;

	public LocateFieldOrMethodVisitor(int chartStart) {
		this.chartStart = chartStart;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(FieldDeclaration node) {
		List<VariableDeclarationFragment> fragments = node.fragments();
		for (VariableDeclarationFragment obj : fragments) {
			if (obj.getStartPosition() == chartStart) {
				this.setNode(node);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		int currentChartStart = -1;

		if (node.getName().getStartPosition() == chartStart) {
			this.setNode(node);
		}
		return currentChartStart == chartStart;
	}

	public ASTNode getNode() {
		return node;
	}

	public void setNode(ASTNode node) {
		this.node = node;
	}
}
