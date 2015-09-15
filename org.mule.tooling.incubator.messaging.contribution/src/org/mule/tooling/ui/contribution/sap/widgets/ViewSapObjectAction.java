package org.mule.tooling.ui.contribution.sap.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.XmlMetaDataModel;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.messageflow.util.MessageFlowUtils;
import org.mule.tooling.metadata.api.StudioInputOutputMetadataDescriptor;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.ui.contribution.sap.widgets.meta.ViewSapObjectDialog;
import org.mule.tooling.ui.modules.core.metadata.MetadataHelpers;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;

public class ViewSapObjectAction extends BaseSapAction {

    
	@Override
	public void click(AttributesPropertyPage page) {
        final MessageFlowNode node = page.getNode();
        final MessageFlowNode nodeToBeTested = MessageFlowUtils.generateNodeToTest(node, page.getHost());
        
        // I always want to bring the XML output, not the SapObject one
        nodeToBeTested.getProperties().setProperty("outputXml", "true");
        
        final StudioInputOutputMetadataDescriptor metadataDescriptor = retrieveMetaData(page, node, nodeToBeTested);
        
        if (metadataDescriptor != null) {
            if (!MetadataHelpers.isResultUseable(metadataDescriptor)) {
                MetadataUtils.showGetMetadataErrorMessage(metadataDescriptor, false);
            } else {
                MetaDataModel metaDataModel = metadataDescriptor.getOutputMetaData(metadataDescriptor.getInputMetaData().get()).get().getPayload();

                ViewSapObjectDialog dialog = new ViewSapObjectDialog(page.getParent().getShell(), page);
                dialog.setXmlTemplate(getXmlTemplateContents(metaDataModel));
                dialog.setXsd(getXsdContents(metaDataModel));
                dialog.setProjectBaseDirectory(getProjectBaseDirectory(page));
                dialog.setBlockOnOpen(true);
                dialog.open();
            }
        }
	}
	
	
    private String getXmlTemplateContents(MetaDataModel metaDataModel) {
        if(metaDataModel instanceof XmlMetaDataModel) {
            return ((XmlMetaDataModel) metaDataModel).getExample();
        } else {
            return null;
        }
    }	
    
    private String getXsdContents(MetaDataModel metaDataModel) {
        if(metaDataModel instanceof XmlMetaDataModel) {
            List<InputStream> schemas = ((XmlMetaDataModel) metaDataModel).getSchemas();
            
            if (schemas != null && schemas.size() > 0) {
                try {
                    return IOUtils.toString(schemas.get(0));
                } catch(IOException ioEx) {
                    MuleCorePlugin.logError("Could not process SAP object XSD", ioEx);
                    return null;
                } finally {
                    IOUtils.closeQuietly(schemas.get(0));
                }
            } else {
                return null;
            }
            
        } else {
            return null;
        }
    }     
}
