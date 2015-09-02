package org.mule.tooling.devkit.component;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;

public class HandlerBuilder implements IComponentBuilder {

    @Override
    public String getAnnotation(ICompilationUnit compilationUnit) {
        return "@Handler";
    }

    @Override
    public void createTypeMembers(Map<String, Object> options, IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
        imports.addImport("org.mule.api.annotations.Handle");
        imports.addImport("org.mule.api.annotations.components.Handler");

        newType.createMethod("@Handle public void handleException(Exception ex) throws Exception{ }", null, true, monitor);
    }

}
