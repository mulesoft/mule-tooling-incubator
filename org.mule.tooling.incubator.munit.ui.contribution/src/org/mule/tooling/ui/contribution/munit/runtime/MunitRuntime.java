package org.mule.tooling.ui.contribution.munit.runtime;

import java.util.ArrayList;
import java.util.List;

public class MunitRuntime {

    private String bundleId;
    private String muleVersionRegex;
    private String munitVersion;

    private List<MunitLibrary> libraries = new ArrayList<MunitLibrary>();

    public MunitRuntime(String id, String muleVersionRegex, String munitVersion) {
        this.bundleId = id;
        this.muleVersionRegex = muleVersionRegex;
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

    public boolean accepts(String runtimeId) {
        return runtimeId.matches(muleVersionRegex);
    }

}
