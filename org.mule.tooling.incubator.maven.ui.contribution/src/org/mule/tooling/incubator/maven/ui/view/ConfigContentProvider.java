package org.mule.tooling.incubator.maven.ui.view;

import org.apache.maven.model.Plugin;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mule.tooling.incubator.maven.model.LifeCycle;

public class ConfigContentProvider implements ITreeContentProvider {

    private Configuration configuration;
    private String section = "Projects";
    private String lifeCycleSection = "Lifecycle";
    private String pluginsSection = "Plugins";

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.configuration = (Configuration) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement.equals(configuration)) {
            return new Object[] { section };
        }
        if (parentElement.equals(section)) {
            return configuration.getProjects().toArray();
        }
        if (configuration.getProjects().contains(parentElement)) {
            return new Object[] { lifeCycleSection, new ProjectLabel(pluginsSection, (String) parentElement) };
        }
        if (parentElement.equals(lifeCycleSection)) {
            return LifeCycle.getReducedLifeCycle();
        }
        if (parentElement instanceof ProjectLabel) {
            if (configuration.getPlugins(((ProjectLabel) parentElement).projectName) != null) {
                return configuration.getPlugins(((ProjectLabel) parentElement).projectName).toArray();
            }
        }
        if (parentElement instanceof Plugin) {
            if (configuration.getMojos(((Plugin) parentElement).getKey()) != null)
                return configuration.getMojos(((Plugin) parentElement).getKey()).toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {

        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return this.getChildren(element) != null && this.getChildren(element).length > 0;
    }
}
