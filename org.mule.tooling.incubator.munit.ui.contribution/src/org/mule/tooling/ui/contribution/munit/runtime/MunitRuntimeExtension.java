package org.mule.tooling.ui.contribution.munit.runtime;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.mule.tooling.core.model.IMuleProject;

public class MunitRuntimeExtension {

    private List<MunitRuntime> runtimes = new ArrayList<MunitRuntime>();
    private static MunitRuntimeExtension instance;

    public static synchronized MunitRuntimeExtension getInstance() {
        if (instance == null) {
            instance = new MunitRuntimeExtension();
        }
        return instance;
    }

    private MunitRuntimeExtension() {
        IConfigurationElement[] configurationElementsFor = Platform.getExtensionRegistry().getConfigurationElementsFor("org.mule.tooling.ui.contribution.munit.munitRuntime");
        for (IConfigurationElement configElement : configurationElementsFor) {
            MunitRuntime runtime = new MunitRuntime(configElement.getContributor().getName(), configElement.getAttribute("munitVersion"),
                    configElement.getAttribute("minMuleVersion"), configElement.getAttribute("maxMuleVersion"));

            for (IConfigurationElement library : configElement.getChildren()) {
                MunitLibrary munitLibrary = new MunitLibrary(library.getAttribute("path"));
                for (IConfigurationElement mavenConfig : library.getChildren()) {
                    munitLibrary.setMavenConfiguration(new MunitMavenConfiguration(mavenConfig.getAttribute("groupId"), mavenConfig.getAttribute("artifactId"), mavenConfig
                            .getAttribute("version")));
                }
                runtime.add(munitLibrary);
            }

            runtimes.add(runtime);
        }
    }

    public MunitRuntime getMunitRuntimeFor(IMuleProject muleProject) {
        for (MunitRuntime runtime : runtimes) {
            if (runtime.accepts(muleProject.getServerDefinition())) {
                return runtime;
            }
        }
        return null;
    }

}
