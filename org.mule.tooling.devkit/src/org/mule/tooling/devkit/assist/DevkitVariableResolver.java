package org.mule.tooling.devkit.assist;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

public class DevkitVariableResolver extends TemplateVariableResolver {

    CompilationUnit compilationUnit;

    public DevkitVariableResolver() {

    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    protected String resolve(TemplateContext context) {
        LocateModuleNameVisitor visitor = new LocateModuleNameVisitor();
        compilationUnit.accept(visitor);
        if (!visitor.getValue().isEmpty()) {
            return visitor.getValue();
        }
        return super.resolve(context);
    }
}
