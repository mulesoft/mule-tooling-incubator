package org.mule.tooling.devkit.popup.actions;

import java.util.Map;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JavadocCheckPropertyTester extends PropertyTester {

	private static final String ENABLED = "enabled";

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (!(receiver instanceof IResource))
			return false;

		if ((receiver instanceof IProject) && property.equals(ENABLED)) {

			final IJavaProject selectedProject = JavaCore.create((IProject)receiver);

			if (selectedProject != null && selectedProject.isOpen()) {
				Map<String, String> options = AptConfig
						.getProcessorOptions(selectedProject);
				Boolean enabled = Boolean.parseBoolean(options
						.get("enableJavaDocValidation"));
				return !enabled;
			}
		}
		return false;
	}

	/**
	 * Converts the given expected value to a boolean.
	 * 
	 * @param expectedValue
	 *            the expected value (may be <code>null</code>).
	 * @return <code>false</code> if the expected value equals Boolean.FALSE,
	 *         <code>true</code> otherwise
	 */
	protected boolean toBoolean(Object expectedValue) {
		if (expectedValue instanceof Boolean) {
			return ((Boolean) expectedValue).booleanValue();
		}
		return true;
	}
}
