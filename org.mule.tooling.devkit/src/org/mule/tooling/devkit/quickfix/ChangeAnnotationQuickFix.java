package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.ASTUtils;

@SuppressWarnings("restriction")
public class ChangeAnnotationQuickFix extends QuickFix {

	private final String annotation;
	private final String newAnnotation;

	public ChangeAnnotationQuickFix(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
		this.annotation = "Module";
		this.newAnnotation = "Connector";
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
			NormalAnnotation replacement = ast.newNormalAnnotation();
			Annotation annotation = (Annotation) visitor.getNode();
			if (annotation.isNormalAnnotation()) {
				NormalAnnotation original = (NormalAnnotation) annotation;
				replacement.setTypeName(ast.newName(newAnnotation));

				replacement.values().addAll(
						ASTNode.copySubtrees(replacement.getAST(),
								(List) original.values()));
				rewrite.replace(visitor.getNode(), replacement, null);

				addImportIfRequired(parse, ast, rewrite,
						"org.mule.api.annotations.Connector");

				applyChange(unit, rewrite);
			}
		}
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
	}
}