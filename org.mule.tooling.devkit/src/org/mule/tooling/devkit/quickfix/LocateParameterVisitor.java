package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class LocateParameterVisitor extends ASTVisitor {

	private ASTNode node;
	private int chartStart;

	public LocateParameterVisitor(int chartStart) {
		this.chartStart = chartStart;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		if (node.getName().getStartPosition() == chartStart) {
			setNode(node);
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
