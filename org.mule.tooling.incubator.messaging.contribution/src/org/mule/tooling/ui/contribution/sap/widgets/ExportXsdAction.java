package org.mule.tooling.ui.contribution.sap.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.XmlMetaDataModel;
import org.mule.tooling.core.MuleCorePlugin;

public class ExportXsdAction extends ExportSapMetadataAction {

    @Override
    protected String getExportContents(MetaDataModel metaDataModel) {
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

    @Override
    protected String getExportExtension() {
        return "xsd";
    }

    @Override
    protected String getExportTitle(String sapObjectName) {
        return "Export XSD for " + sapObjectName;
    }   	
}
