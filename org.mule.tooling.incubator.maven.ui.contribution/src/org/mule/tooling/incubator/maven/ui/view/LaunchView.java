package org.mule.tooling.incubator.maven.ui.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.incubator.maven.core.MavenArtifactResolver;
import org.mule.tooling.incubator.maven.core.MavenUtils;
import org.mule.tooling.incubator.maven.model.ILifeCycle;
import org.mule.tooling.incubator.maven.model.LifeCycle;
import org.mule.tooling.incubator.maven.model.Profile;
import org.mule.tooling.incubator.maven.ui.MavenCommandLineConfigurationComponent;
import org.mule.tooling.incubator.maven.ui.MavenImages;
import org.mule.tooling.incubator.maven.ui.actions.LifeCycleJob;
import org.mule.tooling.incubator.maven.ui.internal.ProjectModelCache;
import org.mule.tooling.incubator.maven.ui.launch.MavenLaunchShortcut;
import org.mule.tooling.maven.MavenPlugin;
import org.mule.tooling.maven.ui.MavenUIPlugin;
import org.xml.sax.SAXException;

public class LaunchView extends ViewPart implements IResourceChangeListener {

    public static final String ID = "org.mule.tooling.incubator.maven.ui.view.LaunchView";

    private TreeViewer viewer;
    private CheckboxTableViewer profilesList;
    Configuration profiles;
    private Configuration config;
    boolean skipTestsMode = false;
    boolean offlineMode = false;
    private Action run;
    private TreeSelection selected;
    TableColumn tc;

    @Override
    public void createPartControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        final SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(1, 1).grab(true, true).applyTo(sashForm);

        profilesList = CheckboxTableViewer.newCheckList(sashForm, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        profilesList.setContentProvider(new ProfileContentProvider());
        profilesList.setLabelProvider(new ProfileLabelProvider());

        Table table = profilesList.getTable();
        table.setHeaderVisible(true);
        tc = new TableColumn(table, SWT.LEFT | SWT.FILL, 0);
        tc.setText("Profiles:");
        tc.setResizable(false);
        tc.pack();

        PatternFilter filter = new PatternFilter();
        FilteredTree tree = new FilteredTree(sashForm, SWT.NULL, filter, true);
        viewer = tree.getViewer();
        viewer.setContentProvider(new ConfigContentProvider());
        viewer.setLabelProvider(new FilteredDelegatingStyledCellLabelProvider(new ConfigLabelProvider()));
        // Expand the tree
        viewer.setAutoExpandLevel(2);
        // provide the input to the ContentProvider

        config = new Configuration();
        profiles = new Configuration();
        getMavenProjects();
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                selected = (TreeSelection) event.getSelection();
                if (selected.getFirstElement() instanceof LifeCycle || selected.getFirstElement() instanceof MojoDescriptor) {
                    String command = (offlineMode ? "-o" : "") + (skipTestsMode ? " -DskipTests" : "");
                    command = command.trim();
                    if (profilesList.getCheckedElements() != null && profilesList.getCheckedElements().length > 0) {
                        command += " -P" + StringUtils.join(profilesList.getCheckedElements(), ",");
                    }
                    LifeCycle lifeCycle = LifeCycle.CLEAN;
                    if (selected.getFirstElement() instanceof LifeCycle) {
                        lifeCycle = (LifeCycle) selected.getFirstElement();
                    } else if (selected.getFirstElement() instanceof MojoDescriptor) {
                        MojoDescriptor mojo = (MojoDescriptor) selected.getFirstElement();
                        if (mojo.getPhase() != null)
                            lifeCycle = LifeCycle.valueOf(mojo.getPhase());
                        command = mojo.getPluginDescriptor().getGoalPrefix() + ":" + mojo.getGoal() + " " + command;
                    }
                    LifeCycleJob job = new LifeCycleJob(lifeCycle, ResourcesPlugin.getWorkspace().getRoot().getProject(selected.getPaths()[0].getSegment(1).toString()), command,"");
                    job.setPriority(Job.BUILD);
                    job.schedule();
                }
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selected = (TreeSelection) event.getSelection();
                if (selected.getFirstElement() instanceof LifeCycle) {
                    run.setEnabled(true);
                } else {
                    run.setEnabled(false);
                }

            }
        });

        Action refreshAction = new Action("Refresh", Action.AS_PUSH_BUTTON) {

            @Override
            public void run() {
                getMavenProjects();
            }
        };
        refreshAction.setImageDescriptor(MavenImages.REFRESH);

        run = new Action("Run Maven Build", Action.AS_PUSH_BUTTON) {

            @Override
            public void run() {
                if (selected != null && selected.getFirstElement() instanceof LifeCycle) {
                    scheduleMavenPhaseRun();
                }
            }

            private void scheduleMavenPhaseRun() {
                String command = (offlineMode ? "-o" : "") + (skipTestsMode ? " -DskipTests" : "");
                command = command.trim();
                if (profilesList.getCheckedElements() != null && profilesList.getCheckedElements().length > 0) {
                    command += " -P" + StringUtils.join(profilesList.getCheckedElements(), ",");
                }
                IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(selected.getPaths()[0].getSegment(1).toString());
                LifeCycleJob job = new LifeCycleJob((LifeCycle) selected.getFirstElement(), project, command,"");
                job.setPriority(Job.BUILD);
                job.schedule();
            }
        };
        run.setImageDescriptor(MavenImages.RUN);
        run.setEnabled(false);

        Action runOffline = new Action("Toggle 'Offline' Mode", Action.AS_CHECK_BOX) {

            @Override
            public void run() {
                offlineMode = isChecked();
            }
        };
        runOffline.setImageDescriptor(MavenImages.OFFLINE);

        Action skipTests = new Action("Toggle 'Skip Tests' Mode", Action.AS_CHECK_BOX) {

            @Override
            public void run() {
                skipTestsMode = isChecked();
            }
        };
        skipTests.setImageDescriptor(MavenImages.SKIP_TESTS);

        Action configure = new Action("Maven Settings", Action.AS_PUSH_BUTTON) {

            @Override
            public void run() {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, "org.mule.tooling.maven.mavenpage", null, null);
                dialog.open();
            }
        };
        configure.setImageDescriptor(MavenImages.CONFIGURE);

        getViewSite().getActionBars().getToolBarManager().add(refreshAction);
        getViewSite().getActionBars().getToolBarManager().add(new Separator());
        getViewSite().getActionBars().getToolBarManager().add(runOffline);
        getViewSite().getActionBars().getToolBarManager().add(skipTests);
        getViewSite().getActionBars().getToolBarManager().add(run);
        getViewSite().getActionBars().getToolBarManager().add(new Separator());
        getViewSite().getActionBars().getToolBarManager().add(configure);
        getViewSite().getActionBars().getToolBarManager().update(true);

        createContextMenu();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

    }

    @Override
    public void setFocus() {

    }

    private void getMavenProjects() {
        config.clear();
        String m2repo = MavenPlugin.getM2repoPath();
        if (!m2repo.isEmpty()) {
            File settingsFile = new File(new File(m2repo).getParentFile(), "settings.xml");
            SettingsXpp3Reader reader = new SettingsXpp3Reader();
            if (settingsFile.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(settingsFile);
                    Settings settings = reader.read(fis);
                    List<Profile> newProfiles = new ArrayList<Profile>();

                    for (org.apache.maven.settings.Profile profile : settings.getProfiles()) {
                        newProfiles.add(new Profile(profile.getId()));
                    }
                    profiles.setProfiles(newProfiles);
                    profilesList.setInput(profiles);
                    tc.pack();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        List<String> names = new ArrayList<String>();
        IProject[] projects = workspace.getRoot().getProjects();
        for (int index = 0; index < projects.length; index++) {
            if (projects[index].isOpen()) {
                if (MavenUtils.isMavenBased(projects[index])) {
                    IProject project = projects[index];
                    IFile pomFile = project.getFile("pom.xml");
                    Model currentModel = null;
                    try {
                        currentModel = MavenArtifactResolver.getInstance().getModel(pomFile.getRawLocation().toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (currentModel == null)
                            return;
                        List<Profile> newProfiles = new ArrayList<Profile>();
                        for (org.apache.maven.model.Profile profile : currentModel.getProfiles()) {
                            newProfiles.add(new Profile(profile.getId()));
                            if (profile.getBuild() != null) {
                                config.addPlugins(project.getName(), profile.getBuild().getPlugins());
                                for (Plugin plugin : profile.getBuild().getPlugins()) {
                                    getMojosAndGoals(currentModel, plugin);
                                }
                            }
                        }
                        for (Profile profile : newProfiles) {
                            if (!profiles.getProfiles().contains(profile)) {
                                profiles.getProfiles().add(profile);
                            }
                        }
                        if (currentModel.getBuild() != null) {
                            config.addPlugins(project.getName(), currentModel.getBuild().getPlugins());
                            for (Plugin plugin : currentModel.getBuild().getPlugins()) {
                                getMojosAndGoals(currentModel, plugin);
                            }
                        }
                        if (currentModel.getParent() != null) {
                            DefaultArtifact artifact = new DefaultArtifact(currentModel.getParent().getGroupId(), currentModel.getParent().getArtifactId(),
                                    VersionRange.createFromVersion(currentModel.getParent().getVersion()), null, "pom", null, new DefaultArtifactHandler("pom"));
                            Model model = MavenArtifactResolver.getInstance().getModel(artifact);
                            config.addPlugins(project.getName(), model.getBuild().getPlugins());
                            for (Plugin plugin : model.getBuild().getPlugins()) {
                                getMojosAndGoals(model, plugin);
                            }
                            for (org.apache.maven.model.Profile prof : model.getProfiles()) {
                                if (prof.getBuild() != null) {
                                    config.addPlugins(project.getName(), prof.getBuild().getPlugins());
                                    for (Plugin plugin : prof.getBuild().getPlugins()) {
                                        getMojosAndGoals(model, plugin);
                                    }
                                }
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (currentModel.getParent() != null) {
                        String path = currentModel.getParent().getGroupId().replace('.', File.separatorChar) + File.separatorChar
                                + currentModel.getParent().getArtifactId().replace(':', File.separatorChar) + File.separatorChar + currentModel.getParent().getVersion();
                        File pom = new File(m2repo, path);
                        if (pom.exists()) {
                            FilenameFilter fileNameFilter = new FilenameFilter() {

                                @Override
                                public boolean accept(File dir, String name) {
                                    if (name.lastIndexOf('.') > 0) {
                                        // get last index for '.' char
                                        int lastIndex = name.lastIndexOf('.');

                                        // get extension
                                        String str = name.substring(lastIndex);

                                        // match path name extension
                                        if (str.equals(".pom")) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            };

                            File[] files = pom.listFiles(fileNameFilter);
                            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                                List<Profile> newProfiles = new ArrayList<Profile>();
                                try {
                                    Model model = MavenArtifactResolver.getInstance().getModel(files[fileIndex]);
                                    for (org.apache.maven.model.Profile profile : model.getProfiles()) {
                                        newProfiles.add(new Profile(profile.getId()));
                                    }
                                } catch (Exception e) {
                                }

                                for (Profile profile : newProfiles) {
                                    if (!profiles.getProfiles().contains(profile)) {
                                        profiles.getProfiles().add(profile);
                                    }
                                }
                            }
                            profilesList.setInput(profiles);
                            tc.pack();
                        }
                    }
                    names.add(projects[index].getName());
                }
            }
        }
        config.setProjects(names);
        viewer.setInput(config);
    }

    private void getMojosAndGoals(Model model, Plugin plugin) throws IOException, PlexusConfigurationException, Exception {
        String version = plugin.getVersion();
        if (version != null && version.startsWith("$")) {
            version = model.getProperties().getProperty(plugin.getVersion().substring(2, plugin.getVersion().length() - 1));
            plugin.setVersion(version);
        }
        Set<MojoDescriptor> mojoList = new HashSet<MojoDescriptor>();
        try {
            PluginDescriptor pluginDesc = ProjectModelCache.getInstance().getPluginDescriptor(plugin);
            if (pluginDesc != null) {
                for (Object exec : pluginDesc.getMojos()) {
                    MojoDescriptor mojoDesc = (MojoDescriptor) exec;
                    mojoList.add(mojoDesc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        config.addMojos(plugin.getKey(), mojoList);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getDelta().getKind() == IResourceDelta.CHANGED) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    getMavenProjects();
                }
            });
        }
    }

    private void createContextMenu() {
        // Create menu manager.
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu(mgr);
            }
        });

        // Create menu.
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager mgr) {
        String label = "";
        String project = "";
        String mojoLabel = "";
        if (!((selected.getFirstElement() instanceof MojoDescriptor) || (selected.getFirstElement() instanceof LifeCycle))) {
            return;
        }
        String lifeCycle = "";
        if (selected != null) {
            project = selected.getPaths()[0].getSegment(1).toString();
            if (selected.getFirstElement() instanceof MojoDescriptor) {
                MojoDescriptor mojo = (MojoDescriptor) selected.getFirstElement();
                TreePath[] paths = selected.getPaths();
                for (int i = 0; i < paths.length; i++) {
                    TreePath path = paths[i];
                    Plugin plugin = (Plugin) path.getSegment(path.getSegmentCount() - 2);
                    label += plugin.getKey() + ":" + plugin.getVersion();
                }
                label += ":" + mojo.getGoal();
                mojoLabel = mojo.getPluginDescriptor().getGoalPrefix() + " " + mojo.getGoal();
            }
            if (selected.getFirstElement() instanceof LifeCycle) {
                label = selected.getFirstElement().toString();
                lifeCycle = label;
            }
        }
        final String lifeCyclePhase = lifeCycle;
        final String projectName = project;
        final String configName = project + " [" + (mojoLabel.isEmpty() ? lifeCyclePhase : mojoLabel) + "]";
        final String mojoCommand = "mvn "+label;

        Action run = new Action("Run '" + project + " [" + label + "]'") {

            @Override
            public void run() {
                String command = (offlineMode ? "-o" : "") + (skipTestsMode ? " -DskipTests" : "");
                command = command.trim();
                if (profilesList.getCheckedElements() != null && profilesList.getCheckedElements().length > 0) {
                    command += " -P" + StringUtils.join(profilesList.getCheckedElements(), ",");
                }
                IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(selected.getPaths()[0].getSegment(1).toString());
                LifeCycle lifeCycle = LifeCycle.CLEAN;
                if (selected.getFirstElement() instanceof LifeCycle) {
                    lifeCycle = (LifeCycle) selected.getFirstElement();
                } else if (selected.getFirstElement() instanceof MojoDescriptor) {
                    MojoDescriptor mojo = (MojoDescriptor) selected.getFirstElement();
                    lifeCycle = LifeCycle.valueOf(mojo.getPhase());
                }
                LifeCycleJob job = new LifeCycleJob(lifeCycle, project, mojoCommand + " " + command,"");
                job.setPriority(Job.BUILD);
                job.schedule();
            }
        };
        run.setImageDescriptor(MavenImages.RUN);
        Action create = new Action("Create '" + project + " [" + label + "]'") {

            @Override
            public void run() {
                ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
                ILaunchConfigurationType type = manager.getLaunchConfigurationType(MavenLaunchShortcut.MAVEN_LAUNCH_CONFIGURATION_TYPE);
                ILaunchConfigurationWorkingCopy workingCopy = null;
                try {
                    workingCopy = type.newInstance(null, configName);
                    if (profilesList.getCheckedElements() != null && profilesList.getCheckedElements().length > 0) {
                        workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_PROFILES, StringUtils.join(profilesList.getCheckedElements(), ","));
                    } else {
                        workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_PROFILES, "");
                    }
                    ILifeCycle lifeCycle = LifeCycle.CLEAN;
                    if (selected.getFirstElement() instanceof LifeCycle) {
                        lifeCycle = (LifeCycle) selected.getFirstElement();
                    } else if (selected.getFirstElement() instanceof MojoDescriptor) {
                        MojoDescriptor mojo = (MojoDescriptor) selected.getFirstElement();
                        if (mojo.getPhase() != null) {
                            lifeCycle = LifeCycle.valueOf(mojo.getPhase());
                        }
                        lifeCycle = new ILifeCycle() {

                            @Override
                            public String getPhase() {
                                return "";
                            }
                        };
                    }
                    workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_OFFLINE, offlineMode);
                    workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_SKIP_TESTS, skipTestsMode);
                    workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_GOALS, (lifeCyclePhase.isEmpty() ? mojoCommand : "") + " " + lifeCycle.getPhase());
                    workingCopy.setAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, mojoCommand
                            + " -f "
                            + ResourcesPlugin.getWorkspace().getRoot().getProject(selected.getPaths()[0].getSegment(1).toString()).getFile("pom.xml").getRawLocation().toFile()
                                    .getAbsolutePath().toString());
                    workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
                    workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-XX:PermSize=128M -XX:MaxPermSize=256M");
                    workingCopy.doSave();
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        };
        create.setImageDescriptor(MavenImages.CONFIGURE);
        mgr.add(run);
        mgr.add(new Separator());
        mgr.add(create);
    }
}
