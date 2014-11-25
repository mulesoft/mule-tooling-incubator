package org.mule.tooling.devkit.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.ui.internal.DefaultHelpUI;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.apt.core.util.IFactoryPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.mule.tooling.devkit.builder.DevkitNature;
import org.mule.tooling.devkit.maven.MavenDevkitProjectDecorator;
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

    public static final String DEVKIT_CURRENT = "3.6.0-M3-SNAPSHOT";

    public static final String CATEGORY_COMMUNITY = "Community";
    public static final String CATEGORY_STANDARD = "Standard";
    public static final String connectorCategories[] = { CATEGORY_COMMUNITY, CATEGORY_STANDARD };

    private static final String[] RESERVED_NAMES = { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if",
            "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return",
            "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const",
            "float", "native", "super", "while" };

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
                                    } else {
                                        MessageDialog.openError(null, "Error while generating javadoc", "Unable to generate the documentation. Check the console for errors.");
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
            @SuppressWarnings("unchecked")
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
            return false;
        SaveModifiedResourcesDialog dialog = new SaveModifiedResourcesDialog(Display.getDefault().getActiveShell());

        if (dialog.open(Display.getDefault().getActiveShell(), dirtyEditors))
            return false;
        return true;
    }

    public static String toConnectorName(String camelCaseName) {
        return splitWithSeparator(camelCaseName, "-").toLowerCase();
    }

    private static String splitWithSeparator(String camelCaseName, String separator) {
        StringBuilder result = new StringBuilder();
        char[] characters = camelCaseName.toCharArray();

        boolean wasDigit = false;
        boolean waslowerCase = false;
        for (int i = 0; i < characters.length; i++) {
            char current = characters[i];
            if (current == '-') {
                waslowerCase = false;
                wasDigit = false;
                result.append(current);
            } else if (Character.isDigit(current)) {
                result.append(current);
                wasDigit = true;
            } else if (Character.isLowerCase(current)) {
                waslowerCase = true;
                result.append(current);
            } else {
                if (wasDigit || waslowerCase) {
                    result.append("-");
                } else if (i > 0 && i < characters.length-1) {
                    // If this is not the first character,and  If the next caracter is a lowercase, this uppercase is actually from the next word
                    if (Character.isLowerCase(characters[i + 1])) {
                        result.append("-");
                    }
                }
                result.append(current);
                waslowerCase = false;
                wasDigit = false;
            }
        }

        return result.toString();
    }

    public static boolean isReserved(String word) {
        return ArrayUtils.contains(RESERVED_NAMES, word);
    }

    public static void configureDevkitAPT(IJavaProject javaProject) throws CoreException {
        AptConfig.setEnabled(javaProject, true);
        IFactoryPath path = AptConfig.getFactoryPath(javaProject);
        path.enablePlugin(org.mule.tooling.devkit.apt.Activator.PLUGIN_ID);
        AptConfig.setFactoryPath(javaProject, path);
        AptConfig.addProcessorOption(javaProject, "enableJavaDocValidation", "false");
    }

    /**
     * Get a label from the project with the following structure. { groupId } : { artifactId } : { version } or { project name } if the result was empty.
     * 
     * @param selectedProject
     *            maven project with a valid pom.xml file
     * @return the label.
     */
    public static String getProjectLabel(final IJavaProject selectedProject) {
        MavenDevkitProjectDecorator maven = MavenDevkitProjectDecorator.decorate(selectedProject);
        String label = ((maven.getGroupId() != null) ? maven.getGroupId() + ":" : "") + ((maven.getArtifactId() != null) ? maven.getArtifactId() + ":" : "")
                + ((maven.getVersion() != null) ? maven.getVersion() : "");
        if (label.isEmpty()) {
            label = selectedProject.getProject().getName();
        }
        return label;
    }

    /**
     * Add the Devkit nature to a given project.
     * 
     * @param project
     * @throws CoreException
     */
    public static void addDevkitNature(IProject project) throws CoreException {
        if (project.hasNature(DevkitNature.NATURE_ID)) {
            return;
        }
        final IProjectDescription description = project.getDescription();
        final String[] ids = description.getNatureIds();
        final String[] newIds = new String[ids.length + 1];
        System.arraycopy(ids, 0, newIds, 1, ids.length);
        newIds[0] = DevkitNature.NATURE_ID;
        description.setNatureIds(newIds);
        project.setDescription(description, null);
    }
}
