package org.mule.tooling.devkit.component;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;

public class MedaDataCategoryBuilder implements IComponentBuilder {

    @Override
    public String getAnnotation(ICompilationUnit compilationUnit) {
        return "@MetaDataCategory";
    }

    @Override
    public void createTypeMembers(Map<String, Object> options, IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {

        imports.addImport("javax.inject.Inject");
        imports.addImport("java.util.List");
        imports.addImport("org.mule.api.annotations.MetaDataKeyRetriever");
        imports.addImport("org.mule.api.annotations.MetaDataRetriever");
        imports.addImport("org.mule.api.annotations.components.MetaDataCategory");
        imports.addImport("org.mule.common.metadata.MetaData");
        imports.addImport("org.mule.common.metadata.MetaDataKey");

        newType.createField("@Inject private Object connector;", null, true, monitor);

        newType.createMethod("/**\n" + " * Retrieves the list of keys\n" + " */\n"
                + "@MetaDataKeyRetriever public List<MetaDataKey> getMetaDataKeys() throws Exception { return null; }", null, true, monitor);

        newType.createMethod("/**\n" + " * Get MetaData given a key\n" + " */\n"
                + "@MetaDataRetriever public MetaData getMetaData(MetaDataKey key) throws Exception { return null; }", null, true, monitor);

        newType.createMethod("public void setConnector(Object connector) throws Exception { this.connector=connector; }", null, true, monitor);
    }
}
