package org.mule.tooling.incubator.utils.placeholder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentInformationMapping;
import org.eclipse.jface.text.IRegion;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.utils.placeholder.impl.PropertiesCollector;
import org.mule.tooling.incubator.utils.placeholder.impl.PropertiesMerger;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class ExtractPlaceholdersJob extends WorkspaceJob {
	
	private final IFile targetFile;
	private final IMuleProject muleProject;
	
	
	public ExtractPlaceholdersJob(IFile targetFile, IMuleProject muleProject) {
		super("Extract property placeholders");
		this.targetFile = targetFile;
		this.muleProject = muleProject;
	}


	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		try {
			doGenerateProperties(monitor);
			return Status.OK_STATUS;
		} catch (Exception ex) {
			MuleCorePlugin.logError("Could not extract placeholders", ex);
			return Status.CANCEL_STATUS;
		}
	}
	
    private void doGenerateProperties(IProgressMonitor monitor) throws Exception {
		
    	List<IFile> muleConfigs = muleProject.getConfigurationsCache().getConfigurationResources();
    	
    	List<File> configFiles = new ArrayList<File>();
    	
    	//build the files list
    	for (IFile f : muleConfigs) {
    		configFiles.add(f.getLocation().toFile());
    	}
    	
    	//call the backing object
    	PropertiesCollector collector = new PropertiesCollector(configFiles);
    	Set<String> keys = collector.collectKeys();
    	
    	PropertiesMerger merger = new PropertiesMerger(targetFile.getLocation().toFile(), keys);    	
    	merger.doMerge();
    	
    	
    	targetFile.refreshLocal(IResource.DEPTH_ONE, monitor);
    	
    	//add markers to not-used properties
    	addMarkers(merger.getDeletedKeys());    	
	}


	private void addMarkers(Set<String> deletedKeys) throws Exception {
		
		
		targetFile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		
		
		if (deletedKeys == null || deletedKeys.isEmpty()) {
			//do nothing
			return;
		}
		
		IDocumentProvider provider = new TextFileDocumentProvider();
		provider.connect(targetFile);
		IDocument targetFileDocument  = provider.getDocument(targetFile);
		FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(targetFileDocument);
		
		
		for (String deletedkey: deletedKeys) {
			
			IRegion region = adapter.find(0, deletedkey, true, false, false, false); 
			
			IMarker marker = targetFile.createMarker(IMarker.PROBLEM);
			
			int lineNumber = targetFileDocument.getLineOfOffset(region.getOffset());
			
			if (marker.exists()) {
				
				marker.setAttribute(IMarker.MESSAGE, "Property might not be used within mule configs.");
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			    marker.setAttribute(IMarker.LINE_NUMBER, lineNumber + 1);
			    marker.setAttribute(IMarker.CHAR_START, region.getOffset());
			    marker.setAttribute(IMarker.CHAR_END, region.getOffset() + region.getLength());
				
			}
			
			
			
		}
		
		provider.disconnect(targetFile);
		
	}


	public void configureAndSchedule() {
        super.setUser(false);
        super.setPriority(Job.INTERACTIVE);
        super.setRule(muleProject.getJavaProject().getProject());
        super.schedule();
    }
	
}
