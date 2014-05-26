package org.mule.tooling.devkit.popup.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.dialogs.GenerateTestDialog;

public class GenerateTestsCommand extends AbstractMavenCommandRunner {

    public static enum ConfigKeys {
        outputFile, credentialsFile, replaceAllTestData, replaceAllInterop, selectedScafolding, selectedInterop, selectedFunctional
    };

    private final String generateAll = "all";
    private final String generateInterop = "interop";
    private final String generateFunctional = "functional";
    private boolean generateScafolding = false;

    private Map<ConfigKeys, String> generationProperties = null;

    @Override
    protected void doCommandJobOnProject(final IProject selectedProject) {

        if (getConfigurationAndContinue()) {

            final String[] commonCommand = new String[] { "clean", "-DskipTests" };

            List<String> commandArgs = new ArrayList<String>();

            commandArgs.addAll(Arrays.asList(commonCommand));

            if (generateScafolding) {

                String[] generateTestsCommand = new String[] { 
                        "compile",
                        "-Ddevkit.studio.package.skip=true",
                        "-Ddevkit.javadoc.check.skip=true",
                        "-Dmaven.javadoc.skip=true",
                        "org.mule.tools.devkit:"+
                        "connector-automation-generator-maven-plugin:"+
                        "connector-automation-generator"
                };
                commandArgs.addAll(Arrays.asList(generateTestsCommand));
                final String[] command = new String[commandArgs.size()];
                commandArgs.toArray( command);
                System.out.println("** Command :: " + StringUtils.join(command, " "));
                runMavenGoalJob(selectedProject, command, "Generating Tests Cases...",
                        DevkitUtils.refreshFolder(selectedProject.getFolder(DevkitUtils.TEST_JAVA_FOLDER), null));

            } 
            commandArgs = new ArrayList<String>();

            if (!generateScafolding) {
                commandArgs.addAll(Arrays.asList(commonCommand));
            }
            Boolean selectedInterop = new Boolean(generationProperties.get(ConfigKeys.selectedInterop));
            Boolean selectedFunctional = new Boolean(generationProperties.get(ConfigKeys.selectedFunctional));
            if (selectedInterop || selectedFunctional) {
                final String[] mavenCommand = new String[] { "package", "-P", "testdata-generator", "-Dtestdata.type=" + getGenerationType(),
                        "-Dtestdata.replaceAll=" + generationProperties.get(ConfigKeys.replaceAllInterop),
                        "-Dtestdata.credentialsFile=" + generationProperties.get(ConfigKeys.credentialsFile),
                        "-Dtestdata.outputFile=" + generationProperties.get(ConfigKeys.outputFile) };

                commandArgs.addAll(Arrays.asList(mavenCommand));
                final String jobMsg = "Generating Sources...";

                final String[] command = new String[commandArgs.size()];
                commandArgs.toArray( command);
                System.out.println("** Command :: " + StringUtils.join(command, " "));
                runMavenGoalJob(selectedProject, command, jobMsg,
                        DevkitUtils.refreshFolder(selectedProject.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER), null));
            }
        }
    }

    private Boolean getConfigurationAndContinue() {

        GenerateTestDialog dialog = new GenerateTestDialog(Display.getCurrent().getActiveShell());
        int returnStatus = dialog.open();

        generationProperties = dialog.getConfigProperties();

        Boolean selectedInterop = new Boolean(generationProperties.get(ConfigKeys.selectedInterop));
        Boolean selectedFunctional = new Boolean(generationProperties.get(ConfigKeys.selectedFunctional));
        generateScafolding = new Boolean(generationProperties.get(ConfigKeys.selectedScafolding));

        return ((returnStatus == 0) && (selectedFunctional || selectedInterop || generateScafolding));
    }

    private String getGenerationType() {

        Boolean selectedInterop = new Boolean(generationProperties.get(ConfigKeys.selectedInterop));
        Boolean selectedFunctional = new Boolean(generationProperties.get(ConfigKeys.selectedFunctional));

        if (selectedFunctional && selectedInterop)
            return generateAll;

        if (selectedFunctional)
            return generateFunctional;
        if (selectedInterop)
            return generateInterop;

        return "";
    }

}
