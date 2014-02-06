package org.mule.tooling.ui.contribution.munit.editors.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MessageProcessorAttributeCollectionMapper;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MunitMockModuleMapper;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributeHelper;

/**
 * Custom editor for the Verify message processor of the mock module 
 */
public class VerifyCustomEditor extends MunitMockModuleCustomEditor{

	public VerifyCustomEditor(AttributesPropertyPage parentPage,
			AttributeHelper helper) {
		super(parentPage, helper);
	}

	@Override
	protected MessageProcessorMatchingForm createMatchingForm(
			MessageFlowEditor messageFlowEditor) {
		return MessageProcessorMatchingForm.newVerifyInstance(messageFlowEditor, getParentPage());
	}

	@Override
	protected void drawCustomFormIn(Composite parentEditorGroup) {
	
	}

	@Override
	protected String getParentIdentifier() {
		return "Message processor Verify definition";
	}

	@Override
	protected List<MunitMockModuleMapper> buildLoaders(){
		List<MunitMockModuleMapper> loaders = new ArrayList<MunitMockModuleMapper>();
		loaders.add(MessageProcessorAttributeCollectionMapper.verifyAttributeCollectionMapper(messageProcessorMatchingForm));
		return loaders;
	}

	@Override
	public void saveTo(MessageFlowNode node, PropertyCollectionMap props) {
		MessageProcessorAttributeCollectionMapper.verifyAttributeCollectionMapper(messageProcessorMatchingForm).mapTo(node, props);		
	}

}
