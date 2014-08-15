package org.mule.tooling.incubator.maven.ui.view;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.incubator.maven.model.Profile;
import org.mule.tooling.incubator.maven.ui.MavenImages;

public class ProfileLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof Profile) {
            return ((Profile) element).getName();
        }
        return element.toString();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof Profile) {
            return MavenImages.PROFILES;
        }
        return PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
    }
}
