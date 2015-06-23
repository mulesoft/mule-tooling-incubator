package org.mule.tooling.incubator.utils.placeholder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;

public class PlaceholderExtractorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		//get the current selected file
		IStructuredSelection sel = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		
		//this is the target file for the properties placeholder.
		IFile currentFile = (IFile) sel.getFirstElement();
		
		IMuleProject muleProj = getMuleProjectFromSelection(sel);
		
		ExtractPlaceholdersJob job = new ExtractPlaceholdersJob(currentFile, muleProj);		
		job.configureAndSchedule();		
		return null;
	}
	
	private IMuleProject getMuleProjectFromSelection(IStructuredSelection sel) throws ExecutionException {
		try {
			return CoreUtils.getMuleProjectForSelection(sel);
		} catch (Exception ex) {
			throw new ExecutionException("Could not get mule project", ex);
		}
	}
	
}
