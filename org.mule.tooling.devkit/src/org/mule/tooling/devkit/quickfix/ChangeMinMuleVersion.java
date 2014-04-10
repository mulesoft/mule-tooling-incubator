package org.mule.tooling.devkit.quickfix;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.common.DevkitUtils;

@SuppressWarnings("restriction")
public class ChangeMinMuleVersion extends QuickFix {

	private final String annotation;

	public ChangeMinMuleVersion(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
		this.annotation = "Connector";
	}

	public String getLabel() {
		return label;
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {

		CompilationUnit parse = ASTUtils.parse(unit);
		LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(
				charStart, annotation);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = parse.getAST();

			ASTRewrite rewrite = ASTRewrite.create(ast);
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

				applyChange(unit, rewrite);
			}

		}
	}
}