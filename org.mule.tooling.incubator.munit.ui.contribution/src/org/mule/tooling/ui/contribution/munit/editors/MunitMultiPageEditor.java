package org.mule.tooling.ui.contribution.munit.editors;


import org.eclipse.core.resources.IResourceChangeListener;
import org.mule.tooling.messageflow.editor.MessageFlowEditor;
import org.mule.tooling.messageflow.editor.MultiPageMessageFlowEditor;


public class MunitMultiPageEditor extends MultiPageMessageFlowEditor implements IResourceChangeListener{

	public MunitMultiPageEditor() {
		setFlowEditor(createMessageFlowEditor());
	}

	@Override
	protected MessageFlowEditor createMessageFlowEditor() {
		return new MunitMessageFlowEditor();
	}
	
}
