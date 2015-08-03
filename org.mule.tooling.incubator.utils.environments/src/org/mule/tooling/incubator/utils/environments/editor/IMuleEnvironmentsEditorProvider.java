package org.mule.tooling.incubator.utils.environments.editor;

import org.mule.tooling.incubator.utils.environments.model.EnvironmentsConfiguration;

public interface IMuleEnvironmentsEditorProvider {
	
	/**
	 * Returns null if the environments editor is not reachable.
	 * @return
	 */
	public MuleEnvironmentsEditor getMuleEnvironmentsEditor();
	
	/**
	 * The environment configuration model associated with the given editor.
	 * This method is provided mainly for convenience.
	 * @return
	 */
	public EnvironmentsConfiguration getEditorModel();
	
}
