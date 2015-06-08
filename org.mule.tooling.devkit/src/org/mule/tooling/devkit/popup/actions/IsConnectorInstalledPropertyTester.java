package org.mule.tooling.devkit.popup.actions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.mule.tooling.core.utils.BundleJarFileInspector;
import org.mule.tooling.core.utils.BundleManifestReader;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.osgi.framework.Bundle;

public class IsConnectorInstalledPropertyTester extends PropertyTester {

    private static final String IS_INSTALLED = "isConnectorInstalled";

    public IsConnectorInstalledPropertyTester() {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

        if ((receiver instanceof IProject) && property.equals(IS_INSTALLED)) {

            final IJavaProject selectedProject = JavaCore.create((IProject) receiver);

            if (selectedProject != null && selectedProject.isOpen()) {
                IFolder folder = selectedProject.getProject().getFolder(DevkitUtils.UPDATE_SITE_FOLDER).getFolder("plugins");
                if (folder.exists()) {
                    Collection<File> files = FileUtils.listFiles(folder.getLocation().toFile(), new String[] { "jar" }, false);
                    for (File pluginJarFile : files) {

                        BundleManifestReader manifestReader;
                        try {
                            manifestReader = geManifestFromJar(pluginJarFile);
                            Bundle bundle = Platform.getBundle(manifestReader.getSymbolicName());
                            if (bundle != null) {
                                return bundle.getState() != Bundle.UNINSTALLED;
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    private BundleManifestReader geManifestFromJar(File pluginJar) throws IOException {
        return new BundleJarFileInspector(new JarFile(pluginJar)).getManifest();

    }
}
