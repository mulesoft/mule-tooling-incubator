package org.mule.tooling.incubator.gradle.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;

public abstract class AbstractGradleHandler extends AbstractHandler {

	protected IMuleProject getCurrentProjectForSelection(IStructuredSelection selection) throws ExecutionException {
		try {
			return CoreUtils.getMuleProjectForSelection(selection);
		} catch (Exception ex) {
			throw new ExecutionException("Could not retrieve mule project for selection", ex);
		}
	}
	
}
