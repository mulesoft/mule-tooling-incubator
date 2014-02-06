package org.mule.tooling.ui.contribution.munit.editors.custom;

import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;
import org.mule.tooling.model.messageflow.MessageFlowEntity;
import org.mule.tooling.model.messageflow.MessageFlowNode;
import org.mule.tooling.model.messageflow.MuleConfiguration;
import org.mule.tooling.model.messageflow.PropertyCollection;
import org.mule.tooling.model.messageflow.decorator.PropertyCollectionMap;
import org.mule.tooling.ui.contribution.munit.editors.custom.mappers.MunitMockModuleMapper;
import org.mule.tooling.ui.modules.core.widgets.AttributesPropertyPage;
import org.mule.tooling.ui.modules.core.widgets.IForceSave;
import org.mule.tooling.ui.modules.core.widgets.editors.CustomEditor;
import org.mule.tooling.ui.modules.core.widgets.meta.AttributeHelper;

/**
 * <p>
 * Abstract class for all the message processors of the Mock module of Munit
 * </p> 
 * 
 * <p>
 * All the message processors of the Munit Mock module have the same common UI. That common part is created by this class.
 * </p>
 */
public abstract class MunitMockModuleCustomEditor extends CustomEditor implements IForceSave {

    
	/**
	 * The form that contains all the message processor matching information
	 */
	protected MessageProcessorMatchingForm messageProcessorMatchingForm;

	/**
	 * Factory method to create the {@link MunitMockModuleCustomEditor#messageProcessorMatchingForm} 
	 */
	protected abstract MessageProcessorMatchingForm createMatchingForm(final MessageFlowEditor messageFlowEditor);
	
	/**
	 * Draw the particular form for the sub class extending this class. For example, the Spy message processor should
	 * draw the spy action form in this method, the mock message processor should draw the "then-return" form here.
	 */
	protected abstract void drawCustomFormIn(Composite parentEditorGroup);
	
	
	/**
	 * Each custom editor must search for its parent because of the studio framework, this method forces the sub classes
	 * of the {@link MunitMockModuleCustomEditor} to define the parent identifier. It has to be the same as specified in
	 * the mock.xml file.
	 */
	protected abstract String getParentIdentifier();
	
	/**
	 * The {@link PropertyCollection} mappers to transform from the xml to the form.
	 */
	protected abstract List<MunitMockModuleMapper> buildLoaders();
	
	public MunitMockModuleCustomEditor(AttributesPropertyPage parentPage,
			AttributeHelper helper) {
		super(parentPage, helper);
	}

	@Override
	public void refreshEditor() {

	}

	@Override
	public boolean hasToForceSave(MuleConfiguration currentMuleConfiguration,
			IMuleProject muleProject, MessageFlowEntity messageFlowEntity) {
		return true;
	}

	
	@Override
	protected Control createControl(AttributesPropertyPage parentPage) {
		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}

		Group parentEditorGroup = getGroup(parentPage);

		final IEditorPart activeEditor = activeWorkbenchWindow.getActivePage().getActiveEditor();
		if (activeEditor instanceof MultiPageMessageFlowEditor) {

			final MultiPageMessageFlowEditor editor = (MultiPageMessageFlowEditor) activeEditor;
			final MessageFlowEditor messageFlowEditor = editor.getFlowEditor();

			messageProcessorMatchingForm = createMatchingForm(messageFlowEditor);
			messageProcessorMatchingForm.drawInto(parentEditorGroup);
			
			drawCustomFormIn(parentEditorGroup);
		}

		return parentEditorGroup;
	}

	
	private Group getGroup(final AttributesPropertyPage parentPage) {
		Group parent = null;
		for (Control control : parentPage.getChildren()) {
			if (control instanceof Group) {
				Group group = (Group) control;
				if (group.getText().equalsIgnoreCase(getParentIdentifier())) {
					parent = group;
					break;
				}
			}
		}

		if (parent == null) {
			parent = new Group(parentPage, SWT.NONE);
			parent.setText(getParentIdentifier());
		}
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).extendedMargins(0, 0, 0, 0).applyTo(parent);
		return parent;
	}
	
	public void loadFrom(MessageFlowNode node, PropertyCollectionMap props) {
		List<MunitMockModuleMapper> loaders = buildLoaders();
		for (MunitMockModuleMapper loader : loaders){
			loader.loadFrom(node, props);
		}
} 
	
}