package org.mule.tooling.devkit.export;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;

public class ProjectLabelProvider extends LabelProvider {

    public String getText(Object element) {
        if (element instanceof IProject) {
            return ((IProject) element).getName();
        } else {
            return super.getText(element);
        }
    }
}
