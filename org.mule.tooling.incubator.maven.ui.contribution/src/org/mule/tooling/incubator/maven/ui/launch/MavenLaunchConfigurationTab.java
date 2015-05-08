package org.mule.tooling.incubator.maven.ui.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mule.tooling.incubator.maven.ui.MavenCommandLineConfigurationComponent;
import org.mule.tooling.incubator.maven.ui.MavenImages;
import org.mule.tooling.incubator.maven.ui.MavenRunCommandLineConfigurationComponent;
import org.mule.tooling.maven.cmdline.MavenCommandLine;
import org.mule.tooling.ui.wizards.extensible.PartStatusHandler;
import org.mule.tooling.ui.wizards.extensible.WizardPagePartExtension;

public class MavenLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

    public static final String MVN_IMG_KEY = "mvn-icon-16x16";
    private String commandLine;
    private MavenRunCommandLineConfigurationComponent configurationComponent;

    @Override
    public void createControl(Composite parent) {
        if (JFaceResources.getImageRegistry().get(MVN_IMG_KEY) == null) {
            JFaceResources.getImageRegistry().put(MVN_IMG_KEY, MavenImages.MVN.createImage());
        }

        configurationComponent = new MavenRunCommandLineConfigurationComponent();
        configurationComponent.setStatusHandler(new PartStatusHandler() {

            @Override
            public void setPartComplete(WizardPagePartExtension part, boolean isComplete) {
            }

            @Override
            public void setErrorMessage(WizardPagePartExtension part, String message) {
            }

            @Override
            public void clearErrors(WizardPagePartExtension part) {
            }

            @Override
            public void notifyUpdate(WizardPagePartExtension part, String key, Object value) {
                if (MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE.equals(key)) {
                    onCommandLineUpdated((String) value);
                }
            }
        });
        Control control = configurationComponent.createControl(parent);
        setControl(control);
    }

    protected void onCommandLineUpdated(String value) {
        commandLine = value;
        setDirty(true);
        updateLaunchConfigurationDialog();
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        String value = "mvn clean test";
        configuration.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, value);

        if (configurationComponent != null) {
            configurationComponent.setCommandLine(value);
        }
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            String storedCommandline = configuration.getAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, MavenLaunchDelegate.MVN_BASE_COMMANDLINE);
            if (!storedCommandline.startsWith("mvn")) {
                storedCommandline = "mvn " + storedCommandline;
            }
            MavenCommandLine mavenCommandLine = MavenCommandLine.fromString(storedCommandline);
            configurationComponent.loadFrom(mavenCommandLine);
            setDirty(false);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, commandLine);
    }

    @Override
    public Image getImage() {
        return JFaceResources.getImageRegistry().get(MVN_IMG_KEY);
    }

    @Override
    public String getName() {
        return "Maven Settings";
    }

}
