package org.mule.tooling.properties.extension;

import org.eclipse.ui.IEditorPart;
import org.mule.tooling.properties.editors.MultiPagePropertiesEditorContributor;

public abstract class PropertiesEditorAction {
	
	private MultiPagePropertiesEditorContributor editor;

	public IEditorPart getEditor() {
		return editor.getActiveEditor();
	}

	public void setEditor(MultiPagePropertiesEditorContributor editor) {
		this.editor = editor;
	}
	
	public abstract void run();
	
}
