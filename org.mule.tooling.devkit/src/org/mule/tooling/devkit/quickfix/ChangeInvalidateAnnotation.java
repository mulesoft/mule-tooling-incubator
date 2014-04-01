package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

public class ChangeInvalidateAnnotation extends QuickFix {

	private final String annotation;
	private final String newAnnotation;

	public ChangeInvalidateAnnotation(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
		this.annotation = "InvalidateConnectionOn";
		this.newAnnotation = "ReconnectOn";
	}

	public String getLabel() {
		return label;
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = parse(unit);
		LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(
				charStart, annotation);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = parse.getAST();

			ASTRewrite rewrite = ASTRewrite.create(ast);
			NormalAnnotation replacement = ast.newNormalAnnotation();
			Annotation annotation = (Annotation) visitor.getNode();
			if (annotation.isNormalAnnotation()) {
				NormalAnnotation original = (NormalAnnotation) annotation;
				MemberValuePair value2 = (MemberValuePair) original.values()
						.get(0);
				Expression exp = value2.getValue();
				replacement.setTypeName(ast.newName(newAnnotation));
				MemberValuePair memberValuePair = ast.newMemberValuePair();
				memberValuePair.setName(ast.newSimpleName("exceptions"));
				TypeLiteral value = ast.newTypeLiteral();

				SimpleType arrayType = ast.newSimpleType(ast
						.newName(((SimpleType) ((TypeLiteral) exp).getType())
								.getName().toString()));

				value.setType(arrayType);
				memberValuePair.setValue(value);
				replacement.values().add(memberValuePair);
				rewrite.replace(visitor.getNode(), replacement, null);

				addImportIfRequired(parse, ast, rewrite,
						"org.mule.api.annotations.ReconnectOn");

				unit.applyTextEdit(rewrite.rewriteAST(), null);
				unit.becomeWorkingCopy(null);
				unit.commitWorkingCopy(true, null);
				unit.discardWorkingCopy();
			}
		}
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
	}
}