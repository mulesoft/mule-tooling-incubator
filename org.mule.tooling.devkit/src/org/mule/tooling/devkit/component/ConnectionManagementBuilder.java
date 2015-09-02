package org.mule.tooling.devkit.component;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;

public class ConnectionManagementBuilder implements IComponentBuilder {

    @Override
    public String getAnnotation(ICompilationUnit compilationUnit) {
        return "@ConnectionManagement(configElementName = \"config-type\", friendlyName = \"Connection Managament type strategy\")";
    }

    @Override
    public void createTypeMembers(Map<String, Object> options, IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
        imports.addImport("org.mule.api.annotations.components.ConnectionManagement");
        imports.addImport("org.mule.api.ConnectionException");
        imports.addImport("org.mule.api.ConnectionExceptionCode");
        imports.addImport("org.mule.api.annotations.Configurable");
        imports.addImport("org.mule.api.annotations.Connect");
        imports.addImport("org.mule.api.annotations.ConnectionIdentifier");
        imports.addImport("org.mule.api.annotations.Disconnect");
        imports.addImport("org.mule.api.annotations.TestConnectivity");
        imports.addImport("org.mule.api.annotations.ValidateConnection");
        imports.addImport("org.mule.api.annotations.display.Password");
        imports.addImport("org.mule.api.annotations.param.ConnectionKey");
        
        newType.createMethod("@Connect @TestConnectivity public void connect(@ConnectionKey String username, @Password String password) throws ConnectionException { }", null,
                true, monitor);

        newType.createMethod("@Disconnect public void disconnect() { }", null, true, monitor);

        newType.createMethod("@ValidateConnection public boolean isConnected() { return false; }", null, true, monitor);

        newType.createMethod("@ConnectionIdentifier public String connectionId() { return \"001\"; }", null, true, monitor);
    }

}
