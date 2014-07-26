package org.mule.tooling.incubator.maven.ui.view;

import org.apache.maven.model.Dependency;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.incubator.maven.core.TreeNode;
import org.mule.tooling.incubator.maven.ui.MavenImages;

public class DependencyTreeLabelProvider extends LabelProvider implements DelegatingStyledCellLabelProvider.IStyledLabelProvider {

    @Override
    public String getText(Object element) {
        StringBuilder stringBuffer = new StringBuilder();
        if (element instanceof TreeNode) {
            TreeNode item = (TreeNode) element;
            Dependency plugin = (Dependency) item.getNodeItem();
            stringBuffer.append(plugin.getArtifactId());
            stringBuffer.append(" (" + plugin.getGroupId() + ":" + plugin.getArtifactId() + ":" + plugin.getVersion() + ") [" + plugin.getScope() + "]");
            return stringBuffer.toString();
        }
        return element.toString();
    }

    @Override
    public Image getImage(Object element) {
        return MavenImages.JAR;
    }

    @Override
    public StyledString getStyledText(Object element) {
        StyledString ss = new StyledString();
        if (element instanceof TreeNode) {
            TreeNode item = (TreeNode) element;
            Dependency plugin = (Dependency) item.getNodeItem();
            ss.append(plugin.getArtifactId());
            ss.append(" (" + plugin.getGroupId() + ":" + plugin.getArtifactId() + ":" + plugin.getVersion() + ") [" + plugin.getScope() + "]", StyledString.QUALIFIER_STYLER);
        } else {
            ss.append(getText(element));
        }
        return ss;
    }
}
