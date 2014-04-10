package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class AddParamSourceCallbackQuickFix extends QuickFix {

	AddParamSourceCallbackQuickFix(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	@Override
	protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
		ASTRewrite rewrite = null;
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				errorMarkerStart);

		unit.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = unit.getAST();
			rewrite = ASTRewrite.create(ast);

			MethodDeclaration method = (MethodDeclaration) visitor.getNode();

			ast = method.getAST();

			addSourceCallbackParameter(unit, rewrite, ast, method);

			Javadoc javadoc = method.getJavadoc();
			if (javadoc != null) {
				addJavadoc(rewrite, ast, javadoc);
			}
		}
		return rewrite;
	}

	private void addSourceCallbackParameter(CompilationUnit unit,
			ASTRewrite rewrite, AST ast, MethodDeclaration method) {

		ListRewrite list = rewrite.getListRewrite(method,
				MethodDeclaration.PARAMETERS_PROPERTY);

		SingleVariableDeclaration newNode = ast.newSingleVariableDeclaration();
		newNode.setName(ast.newSimpleName("sourceCallback"));

		newNode.setType(ast.newSimpleType(ast.newName("SourceCallback")));
		list.insertLast(newNode, null);

		this.addImportIfRequired(unit, rewrite,
				"org.mule.api.callback.SourceCallback");
	}

	@SuppressWarnings("unchecked")
	private void addJavadoc(ASTRewrite rewrite, AST ast, Javadoc javadoc) {
		TagElement newTagElement = ast.newTagElement();
		newTagElement.setTagName(TagElement.TAG_PARAM);
		SimpleName arg = ast.newSimpleName("sourceCallback"); //$NON-NLS-1$
		newTagElement.fragments().add(arg);
		TextElement comment = ast.newTextElement();
		comment.setText("Comment for callback");
		newTagElement.fragments().add(comment);
		ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc,
				Javadoc.TAGS_PROPERTY);
		TagElement after = getLastParamTag(javadoc);
		if (after != null) {
			tagsRewriter.insertAfter(newTagElement, after, null);
		} else {
			tagsRewriter.insertLast(newTagElement, null);
		}
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

	@SuppressWarnings("rawtypes")
	public static TagElement getLastParamTag(Javadoc javadoc) {

		List tags = javadoc.tags();

		int nTags = tags.size();
		TagElement retVal = null;
		for (int i = 0; i < nTags; i++) {

			TagElement curr = (TagElement) tags.get(i);

			String currName = curr.getTagName();

			if (TagElement.TAG_PARAM.equals(currName)) {
				retVal = curr;
			}
		}
		return retVal;
	}
}
