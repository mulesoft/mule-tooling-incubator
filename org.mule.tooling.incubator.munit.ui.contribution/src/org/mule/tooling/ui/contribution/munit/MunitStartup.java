package org.mule.tooling.ui.contribution.munit;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;
import org.mule.tooling.ui.contribution.munit.listeners.PomResourceListener;

public class MunitStartup implements IStartup {

    @Override
    public void earlyStartup() {
        addListeners();
    }

    private void addListeners() {
        // TODO move the getting of the workspace to MunitUtilsAPI
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IResourceChangeListener resourceChangeListener = new PomResourceListener();
        workspace.addResourceChangeListener(resourceChangeListener);
    }

}
