package org.mule.tooling.devkit;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class DevkitImages {

    /** Image registry */
    private static ImageRegistry registry = new ImageRegistry();

    /** Base path for icons */
    private static final IPath ICONS_PATH = new Path("icons");
    
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
     * Create an image descriptor.
     * 
     * @param prefix
     * @param name
     * @return
     */
    public static ImageDescriptor create(IPath path) {
        return createImageDescriptor(DevkitUIPlugin.getDefault().getBundle(), path, true);
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
