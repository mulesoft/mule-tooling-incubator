package org.mule.tooling.devkit.treeview;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.devkit.treeview.model.NodeItem;

public class ModuleLabelProvider extends LabelProvider {

    private static final Image DEFAULT = getImage("default.gif");

    @Override
    public String getText(Object element) {
        if (element instanceof NodeItem) {
            return ((NodeItem) element).getLabel();
        }
        return "n/a";
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof NodeItem) {
            return ((NodeItem) element).getImage();
        }
        return DEFAULT;
    }

    // Helper Method to load the images
    private static Image getImage(String file) {
        return DevkitImages.getManagedImage("", file);

    }
}
