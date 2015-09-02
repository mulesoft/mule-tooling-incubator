package org.mule.tooling.devkit.component;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;


public class DefaultBuilder implements IComponentBuilder {

    @Override
    public String getAnnotation(ICompilationUnit compilationUnit) {
        return "";
    }

    @Override
    public void createTypeMembers(Map<String, Object> options, IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
        
    }

}
