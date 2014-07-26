package org.mule.tooling.incubator.maven.ui.view;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.incubator.maven.model.LifeCycle;
import org.mule.tooling.incubator.maven.model.Profile;
import org.mule.tooling.incubator.maven.ui.MavenImages;

public class ConfigLabelProvider extends LabelProvider implements DelegatingStyledCellLabelProvider.IStyledLabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof Profile) {
            return ((Profile) element).getName();
        }
        if (element instanceof Plugin) {
            return ((Plugin) element).getKey();
        }
        if (element instanceof ProjectLabel) {
            return ((ProjectLabel) element).label;
        }
        if (element instanceof MojoDescriptor) {
            MojoDescriptor mojo = ((MojoDescriptor) element);
            return mojo.getPluginDescriptor().getGoalPrefix() + ":" + mojo.getGoal();
        }
        return element.toString();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof LifeCycle) {
            return MavenImages.LIFE_CYCLE_GOAL;
        }
        if (element.equals("Projects")) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
        }
        if (element.equals("Profiles")) {
            return MavenImages.PROFILES;
        }
        if ("Lifecycle".equals(element)) {
            return MavenImages.MID_LABEL;
        }
        if (element instanceof ProjectLabel) {
            return MavenImages.MID_LABEL;
        }
        if (element instanceof MojoDescriptor) {
            return MavenImages.LIFE_CYCLE_GOAL;
        }
        if (element instanceof Plugin) {
            return MavenImages.PLUGIN;
        }
        return PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
    }

    @Override
    public StyledString getStyledText(Object element) {
        StyledString ss = new StyledString();
        if (element instanceof Plugin) {
            Plugin plugin = (Plugin) element;
            ss.append(plugin.getArtifactId());
            ss.append(" (" + plugin.getKey() + ((plugin.getVersion() != null) ? ":" + plugin.getVersion() : "") + ")", StyledString.QUALIFIER_STYLER);
        } else {
            ss.append(getText(element));
        }
        return ss;
    }
}
