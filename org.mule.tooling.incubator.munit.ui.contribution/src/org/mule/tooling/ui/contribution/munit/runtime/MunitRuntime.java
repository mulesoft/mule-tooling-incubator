package org.mule.tooling.ui.contribution.munit.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.core.runtime.server.ServerDefinition;

public class MunitRuntime {

    private String bundleId;
    private String minMuleVersion;
    private String maxMuleVersion;
    private String munitVersion;

    private List<MunitLibrary> libraries = new ArrayList<MunitLibrary>();

    public MunitRuntime(String id, String munitVersion, String minMuleVersion, String maxMuleVersion) {
        this.bundleId = id;
        this.minMuleVersion = minMuleVersion;
        this.maxMuleVersion = maxMuleVersion;
        this.munitVersion = munitVersion;
    }

    public void add(MunitLibrary library) {
        libraries.add(library);
    }

    public String getBundleId() {
        return bundleId;
    }

    public String getMunitVersion() {
        return munitVersion;
    }

    public List<MunitLibrary> getLibraries() {
        return libraries;
    }

    public boolean accepts(ServerDefinition serverDefinition) {
        if (!StringUtils.isBlank(maxMuleVersion)) {
            if (serverDefinition.compareVersionTo(maxMuleVersion) >= 0) {
                return false;
            }
        }

        if (!StringUtils.isBlank(minMuleVersion)) {
            if (serverDefinition.compareVersionTo(minMuleVersion) < 0) {
                return false;
            }
        }

        return true;
    }

}
