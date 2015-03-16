package org.mule.tooling.devkit.popup.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class InteropConfigDto {

    private String testDataPath = "";
    private String testDataOverridePath = "";

    private Boolean runLocal = true;
    private Boolean runConnectivityTest = false;
    private Boolean runDMapperTest = false;
    private Boolean runXmlTest = false;
    private Boolean runOAuth = false;

    private String destinationEmail = "";
    private String repository = "";
    private List<String> branches = new ArrayList<String>();
    private String selectedBranch = "";
    private String serverURL = "http://localhost:8080/";
    private Boolean selectedDebug = false;
    private Boolean selectedVerbose = false;
    private Boolean selectedWindows = true;
    private Boolean selectedLinux = false;

    public String getTestDataPath() {
        return testDataPath;
    }

    public void setTestDataPath(String testDataPath) {
        this.testDataPath = testDataPath;
    }

    public String getTestDataOverridePath() {
        return testDataOverridePath;
    }

    public void setTestDataOverridePath(String testDataOverridePath) {
        this.testDataOverridePath = testDataOverridePath;
    }

    public String getDestinationEmail() {
        return destinationEmail;
    }

    public void setDestinationEmail(String destinationEmail) {
        this.destinationEmail = destinationEmail;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public Boolean getSelectedDebug() {
        return selectedDebug;
    }

    public void setSelectedDebug(Boolean selectedDebug) {
        this.selectedDebug = selectedDebug;
    }

    public Boolean getSelectedVerbose() {
        return selectedVerbose;
    }

    public void setSelectedVerbose(Boolean selectedVerbose) {
        this.selectedVerbose = selectedVerbose;
    }

    public Boolean getSelectedWindows() {
        return selectedWindows;
    }

    public void setSelectedWindows(Boolean selectedWindows) {
        this.selectedWindows = selectedWindows;
    }

    public Boolean getSelectedLinux() {
        return selectedLinux;
    }

    public void setSelectedLinux(Boolean selectedLinux) {
        this.selectedLinux = selectedLinux;
    }

    public Boolean runAsLocal() {
        return runLocal;
    }

    public void setRunAsLocal(Boolean runLocal) {
        this.runLocal = runLocal;
    }

    public Boolean getRunConnectivityTest() {
        return runConnectivityTest;
    }

    public void setRunConnectivityTest(Boolean runConnectivityTest) {
        this.runConnectivityTest = runConnectivityTest;
    }

    public Boolean getRunDMapperTest() {
        return runDMapperTest;
    }

    public void setRunDMapperTest(Boolean runDMapperTest) {
        this.runDMapperTest = runDMapperTest;
    }

    public Boolean getRunXmlTest() {
        return runXmlTest;
    }

    public void setRunXmlTest(Boolean runXmlTest) {
        this.runXmlTest = runXmlTest;
    }

    public Boolean getRunOAuth() {
        return runOAuth;
    }

    public void setRunOAuth(Boolean runOAuth) {
        this.runOAuth = runOAuth;
    }

    public void setSelectedBranch(String branch) {
        this.selectedBranch = branch;
    }

    public String getSelectedBranch() {
        return selectedBranch;
    }

    public List<String> getBranches() {
        return branches;
    }

    public boolean addBranch(String name) {
        if (!StringUtils.isEmpty(name) && !branches.contains(name)) {
            return branches.add(name);
        }

        return false;
    }

}