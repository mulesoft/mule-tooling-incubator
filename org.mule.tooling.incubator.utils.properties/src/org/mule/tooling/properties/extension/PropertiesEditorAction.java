package org.mule.tooling.properties.extension;

import org.mule.tooling.properties.editors.IPropertiesEditor;
import org.mule.tooling.properties.editors.MultiPagePropertiesEditorContributor;

public abstract class PropertiesEditorAction {
	
	private MultiPagePropertiesEditorContributor editor;

	public IPropertiesEditor getEditor() {
		return editor.getCurrentEditor();
	}

	public void setEditor(MultiPagePropertiesEditorContributor editor) {
		this.editor = editor;
	}
	
	public abstract void run();
	
}
