package org.mule.tooling.incubator.maven.ui.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.mule.tooling.incubator.maven.core.MavenArtifactResolver;

public class ProjectModelCache {

    private static ProjectModelCache INSTANCE;

    ProjectModelCache() {

    }

    public static ProjectModelCache getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ProjectModelCache();
        return INSTANCE;
    }

    Map<String, Object> cache = new HashMap<String, Object>();

    public PluginDescriptor getPluginDescriptor(Plugin plugin) throws IOException, PlexusConfigurationException, Exception {
        synchronized (cache) {
            PluginDescriptor descriptor = (PluginDescriptor) cache.get(plugin.getKey());
            if (descriptor == null) {
                String version = plugin.getVersion();
                VersionRange range = null;
                if (version == null) {
                    version = "LATEST";
                    range = VersionRange.createFromVersionSpec(version);
                } else {
                    range = VersionRange.createFromVersion(version);
                }
                DefaultArtifact pluginArtifact = new DefaultArtifact(plugin.getGroupId(), plugin.getArtifactId(), range, null, "jar", null, new DefaultArtifactHandler("jar"));
                descriptor = MavenArtifactResolver.getInstance().getPluginDescriptor(pluginArtifact);
                cache.put(plugin.getKey(), descriptor);
            }

            return descriptor;
        }
    }
}
