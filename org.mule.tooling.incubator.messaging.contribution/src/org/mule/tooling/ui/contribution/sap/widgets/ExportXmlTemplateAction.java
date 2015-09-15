package org.mule.tooling.ui.contribution.sap.widgets;

import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.XmlMetaDataModel;

public class ExportXmlTemplateAction extends ExportSapMetadataAction {

    @Override
    protected String getExportContents(MetaDataModel metaDataModel) {
        if(metaDataModel instanceof XmlMetaDataModel) {
            return ((XmlMetaDataModel) metaDataModel).getExample();
        } else {
            return null;
        }
    }

    @Override
    protected String getExportExtension() {
        return "xml";
    }

    @Override
    protected String getExportTitle(String sapObjectName) {
        return "Export XML template for " + sapObjectName;
    }   	
}
