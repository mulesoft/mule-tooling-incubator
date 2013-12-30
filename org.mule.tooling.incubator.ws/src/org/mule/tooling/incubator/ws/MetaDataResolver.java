
package org.mule.tooling.incubator.ws;

import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultXmlMetaDataModel;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.util.IMessageProcessorNode;
import org.mule.tooling.model.messageflow.util.MessageProcessorNode;
import org.mule.tooling.ui.modules.core.metadata.ConnectorMetaData;
import org.mule.tooling.ui.modules.core.metadata.INodeMetadataResolver;
import org.mule.tooling.ui.modules.core.metadata.MetadataPropagationManager;

import java.nio.charset.Charset;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

public class MetaDataResolver implements INodeMetadataResolver
{

    @Override
    public ConnectorMetaData getInputMetadata(MetadataPropagationManager manager,
                                              IMessageProcessorNode<?> messageProcessorNode,
                                              IMuleProject project)
    {
        MessageProcessorNode<MessageFlowNode> node = (MessageProcessorNode<MessageFlowNode>) messageProcessorNode;
        String wsdlLocation = (node.getValue().getProperties().getProperty("wsdlLocation").getValue());
        String wsdlService = (node.getValue().getProperties().getProperty("wsdlService").getValue());
        String wsdlPort = (node.getValue().getProperties().getProperty("wsdlPort").getValue());
        String wsdlOperation = (node.getValue().getProperties().getProperty("wsdlOperation").getValue());

        try
        {
            Definition def = WSDLUtils.loadWSDL(wsdlLocation);
            List<String> schemas = WSDLUtils.getSchemas(def);

            DefaultMetaData metaData = new DefaultMetaData(new DefaultXmlMetaDataModel(schemas,
                getInputElementName(def, wsdlService, wsdlPort, wsdlOperation), Charset.defaultCharset()));

            return new ConnectorMetaData(metaData, "webservices", wsdlOperation, "Web Service");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ConnectorMetaData getOutputMetadata(MetadataPropagationManager manager,
                                               IMessageProcessorNode<?> messageProcessorNode,
                                               IMuleProject project)
    {
        MessageProcessorNode<MessageFlowNode> node = (MessageProcessorNode<MessageFlowNode>) messageProcessorNode;
        String wsdlLocation = (node.getValue().getProperties().getProperty("wsdlLocation").getValue());
        String wsdlService = (node.getValue().getProperties().getProperty("wsdlService").getValue());
        String wsdlPort = (node.getValue().getProperties().getProperty("wsdlPort").getValue());
        String wsdlOperation = (node.getValue().getProperties().getProperty("wsdlOperation").getValue());

        try
        {
            Definition def = WSDLUtils.loadWSDL(wsdlLocation);
            List<String> schemas = WSDLUtils.getSchemas(def);

            DefaultMetaData metaData = new DefaultMetaData(new DefaultXmlMetaDataModel(schemas,
                getOutputElementName(def, wsdlService, wsdlPort, wsdlOperation), Charset.defaultCharset()));

            return new ConnectorMetaData(metaData, "webservices", wsdlOperation, "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String getInputElementName(Definition def,
                                       String wsdlService,
                                       String wsdlPort,
                                       String wsdlOperation)
    {
        return ((Part) def.getService(new QName(def.getTargetNamespace(), wsdlService))
            .getPort(wsdlPort)
            .getBinding()
            .getPortType()
            .getOperation(wsdlOperation, null, null)
            .getInput()
            .getMessage()
            .getParts()
            .values()
            .iterator()
            .next()).getElementName().toString();
    }

    private String getOutputElementName(Definition def,
                                        String wsdlService,
                                        String wsdlPort,
                                        String wsdlOperation)
    {
        return ((Part) def.getService(new QName(def.getTargetNamespace(), wsdlService))
            .getPort(wsdlPort)
            .getBinding()
            .getPortType()
            .getOperation(wsdlOperation, null, null)
            .getOutput()
            .getMessage()
            .getParts()
            .values()
            .iterator()
            .next()).getElementName().toString();
    }

}
