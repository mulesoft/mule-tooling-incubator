package org.mule.tooling.devkit.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.ui.internal.DefaultHelpUI;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.popup.actions.DevkitCallback;
import org.mule.tooling.devkit.quickfix.LocateModuleNameVisitor;
import org.mule.tooling.ui.utils.SaveModifiedResourcesDialog;
import org.mule.tooling.ui.utils.UiUtils;
import org.mule.tooling.utils.SilentRunner;

@SuppressWarnings("restriction")
public class DevkitUtils {

    public static final String DOT_GIT_PATH = "/.git/";
    public static final String PROCESSOR = "Processor";

    public static final String ICONS_FOLDER = "icons";
    public static final String DOCS_FOLDER = "doc";
    public static final String DEMO_FOLDER = "demo";
    public static final String TEST_JAVA_FOLDER = "src/test/java";
    public static final String MAIN_MULE_FOLDER = "src/main/app";
    public static final String MAIN_FLOWS_FOLDER = "flows";
    public static final String TEST_RESOURCES_FOLDER = "src/test/resources";
    public static final String GENERATED_SOURCES_FOLDER = "/target/generated-sources/mule";
    public static final String CXF_GENERATED_SOURCES_FOLDER = "/target/generated-sources/cxf";
    public static final String MAIN_RESOURCES_FOLDER = "src/main/resources";
    public static final String MAIN_JAVA_FOLDER = "src/main/java";
    public static final String POM_FILENAME = "pom.xml";
    public static final String POM_TEMPLATE_PATH = "/templates/devkit-pom.xml.tmpl";
    public static final String UPDATE_SITE_FOLDER = "/target/update-site/";

    public static final String DEVKIT_3_4_0 = "3.4.0";
    public static final String DEVKIT_3_4_1 = "3.4.1";
    public static final String DEVKIT_3_4_2 = "3.4.2";
    public static final String DEVKIT_3_5_0 = "3.5.0";
    public static final String DEVKIT_3_5_1 = "3.5.1";

    public static final String devkitVersions[] = { DEVKIT_3_4_0, DEVKIT_3_4_1, DEVKIT_3_4_2, DEVKIT_3_5_0, DEVKIT_3_5_1 };
    public static final String CATEGORY_COMMUNITY = "Community";
    public static final String CATEGORY_STANDARD = "Standard";
    public static final String connectorCategories[] = { CATEGORY_COMMUNITY, CATEGORY_STANDARD };

    public static String createModuleNameFrom(String name) {
        return name + "Module";
    }

    public static String createConnectorNameFrom(String name) {
        return name + "Connector";
    }

    public static DevkitCallback refreshFolder(final IFolder folder, IProgressMonitor monitor) {
        return new DevkitCallback() {

            public int execute(int previousResult) {
                try {
                    if (folder.exists()) {
                        folder.refreshLocal(IResource.DEPTH_INFINITE, null);
                    }
                } catch (CoreException e) {
                    return Status.ERROR;
                }
                return Status.OK;
            }
        };
    }

    public static Status getDefaultErrorStatus() {
        return new OperationStatus(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, OperationStatus.ERROR, "Failed to generate Update Site. Check the logs for more details.", null);
    }

    public static DevkitCallback openFileInBrower(final IFile file) {
        return new DevkitCallback() {

            public int execute(int previousResult) {
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        SilentRunner.run(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    file.getParent().refreshLocal(IResource.DEPTH_ONE, null);
                                    if (file.exists()) {
                                        DefaultHelpUI.showInWorkbenchBrowser(file.getLocationURI().toURL().toString(), true);
                                    }
                                } catch (MalformedURLException e) {
                                    throw new RuntimeException(e);
                                } catch (CoreException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        });
                    }
                });

                return Status.OK;
            };
        };
    }

    public static CompilationUnit getConnectorClass(IProject selectedProject, IPath folderResource) {

        return locateConnectorInResource(selectedProject, folderResource);
    }

    public static CompilationUnit getConnectorClass(IProject selectedProject) {

        IFolder folder = selectedProject.getFolder(DevkitUtils.MAIN_JAVA_FOLDER);

        return locateConnectorInResource(selectedProject, folder.getProjectRelativePath());
    }

    private static CompilationUnit locateConnectorInResource(IProject project, IPath folderResource) {

        IFolder folder = project.getFolder(folderResource.makeRelative());

        try {
            for (IResource resource : folder.members()) {
                IJavaElement element = (IJavaElement) resource.getAdapter(IJavaElement.class);
                if (element != null) {
                    switch (element.getElementType()) {
                    case IJavaElement.PACKAGE_FRAGMENT:
                        System.out.println(element);
                        return locateConnectorInResource(project, element.getPath().makeRelativeTo(folderResource));

                    case IJavaElement.COMPILATION_UNIT:
                        CompilationUnit connectorClass = ASTUtils.parse((ICompilationUnit) element);
                        LocateModuleNameVisitor locator = new LocateModuleNameVisitor();
                        connectorClass.accept(locator);
                        if (!locator.getValue().isEmpty()) {
                            return connectorClass;
                        }
                        break;
                    default:
                        System.out.println(element);
                        break;
                    }
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<MethodDeclaration> getProjectProcessors(IProject selectedProject) throws FileNotFoundException {

        CompilationUnit unit = DevkitUtils.getConnectorClass(selectedProject);
        return getProjectProcessors(unit);
    }

    public static List<MethodDeclaration> getProjectProcessors(CompilationUnit connector) throws FileNotFoundException {
        List<MethodDeclaration> processors = new ArrayList<MethodDeclaration>();

        MethodVisitor visitor = new MethodVisitor();
        if (connector != null)
            connector.accept(visitor);

        List<MethodDeclaration> methods = visitor.getMethods();

        if (methods == null) {
            throw new FileNotFoundException("Unable to locate the Connector's compilation unit");
        }

        for (MethodDeclaration method : methods) {
            Iterator<IExtendedModifier> modifiers = method.modifiers().iterator();
            boolean isProcessor = false;
            while (modifiers.hasNext() && !isProcessor) {
                IExtendedModifier modifier = modifiers.next();
                if (modifier.isAnnotation())
                    isProcessor = ((Annotation) modifier).getTypeName().toString().equals(PROCESSOR);
            }
            if (isProcessor)
                processors.add(method);
        }

        return processors;
    }

    public static void setControlsEnable(boolean enabled, Control... controls) {
        for (Control part : controls) {
            part.setEnabled(enabled);
            if (part instanceof Composite) {
                Composite comp = (Composite) part;
                for (Control child : comp.getChildren())
                    setControlsEnable(enabled, child);
            }
        }
    }

    public static String findResourceInFolder(IContainer folder, String resourceNameFormat) throws CoreException {

        String resourceName = "";

        IResource[] resources = folder.members();
        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];

            if (resource.getName().matches(resourceNameFormat) && resourceName.equals("")) {
                resourceName = resource.getLocation().toOSString();
            }

            if (!resourceName.equals(""))
                break;
        }

        return resourceName;
    }

    public static List<String> getProjectBranches(IProject project, ListMode mode) {
        List<String> branches = new LinkedList<String>();
        if (project == null) {
            return branches;
        }

        String repoPath = project.getLocationURI().getPath() + DOT_GIT_PATH;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            Repository repository = builder.setGitDir(new File(repoPath)).build();
            List<Ref> call = new Git(repository).branchList().setListMode(mode).call();
            for (Ref ref : call) {
                branches.add(StringUtils.substring(ref.getName(), ref.getName().lastIndexOf("/") + 1));
                // System.out.println("Branch: " + StringUtils.substring(ref.getName(), ref.getName().lastIndexOf("/")+1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return branches;
    }

    public static boolean existsUnsavedChanges(IProject project) {
        List<IEditorPart> dirtyEditors = UiUtils.getDirtyEditors(project.getProject());
        if (dirtyEditors.isEmpty())
            return true;
        SaveModifiedResourcesDialog dialog = new SaveModifiedResourcesDialog(Display.getDefault().getActiveShell());
        
        if (dialog.open(Display.getDefault().getActiveShell(), dirtyEditors))
            return false;
        return true;
    }
}
