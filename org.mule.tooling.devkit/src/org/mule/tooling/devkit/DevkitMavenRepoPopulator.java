package org.mule.tooling.devkit;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

public class DevkitMavenRepoPopulator {

    private static final String TAG_CLASSIFIER = "classifier";
    private static final String MULTINSTALL_ARTIFACTID = "multinstall-maven-plugin";
    private static final String MULTINSTALL_GROUPID = "org.mule.tools";

    private static final String TAG_INSTALLABLE_ARTIFACTS = "installableArtifacts";
    private static final String TAG_POM_FILE = "pomFile";
    private static final String TAG_INSTALLABLE_ARTIFACT = "installableArtifact";
    private static final String TAG_FILE = "file";
    private static final String TAG_TYPE = "type";

    private static final String PACKAGING_JAR = "jar";
    private static final String PACKAGING_ZIP = "zip";

    private static final String MONITOR_TASK_TITLE = "Installing DevKit Plugin Maven Dependencies";
    private static final int MONITOR_TOTAL_WORK = 100;

    private final String mavenInstallationHome;
    private final String mavenOpts;
    private final String m2repoPath;
    private OutputStream loggerOutputStream;

    private DevkitMavenRepoPopulator(String mavenInstallationHome, String mavenOpts, String m2repoPath, OutputStream loggerOutputStream) {
        this.mavenInstallationHome = mavenInstallationHome;
        this.mavenOpts = mavenOpts;
        this.m2repoPath = m2repoPath;
        this.loggerOutputStream = loggerOutputStream;
    }

    public static DevkitMavenRepoPopulator newInstance(String mavenInstallationHome, String mavenOpts, String m2repoPath, OutputStream loggerOutputStream) {
        return new DevkitMavenRepoPopulator(mavenInstallationHome, mavenOpts, m2repoPath, loggerOutputStream);
    }

    public int populate(IProgressMonitor monitor) {
        monitor.beginTask(MONITOR_TASK_TITLE, MONITOR_TOTAL_WORK);
        try {
            List<File> jarFiles = collectJarFilesFromLibFolder(monitor);
            List<File> filesToInstall = new ArrayList<File>(jarFiles.size());
            filesToInstall.addAll(jarFiles);
            if (!filesToInstall.isEmpty()) {
                return this.installFiles(filesToInstall, new SubProgressMonitor(monitor, MONITOR_TOTAL_WORK / 2));
            } else {
                // assume everything went ok, because actually nothing had to be done here
                return 0;
            }
        } finally {
            monitor.done();
        }
    }

    private int installFiles(List<File> filesToInstall, SubProgressMonitor subProgressMonitor) {
        // TODO Auto-generated method stub
        return 0;
    }

    private List<File> collectJarFilesFromLibFolder(IProgressMonitor monitor) {
        List<File> jarFiles = new ArrayList<File>();
        List<IPath> jarPaths = new ArrayList<IPath>();
        // TODO: LOCATE INSTALLATION AND GET LIB FOLDER
        System.out.println(DevkitUIPlugin.getDefault().getBundle().getEntry("lib").getPath());
        // collection of should-be jarfiles, that are files because JarFile
        // misses many useful methods
        for (IPath path : jarPaths) {
            if (shouldCopyJarAccordingToInclusionsAndExclusions(path)) {
                File file = path.toFile().getAbsoluteFile();
                jarFiles.add(file);
            }
        }
        return jarFiles;
    }

    private boolean shouldCopyJarAccordingToInclusionsAndExclusions(IPath path) {
        // TODO Auto-generated method stub
        return false;
    }

}
