package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;
import org.mule.tooling.ui.contribution.munit.MunitPlugin;

public class MuleResourcePropertyTester extends PropertyTester {

    public static final String PROP_IS_IN_MULE_PROJECT = "isMunitFile";

    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof IResource) {
            if (PROP_IS_IN_MULE_PROJECT.equals(property)) {
                final IResource resource = ((IResource) receiver);
                if (resource.getFileExtension() != null && resource.getFileExtension().equals("xml") && resource.getProjectRelativePath() != null
                        && resource.getProjectRelativePath().toString().contains(MunitPlugin.MUNIT_FOLDER_PATH)) {
                    return true;
                }
            }
        }
        return false;
    }
}