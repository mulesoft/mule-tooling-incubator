package org.mule.tooling.incubator.maven.ui.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class MavenLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

    MavenLaunchConfigurationTab mavenTab;

    protected ILaunchConfigurationTab[] getLaunchConfigurationTabs() {
        return new ILaunchConfigurationTab[] { mavenTab };
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
        mavenTab.initializeFrom(configuration);
    }

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        mavenTab = new MavenLaunchConfigurationTab();
        setTabs(new ILaunchConfigurationTab[] { mavenTab });
    }

}
