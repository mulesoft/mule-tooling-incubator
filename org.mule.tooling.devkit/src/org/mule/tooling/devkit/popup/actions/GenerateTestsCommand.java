package org.mule.tooling.devkit.popup.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.dialogs.ExportTypeSelectionDialog;
import org.mule.tooling.devkit.maven.MavenUtils;
import org.mule.tooling.devkit.popup.dto.TestDataModelDto;
import org.mule.tooling.devkit.wizards.GenerateTestWizard;

public class GenerateTestsCommand extends AbstractMavenCommandRunner {

    private static final String CONNECTOR_AUTOMATION_GENERATOR_VERSION = "2.0.4";

    private static final String FUNCTIONAL_FILES_DIALOG_TITLE = "Functional files";
    private static final String INTEROP_FILES_DIALOG_TITLE = "Interop files";
    private static final String TEST_FLOWS_XML = "automation-test-flows.xml";
    private static final String SPRING_BEANS_XML = "AutomationSpringBeans.xml";
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
                CompilationUnit unit = DevkitUtils.getConnectorClass(selectedProject);

                String[] generateTestsCommand = new String[] { "compile", "-Ddevkit.studio.package.skip=true", "-Ddevkit.javadoc.check.skip=true", "-Dmaven.javadoc.skip=true",
                        "org.mule.tools.devkit:" + "connector-automation-generator-maven-plugin:" + CONNECTOR_AUTOMATION_GENERATOR_VERSION + ":connector-automation-generator",
                        "-DprocessorsList=" + testdataDto.getFilteredProcessors(), "-DautomationRootPackage=" + testdataDto.getAutomationPackage(),
                        "-DconnectorClassName=" + unit.getPackage().getName() + "." + unit.getTypeRoot().getElementName().replace(".java", "") };
                commandArgs.addAll(Arrays.asList(generateTestsCommand));
                final String[] command = new String[commandArgs.size()];
                commandArgs.toArray(command);
                System.out.println("** Command :: " + StringUtils.join(command, " "));
                MavenUtils.runMavenGoalJob(selectedProject, command, "Generating Tests Cases...",
                        DevkitUtils.refreshFolder(selectedProject.getFolder(DevkitUtils.TEST_JAVA_FOLDER), null),
                        "Generating tests cases for " + DevkitUtils.getProjectLabel(JavaCore.create(selectedProject)));

            }
            commandArgs = new ArrayList<String>();

            if (!testdataDto.selectedScafolding()) {
                commandArgs.addAll(Arrays.asList(commonCommand));
            }

            if (testdataDto.selectedInterop() || testdataDto.selectedFunctional()) {
                final String[] mavenCommand = new String[] { "package", "-P", "testdata-generator", "-Dtype=" + getGenerationType(),
                        "-DinteropPolicy=" + testdataDto.getExportInteropPolicy(), "-DfunctionalPolicy=" + testdataDto.getExportFunctionalPolicy(),
                        "-DcredentialsFile=" + testdataDto.getCredentialsFile(), "-DoutputFile=" + testdataDto.getOutputFile(),
                        "-DprocessorsList=" + testdataDto.getFilteredProcessors(), "-DskipTests", "-Ddevkit.javadoc.check.skip=true", "-Dmaven.javadoc.skip=true" };

                commandArgs.addAll(Arrays.asList(mavenCommand));
                final String jobMsg = "Generating Sources...";

                final String[] command = new String[commandArgs.size()];
                commandArgs.toArray(command);
                System.out.println("** Command :: " + StringUtils.join(command, " "));
                MavenUtils.runMavenGoalJob(selectedProject, command, jobMsg, DevkitUtils.refreshFolder(selectedProject.getFolder(DevkitUtils.TEST_RESOURCES_FOLDER), null),
                        "Generating sources for " + DevkitUtils.getProjectLabel(JavaCore.create(selectedProject)));
            }
        }
    }

    private Boolean getConfigurationAndContinue(IProject selectedProject) {

        GenerateTestWizard wizard = new GenerateTestWizard(selectedProject);
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard) {

            @Override
            protected void configureShell(Shell newShell) {
                super.configureShell(newShell);
                newShell.setSize(510, 550);
            }
        };
        int returnStatus = wizardDialog.open();

        if (returnStatus == 1)
            return false;

        this.testdataDto = wizard.getRunConfig();

        boolean skipFunctional = verifyFunctionalExportPolicy(selectedProject);
        boolean skipInterop = verifyInteropExportPolicy(selectedProject);

        return (!(skipFunctional && skipInterop) && (testdataDto.selectedFunctional() || testdataDto.selectedInterop() || testdataDto.selectedScafolding()));
    }

    private boolean verifyInteropExportPolicy(IProject selectedProject) {
        boolean skip = false;
        ExportTypeSelectionDialog selectionDialog;
        if (testdataDto.selectedInterop()) {
            if (fileExistsInTestResources(selectedProject, testdataDto.getOutputFile())
                    || fileExistsInTestResources(selectedProject, testdataDto.getOutputFile().replace(".xml", ".-override.xml"))) {
                selectionDialog = new ExportTypeSelectionDialog(Display.getCurrent().getActiveShell(), INTEROP_FILES_DIALOG_TITLE);
                skip = (selectionDialog.open() == 1);
                testdataDto.setExportInteropPolicy(selectionDialog.getSelectedPolicy());
            }
        }

        return skip;
    }

    public boolean verifyFunctionalExportPolicy(IProject selectedProject) {
        boolean skip = false;
        ExportTypeSelectionDialog selectionDialog;
        if (testdataDto.selectedFunctional()) {
            if (fileExistsInTestResources(selectedProject, SPRING_BEANS_XML) || fileExistsInTestResources(selectedProject, TEST_FLOWS_XML)) {

                selectionDialog = new ExportTypeSelectionDialog(Display.getCurrent().getActiveShell(), FUNCTIONAL_FILES_DIALOG_TITLE);
                skip = (selectionDialog.open() == 1);
                testdataDto.setExportFunctionalPolicy(selectionDialog.getSelectedPolicy());
            }
        }
        return skip;
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

    private boolean fileExistsInTestResources(IProject selectedProject, String filename) {
        return selectedProject.getFile(DevkitUtils.TEST_RESOURCES_FOLDER + "/" + filename).exists();
    }

}
