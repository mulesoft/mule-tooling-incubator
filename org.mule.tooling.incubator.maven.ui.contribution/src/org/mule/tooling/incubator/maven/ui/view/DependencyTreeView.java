package org.mule.tooling.incubator.maven.ui.view;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.incubator.maven.core.TreeNode;
import org.mule.tooling.incubator.maven.ui.MavenImages;
import org.mule.tooling.incubator.maven.ui.actions.CreateDependencyTreeCommand;
import org.mule.tooling.incubator.maven.ui.actions.EffectivePomCommand;
import org.mule.tooling.incubator.maven.ui.command.MavenDownload;

public class DependencyTreeView extends ViewPart implements ISelectionListener, IResourceChangeListener {

    public static final String ID = "org.mule.tooling.incubator.maven.ui.view.DependencyTreeView";

    private TreeViewer viewer;
    private IProject currentProject;

    private Map<String, TreeNode<Dependency>> cache = new HashMap<String, TreeNode<Dependency>>();

    @Override
    public void createPartControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        final SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(1, 1).grab(true, true).applyTo(sashForm);

        PatternFilter filter = new PatternFilter();
        FilteredTree tree = new FilteredTree(sashForm, SWT.NULL, filter, true);
        viewer = tree.getViewer();
        viewer.setContentProvider(new DependencyTreeContentProvider());
        viewer.setLabelProvider(new FilteredDelegatingStyledCellLabelProvider(new DependencyTreeLabelProvider()));
        // Expand the tree
        viewer.setAutoExpandLevel(2);

        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                IViewPart viewPart = page.findView("org.eclipse.jdt.ui.PackageExplorer");
                Viewer selectionService = (Viewer) viewPart.getSite().getSelectionProvider();
                IJavaProject javaProject = JavaCore.create(currentProject);
                TreeSelection selected = (TreeSelection) event.getSelection();
                TreeNode<Dependency> tremp = (TreeNode<Dependency>) selected.getFirstElement();
                Dependency dep = tremp.getNodeItem();
                // By default we want to select the project,
                Object selectionAtTree = javaProject;
                try {
                    IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
                    for (int i = 0; i < roots.length; i++) {
                        if (roots[i].getPath().toString().endsWith("" + dep.getArtifactId() + "-" + dep.getVersion() + "." + dep.getType())) {
                            selectionAtTree = roots[i];
                        }
                    }
                } catch (JavaModelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                TreeSelection selection = new TreeSelection(new TreePath(new Object[] { selectionAtTree }));
                selectionService.setSelection(selection, true);
            }

        });
        Action getEffectivePom = new Action("View Effective Pom", Action.AS_PUSH_BUTTON) {

            @Override
            public void run() {
                if (currentProject == null)
                    return;
                final EffectivePomCommand command = new EffectivePomCommand(currentProject);

                Job job = new Job("Calculating effective pom") {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {

                        IFile pomFile = currentProject.getFile("pom.xml");

                        command.run(pomFile, monitor);
                        return Status.OK_STATUS;
                    }

                };
                job.addJobChangeListener(new IJobChangeListener() {

                    @Override
                    public void aboutToRun(IJobChangeEvent event) {

                    }

                    @Override
                    public void awake(IJobChangeEvent event) {

                    }

                    @Override
                    public void done(IJobChangeEvent event) {
                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                File fileToOpen = command.getEffectivePom();

                                if (fileToOpen.exists() && fileToOpen.isFile()) {
                                    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
                                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                                    try {
                                        IDE.openEditorOnFileStore(page, fileStore);
                                    } catch (PartInitException e) {
                                        // Put your exception handler here if you wish to
                                    }
                                } else {
                                    // Do something if the file does not exist
                                }

                            }

                        });
                    }

                    @Override
                    public void running(IJobChangeEvent event) {

                    }

                    @Override
                    public void scheduled(IJobChangeEvent event) {

                    }

                    @Override
                    public void sleeping(IJobChangeEvent event) {

                    }

                });
                job.schedule();
            }
        };
        getEffectivePom.setImageDescriptor(MavenImages.EFFECTIVE_POM);
        getViewSite().getActionBars().getToolBarManager().add(getEffectivePom);
        getSite().getPage().addSelectionListener(this);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        createContextMenu();
    }

    public void dispose() {
        getSite().getPage().removeSelectionListener(this);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.removeResourceChangeListener(this);
    }

    @Override
    public void setFocus() {

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

    @SuppressWarnings("unchecked")
    private void fillContextMenu(IMenuManager mgr) {

        IJavaProject javaProject = JavaCore.create(currentProject);
        TreeSelection selected = (TreeSelection) viewer.getSelection();
        TreeNode<Dependency> tremp = (TreeNode<Dependency>) selected.getFirstElement();
        final Dependency dep = tremp.getNodeItem();

        boolean hasSources = false;
        boolean hasJavadoc = false;
        try {
            IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
            for (int i = 0; i < roots.length; i++) {
                if (roots[i].getPath().toString().endsWith("" + dep.getArtifactId() + "-" + dep.getVersion() + "." + dep.getType())) {
                    hasSources = roots[i].getSourceAttachmentPath() != null;
                    IClasspathEntry entry = roots[i].getResolvedClasspathEntry();
                    if (entry.getExtraAttributes() != null) {
                        IClasspathAttribute[] attributes = entry.getExtraAttributes();
                        for (int j = 0; j < attributes.length; j++) {
                            if (attributes[j].getName().equals(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME)) {
                                hasJavadoc = true;
                            }
                        }
                    }

                }
            }
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!hasSources) {
            Action run;
            run = new Action("Download Sources") {

                @Override
                public void run() {
                    try {
                        new MavenDownload(dep, "sources", currentProject).execute(null);
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            run.setImageDescriptor(MavenImages.SOURCE);
            mgr.add(run);
        }
        if (!hasJavadoc) {
            Action create = new Action("Download javadoc") {

                @Override
                public void run() {
                    try {
                        new MavenDownload(dep, "javadoc", currentProject).execute(null);
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            create.setImageDescriptor(MavenImages.JAVADOC);

            mgr.add(create);
        }
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        IResource resource = event.getResource();
        if (resource == null) {
            resource = event.getDelta().getResource();
            try {
                event.getDelta().accept(new IResourceDeltaVisitor() {

                    @Override
                    public boolean visit(IResourceDelta delta) throws CoreException {
                        final IResource resource = delta.getResource();
                        switch (delta.getKind()) {
                        case IResourceDelta.ADDED:
                            scheduleCacheUpdateIfRequired(resource);
                            break;
                        case IResourceDelta.REMOVED:
                            // handle removed resource
                            break;
                        case IResourceDelta.CHANGED:
                            scheduleCacheUpdateIfRequired(resource);
                            break;
                        }
                        // return true to continue visiting children.
                        return true;
                    }

                    protected void scheduleCacheUpdateIfRequired(final IResource resource) {
                        if (resource instanceof IFile && resource.getName().equals("pom.xml")) {
                            final CreateDependencyTreeCommand command = new CreateDependencyTreeCommand(resource.getProject());
                            Job job = new Job("Calculating dependencies") {

                                @Override
                                protected IStatus run(IProgressMonitor monitor) {

                                    IFile pomFile = resource.getProject().getFile("pom.xml");

                                    command.run(pomFile, monitor);
                                    return Status.OK_STATUS;
                                }

                            };
                            job.addJobChangeListener(new IJobChangeListener() {

                                @Override
                                public void aboutToRun(IJobChangeEvent event) {

                                }

                                @Override
                                public void awake(IJobChangeEvent event) {

                                }

                                @SuppressWarnings("unchecked")
                                @Override
                                public void done(IJobChangeEvent event) {
                                    final Object result = command.getDepResult();
                                    cache.put(command.getProject().getName(), (TreeNode<Dependency>) result);
                                    if (command.getProject().equals(currentProject)) {
                                        Display.getDefault().asyncExec(new Runnable() {

                                            @Override
                                            public void run() {
                                                viewer.setInput(result);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void running(IJobChangeEvent event) {

                                }

                                @Override
                                public void scheduled(IJobChangeEvent event) {

                                }

                                @Override
                                public void sleeping(IJobChangeEvent event) {

                                }

                            });
                            job.schedule();
                        }
                    }
                });
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(selection instanceof IStructuredSelection))
            return;

        IStructuredSelection ss = (IStructuredSelection) selection;
        Object o = ss.getFirstElement();
        if (o instanceof IJavaElement) {
            final IProject selectedProject = ((IJavaElement) o).getJavaProject().getProject();
            if (currentProject != null && currentProject.equals(selectedProject)) {
                return;
            }
            currentProject = selectedProject;

            if (cache.containsKey(currentProject.getName())) {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        Object result = cache.get(currentProject.getName());
                        viewer.setInput(result);
                    }

                });
            } else {
                final CreateDependencyTreeCommand command = new CreateDependencyTreeCommand(selectedProject);

                Job job = new Job("Calculating dependencies") {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {

                        IFile pomFile = selectedProject.getFile("pom.xml");

                        command.run(pomFile, monitor);
                        return Status.OK_STATUS;
                    }

                };
                job.addJobChangeListener(new IJobChangeListener() {

                    @Override
                    public void aboutToRun(IJobChangeEvent event) {

                    }

                    @Override
                    public void awake(IJobChangeEvent event) {

                    }

                    @Override
                    public void done(IJobChangeEvent event) {
                        Display.getDefault().asyncExec(new Runnable() {

                            @SuppressWarnings("unchecked")
                            @Override
                            public void run() {
                                Object result = command.getDepResult();
                                cache.put(currentProject.getName(), (TreeNode<Dependency>) result);
                                viewer.setInput(result);
                            }

                        });
                    }

                    @Override
                    public void running(IJobChangeEvent event) {

                    }

                    @Override
                    public void scheduled(IJobChangeEvent event) {

                    }

                    @Override
                    public void sleeping(IJobChangeEvent event) {

                    }

                });
                job.schedule();
            }
        }
    }
}
