package org.mule.tooling.ui.contribution.munit.editors.custom;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.outline.LabelRetrieverEntityVisitor;
import org.mule.tooling.messageflow.outline.MessageFlowOutlinePage;
import org.mule.tooling.model.messageflow.MessageFlowEntity;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.util.IMessageProcessorNode;
import org.mule.tooling.model.messageflow.util.MessageProcessorNode;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.editors.MunitMessageFlowEditor;
import org.mule.tooling.ui.contribution.munit.editors.custom.validators.MessageProcessorMatcherValidator;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.widgets.table.MapTableComposite;
import org.mule.tooling.ui.widgets.util.WidgetUtils;

/**
 * <p>
 * The part of every Munit custom editor that allows the user to match a message processor
 * </p>
 */
public class MessageProcessorMatchingForm {

    private static final String DOC_NAME = "doc:name";

    private static final String ALL_MESSAGE_PROCESSORS_REGEX = ".*:.*";

    private static final int ATTRIBUTE_VIEWER_COLUMNS = 3;

    private MessageFlowEditor messageFlowEditor;

    private Text messageProcessorMatchingRegex;
    private MapTableComposite attributeMatchingTable;

    private Action showOutline;
    private MessageFlowOutlinePage outlinePage;

    private String initialMessage;

    private AttributesPropertyPage propertyPage;
    private MessageProcessorMatcherValidator messageProcessorMatcherValidator;

    public static MessageProcessorMatchingForm newMockingInstance(MessageFlowEditor messageFlowEditor, AttributesPropertyPage propertyPage) {
        return new MessageProcessorMatchingForm(messageFlowEditor, "When message processor matches:", propertyPage);
    }

    public static MessageProcessorMatchingForm newVerifyInstance(MessageFlowEditor messageFlowEditor, AttributesPropertyPage propertyPage) {
        return new MessageProcessorMatchingForm(messageFlowEditor, "Verify call of message processor that matches:", propertyPage);
    }

    public MessageProcessorMatchingForm(MessageFlowEditor messageFlowEditor, String initialMessage, AttributesPropertyPage propertyPage) {
        this.messageFlowEditor = messageFlowEditor;
        this.initialMessage = initialMessage;
        this.propertyPage = propertyPage;
    }

    public void drawInto(Composite parentPage) {
        Composite messageProcessorMatchingForm = createMockingConditionForm(parentPage);
        outlinePage = createOutlinePage(messageFlowEditor, parentPage);

        drawMockingConditionForm(messageProcessorMatchingForm);
    }

    public String getMessageProcessorRegexMatching() {
        return messageProcessorMatchingRegex.getText();
    }

    public Map<String, String> getAttributeMatching() {
        return attributeMatchingTable.getInputData();
    }

    public void setMessageProcessorRegexMatching(String regex) {
        messageProcessorMatchingRegex.setText(regex);
    }

    public void setAttributeMatching(Map<String, String> attrbiutes) {
        attributeMatchingTable.setInputData(attrbiutes);
    }

    private void createAttributeTable(Composite parentForm) {
        Label attributesLabel = new Label(parentForm, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).span(3, 1).grab(true, false).applyTo(attributesLabel);
        attributesLabel.setText("And attributes satisfy:");
        attributeMatchingTable = new WidgetUtils().createTableForMap(parentForm);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(ATTRIBUTE_VIEWER_COLUMNS, 3).grab(true, true).hint(SWT.DEFAULT, 120).indent(0, 10).applyTo(attributeMatchingTable);
    }

    private void drawMockingConditionForm(final Composite parentForm) {
        Label messageProcessorNameLabel = new Label(parentForm, SWT.NONE);
        messageProcessorNameLabel.setText(initialMessage);
        messageProcessorMatchingRegex = new Text(parentForm, SWT.BORDER);
        messageProcessorMatcherValidator = new MessageProcessorMatcherValidator(messageProcessorMatchingRegex);
        messageProcessorMatchingRegex.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                messageProcessorMatcherValidator.validate(propertyPage);
            }

            @Override
            public void focusGained(FocusEvent e) {
                messageProcessorMatcherValidator.validate(propertyPage);
            }
        });

        messageProcessorMatchingRegex.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                messageProcessorMatcherValidator.validate(propertyPage);

            }
        });

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(messageProcessorMatchingRegex);

        hideOutlinePageIn(parentForm);
        createShowOutlineToolbar(parentForm);
        createAttributeTable(parentForm);
    }

    private void createShowOutlineToolbar(final Composite parentForm) {
        ToolBar fieldToolBar = new ToolBar(parentForm, SWT.NONE);
        ToolBarManager fieldTBManager = new ToolBarManager(fieldToolBar);

        showOutline = new Action("Show outline to select a message processor", Action.AS_CHECK_BOX) {

            @Override
            public void run() {
                if (showOutline.isChecked()) {
                    showOutlinePageIn(parentForm);
                    parentForm.getParent().layout();
                } else {
                    hideOutlinePageIn(parentForm);
                    parentForm.getParent().layout();
                }
            }
        };

        showOutline.setImageDescriptor(MunitPlugin.ZOOM_ICON_DESCRIPTOR);

        fieldTBManager.add(showOutline);
        fieldTBManager.update(true);
    }

    private void showOutlinePageIn(Composite parentForm) {
        ((GridData) outlinePage.getControl().getLayoutData()).exclude = false;
        outlinePage.getControl().setVisible(true);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(1, 1).grab(true, true).applyTo(parentForm);

    }

    private void hideOutlinePageIn(Composite parentForm) {
        ((GridData) outlinePage.getControl().getLayoutData()).exclude = true;
        outlinePage.getControl().setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).grab(true, true).applyTo(parentForm);

    }

    private Composite createMockingConditionForm(Composite composite) {
        Composite attributeViewer = new Composite(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(ATTRIBUTE_VIEWER_COLUMNS).equalWidth(false).applyTo(attributeViewer);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(1, 1).grab(true, true).applyTo(attributeViewer);
        return attributeViewer;
    }

    private MessageFlowOutlinePage createOutlinePage(final MessageFlowEditor messageFlowEditor, Composite parentEditorGroup) {
        MessageFlowOutlinePage page = new MessageFlowOutlinePage(messageFlowEditor) {

            @Override
            protected MuleConfiguration getMuleConfiguration() {
                return ((MunitMessageFlowEditor) messageFlowEditor).getProductionMuleConfiguration();
            }

        };

        page.setTreeViewerOpenLister(new IOpenListener() {

            @Override
            public void open(OpenEvent arg0) {

                IStructuredSelection selection = (IStructuredSelection) ((org.eclipse.jface.viewers.TreeViewer) arg0.getSource()).getSelection();
                IMessageProcessorNode<?> selectedElement = (IMessageProcessorNode<?>) selection.getFirstElement();
                if (selectedElement != null) {
                    MessageProcessorNode<?> node = (MessageProcessorNode<?>) selectedElement;
                    LabelRetrieverEntityVisitor visitor = new LabelRetrieverEntityVisitor(node);
                    MessageFlowEntity entity = node.getValue();
                    entity.accept(visitor);
                    messageProcessorMatchingRegex.setText(ALL_MESSAGE_PROCESSORS_REGEX);
                    Map<String, String> inputData = attributeMatchingTable.getInputData();
                    inputData.put(DOC_NAME, String.format("#[string:%s]", visitor.getLabel()));
                    attributeMatchingTable.setInputData(inputData);
                    // }
                }

            }
        });

        page.createControl(parentEditorGroup);

        return page;
    }

}
