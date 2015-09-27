package org.mule.tooling.incubator.gradle.jobs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.utils.CoreUtils;

public class InitProjectJob extends GradleBuildJob {
	
	private static final String INIT_MULE_DOMAIN_TASK_NAME = "initDomain";
	private static final String INIT_MULE_PROJECT_TASK_NAME = "initMuleProject";
	private static final String TASK_NAME = "Creating initial structure...";
	
	private final String moduleName;
	
	public InitProjectJob(IProject project, String module) {
		super(TASK_NAME, project, generateTask(project, module));
		this.moduleName = module;
	}
	
	private static String[] generateTask(IProject project, String moduleName) {
		
		try {
		
		if (StringUtils.isEmpty(moduleName) && CoreUtils.hasMuleDomainNature(project)) {
			return new String[] { ":" + INIT_MULE_DOMAIN_TASK_NAME };
		}
		
		if (StringUtils.isEmpty(moduleName)) {
			return new String[] {INIT_MULE_PROJECT_TASK_NAME};
		}
		
		return new String[] {":" + moduleName + ":" + INIT_MULE_PROJECT_TASK_NAME, 
				":" + moduleName + ":studio"};
		
		} catch (Exception ex) {
			MuleCorePlugin.logError("Running unit tests has failed", ex);
		}
		return new String[] {INIT_MULE_PROJECT_TASK_NAME};
	}
	
	@Override
	protected void handleException(Exception ex) {
		MuleCorePlugin.logError("Running unit tests has failed", ex);
	}
	
	@Override
	protected void handleCompletion() {
		new OpenNestedProjectJob(project, moduleName).schedule();
	}
	
}
