package org.mule.tooling.ui.contribution.debugger.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeNode;
import org.mule.tooling.core.StudioDesignContextRunner;
import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.ui.contribution.debugger.controller.events.ISnapshotClearedHandler;
import org.mule.tooling.ui.contribution.debugger.controller.events.ISnapshotRemovedHandler;
import org.mule.tooling.ui.contribution.debugger.controller.events.ISnapshotTakenHandler;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotEventTypes;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.ui.contribution.debugger.service.MessageSnapshotService;
import org.mule.tooling.ui.contribution.debugger.utils.FlowEditorEntityEditPartPair;
import org.mule.tooling.ui.contribution.debugger.utils.MuleDebuggerUtils;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;

import com.mulesoft.mule.debugger.commons.IObjectFactory;
import com.mulesoft.mule.debugger.commons.MapObjectFactory;
import com.mulesoft.mule.debugger.commons.MessageSnapshot;
import com.mulesoft.mule.debugger.response.ObjectFieldDefinition;
import com.mulesoft.mule.debugger.response.ObjectFieldDefinitionFactory;

public class MuleSnapshotsController {

    private IMuleSnapshotEditor editor;
    private EventBus eventBus;
    private MessageSnapshotService service;

    public MuleSnapshotsController(MessageSnapshotService service, IMuleSnapshotEditor editor, EventBus eventBus) {
        super();
        this.service = service;
        this.editor = editor;
        this.eventBus = eventBus;
        bind();
    }

    public void bind() {
        editor.getSnapshotTable().setContentProvider(new ArrayContentProvider());
        editor.getSnapshotTable().setInput(service.getAllDefinedSnapshots());
        editor.getSnapshotTable().setLabelProvider(new MessageSnapshotLabelProvider());
        eventBus.registerUIThreadListener(SnapshotEventTypes.SNAPSHOT_TAKEN, new ISnapshotTakenHandler() {

            @Override
            public void onSnapshotTaken(MessageSnapshotDecorator snapshot) {
                editor.getSnapshotTable().add(snapshot);
            }
        });

        eventBus.registerUIThreadListener(SnapshotEventTypes.SNAPSHOT_CLEARED, new ISnapshotClearedHandler() {

            @Override
            public void onSnapshotsCleared() {
                editor.getSnapshotTable().setInput(service.getAllDefinedSnapshots());
            }
        });

        eventBus.registerUIThreadListener(SnapshotEventTypes.SNAPSHOT_REMOVED, new ISnapshotRemovedHandler() {

            @Override
            public void onSnapshotRemoved(MessageSnapshotDecorator snapshot) {
                editor.getSnapshotTable().remove(snapshot);
            }

        });

        editor.getPreviewSnapshot().getPayloadTreeViewer().setContentProvider(new ObjectFieldDefinitionTreeContentProvider());
        editor.getPreviewSnapshot().getPayloadTreeViewer().setLabelProvider(new TreeNodeLabelProvider());
        editor.getPreviewSnapshot().getPayloadTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (!selection.isEmpty()) {
                    IStructuredSelection treeSelection = (IStructuredSelection) selection;
                    TreeNode node = (TreeNode) treeSelection.getFirstElement();
                    ObjectFieldDefinition def = (ObjectFieldDefinition) node.getValue();
                    editor.getPreviewSnapshot().setSelectionTextValue(def.getValue());
                } else {
                    editor.getPreviewSnapshot().setSelectionTextValue("");
                }
            }
        });

        editor.getSnapshotTable().addOpenListener(new IOpenListener() {

            @Override
            public void open(OpenEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!selection.isEmpty()) {
                    final MessageSnapshotDecorator snapshotDescriptor = (MessageSnapshotDecorator) selection.getFirstElement();
                    StudioDesignContextRunner.runSilentWithMuleProject(new Callable<Void>() {

                        @Override
                        public Void call() throws Exception {
                            FlowEditorEntityEditPartPair findEditPartAndFlowEditor = MuleDebuggerUtils.findEditPartAndFlowEditor(snapshotDescriptor.getSnapshot().getAppName(),
                                    snapshotDescriptor.getSnapshot().getPath(), true);
                            if (findEditPartAndFlowEditor != null) {
                                findEditPartAndFlowEditor.getEditor().getFlowEditor()
                                        .setSelection(new org.eclipse.jface.viewers.StructuredSelection(findEditPartAndFlowEditor.getEditPart()));
                            }
                            return null;
                        }
                    }, snapshotDescriptor.getProject());

                }

            }
        });

        editor.getSnapshotTable().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    final IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
                    final MessageSnapshotDecorator snapshotDescriptor = (MessageSnapshotDecorator) structuredSelection.getFirstElement();
                    final MessageSnapshot snapshot = snapshotDescriptor.getSnapshot();
                    final IObjectFactory<?> payload = snapshot.getPayload();
                    final String appName = snapshot.getAppName();
                    IMuleProject muleProject = CoreUtils.getMuleProject(appName);
                    if (muleProject != null) {
                        ClassLoader newClassLoader = MetadataUtils.createMuleClassLoader(muleProject);
                        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                        try {
                            Thread.currentThread().setContextClassLoader(newClassLoader);
                            final Object createObject = payload.createObject();
                            final ObjectFieldDefinition payloadDefinition = ObjectFieldDefinitionFactory.createFromObject(createObject, "payload", "#[payload]");
                            final TreeNode payloadTreeNode = LocalObjectTreeNodeBuilder.createTreeNode(payloadDefinition);
                            final TreeNode flowVariablesTreeNode = createFlowVariablesTreeNode(snapshot);
                            final TreeNode inboundPropertiesDefinitionNode = createInboundPropertiesTreeNode(snapshot);
                            final TreeNode outboundPropertiesTreeNode = createOutboundPropertiesTreeNode(snapshot);
                            final TreeNode sessionVariablesTreeNode = createSessionVariablesTreeNode(snapshot);
                            editor.getPreviewSnapshot()
                                    .getPayloadTreeViewer()
                                    .setInput(
                                            new TreeNode[] { payloadTreeNode, flowVariablesTreeNode, inboundPropertiesDefinitionNode, outboundPropertiesTreeNode,
                                                    sessionVariablesTreeNode });
                        } finally {
                            Thread.currentThread().setContextClassLoader(contextClassLoader);
                        }
                    }
                } else {
                    editor.getPreviewSnapshot().getPayloadTreeViewer().setInput(null);
                }

            }
        });
    }

    protected TreeNode createInboundPropertiesTreeNode(MessageSnapshot snapshot) {
        // Inbound Properties
        final MapObjectFactory inboundProperties = snapshot.getInboundProperties();
        final Map<String, Object> inboundPropertiesMap = inboundProperties.createObject();
        final List<ObjectFieldDefinition> inboundPropertiesDefinitions = new ArrayList<ObjectFieldDefinition>();
        for (Entry<String, Object> inboundProp : inboundPropertiesMap.entrySet()) {
            ObjectFieldDefinition variableDefinition = ObjectFieldDefinitionFactory.createFromObject(inboundProp.getValue(), inboundProp.getKey(), "#[inboundProperties['"
                    + inboundProp.getKey() + "']");
            inboundPropertiesDefinitions.add(variableDefinition);
        }
        final ObjectFieldDefinition inboundPropertiesDefinition = new ObjectFieldDefinition("Inbound Properties", "", "", inboundPropertiesDefinitions, null);
        final TreeNode inboundPropertiesDefinitionNode = LocalObjectTreeNodeBuilder.createTreeNode(inboundPropertiesDefinition);
        return inboundPropertiesDefinitionNode;
    }

    protected TreeNode createOutboundPropertiesTreeNode(MessageSnapshot snapshot) {
        // Outbound Properties
        final MapObjectFactory outboundProperties = snapshot.getOutboundProperties();
        final Map<String, Object> outboundPropertiesMap = outboundProperties.createObject();
        final List<ObjectFieldDefinition> inboundPropertiesDefinitions = new ArrayList<ObjectFieldDefinition>();
        for (Entry<String, Object> outboundProp : outboundPropertiesMap.entrySet()) {
            ObjectFieldDefinition variableDefinition = ObjectFieldDefinitionFactory.createFromObject(outboundProp.getValue(), outboundProp.getKey(), "#[outboundProperties['"
                    + outboundProp.getKey() + "']");
            inboundPropertiesDefinitions.add(variableDefinition);
        }
        final ObjectFieldDefinition inboundPropertiesDefinition = new ObjectFieldDefinition("Outbound Properties", "", "", inboundPropertiesDefinitions, null);
        final TreeNode outboundPropertiesDefinitionNode = LocalObjectTreeNodeBuilder.createTreeNode(inboundPropertiesDefinition);
        return outboundPropertiesDefinitionNode;
    }

    protected TreeNode createSessionVariablesTreeNode(MessageSnapshot snapshot) {
        // Session variables
        final MapObjectFactory sessionVariables = snapshot.getSessionVars();
        final Map<String, Object> outboundPropertiesMap = sessionVariables.createObject();
        final List<ObjectFieldDefinition> sessionVariablesDefinitions = new ArrayList<ObjectFieldDefinition>();
        for (Entry<String, Object> sessionVariable : outboundPropertiesMap.entrySet()) {
            ObjectFieldDefinition variableDefinition = ObjectFieldDefinitionFactory.createFromObject(sessionVariable.getValue(), sessionVariable.getKey(), "#[sessionVars['"
                    + sessionVariable.getKey() + "']");
            sessionVariablesDefinitions.add(variableDefinition);
        }
        final ObjectFieldDefinition inboundPropertiesDefinition = new ObjectFieldDefinition("Session Variables", "", "", sessionVariablesDefinitions, null);
        final TreeNode outboundPropertiesDefinitionNode = LocalObjectTreeNodeBuilder.createTreeNode(inboundPropertiesDefinition);
        return outboundPropertiesDefinitionNode;
    }

    protected TreeNode createFlowVariablesTreeNode(MessageSnapshot snapshot) {
        // Flow Variables
        final MapObjectFactory flowVars = snapshot.getFlowVars();
        final Map<String, Object> flowVariablesMap = flowVars.createObject();
        final List<ObjectFieldDefinition> flowVariablesDefinitions = new ArrayList<ObjectFieldDefinition>();
        for (Entry<String, Object> flowVar : flowVariablesMap.entrySet()) {
            ObjectFieldDefinition variableDefinition = ObjectFieldDefinitionFactory
                    .createFromObject(flowVar.getValue(), flowVar.getKey(), "#[flowVars['" + flowVar.getKey() + "']");
            flowVariablesDefinitions.add(variableDefinition);
        }
        ObjectFieldDefinition flowVariablesDefinition = new ObjectFieldDefinition("Flow Variables", "", "", flowVariablesDefinitions, null);
        final TreeNode flowVariablesTreeNode = LocalObjectTreeNodeBuilder.createTreeNode(flowVariablesDefinition);
        return flowVariablesTreeNode;
    }
}