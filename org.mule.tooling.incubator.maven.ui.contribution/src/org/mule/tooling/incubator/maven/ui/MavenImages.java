package org.mule.tooling.incubator.maven.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * Holds images used by various Mule views.
 */
public class MavenImages {

    /** Image registry */
    private static ImageRegistry registry = new ImageRegistry();

    

    /** Base path for icons */
    private static final IPath ICONS_PATH = new Path("icons");

    /** Mule icon */
    public static final ImageDescriptor MVN = getManaged("", "maven-16x16.png");
    public static final Image JAR  = getManagedImage("", "jar.gif");
    public static final ImageDescriptor REFRESH = getManaged("", "refresh.gif");
    public static final ImageDescriptor EFFECTIVE_POM = getManaged("", "effectivePom.gif");
    public static final ImageDescriptor ADD = getManaged("", "add.gif");
    public static final ImageDescriptor RUN = getManaged("", "run.gif");
    public static final ImageDescriptor OFFLINE = getManaged("", "offline.gif");
    public static final ImageDescriptor SKIP_TESTS = getManaged("", "skipTests.png");
    public static final ImageDescriptor CONFIGURE = getManaged("", "configure.gif");
    public static final Image LIFE_CYCLE_GOAL = getManagedImage("", "configure.gif");
    public static final Image PROFILES = getManagedImage("", "users.gif");
    public static final Image PLUGIN = getManagedImage("", "plugin.gif");
    public static final Image MID_LABEL = getManagedImage("", "midLabel.gif");
    public static final ImageDescriptor SOURCE = getManaged("", "source.gif");
    public static final ImageDescriptor JAVADOC = getManaged("", "javadoc.gif");
    
    /**
     * Get an image descriptor from the managed registry.
     * 
     * @param prefix
     * @param path
     * @return
     */
    public static ImageDescriptor getManaged(String prefix, String path) {
        IPath relPath = ICONS_PATH.append(prefix).append(path);
        ImageDescriptor desc = registry.getDescriptor(relPath.toString());
        if (desc == null) {
            desc = create(relPath);
            registry.put(relPath.toString(), desc);
        }
        return desc;
    }

    /**
     * Get an image from the managed registry.
     * 
     * @param prefix
     * @param path
     * @return
     */
    public static Image getManagedImage(String prefix, String path) {
        IPath relPath = ICONS_PATH.append(prefix).append(path);
        Image image = registry.get(relPath.toString());
        if (image == null) {
            ImageDescriptor desc = getManaged(prefix, path);
            image = desc.createImage();
        }
        return image;
    }

    /**
     * Create an image descriptor.
     * 
     * @param prefix
     * @param name
     * @return
     */
    public static ImageDescriptor create(IPath path) {
        return createImageDescriptor(MavenUIPlugin.getDefault().getBundle(), path, true);
    }

    /**
     * Create an ImageDescriptor for a bundle-relative path.
     * 
     * @param bundle
     * @param path
     * @param useMissingImageDescriptor
     * @return
     */
    public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor) {
        URL url = FileLocator.find(bundle, path, null);
        if (url != null) {
            return ImageDescriptor.createFromURL(url);
        }
        if (useMissingImageDescriptor) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
        return null;
    }
}