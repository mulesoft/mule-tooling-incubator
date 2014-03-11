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

	AddDatasenseMethodQuickFix(String label) {
		super(label);
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
			
//			MethodDeclaration method = ast.newMethodDeclaration();
//			method.setName(ast.newSimpleName("getMetaData"));
//			method.setBody(ast.newBlock());
//			Block block = method.getBody();
//			List<Modifier> modifiers = method.modifiers();
//			modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
//			method.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
//			
//			MarkerAnnotation ann=ast.newMarkerAnnotation();
//			ann.setTypeName(ast.newName("Optional"));
//			ListRewrite lr = rewrite.getListRewrite(method, MethodDeclaration.MODIFIERS2_PROPERTY);
//		    lr.insertFirst(ann, null);
//			
//			ListRewrite listRewrite = rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
//			//notice here, we just create a string placeholder, and string is simply as empty
//			Statement placeHolder = (Statement) rewrite.createStringPlaceholder("//TODO implement datasense", ASTNode.EMPTY_STATEMENT);
//			listRewrite.insertFirst(placeHolder, null);
//			placeHolder = (Statement) rewrite.createStringPlaceholder("return null;", ASTNode.EMPTY_STATEMENT);
//			listRewrite.insertLast(placeHolder, null);
			
			Statement placeHolder = (Statement) rewrite.createStringPlaceholder("@MetaDataKeyRetriever\npublic List<MetaDataKey> getKeys() throws Exception {\n\treturn null;\n}", ASTNode.EMPTY_STATEMENT);
			list.insertLast(placeHolder, null);
			
			placeHolder = (Statement) rewrite.createStringPlaceholder("@MetaDataRetriever\npublic MetaData getMetaData(MetaDataKey key) throws Exception {\n\treturn null;\n}", ASTNode.EMPTY_STATEMENT);
			list.insertLast(placeHolder, null);
			boolean hasMetaDataKeyRetriever = false;
			boolean hasMetaDataRetriever = false;
			boolean hasUtilsList = false;
			boolean hasMetaDataKey = false;
			boolean hasMetaData = false;
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
		    
			ListRewrite listImports = rewrite.getListRewrite(parse, CompilationUnit.IMPORTS_PROPERTY);
			ImportDeclaration id=null;
			
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
