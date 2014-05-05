package org.mule.tooling.devkit.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

@SuppressWarnings("restriction")
public class DevkitVariableResolver extends TemplateVariableResolver {

    protected String resolve(TemplateContext context) {
        if (context.getContextType().getId().equals(JavaContextType.ID_MEMBERS)) {
            JavaContext javaContext = (JavaContext) context;
            ICompilationUnit compilationUnit = javaContext.getCompilationUnit();
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setSource(compilationUnit);
            parser.setResolveBindings(true);
            CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            LocateModuleNameVisitor visitor = new LocateModuleNameVisitor();
            cu.accept(visitor);
            if (!visitor.getValue().isEmpty()) {
                return visitor.getValue();
            }
        }
        return super.resolve(context);
    }
}
