package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.ASTUtils;

@SuppressWarnings("restriction")
public class AddAnnotationQuickFix extends QuickFix {

	private final String newAnnotation;

	public AddAnnotationQuickFix(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
		this.newAnnotation = "Default";
	}

	public String getLabel() {
		return label;
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = ASTUtils.parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				charStart);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = parse.getAST();

			ASTRewrite rewrite = ASTRewrite.create(ast);
			FieldDeclaration field = (FieldDeclaration) visitor.getNode();
			NormalAnnotation replacement = ast.newNormalAnnotation();
			replacement.setTypeName(ast.newName(newAnnotation));
			MemberValuePair valuePair = ast.newMemberValuePair();

			StringLiteral literal = ast.newStringLiteral();
			literal.setLiteralValue("${value}");
			valuePair.setValue(literal);
			ListRewrite values = rewrite.getListRewrite(replacement,
					NormalAnnotation.VALUES_PROPERTY);
			values.insertFirst(literal, null);

			ListRewrite annotations = rewrite.getListRewrite(field,
					FieldDeclaration.MODIFIERS2_PROPERTY);
			annotations.insertFirst(replacement, null);
			addImportIfRequired(parse, ast, rewrite,
					"org.mule.api.annotations.param.Default");

			applyChange(unit, rewrite);
		}
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
	}
}