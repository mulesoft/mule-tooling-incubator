package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class AddDatasenseMethodQuickFix extends QuickFix {

	AddDatasenseMethodQuickFix(String label, ConditionMarkerEvaluator evaluator) {
		super(label, evaluator);
	}

	@Override
	protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
		ASTRewrite rewrite = null;
		LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(
				errorMarkerStart);

		unit.accept(visitor);

		if (visitor.getNode() != null) {
			if (!unit.types().isEmpty()) {
				AST ast = unit.getAST();
				rewrite = ASTRewrite.create(ast);

				// for getting insertion position
				TypeDeclaration typeDecl = (TypeDeclaration) unit.types()
						.get(0);

				ListRewrite list = rewrite.getListRewrite(typeDecl,
						TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

				addDatasenseMethods(rewrite, list);

				addImports(unit, rewrite);
			}
		}
		return rewrite;
	}

	private void addDatasenseMethods(ASTRewrite rewrite, ListRewrite list) {
		Statement placeHolder = (Statement) rewrite
				.createStringPlaceholder(
						"@MetaDataKeyRetriever\npublic List<MetaDataKey> getKeys() throws Exception {\n\treturn null;\n}",
						ASTNode.EMPTY_STATEMENT);
		list.insertLast(placeHolder, null);

		placeHolder = (Statement) rewrite
				.createStringPlaceholder(
						"@MetaDataRetriever\npublic MetaData getMetaData(MetaDataKey key) throws Exception {\n\treturn null;\n}",
						ASTNode.EMPTY_STATEMENT);
		list.insertLast(placeHolder, null);
	}

	private void addImports(CompilationUnit unit, ASTRewrite rewrite) {
		addImportIfRequired(unit, rewrite,
				"org.mule.api.annotations.MetaDataKeyRetriever");
		addImportIfRequired(unit, rewrite,
				"org.mule.api.annotations.MetaDataRetriever");
		addImportIfRequired(unit, rewrite, "java.util.List");
		addImportIfRequired(unit, rewrite,
				"org.mule.common.metadata.MetaDataKey");
		addImportIfRequired(unit, rewrite,
				"org.mule.common.metadata.MetaData");
	}

	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}
}
