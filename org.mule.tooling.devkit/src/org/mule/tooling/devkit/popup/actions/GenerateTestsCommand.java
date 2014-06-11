package org.mule.tooling.devkit.popup.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.common.TestDataModelDto;
import org.mule.tooling.devkit.dialogs.ExportTypeSelectionDialog;
import org.mule.tooling.devkit.wizards.GenerateTestWizard;

public class GenerateTestsCommand extends AbstractMavenCommandRunner {

    private final String generateAll = "all";
    private final String generateInterop = "interop";
    private final String generateFunctional = "functional";

    private TestDataModelDto testdataDto;

    @Override
    protected void doCommandJobOnProject(final IProject selectedProject) {

        if (getConfigurationAndContinue(selectedProject)) {

            final String[] commonCommand = new String[] { "clean", "-DskipTests" };

            List<String> commandArgs = new ArrayList<String>();

            commandArgs.addAll(Arrays.asList(commonCommand));

            if (testdataDto.selectedScafolding()) {

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

            if (!testdataDto.selectedScafolding()) {
                commandArgs.addAll(Arrays.asList(commonCommand));
            }
            
            if (testdataDto.selectedInterop() || testdataDto.selectedFunctional()) {
                final String[] mavenCommand = new String[] { "package", "-P", "testdata-generator",
                        "-Dtype=" + getGenerationType(),
                        "-DinteropPolicy=" + testdataDto.getExportInteropPolicy(),
                        "-DfunctionalPolicy=" + testdataDto.getExportFunctionalPolicy(),
                        "-DcredentialsFile=" + testdataDto.getCredentialsFile(),
                        "-DoutputFile=" + testdataDto.getOutputFile(),
                        "-DprocessorsList=" + testdataDto.getFilteredProcessors(),
                        "-DskipTests","-Ddevkit.javadoc.check.skip=true", "-Dmaven.javadoc.skip=true"};

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

    private Boolean getConfigurationAndContinue(IProject selectedProject) {
        
        GenerateTestWizard wizard = new GenerateTestWizard(selectedProject);
        Shell activeShell = Display.getCurrent().getActiveShell();
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard) {
                    @Override
                    protected void configureShell(Shell newShell) {
                    super.configureShell(newShell);
                    newShell.setSize(510, 550);
                }   
            };
        int returnStatus = wizardDialog.open(); 
        
        boolean skip = (returnStatus != 0);

        if (skip) 
            return skip;
        
        ExportTypeSelectionDialog selectionDialog;
        this.testdataDto = wizard.getRunConfig();
        if (testdataDto.selectedFunctional()) {  
            if (selectedProject.getFile("src/test/resources/generated/AutomationSpringBeans.xml").exists() ||
                selectedProject.getFile("src/test/resources/generated/automation-test-flows.xml").exists()) {
                
                selectionDialog = new ExportTypeSelectionDialog(activeShell, "AutomationSpringBeans.xml");
                skip = (selectionDialog.open() == 1);
                testdataDto.setExportFunctionalPolicy(selectionDialog.getSelectedPolicy());
                
            }
        }

        if (testdataDto.selectedInterop()) {
            if (selectedProject.getFile("src/test/resources/generated/"+testdataDto.getOutputFile()).exists() || 
                selectedProject.getFile("src/test/resources/generated/"+testdataDto.getOutputFile().replace(".xml", ".-override.xml")).exists()) {
                
                selectionDialog = new ExportTypeSelectionDialog(activeShell, "TestData.xml");
                skip = (selectionDialog.open() == 1);
                testdataDto.setExportInteropPolicy(selectionDialog.getSelectedPolicy());    
            }
        }
        
        return ((!skip) && 
                (testdataDto.selectedFunctional() || testdataDto.selectedInterop() || testdataDto.selectedScafolding()));
    }

    private String getGenerationType() {

        if (testdataDto.selectedFunctional() && testdataDto.selectedInterop())
            return generateAll;

        if (testdataDto.selectedFunctional())
            return generateFunctional;
        
        if (testdataDto.selectedInterop())
            return generateInterop;

        return "";
    }
}
