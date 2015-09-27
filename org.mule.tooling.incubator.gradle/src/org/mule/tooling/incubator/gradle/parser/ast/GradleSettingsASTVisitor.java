package org.mule.tooling.incubator.gradle.parser.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

public class GradleSettingsASTVisitor extends CodeVisitorSupport {
	
	private boolean isInclude = false;
	
	private boolean isArgumentList = false;
	
	private List<String> modules;
	
	public static enum STATE {
		include
	}
	
	public GradleSettingsASTVisitor() {
		modules = new ArrayList<String>();
	}
	
	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		
		//we currently only care for includes, but more to come!
		if (STATE.include.toString().equals(call.getMethodAsString())) {
			isInclude = true;
		}
		super.visitMethodCallExpression(call);
		isInclude = false;
	}
	
	
	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		isArgumentList = true;
		super.visitArgumentlistExpression(ale);
		isArgumentList = false;
	}
	
	@Override
	public void visitConstantExpression(ConstantExpression expression) {
		
		if (isInclude && isArgumentList) {
			modules.add(expression.getValue().toString());
		}
		
		super.visitConstantExpression(expression);
	}
	
	public List<String> getModules() {
		return modules;
	}
	
}
