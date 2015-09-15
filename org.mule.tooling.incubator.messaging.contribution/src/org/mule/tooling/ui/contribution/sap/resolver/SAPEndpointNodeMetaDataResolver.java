package org.mule.tooling.ui.contribution.sap.resolver;

import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultUnknownMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataModel;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.metadata.cache.IMetadataCache;
import org.mule.tooling.metadata.cache.MetadataCacheManager;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.model.messageflow.IMessageFlowNode;
import org.mule.tooling.model.messageflow.util.IMessageProcessorNode;
import org.mule.tooling.ui.modules.core.metadata.ConnectorMetaData;
import org.mule.tooling.ui.modules.core.metadata.MetadataHelpers;
import org.mule.tooling.ui.modules.core.metadata.MetadataPropagationManager;
import org.mule.tooling.ui.modules.core.metadata.resolver.ConnectorMetaDataFactory;
import org.mule.tooling.ui.modules.core.metadata.resolver.EndpointNodeMetaDataResolver;
import org.mule.tooling.ui.modules.core.metadata.resolver.MetaDataResolverUtils;

public class SAPEndpointNodeMetaDataResolver extends EndpointNodeMetaDataResolver {

    @Override
    protected MetaDataModel createOutputPayload(IMessageProcessorNode<?> node, IMuleProject project) {
        final IMessageFlowNode entity = (IMessageFlowNode) node.getValue();
        final String type = MetadataHelpers.getTypeAttributeName(entity);
        final String typeValue = MetadataUtils.getTypeValue(entity, type);
        IMetadataCache cache = MetadataCacheManager.getCacheFor(project);
        if (cache != null) {
            final MetaData outputMetadata = cache.getOutputMetadata(entity, typeValue);
            return outputMetadata != null ? outputMetadata.getPayload() : new DefaultUnknownMetaDataModel();
        } else {
            return new DefaultUnknownMetaDataModel();
        }
    }

    @Override
    public ConnectorMetaData getInputMetadata(MetadataPropagationManager propagator, IMessageProcessorNode<?> node, IMuleProject project) {
        final IMessageFlowNode entity = (IMessageFlowNode) node.getValue();
        final String type = MetadataHelpers.getTypeAttributeName(entity);
        final String typeValue = MetadataUtils.getTypeValue(entity, type);
        IMetadataCache cache = MetadataCacheManager.getCacheFor(project);
        MetaDataModel sapPayload;
        final ConnectorMetaData expectedOutputMetadata = propagator.getExpectedOutputMetadata(node, project);
        if (cache != null) {
            final MetaData outputMetadata = cache.getInputMetadata(entity, typeValue);
            
            if (outputMetadata != null) {
                sapPayload = outputMetadata.getPayload();
            } else {
                sapPayload = new DefaultUnknownMetaDataModel();
            }
        } else {
            sapPayload = new DefaultUnknownMetaDataModel();
        }
        final DefaultMetaData resultMetaData = new DefaultMetaData(sapPayload);
        MetaDataResolverUtils.copyAllProperties(expectedOutputMetadata != null ? expectedOutputMetadata.getMetadata() : ConnectorMetaDataFactory.createUnknownConnectorMetaData(node).getMetadata(), resultMetaData);
        return buildConnectorMetadata(node, project, resultMetaData);
    }

    public ConnectorMetaData buildConnectorMetadata(final IMessageProcessorNode<?> node, IMuleProject project, MetaData outputMetadata) {
        if (outputMetadata == null) {
            outputMetadata = ConnectorMetaDataFactory.createEmptyMetadata();
        }
        final IMessageFlowNode entity = (IMessageFlowNode) node.getValue();
        final String connectorName = getConnectorName(entity);
        final String operationName = getOperationName(node, project, entity);
        final String typeValue = getTypeValue(entity);
        return new ConnectorMetaData(outputMetadata, connectorName, operationName, typeValue);
    }

    protected String getTypeValue(final IMessageFlowNode entity) {
        final String typeName = MetadataHelpers.getTypeAttributeName(entity);
        final String typeValue = MetadataUtils.getTypeValue(entity, typeName);
        return typeValue;
    }

    protected String getOperationName(IMessageProcessorNode<?> node, IMuleProject project, final IMessageFlowNode entity) {
        return MetadataUtils.getOperationName(entity);
    }

    protected String getConnectorName(final IMessageFlowNode entity) {
        return MetadataUtils.getGlobalReference(entity);
    }

}
