package org.mule.tooling.incubator.maven.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.mule.tooling.core.action.ProjectLifecycleAction;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.incubator.maven.ui.MavenUIPlugin;

public abstract class MavenProjectLifecycleAction implements ProjectLifecycleAction {

    @Override
    public void execute(IMuleProject muleProject, IProgressMonitor monitor) {
        checkedExecute(muleProject, monitor);
    }

    public boolean checkedExecute(IMuleProject muleProject, IProgressMonitor monitor) {
        return this.doExecute(muleProject, monitor);
    }

    protected abstract boolean doExecute(IMuleProject muleProject, IProgressMonitor monitor);

}
