package org.mule.tooling.incubator.utils.environments.editor;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.mule.tooling.incubator.utils.environments.actions.AddEnvironmentAction;
import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;

public class MultiPageEnvironmentsEditorContributor extends
		MultiPageEditorActionBarContributor {
	
	private AddEnvironmentAction addEnvAction;
	
	private IEditorPart currentEditor;
	
	public MultiPageEnvironmentsEditorContributor() {
		addEnvAction = new AddEnvironmentAction(new IMuleEnvironmentsEditorProvider() {
			
			@Override
			public MuleEnvironmentsEditor getMuleEnvironmentsEditor() {
				return getEnvironmentsEditor();
			}

			@Override
			public EnvironmentsConfiguration getEditorModel() {
				
				MuleEnvironmentsEditor editor = getEnvironmentsEditor();
				
				if(editor == null) {
					return null;
				}
				
				return editor.getConfiguration();
			}
			
			
		});		
	}
	
	@Override
	public void setActivePage(IEditorPart activeEditor) {
		addEnvAction.setEnabled(currentEditor != null);
	}
	
	@Override
	public void setActiveEditor(IEditorPart part) {
		this.currentEditor = part;
		super.setActiveEditor(part);
		
		if (currentEditor instanceof MultiPageEnvironmentsEditor) {
			MultiPageEnvironmentsEditor editor = (MultiPageEnvironmentsEditor) currentEditor;
			this.currentEditor = editor.getActivePageInstance();
		}

	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator());
		toolBarManager.add(addEnvAction);
	}
	
	private MuleEnvironmentsEditor getEnvironmentsEditor() {
		
		if (currentEditor instanceof MuleEnvironmentsEditor) {
			return (MuleEnvironmentsEditor) currentEditor;
		}
		
		return null;
	}
	
}
