package org.mule.tooling.incubator.gradle.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
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
	
	
	private void doRefreshProject(IProject proj) {
		GradleBuildJob refreshProjectJob = new GradleBuildJob("Refreshing project after change...", proj, "studio") {
			
			@Override
			protected void handleException(Exception ex) {
				ex.printStackTrace();
			}
		};
		refreshProjectJob.doSchedule();
	}
	
}
