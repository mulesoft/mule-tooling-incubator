package org.mule.tooling.ui.contribution.sap.widgets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.mule.common.metadata.MetaDataModel;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.metadata.api.StudioInputOutputMetadataDescriptor;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.ui.modules.core.metadata.MetadataHelpers;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;

public abstract class ExportSapMetadataAction extends BaseSapAction {

    private String sapObject = null;
    private Integer xmlVersion = null;
    

    
	@Override
	public void click(AttributesPropertyPage page) {
        final MessageFlowNode node = page.getNode();
        final MessageFlowNode nodeToBeTested = MessageFlowUtils.generateNodeToTest(node, page.getHost());

        if (StringUtils.isNotEmpty(getSapObject())) {
            nodeToBeTested.getProperties().setProperty(MetadataHelpers.getTypeAttributeName(nodeToBeTested), getSapObject());
        }

        if (xmlVersion != null) {
            nodeToBeTested.getProperties().setProperty("xmlVersion", xmlVersion.toString());
        }        
        
        // I always want to bring the XML output, not the SapObject one
        nodeToBeTested.getProperties().setProperty("outputXml", "true");

        final StudioInputOutputMetadataDescriptor metadataDescriptor = retrieveMetaData(page, node, nodeToBeTested);

        if (metadataDescriptor != null) {
            Shell shell = page.getShell();
            if (!MetadataHelpers.isResultUseable(metadataDescriptor)) {
            	MetadataUtils.showGetMetadataErrorMessage(metadataDescriptor, false);
            } else {
                MetaDataModel metaDataModel = metadataDescriptor.getOutputMetaData(metadataDescriptor.getInputMetaData().get()).get().getPayload();
                String sapObjectName = StringUtils.isNotEmpty(getSapObject()) ? getSapObject() : getObjectType(page);
                String projectHomePath = getProjectBaseDirectory(page);
                FileDialog dialog = new FileDialog(shell, SWT.SAVE);
                dialog.setFilterPath(projectHomePath);
                dialog.setFilterExtensions(new String[] {getExportExtension()});
                dialog.setFileName(escapeObjectName(sapObjectName) + "." + getExportExtension());
                dialog.setText(getExportTitle(sapObjectName));
                dialog.setOverwrite(true);

                String path = null;
                if ((path = dialog.open()) != null) {
                    try {
                        writeFile(path, getExportContents(metaDataModel));
                        // Refresh
                        if(path.startsWith(projectHomePath)) {
                            page.getMuleProject().getJavaProject().getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                        }
                    } catch(Exception ex) {
                        MetadataUtils.openError(shell, "Failed to save file " + path + ": " + ex.getMessage() , null, ex, getExportTitle(sapObjectName), false);
                    }
                }
            }
        }
	}
	
    protected abstract String getExportContents(MetaDataModel metaDataModel);

    protected abstract String getExportExtension();
    
    protected abstract String getExportTitle(String sapObjectName);
    
    public String getSapObject() {
        return sapObject;
    }

    
    public void setSapObject(String sapObject) {
        this.sapObject = sapObject;
    }

    
    public Integer getXmlVersion() {
        return xmlVersion;
    }

    
    public void setXmlVersion(Integer xmlVersion) {
        this.xmlVersion = xmlVersion;
    }   	
}
