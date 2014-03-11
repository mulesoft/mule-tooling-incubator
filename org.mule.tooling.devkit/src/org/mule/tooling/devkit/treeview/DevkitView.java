package org.mule.tooling.devkit.treeview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.devkit.common.DevkitUtils;
import org.mule.tooling.devkit.treeview.model.Module;
import org.mule.tooling.devkit.treeview.model.NodeItem;
import org.mule.tooling.devkit.treeview.model.ProjectRoot;
import org.mule.tooling.devkit.treeview.model.Property;

@SuppressWarnings("restriction")
public class DevkitView extends ViewPart implements IResourceChangeListener,
		ISelectionListener {

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
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		PatternFilter filter = new PatternFilter();
		FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL, filter, true);
		viewer = tree.getViewer();
		viewer.setContentProvider(new ModuleContentProvider());
		viewer.setLabelProvider(new ModuleLabelProvider());
		// Expand the tree
		viewer.setAutoExpandLevel(2);
		// provide the input to the ContentProvider
		viewer.setInput(new ProjectRoot());

		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection thisSelection = (IStructuredSelection) event
						.getSelection();
				if (thisSelection.getFirstElement() instanceof Property) {
					goToSampleInDocSampleFile(thisSelection);

				} else if (thisSelection.getFirstElement() instanceof NodeItem) {
					NodeItem method = (NodeItem) thisSelection
							.getFirstElement();
					ICompilationUnit cu = method.getCompilationUnit();
					IEditorPart javaEditor;
					try {
						javaEditor = JavaUI.openInEditor(cu);
						JavaUI.revealInEditor(javaEditor,
								method.getJavaElement());
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}

			@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
			private void goToSampleInDocSampleFile(
					IStructuredSelection thisSelection) {
				IFile file = null;
				IFolder folder = getCurrent()
						.getFolder(DevkitUtils.DOCS_FOLDER);

				try {
					for (IResource resource : folder.members()) {
						if (resource.getName().matches(".*.sample")) {
							file = getCurrent().getFile(
									resource.getProjectRelativePath());
							break;
						}
					}
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				if (file == null)
					return;
				InputStreamReader isr = null;
				try {

					isr = new InputStreamReader(file.getContents());
					BufferedReader ir = new BufferedReader(isr);
					String line;
					int lineNumber = 0;
					Property prop = (Property) thisSelection.getFirstElement();
					boolean found = false;
					while ((line = ir.readLine()) != null) {
						lineNumber++;
						if (line.contains(prop.getValue())) {
							found = true;
							break;
						}

					}
					if (!found) {
						lineNumber = 0;
					}
					HashMap map = new HashMap();
					map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
					map.put(IWorkbenchPage.EDITOR_ID_ATTR,
							"org.mule.tooling.devkit.sample.editor.editors.XMLEditor");
					IMarker marker;

					marker = file.createMarker(IMarker.TEXT);

					marker.setAttributes(map);
					// page.openEditor(marker); //2.1 API
					IDE.openEditor(getSite().getPage(), marker); // 3.0 API
					marker.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
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
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selected instanceof IJavaElement) {
				handleNewJavaElementSelected(selected);
			} else if (selected instanceof IProject) {
				handleNewProjectSelectedChange(selected);
			}
		} else if (selection instanceof ITextSelection) {
			handleNewEditorPageSelected(part, selection);
		}

	}

	private void handleNewJavaElementSelected(Object selected) {
		final IProject selectedProject = ((IJavaElement) selected)
				.getJavaProject().getProject();
		try {
			analyseMethods(selectedProject);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void handleNewProjectSelectedChange(Object selected) {
		try {
			final IProject selectedProject = (IProject) selected;
			if (selectedProject.isOpen()) {
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
		}
	}

	private void handleNewEditorPageSelected(IWorkbenchPart part,
			ISelection selection) {
		if (part instanceof JavaEditor) {
			JavaEditor editor = (JavaEditor) part;
			IEditorInput file = editor.getEditorInput();
			if (file.equals(currentInput))
				return;
			currentInput = file;

			try {
				IJavaElement element = SelectionConverter
						.resolveEnclosingElement((JavaEditor) part,
								(ITextSelection) selection);
				try {
					final IProject selectedProject = element.getJavaProject()
							.getProject();
					analyseMethods(selectedProject);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
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
						IResourceDelta delta = event.getDelta()
								.getAffectedChildren()[0];

						if (delta.getResource().getProject() != null) {
							analyseMethods(delta.getResource().getProject());
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
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
		IPackageFragment[] packages = JavaCore.create(project)
				.getPackageFragments();
		// parse(JavaCore.create(project));
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				createAST(mypackage);
			}

		}
	}

	private void createAST(IPackageFragment mypackage)
			throws JavaModelException {
		ModuleVisitor visitor = new ModuleVisitor();
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			// now create the AST for the ICompilationUnits
			CompilationUnit parse = parse(unit);

			parse.accept(visitor);

		}
		if (visitor.getRoot().getModules() != null
				&& !visitor.getRoot().getModules().isEmpty()) {
			final ProjectRoot root = visitor.getRoot();
			// Update the user interface asynchronously
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					viewer.setInput(root);
				}
			});
		}
	}

	/**
	 * * Reads a ICompilationUnit and creates the AST DOM for manipulating the *
	 * Java source file * * @param unit * @return
	 */

	private CompilationUnit parse(ICompilationUnit unit) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
