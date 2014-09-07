package org.mule.tooling.incubator.gradle.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.incubator.gradle.GradleBuildJob;

public class BuildUpdatedListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			visitResource(event.getDelta());
		}
	}
	
	private void visitResource(IResourceDelta delta) {
		try {
			delta.accept(new IResourceDeltaVisitor() {
				
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					
					if (delta.getResource().getName().equals("build.gradle")) {
						IProject proj = delta.getResource().getProject();
						//trigger the refresh of the project.
						doRefreshProject(proj);
					}
					return true;
				}
			});
		} catch(CoreException ex) {
			ex.printStackTrace();
		}
	}
	
	
	private void doRefreshProject(IProject proj) throws CoreException {
		
		//the project should be a mule project.
		if (!CoreUtils.hasMuleNature(proj)) {
			//this is simply not a mule project. don't bother
			return;
		}
		
		GradleBuildJob refreshProjectJob = new GradleBuildJob("Refreshing project after change...", proj, "studio") {
			
			@Override
			protected void handleException(Exception ex) {
				ex.printStackTrace();
			}
		};
		refreshProjectJob.doSchedule();
	}
	
}
