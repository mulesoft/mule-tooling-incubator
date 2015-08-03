package org.mule.tooling.incubator.utils.environments.editor;

public interface IMuleEnvironmentsEditorProvider {
	
	/**
	 * Returns null if the environments editor is not reachable.
	 * @return
	 */
	public MuleEnvironmentsEditor getMuleEnvironmentsEditor();
	
}
