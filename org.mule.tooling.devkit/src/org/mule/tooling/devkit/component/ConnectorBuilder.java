package org.mule.tooling.devkit.component;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;

public class ConnectorBuilder implements IComponentBuilder {

    @Override
    public String getAnnotation(ICompilationUnit compilationUnit) {
        return "@Connector(name = \"namespace-name\", friendlyName = \"Palette Display Name\")";
    }

    @Override
    public void createTypeMembers(Map<String, Object> options, IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
        imports.addImport("org.mule.api.annotations.Connector");
        imports.addImport("org.mule.api.annotations.Processor");
        newType.createMethod("@Processor public void foo(){ }", null, true, monitor);
    }

}
