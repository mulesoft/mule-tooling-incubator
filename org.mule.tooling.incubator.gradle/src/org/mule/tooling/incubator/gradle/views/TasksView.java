package org.mule.tooling.incubator.gradle.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.mule.tooling.incubator.gradle.GradlePluginUtils;
import org.mule.tooling.incubator.gradle.GradleRunner;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real
 * implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if
 * needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class TasksView extends ViewPart implements ISelectionListener {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.mule.tooling.incubator.gradle.views.TasksView";

    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action doubleClickAction;
    ProjectConnection connection;
    IProject project;

    class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        private GradleProject invisibleRoot;

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            if (parent.equals(getViewSite())) {
                return getChildren(invisibleRoot);
            }
            return getChildren(parent);
        }

        public Object getParent(Object child) {
            return null;
        }

        public Object[] getChildren(Object parent) {
            if (parent instanceof GradleProject) {
                return ((GradleProject) parent).getTasks().toArray();
            }
            return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            if (parent instanceof GradleProject) {
                return getChildren(parent) != null && getChildren(parent).length > 0;
            }
            return false;
        }

    }

    class ViewLabelProvider extends LabelProvider {

        public String getText(Object obj) {
            if (obj instanceof GradleTask) {
                GradleTask task = (GradleTask) obj;
                return task.getName();
            }
            return obj.toString();
        }

        public Image getImage(Object obj) {
            String imageKey = org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_TASK_TSK;
            if (obj instanceof GradleProject)
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }

    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public TasksView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        PatternFilter filter = new PatternFilter();
        FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
        viewer = tree.getViewer();
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                TasksView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {

        doubleClickAction = new Action() {

            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                final GradleTask task = (GradleTask) obj;

                WorkspaceJob job = new WorkspaceJob("Running task " + task.getName()) {

                    @Override
                    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                        try {
                            GradleRunner.run(connection.newBuild().forTasks(task), monitor);
                            project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                        } catch (CoreException e) {
                            return Status.CANCEL_STATUS;
                        }
                        return Status.OK_STATUS;
                    }

                };
                job.setUser(false);
                job.setPriority(Job.DECORATE);
                job.setRule(project.getProject());
                job.schedule();

            }
        };
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection.isEmpty())
            return;
        final ISelection currentSelection = selection;
        final String convertingMsg = "Checking Modules in project...";
        final WorkspaceJob refreshDevkitViewJob = new WorkspaceJob(convertingMsg) {

            @Override
            public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
                if (currentSelection instanceof IStructuredSelection) {
                    Object selected = ((IStructuredSelection) currentSelection).getFirstElement();
                    
                    //sometimes project might be a simple project.
                    if (selected instanceof IJavaProject) {
                    	//convert into a java project
                    	selected = ((IJavaProject) selected).getProject();
                    }
                    
                    if (selected instanceof IProject) {
                        project = (IProject) selected;

                        //it should be valid always to run now.
                        if (connection != null) {
                            connection.close();
                        }
                        connection  = GradlePluginUtils.buildConnectionForProject(project.getLocation().toFile().getAbsoluteFile()).connect();
                        final GradleProject gradleProject = connection.getModel(GradleProject.class);
                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                viewer.setInput(gradleProject);

                            }

                        });

                    }
                }
                return Status.OK_STATUS;
            }
        };
        refreshDevkitViewJob.setUser(false);
        refreshDevkitViewJob.setPriority(Job.SHORT);
        refreshDevkitViewJob.schedule();
    }

    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
    }
}