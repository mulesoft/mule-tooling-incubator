package org.mule.tooling.ui.contribution.sap.widgets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.mule.common.metadata.MetaData;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.metadata.api.StudioInputOutputMetadataDescriptor;
import org.mule.tooling.metadata.api.utils.DefaultStudioMetadataDescriptor;
import org.mule.tooling.metadata.cache.IMetadataCacheIO;
import org.mule.tooling.metadata.cache.MetadataCacheManager;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.sap.metadata.SAPMetadataStrategy;
import org.mule.tooling.ui.modules.core.metadata.MetadataHelpers;
import org.mule.tooling.ui.modules.core.metadata.MetadataStatus;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.meta.IDialogAction;


public abstract class BaseSapAction implements IDialogAction {

    public BaseSapAction() {
    }

    protected String escapeObjectName(String sapObjectName) {
        return StringUtils.replace(sapObjectName, "/", "_");
    }
    
    
    protected StudioInputOutputMetadataDescriptor retrieveMetaData(AttributesPropertyPage page, MessageFlowNode oldNode, MessageFlowNode newNode) {
        final MetadataStatus metadataStatus = MetadataHelpers.getMetadataStatus(newNode.getProperties().toPropertyCollectionMap(), oldNode);
        final String connectorRefName = MetadataUtils.getGlobalReference(newNode);
        final String operationName = MetadataUtils.getOperationName(newNode);
        final String typeName = MetadataHelpers.getTypeAttributeName(newNode);
        final String typeValue = MetadataUtils.getTypeValue(newNode, typeName);

        // the connector MUST be configured in order to use/store metadata. If it is null, no metadata is retrieved. :)
        if (!StringUtils.isBlank(connectorRefName) && metadataStatus != null && metadataStatus != MetadataStatus.OFF) {
            // Save Current metadata
            StudioInputOutputMetadataDescriptor currentMetadata = doGetMetaDataFromCache(page, connectorRefName, operationName, typeName, typeValue);
            // Delete current metadata
            doSetMetaDataToCache(page, connectorRefName, operationName, typeName, typeValue, null);
            
            try {
                SAPMetadataStrategy metadator = (SAPMetadataStrategy) MetadataHelpers.getMetadataStrategy(newNode.getProperties(), newNode.getType());
                metadator.setForceRetrieveMetadata(true);
                final MuleConfiguration muleConfiguration = MessageFlowUtils.getMuleConfigFromActivePage();
                if (muleConfiguration != null) {
                    if (metadator.getMetadata(new PropertyCollectionMap(oldNode.getProperties()), newNode, page.getMuleProject(), muleConfiguration, true, true)) {
                        return getMetaDataFromCache(page, oldNode, newNode);
                    }
                }
                
                return null;
            } finally {
                // Restore current metadata
                doSetMetaDataToCache(page, connectorRefName, operationName, typeName, typeValue, currentMetadata);
            }
        } else {
            if (StringUtils.isBlank(connectorRefName)) {
                MessageDialog.openError(page.getShell(), "SAP Connection Configuration missing", "The SAP Connection Configuration is required in order to perform the required action.");
            }
            return null;        
        }
    }
    
    private void doSetMetaDataToCache(AttributesPropertyPage page, final String connectorRefName, final String operationName, final String typeName, final String typeValue, StudioInputOutputMetadataDescriptor metaData) {
        MetaData input = null;
        MetaData output = null;
        
        final IMuleProject muleProject = page.getMuleProject();
        if (metaData != null) {
            input = metaData.getInputMetaData() != null ? metaData.getInputMetaData().get() : null;
            output = metaData.getOutputMetaData(input) != null ? metaData.getOutputMetaData(input).get() : null;
            MetadataCacheManager.add(muleProject, connectorRefName, operationName, typeValue, input, output);
        } else {
            MetadataCacheManager.getCacheFor(muleProject).resetOperationMetadataType(connectorRefName, operationName, typeValue);
        }
    }
    
    private StudioInputOutputMetadataDescriptor getMetaDataFromCache(AttributesPropertyPage page, MessageFlowNode oldNode, MessageFlowNode newNode) {
        final String connectorRefName = MetadataUtils.getGlobalReference(newNode);
        final String operationName = MetadataUtils.getOperationName(newNode);
        final String typeName = MetadataHelpers.getTypeAttributeName(newNode);
        final String typeValue = MetadataUtils.getTypeValue(newNode, typeName);

        if (StringUtils.isBlank(connectorRefName)) {
            MessageDialog.openError(page.getShell(), "SAP Connection Configuration missing", "The SAP Connection Configuration is required in order to perform the required action.");
        } else {
            StudioInputOutputMetadataDescriptor metadataDescriptor = doGetMetaDataFromCache(page, connectorRefName, operationName, typeName, typeValue); 
            if (metadataDescriptor != null) {
                return metadataDescriptor;
            } else {
                MuleCorePlugin.logWarning("Could not complete required operation. Requested DataSense cache entry not found for [connector-ref=" + connectorRefName + ", operationName=" + operationName + ", " + typeName + "=" + typeValue + "]" , null);
            }
        }
        return null;
    }

    private StudioInputOutputMetadataDescriptor doGetMetaDataFromCache(AttributesPropertyPage page, final String connectorRefName, final String operationName, final String typeName, final String typeValue) {
        StudioInputOutputMetadataDescriptor metadataDescriptor = null;

        // The Cache is now the entry point for payload metadata. This is not needed for static-operations. Change this?
        final IMetadataCacheIO typeOperationCache = MetadataCacheManager.getCacheFor(page.getMuleProject()).getMetadata(connectorRefName, operationName, typeValue);
        
        if (typeOperationCache != null) {
            // the operation metadata is in the cache and is not transient (queries and others)
            metadataDescriptor = new DefaultStudioMetadataDescriptor(typeOperationCache.getInputMetadata(), typeOperationCache.getOutputMetadata());
            
            return metadataDescriptor;
        } else {
            return null;
        }
    }    
    
    
    protected void writeFile(String path, String contents) throws IOException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(path);
            IOUtils.write(contents, output);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
    
    protected String getObjectType(AttributesPropertyPage page) {
        final String typeName = MetadataHelpers.getTypeAttributeName(page.getNode());
        final String typeValue = MetadataUtils.getTypeValue(page.getNode(), typeName);
        
        return typeValue != null && typeValue.length() > 0 ? typeValue : "template";
    }    
    
    protected String getProjectBaseDirectory(AttributesPropertyPage page) {
        return page.getMuleProject().getJavaProject().getProject().getLocation().toOSString();
    }    
}
