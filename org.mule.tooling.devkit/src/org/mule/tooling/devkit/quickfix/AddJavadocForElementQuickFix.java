package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.ASTUtils;

@SuppressWarnings("restriction")
public class AddJavadocForElementQuickFix extends QuickFix {

	AddJavadocForElementQuickFix(String label,
			ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
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

			MethodDeclaration method = (MethodDeclaration) visitor.getNode();

			ast = method.getAST();

			List parameters = method.parameters();
			String paramName = "";
			for (Object node : parameters) {
				VariableDeclaration item = (VariableDeclaration) node;
				if (item.getName().getStartPosition() == charStart) {
					paramName = item.getName().toString();
				}
			}

			Javadoc javadoc = method.getJavadoc();
			if (javadoc != null) {
				addJavadocForParam(ast, rewrite, method, paramName, javadoc);
			} else {
				final Javadoc doc = (Javadoc) rewrite.createStringPlaceholder(
						"/**\n" + " * Comment for method\n" + " */", ASTNode.JAVADOC);
				rewrite.set(method, MethodDeclaration.JAVADOC_PROPERTY, doc,
						null);
			}

			applyChange(unit, rewrite);
		}
	}

	private void addJavadocForParam(AST ast, ASTRewrite rewrite,
			MethodDeclaration method, String paramName, Javadoc javadoc) {
		TagElement newTagElement = ast.newTagElement();
		newTagElement.setTagName(TagElement.TAG_PARAM);
		SimpleName arg = ast.newSimpleName(paramName); //$NON-NLS-1$
		newTagElement.fragments().add(arg);
		TextElement comment = ast.newTextElement();
		comment.setText("Comment for " + paramName);
		newTagElement.fragments().add(comment);
		ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc,
				Javadoc.TAGS_PROPERTY);
		TagElement after = getLastParamTag(javadoc);
		if (after != null) {
			if (method.getReturnType2().equals(PrimitiveType.VOID)) {
				tagsRewriter.insertAfter(newTagElement, after, null);
			} else {
				tagsRewriter.insertBefore(newTagElement, after, null);
			}
		} else {
			tagsRewriter.insertLast(newTagElement, null);
		}
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

	public static TagElement getLastParamTag(Javadoc javadoc) {

		List tags = javadoc.tags();

		int nTags = tags.size();
		TagElement retVal = null;
		for (int i = 0; i < nTags; i++) {

			TagElement curr = (TagElement) tags.get(i);

			String currName = curr.getTagName();

			if (TagElement.TAG_PARAM.equals(currName)
					|| TagElement.TAG_RETURN.equals(currName)) {
				retVal = curr;
			}
		}
		return retVal;
	}
}
