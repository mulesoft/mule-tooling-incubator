package org.mule.tooling.devkit.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

@SuppressWarnings("restriction")
public class DevkitVariableResolver extends TemplateVariableResolver {

    protected String resolve(TemplateContext context) {
        if (context.getContextType().getId().equals(JavaContextType.ID_MEMBERS)) {
            if (context instanceof JavaContext) {
                JavaContext javaContext = (JavaContext) context;
                ICompilationUnit compilationUnit = javaContext.getCompilationUnit();
                CompilationUnit cu = parse(compilationUnit);
                LocateModuleNameVisitor visitor = new LocateModuleNameVisitor();
                cu.accept(visitor);
                if (!visitor.getValue().isEmpty()) {
                    return visitor.getValue();
                }
            }
        }
        return super.resolve(context);
    }

    private CompilationUnit parse(ICompilationUnit unit) {
        return org.eclipse.jdt.ui.SharedASTProvider.getAST(unit, org.eclipse.jdt.ui.SharedASTProvider.WAIT_YES, null);
    }
}
