package org.mule.tooling.devkit.assist;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.text.edits.MalformedTreeException;

public class ImportResolver extends TemplateVariableResolver {

    private CompilationUnit compilationUnit;
    private IDocument document;
    private int offset;

    protected String resolve(TemplateContext context) {
        return "";
    }

    @Override
    public void resolve(TemplateVariable variable, TemplateContext context) {
        variable.setUnambiguous(true);
        variable.setValue("");
        offset = 0;
        @SuppressWarnings("unchecked")
        List<String> params = variable.getVariableType().getParams();
        AST ast = compilationUnit.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        if (params.size() > 0) {
            for (Iterator<String> iterator = params.iterator(); iterator.hasNext();) {
                String typeName = iterator.next();
                System.out.println(typeName);
                addImportIfRequired(compilationUnit, rewrite, typeName);
            }
        }
        try {
            rewrite.rewriteAST(getDocument(), null).apply(getDocument());
        } catch (MalformedTreeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setDocument(IDocument doc) {
        this.document = doc;
    }

    private IDocument getDocument() {
        // TODO Auto-generated method stub
        return document;
    }

    protected boolean addImportIfRequired(CompilationUnit compilationUnit, ASTRewrite rewrite, String fullyQualifiedName) {
        AST ast = compilationUnit.getAST();
        boolean hasConnectorAnnotationImport = false;

        ListRewrite listImports = rewrite.getListRewrite(compilationUnit, CompilationUnit.IMPORTS_PROPERTY);

        for (Object obj : compilationUnit.imports()) {
            ImportDeclaration importDec = (ImportDeclaration) obj;
            if (importDec.getName().getFullyQualifiedName().equals(fullyQualifiedName)) {
                hasConnectorAnnotationImport = true;
            }
        }

        ImportDeclaration id = null;

        if (!hasConnectorAnnotationImport) {
            offset += fullyQualifiedName.length() + 10;
            id = ast.newImportDeclaration();
            id.setName(ast.newName(fullyQualifiedName));
            listImports.insertLast(id, null);
            return true;
        }
        return false;
    }

    @Override
    protected String[] resolveAll(TemplateContext context) {
        return new String[0];
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public int getOffset() {
        return offset;
    }
}
