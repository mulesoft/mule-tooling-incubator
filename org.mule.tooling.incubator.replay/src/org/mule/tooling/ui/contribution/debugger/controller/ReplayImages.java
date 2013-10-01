package org.mule.tooling.ui.contribution.debugger.controller;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.incubator.replay.ReplayPlugin;

import org.osgi.framework.Bundle;

public class ReplayImages {

    public static String SNAPSHOT = "icons/snapshot-16x16.png";
    public static String EDIT = "icons/edit-16x16.png";
    public static String REPLAY = "icons/replay-16x16.png";
    public static String PLAY = "icons/play-16x16.png";
    public static String DUPLICATE = "icons/copy-16x16.png";

    private static ReplayImages instance = null;

    public static ReplayImages getDebuggerImages() {
        if (instance == null) {
            instance = new ReplayImages();
        }
        return instance;
    }

    private ReplayImages() {

    }

    public Image getImage(String key) {
        return JFaceResources.getImageRegistry().get(key);

    }

    public Image getImageByPath(String path) {
        Bundle bundle = ReplayPlugin.getDefault().getBundle();
        String key = bundle.getSymbolicName() + path;
        getImageDescriptor(path);
        return JFaceResources.getImage(key);

    }

    public ImageDescriptor getImageDescriptor(String path) {
        Bundle bundle = ReplayPlugin.getDefault().getBundle();
        String key = bundle.getSymbolicName() + path;
        ImageDescriptor desc = JFaceResources.getImageRegistry().getDescriptor(key);
        if (desc == null) {
            desc = ImageDescriptor.createFromURL(bundle.getEntry(path));
            JFaceResources.getImageRegistry().put(key, desc);
        }
        return desc;
    }

}
