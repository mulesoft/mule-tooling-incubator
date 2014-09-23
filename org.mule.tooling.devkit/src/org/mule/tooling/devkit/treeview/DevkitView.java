package org.mule.tooling.devkit.treeview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.treeview.model.Module;
import org.mule.tooling.devkit.treeview.model.NodeItem;
import org.mule.tooling.devkit.treeview.model.ProjectRoot;

@SuppressWarnings("restriction")
public class DevkitView extends ViewPart implements IResourceChangeListener, ISelectionListener {

    public static final String ID = "org.mule.tooling.devkit.treeview.DevkitView";

    private TreeViewer viewer;
    private IEditorInput currentInput;
    private IProject current;

    public IProject getCurrent() {
        return current;
    }

    public void setCurrent(IProject current) {
        this.current = current;
    }

    public DevkitView() {
        super();

    }

    @Override
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.removeResourceChangeListener(this);
    }

    public void createPartControl(Composite parent) {
        PatternFilter filter = new PatternFilter();
        FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
        viewer = tree.getViewer();
        viewer.setContentProvider(new ModuleContentProvider());
        viewer.setLabelProvider(new ModuleLabelProvider());
        // Expand the tree
        viewer.setAutoExpandLevel(2);
        // provide the input to the ContentProvider
        viewer.setInput(new ProjectRoot());

        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);

        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
                if (thisSelection.getFirstElement() instanceof NodeItem) {
                    NodeItem method = (NodeItem) thisSelection.getFirstElement();
                    ICompilationUnit cu = method.getCompilationUnit();
                    IEditorPart javaEditor;
                    try {
                        javaEditor = JavaUI.openInEditor(cu);
                        JavaUI.revealInEditor(javaEditor, method.getJavaElement());
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    } catch (JavaModelException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public void updateContent(Module module) {
        viewer.setInput(module);
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {

        if (selection.isEmpty())
            return;
        final ISelection currentSelection = selection;
        final IWorkbenchPart workbenchPart = part;
        final String convertingMsg = "Checking Modules in project...";
        final WorkspaceJob refreshDevkitViewJob = new WorkspaceJob(convertingMsg) {

            @Override
            public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
                if (currentSelection instanceof IStructuredSelection) {
                    Object selected = ((IStructuredSelection) currentSelection).getFirstElement();
                    if (selected instanceof IJavaElement) {
                        handleNewJavaElementSelected(selected);
                    } else if (selected instanceof IProject) {
                        handleNewProjectSelectedChange(selected);
                    }
                } else if (currentSelection instanceof ITextSelection) {
                    handleNewEditorPageSelected(workbenchPart, currentSelection);
                }
                return Status.OK_STATUS;
            }
        };
        refreshDevkitViewJob.setUser(false);
        refreshDevkitViewJob.setPriority(Job.SHORT);
        refreshDevkitViewJob.schedule();

    }

    private void handleNewJavaElementSelected(Object selected) {
        final IProject selectedProject = ((IJavaElement) selected).getJavaProject().getProject();
        handleNewProjectSelectedChange(selectedProject);
    }

    private void handleNewProjectSelectedChange(Object selected) {
        try {
            final IProject selectedProject = (IProject) selected;
            if (current.equals(selectedProject)) {
                return;
            }
            if (selectedProject.isOpen() && selectedProject.hasNature(DevkitNature.NATURE_ID)) {
                analyseMethods(selectedProject);
            } else {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        viewer.setInput(new ProjectRoot());
                    }
                });
            }

        } catch (JavaModelException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private void handleNewEditorPageSelected(IWorkbenchPart part, ISelection selection) {
        if (part instanceof JavaEditor) {
            JavaEditor editor = (JavaEditor) part;
            IEditorInput file = editor.getEditorInput();
            if (file.equals(currentInput))
                return;
            currentInput = file;

            try {
                IJavaElement element = SelectionConverter.resolveEnclosingElement((JavaEditor) part, (ITextSelection) selection);
                final IProject selectedProject = element.getJavaProject().getProject();
                handleNewProjectSelectedChange(selectedProject);
            } catch (JavaModelException e1) {
                e1.printStackTrace();
            }

        }
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {

            if (event.getSource() instanceof IWorkspace) {
                IWorkspace workspace = (IWorkspace) event.getSource();

                try {
                    IWorkspaceRoot root = workspace.getRoot();
                    if (root != null) {
                        if (event.getDelta().getAffectedChildren().length > 0) {
                            IResourceDelta delta = event.getDelta().getAffectedChildren()[0];

                            if (delta.getResource().getProject() != null && delta.getResource().getProject().isOpen()) {
                                // When the user navigates from the sample file to the JAVA File we don't want to trigger the mechanism
                                if (delta.getProjectRelativePath() != null) {
                                    if (delta.getProjectRelativePath().lastSegment() != null) {
                                        if (delta.getProjectRelativePath().lastSegment().equalsIgnoreCase("doc")) {
                                            return;
                                        }
                                    }
                                }
                                analyseMethods(delta.getResource().getProject());
                            }
                        }
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // PRE CLOSE OR DELETE
            if (event.getResource() != null) {
                IResource resource = event.getResource();

                if (resource.getProject() != null && resource.getProject().isOpen()) {
                    if (current != null && current.getName().equals(resource.getProject().getName())) {
                        // Set empty project as input
                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {
                                viewer.setInput(new ProjectRoot());
                            }
                        });

                    }
                }
            }
        }
    }

    public void analyseMethods(IProject project) throws JavaModelException {
        if (!project.exists()) {
            current = null;
            return;
        }
        current = project;
        IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
        boolean hasConnector = false;
        for (IPackageFragment mypackage : packages) {
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE && mypackage.exists()) {
                if (!mypackage.getPath().toString().contains(DevkitUtils.MAIN_JAVA_FOLDER)) {
                    continue;
                }
                hasConnector |= createAST(mypackage);
            }
        }
        if (!hasConnector) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    viewer.setInput(new ProjectRoot());
                }
            });
        }
    }

    private boolean createAST(IPackageFragment mypackage) throws JavaModelException {

        ModuleVisitor visitor = new ModuleVisitor();
        for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
            // now create the AST for the ICompilationUnits
            CompilationUnit parse = ASTUtils.parse(unit);

            parse.accept(visitor);

        }
        if (visitor.getRoot().getModules() != null && !visitor.getRoot().getModules().isEmpty()) {
            final ProjectRoot root = visitor.getRoot();
            // Update the user interface asynchronously
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    viewer.setInput(root);
                }
            });
            return true;
        }
        return false;
    }
}
