package org.mule.tooling.incubator.utils.placeholder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.mule.tooling.incubator.utils.ProjectUtils;
import org.mule.tooling.properties.extension.PropertiesEditorAction;

public class RefreshPropertiesAction extends PropertiesEditorAction {

	@Override
	public void run() {
		 
		 if (getEditor() == null) {
			 return;
		 }
		 
		 if (!(getEditor().getEditorInput() instanceof IFileEditorInput)) {
			 System.out.println("Is not file editor input");
			 return;
		 }
		 
		 IFileEditorInput input =  (IFileEditorInput) getEditor().getEditorInput();
		 
		 IFile file = input.getFile();
		 IProject project = file.getProject();
		 
		 ExtractPlaceholdersJob job = new ExtractPlaceholdersJob(file, ProjectUtils.safeGetMuleProjectFromProject(project));
		 
		 job.configureAndSchedule();
	}

}
