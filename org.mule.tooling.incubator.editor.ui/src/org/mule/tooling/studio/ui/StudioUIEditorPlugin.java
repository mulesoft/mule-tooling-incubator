package org.mule.tooling.studio.ui;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class StudioUIEditorPlugin extends AbstractUIPlugin {

    // The shared instance.
    private static StudioUIEditorPlugin plugin;
    // Resource bundle.
    private ResourceBundle resourceBundle;
    private FormColors formColors;
    public static final String IMG_FORM_BG = "formBg"; //$NON-NLS-1$
    public static final String IMG_LARGE = "large"; //$NON-NLS-1$
    public static final String IMG_HORIZONTAL = "horizontal"; //$NON-NLS-1$
    public static final String IMG_VERTICAL = "vertical"; //$NON-NLS-1$
    public static final String IMG_SAMPLE = "sample"; //$NON-NLS-1$
    public static final String IMG_WIZBAN = "wizban"; //$NON-NLS-1$
    public static final String IMG_LINKTO_HELP = "linkto_help"; //$NON-NLS-1$
    public static final String IMG_HELP_TOPIC = "help_topic"; //$NON-NLS-1$
    public static final String IMG_CLOSE = "close"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public StudioUIEditorPlugin() {
        plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle("org.eclipse.ui.forms.examples.internal.ExamplesPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    protected void initializeImageRegistry(ImageRegistry registry) {
        registerImage(registry, IMG_FORM_BG, "form_banner.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_LARGE, "large_image.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_HORIZONTAL, "th_horizontal.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_VERTICAL, "th_vertical.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_SAMPLE, "sample.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_WIZBAN, "newprj_wiz.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_LINKTO_HELP, "linkto_help.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_HELP_TOPIC, "help_topic.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_CLOSE, "close_view.gif"); //$NON-NLS-1$
    }

    private void registerImage(ImageRegistry registry, String key, String fileName) {
        try {
            IPath path = new Path("icons/" + fileName); //$NON-NLS-1$
            URL url = FileLocator.find(getBundle(), path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
        }
    }

    public FormColors getFormColors(Display display) {
        if (formColors == null) {
            formColors = new FormColors(display);
            formColors.markShared();
        }
        return formColors;
    }

    /**
     * Returns the shared instance.
     */
    public static StudioUIEditorPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = StudioUIEditorPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null ? bundle.getString(key) : key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void stop(BundleContext context) throws Exception {
        try {
            if (formColors != null) {
                formColors.dispose();
                formColors = null;
            }
        } finally {
            super.stop(context);
        }
    }

    public Image getImage(String key) {
        return getImageRegistry().get(key);
    }

    public ImageDescriptor getImageDescriptor(String key) {
        return getImageRegistry().getDescriptor(key);
    }
}