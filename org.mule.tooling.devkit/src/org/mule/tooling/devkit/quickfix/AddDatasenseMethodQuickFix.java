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

@SuppressWarnings("restriction")
public class AddDatasenseMethodQuickFix extends QuickFix{

	AddDatasenseMethodQuickFix(String label,ConditionMarkerEvaluator evaluator) {
		super(label,evaluator);
	}
	
	protected void createAST(ICompilationUnit unit,Integer charStart) throws JavaModelException {
		CompilationUnit parse = parse(unit);
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
			boolean hasMetaDataKeyRetriever = false;
			boolean hasMetaDataRetriever = false;
			boolean hasUtilsList = false;
			boolean hasMetaDataKey = false;
			boolean hasMetaData = false;

			ListRewrite listImports = rewrite.getListRewrite(parse, CompilationUnit.IMPORTS_PROPERTY);
			ImportDeclaration id=null;
			
			for(Object obj:parse.imports()){
				ImportDeclaration importDec=(ImportDeclaration) obj;
				if(importDec.getName().getFullyQualifiedName().equals("org.mule.api.annotations.MetaDataKeyRetriever")){
					hasMetaDataKeyRetriever = true;
				}
				if(importDec.getName().getFullyQualifiedName().equals("org.mule.api.annotations.MetaDataRetriever")){
					hasMetaDataRetriever = true;
				}
				if(importDec.getName().getFullyQualifiedName().equals("java.util.List")){
					hasUtilsList = true;
				}
				if(importDec.getName().getFullyQualifiedName().equals("org.mule.common.metadata.MetaDataKey")){
					hasMetaDataKey= true;
				}
				if(importDec.getName().getFullyQualifiedName().equals("org.mule.common.metadata.MetaData")){
					hasMetaData= true;
				}
				
			}
		    
			
			if(!hasMetaDataKeyRetriever){
				id=ast.newImportDeclaration();
				id.setName(ast.newName("org.mule.api.annotations.MetaDataKeyRetriever"));
				listImports.insertLast(id, null);
			}
			if(!hasMetaDataRetriever){
				id=ast.newImportDeclaration();
				id.setName(ast.newName("org.mule.api.annotations.MetaDataRetriever"));
				listImports.insertLast(id, null);
			}
			
			if(!hasUtilsList){
				id = ast.newImportDeclaration();
				id.setName(ast.newName("java.util.List"));
				listImports.insertLast(id, null);
			}
			if(!hasMetaDataKey){
				id = ast.newImportDeclaration();
				id.setName(ast.newName("org.mule.common.metadata.MetaDataKey"));
				listImports.insertLast(id, null);
			}
			if(!hasMetaData){
				id = ast.newImportDeclaration();
				id.setName(ast.newName("org.mule.common.metadata.MetaData"));
				listImports.insertLast(id, null);
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
}
