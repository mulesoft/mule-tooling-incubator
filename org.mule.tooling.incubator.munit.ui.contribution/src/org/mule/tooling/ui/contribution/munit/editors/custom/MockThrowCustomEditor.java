package org.mule.tooling.ui.contribution.munit.editors.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MessageProcessorAttributeCollectionMapper;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MunitMockModuleMapper;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributeHelper;

/**
 * <p>
 * Custom editor for the mock:throw-an message processor (message processor used
 * for mocking)
 * </p>
 */
public class MockThrowCustomEditor extends MunitMockModuleCustomEditor {

	private static final int ATTRIBUTE_VIEWER_COLUMNS = 3;

	// private Text thenThrowText;

	public MockThrowCustomEditor(AttributesPropertyPage parentPage, AttributeHelper helper) {
		super(parentPage, helper);
	}

	@Override
	protected List<MunitMockModuleMapper> buildLoaders() {
		List<MunitMockModuleMapper> loaders = new ArrayList<MunitMockModuleMapper>();
		loaders.add(MessageProcessorAttributeCollectionMapper.mockThrowAnAttributeCollectionMapper(messageProcessorMatchingForm));
		return loaders;
	}

	@Override
	public void saveTo(MessageFlowNode node, PropertyCollectionMap props) {
		MessageProcessorAttributeCollectionMapper.mockThrowAnAttributeCollectionMapper(messageProcessorMatchingForm).mapTo(node, props);
	}

	@Override
	protected MessageProcessorMatchingForm createMatchingForm(MessageFlowEditor messageFlowEditor) {
		return MessageProcessorMatchingForm.newMockingInstance(messageFlowEditor, getParentPage());
	}

	@Override
	protected void drawCustomFormIn(Composite parentEditorGroup) {
		final Composite returnComposite = new Composite(parentEditorGroup, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(returnComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(ATTRIBUTE_VIEWER_COLUMNS, 1).grab(true, true).applyTo(returnComposite);
	}

	@Override
	protected String getParentIdentifier() {
		return "Message processor Mocking definition";
	}

}
