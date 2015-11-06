package org.mule.tooling.studio.ui.editor;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.editor.model.IComponentElement;
import org.mule.tooling.editor.model.Namespace;

public class NamespaceLabelProvider extends LabelProvider {

    public String getText(Object obj) {
        if (obj instanceof IComponentElement) {
            IComponentElement element = (IComponentElement) obj;
            GetLabelsVisitor visitor = new GetLabelsVisitor();
            element.accept(visitor);
            return visitor.getLabel();
        }
        return obj.toString();
    }

    public Image getImage(Object obj) {
        if (obj instanceof Namespace) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
        return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
    }
}