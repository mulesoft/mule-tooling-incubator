package org.mule.tooling.ui.contribution.munit.runner;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * <p>
 * The Munit launch configuration
 * </p>
 */
public class MunitConfigurationTab extends AbstractLaunchConfigurationTabGroup {

    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new LaunchConfigurationTab(), new EnvironmentTab() };
        setTabs(tabs);
    }
}
