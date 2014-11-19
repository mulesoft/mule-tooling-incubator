package org.mule.tooling.devkit.popup.dto;

public class TestDataModelDto {

    public enum ExportPolicy {
        REPLACE, UPDATE, SKIP
    }

    private String outputFile = "interop-testdata.xml";
    private String credentialsFile = "";

    private ExportPolicy exportInteropPolicy = ExportPolicy.UPDATE;
    private ExportPolicy exportFunctionalPolicy = ExportPolicy.UPDATE;
    private String filteredProcessors = "";

    private Boolean selectedScafolding = true;
    private Boolean selectedInterop = true;
    private Boolean selectedFunctional = true;

    private String automationPackage = "org.mule.modules.automation";

    public TestDataModelDto(TestDataModelDto source) {

        if (source == null)
            return;

        this.setCredentialsFile(source.getCredentialsFile());

        this.setExportFunctionalPolicy(source.getExportFunctionalPolicy());
        this.setExportInteropPolicy(source.getExportInteropPolicy());

        this.setFilteredProcessors(source.getFilteredProcessors());
        this.setOutputFile(source.getOutputFile());

        this.setSelectedFunctional(source.selectedFunctional());
        this.setSelectedInterop(source.selectedInterop());
        this.setSelectedScafolding(source.selectedScafolding());
        this.setAutomationPackage(source.getAutomationPackage());
    }

    public TestDataModelDto() {
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getCredentialsFile() {
        return credentialsFile;
    }

    public void setCredentialsFile(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    public ExportPolicy getExportInteropPolicy() {
        return exportInteropPolicy;
    }

    public void setExportInteropPolicy(ExportPolicy exportInteropPolicy) {
        this.exportInteropPolicy = exportInteropPolicy;
    }

    public ExportPolicy getExportFunctionalPolicy() {
        return exportFunctionalPolicy;
    }

    public void setExportFunctionalPolicy(ExportPolicy exportFunctionalPolicy) {
        this.exportFunctionalPolicy = exportFunctionalPolicy;
    }

    public String getFilteredProcessors() {
        return filteredProcessors;
    }

    public void setFilteredProcessors(String filteredProcessors) {
        this.filteredProcessors = filteredProcessors;
    }

    public Boolean selectedScafolding() {
        return selectedScafolding;
    }

    public void setSelectedScafolding(Boolean selectedScafolding) {
        this.selectedScafolding = selectedScafolding;
    }

    public Boolean selectedInterop() {
        return selectedInterop;
    }

    public void setSelectedInterop(Boolean selectedInterop) {
        this.selectedInterop = selectedInterop;
    }

    public Boolean selectedFunctional() {
        return selectedFunctional;
    }

    public void setSelectedFunctional(Boolean selectedFunctional) {
        this.selectedFunctional = selectedFunctional;
    }

    public String getAutomationPackage() {
        return automationPackage;
    }

    public void setAutomationPackage(String automationPackage) {
        this.automationPackage = automationPackage;
    }
}