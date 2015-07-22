package org.mule.tooling.incubator.gradle;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class GradleProjectPropertyTester extends PropertyTester {
	
	public static final String GRADLE_PROJECT_PROPERTY = "gradleProject";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		if (!(receiver instanceof IResource))
            return false;

        if ((receiver instanceof IProject) && property.equals(GRADLE_PROJECT_PROPERTY)) {
        	return GradlePluginUtils.shallowCheckIsGradleproject((IProject) receiver);
        }
		
		return false;
	}

}
