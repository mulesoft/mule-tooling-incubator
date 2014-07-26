package org.mule.tooling.incubator.maven.ui.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.mule.tooling.incubator.maven.model.Profile;

public class Configuration {

    public List<Profile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<Profile>();
        }
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<String> getProjects() {
        if (projects == null) {
            return new ArrayList<String>();
        }

        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public void addPlugins(String projectName, Collection<Plugin> pluginsList) {
        Set<Plugin> currentPlugin = getPlugins(projectName);
        if (currentPlugin == null) {
            currentPlugin = new HashSet<Plugin>();
            plugins.put(projectName, currentPlugin);
        }
        currentPlugin.addAll(pluginsList);
    }

    public Set<Plugin> getPlugins(String projectName) {
        return plugins.get(projectName);
    }

    public void addMojos(String pluginId, Collection<MojoDescriptor> mojoList) {
        Set<MojoDescriptor> currentMojos = getMojos(pluginId);
        if (currentMojos == null) {
            currentMojos = new HashSet<MojoDescriptor>();
            mojos.put(pluginId, currentMojos);
        }
        currentMojos.addAll(mojoList);

    }

    public Set<MojoDescriptor> getMojos(String pluginId) {
        return mojos.get(pluginId);
    }

    List<Profile> profiles;

    List<String> projects;

    Map<String, Set<Plugin>> plugins = new HashMap<String, Set<Plugin>>();
    Map<String, Set<MojoDescriptor>> mojos = new HashMap<String, Set<MojoDescriptor>>();

    public void clear() {
        plugins.clear();
        mojos.clear();
    }
}
