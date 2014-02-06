package org.mule.tooling.ui.contribution.munit.editors.custom;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MessageProcessorAttributeCollectionMapper;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MunitMockModuleMapper;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.ThenReturnMockMapper;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributeHelper;

/**
 * <p>
 * Custom editor for the mock:when message processor (message processor used for mocking)
 * </p> 
 */
public class MockCustomEditor extends MunitMockModuleCustomEditor {

	private static final int ATTRIBUTE_VIEWER_COLUMNS = 3;
	private Text thenReturnText;
	private Action showReturnProperties;
	private MockPropertiesTable propertiesTableViewer;

	public MockCustomEditor(AttributesPropertyPage parentPage,
			AttributeHelper helper) {
		super(parentPage, helper);
	}
   
	@Override
	protected List<MunitMockModuleMapper> buildLoaders(){
		List<MunitMockModuleMapper> loaders = new ArrayList<MunitMockModuleMapper>();
		loaders.add(MessageProcessorAttributeCollectionMapper.mockAttributeCollectionMapper(messageProcessorMatchingForm));
		loaders.add(ThenReturnMockMapper.thenReturnLoaderInstanceFor(thenReturnText, propertiesTableViewer));
		return loaders;
	}
	
	
	@Override
	public void loadFrom(MessageFlowNode node, PropertyCollectionMap props) {


		propertiesTableViewer.setInputData(new ArrayList<MockProperties>());
		super.loadFrom(node, props);
	}

	@Override
	public void saveTo(MessageFlowNode node, PropertyCollectionMap props) {
		MessageProcessorAttributeCollectionMapper.mockAttributeCollectionMapper(messageProcessorMatchingForm).mapTo(node, props);
		ThenReturnMockMapper.thenReturnLoaderInstanceFor(thenReturnText, propertiesTableViewer).mapTo(node, props);
	}


	@Override
	protected MessageProcessorMatchingForm createMatchingForm(
			MessageFlowEditor messageFlowEditor) {
		return MessageProcessorMatchingForm.newMockingInstance(messageFlowEditor, getParentPage());
	}


	@Override
	protected void drawCustomFormIn(Composite parentEditorGroup) {
		final Composite returnComposite = new Composite(parentEditorGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(returnComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(ATTRIBUTE_VIEWER_COLUMNS, 1).grab(true, true).applyTo(returnComposite);

		Label thenReturnLabel = new Label(returnComposite, SWT.NONE);
		thenReturnLabel.setText("Then return message with payload:");

		thenReturnText = new Text(returnComposite, SWT.BORDER); 
		thenReturnText.setText("#[]");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(1, 1).grab(true, false).applyTo(thenReturnText);
		Label andProperties = new Label(returnComposite, SWT.NONE);
		andProperties.setText(" and properties ...");
		ToolBar fieldToolBar = new ToolBar(returnComposite, SWT.NONE);

		ToolBarManager fieldTBManager = new ToolBarManager(fieldToolBar);

		final Composite returnPropertiesForm = new Composite(returnComposite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(4, 1).grab(true, true).applyTo(returnPropertiesForm);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(returnPropertiesForm);

		propertiesTableViewer = new MockPropertiesTable(returnPropertiesForm, SWT.NULL, "Name", "Value", "Type");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(3, 1).grab(true, true).applyTo(propertiesTableViewer);
		returnPropertiesForm.setVisible(false);
		((GridData) returnPropertiesForm.getLayoutData()).exclude = true;

		showReturnProperties = new Action("Add return message properties", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				if ( showReturnProperties.isChecked() ){
					((GridData) returnPropertiesForm.getLayoutData()).exclude = false;
					returnPropertiesForm.setVisible(true);

					returnPropertiesForm.getParent().layout();	
					returnComposite.getParent().layout();
				}
				else{
					((GridData) returnPropertiesForm.getLayoutData()).exclude = true;
					returnPropertiesForm.setVisible(false);

					returnPropertiesForm.getParent().layout();
					returnComposite.getParent().layout();


				}
			}
		};

		showReturnProperties.setImageDescriptor(MunitPlugin.ZOOM_ICON_DESCRIPTOR);

		fieldTBManager.add(showReturnProperties);
		fieldTBManager.update(true);
	}

	@Override
	protected String getParentIdentifier() {
		return "Message processor Mocking definition";
	}

}
