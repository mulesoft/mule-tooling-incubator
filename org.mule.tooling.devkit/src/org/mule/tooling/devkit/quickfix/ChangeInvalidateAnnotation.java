package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class ChangeInvalidateAnnotation extends QuickFix {

	private final String annotation;
	private final String newAnnotation;

	public ChangeInvalidateAnnotation(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
		this.annotation = "InvalidateConnectionOn";
		this.newAnnotation = "ReconnectOn";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
		ASTRewrite rewrite = null;
		LocateAnnotationVisitor visitor = new LocateAnnotationVisitor(
				errorMarkerStart, annotation);

		unit.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = unit.getAST();

			rewrite = ASTRewrite.create(ast);
			Annotation annotation = (Annotation) visitor.getNode();
			if (annotation.isNormalAnnotation()) {
				NormalAnnotation replacement = ast.newNormalAnnotation();
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

				addImportIfRequired(unit, rewrite,
						"org.mule.api.annotations.ReconnectOn");

			}
		}
		return rewrite;
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
	}
}