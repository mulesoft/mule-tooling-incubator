package org.mule.tooling.incubator.maven.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;

public class MavenLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

    MavenLaunchConfigurationTab mavenTab;
    JavaArgumentsTab javaArgumentsTab;
    protected ILaunchConfigurationTab[] getLaunchConfigurationTabs() {
        return new ILaunchConfigurationTab[] { mavenTab, javaArgumentsTab };
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
        mavenTab.initializeFrom(configuration);
        javaArgumentsTab.initializeFrom(configuration);
    }

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        mavenTab = new MavenLaunchConfigurationTab();
        javaArgumentsTab = new JavaArgumentsTab();
        setTabs(new ILaunchConfigurationTab[] { mavenTab, javaArgumentsTab });
    }

}
