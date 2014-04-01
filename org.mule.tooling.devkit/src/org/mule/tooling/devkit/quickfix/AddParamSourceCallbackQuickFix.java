package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class AddParamSourceCallbackQuickFix extends QuickFix {

	AddParamSourceCallbackQuickFix(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	protected void createAST(ICompilationUnit unit, Integer charStart)
			throws JavaModelException {
		CompilationUnit parse = parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				charStart);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = parse.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			ListRewrite listImports = rewrite.getListRewrite(parse, CompilationUnit.IMPORTS_PROPERTY);
			ImportDeclaration id=null;
			
			// for getting insertion position
			TypeDeclaration typeDecl = (TypeDeclaration) parse.types().get(0);

			MethodDeclaration method = typeDecl.getMethods()[0];

			ast = method.getAST();

			List parameters = method.parameters();

			ListRewrite list = rewrite.getListRewrite(method,
					MethodDeclaration.PARAMETERS_PROPERTY);

			SingleVariableDeclaration newNode = ast
					.newSingleVariableDeclaration();
			newNode.setName(ast.newSimpleName("sourceCallBack"));
			
			newNode.setType(ast.newSimpleType(ast.newName("SourceCallback")));
			list.insertLast(newNode, null);

			boolean hasImport = false;
			for(Object obj:parse.imports()){
				ImportDeclaration importDec=(ImportDeclaration) obj;
				if(importDec.getName().getFullyQualifiedName().equals("org.mule.api.callback.SourceCallback")){
					hasImport = true;
				}
			}
			
			if(!hasImport){
				id=ast.newImportDeclaration();
				id.setName(ast.newName("org.mule.api.callback.SourceCallback"));
				listImports.insertLast(id, null);
			}
			
			Javadoc javadoc = method.getJavadoc();
			if (javadoc != null) {
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

			unit.applyTextEdit(rewrite.rewriteAST(), null);
			unit.becomeWorkingCopy(null);
			unit.commitWorkingCopy(true, null);
			unit.discardWorkingCopy();
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

			if (TagElement.TAG_PARAM.equals(currName)) {
				retVal = curr;
			}
		}
		return retVal;
	}
}
