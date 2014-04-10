package org.mule.tooling.devkit.quickfix;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.ASTUtils;

@SuppressWarnings("restriction")
public class AddJavadocForExceptionQuickFix extends QuickFix {

	AddJavadocForExceptionQuickFix(String label,
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

			Javadoc javadoc = method.getJavadoc();
			if (javadoc != null) {
				addJavadocForParam(ast, rewrite, method, javadoc);
			} else {
				final Javadoc doc = (Javadoc) rewrite.createStringPlaceholder(
						"/**\n" + " * Comment for method\n" + " */",
						ASTNode.JAVADOC);
				rewrite.set(method, MethodDeclaration.JAVADOC_PROPERTY, doc,
						null);
			}

			applyChange(unit, rewrite);
		}
	}

	private void addJavadocForParam(AST ast, ASTRewrite rewrite,
			MethodDeclaration method, Javadoc javadoc) {
		Set<String> exceptions = new HashSet<String>();
		for (Object obj : javadoc.tags()) {
			TagElement node = (TagElement) obj;
			if (node.getTagName().equals(TagElement.TAG_THROWS)) {
				for (Object fragment : node.fragments()) {
					ASTNode frag = (ASTNode) fragment;
					exceptions.add(frag.toString());
				}
			}
		}
		for (Object exception : method.thrownExceptions()) {
			SimpleName node = (SimpleName) exception;
			if (exceptions.contains(node.toString()))
				continue;
			TagElement newTagElement = ast.newTagElement();
			newTagElement.setTagName(TagElement.TAG_THROWS);
			SimpleName arg = ast.newSimpleName(node.toString()); //$NON-NLS-1$
			newTagElement.fragments().add(arg);
			ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc,
					Javadoc.TAGS_PROPERTY);
			tagsRewriter.insertLast(newTagElement, null);
		}

	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}
}
