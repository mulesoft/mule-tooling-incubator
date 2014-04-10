package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.ASTUtils;

@SuppressWarnings("restriction")
public class AddDatasenseMethodQuickFix extends QuickFix{

	AddDatasenseMethodQuickFix(String label,ConditionMarkerEvaluator evaluator) {
		super(label,evaluator);
	}
	
	protected void createAST(ICompilationUnit unit,Integer charStart) throws JavaModelException {
		CompilationUnit parse = ASTUtils.parse(unit);
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(charStart);

		parse.accept(visitor);

		if (visitor.getNode() != null) {
			AST ast = parse.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			// for getting insertion position
			TypeDeclaration typeDecl = (TypeDeclaration) parse.types().get(0);
			
			ListRewrite list = rewrite.getListRewrite(typeDecl,
					TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			
			Statement placeHolder = (Statement) rewrite.createStringPlaceholder("@MetaDataKeyRetriever\npublic List<MetaDataKey> getKeys() throws Exception {\n\treturn null;\n}", ASTNode.EMPTY_STATEMENT);
			list.insertLast(placeHolder, null);
			
			placeHolder = (Statement) rewrite.createStringPlaceholder("@MetaDataRetriever\npublic MetaData getMetaData(MetaDataKey key) throws Exception {\n\treturn null;\n}", ASTNode.EMPTY_STATEMENT);
			list.insertLast(placeHolder, null);
			
			addImportIfRequired(parse, ast, rewrite,
					"org.mule.api.annotations.MetaDataKeyRetriever");
			addImportIfRequired(parse, ast, rewrite,
					"org.mule.api.annotations.MetaDataRetriever");
			addImportIfRequired(parse, ast, rewrite,
					"java.util.List");
			addImportIfRequired(parse, ast, rewrite,
					"org.mule.common.metadata.MetaDataKey");
			addImportIfRequired(parse, ast, rewrite,
					"org.mule.common.metadata.MetaData");
			
			applyChange(unit, rewrite);
		}
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}
}
