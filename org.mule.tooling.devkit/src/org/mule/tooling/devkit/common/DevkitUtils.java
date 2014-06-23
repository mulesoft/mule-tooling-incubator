package org.mule.tooling.devkit.common;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.operations.OperationStatus;
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
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.DevkitUIPlugin;
import org.mule.tooling.devkit.popup.actions.DevkitCallback;
import org.mule.tooling.devkit.quickfix.LocateModuleNameVisitor;
import org.mule.tooling.utils.SilentRunner;

@SuppressWarnings("restriction")
public class DevkitUtils {
    
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
    public static final String POM_TEMPLATE_PATH = "/templates/pom.xml.tmpl";
    public static final String UPDATE_SITE_FOLDER = "/target/update-site/";

    public static final String DEVKIT_3_4_0 = "3.4.0";
    public static final String DEVKIT_3_4_1 = "3.4.1";
    public static final String DEVKIT_3_4_2 = "3.4.2";
    public static final String DEVKIT_3_5_0 = "3.5.0-cascade";

    public static final String devkitVersions[] = { DEVKIT_3_4_0, DEVKIT_3_4_1, DEVKIT_3_4_2, DEVKIT_3_5_0 };
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

            public void execute() {
                try {
                    if (folder.exists()) {
                        folder.refreshLocal(IResource.DEPTH_INFINITE, null);
                    }
                } catch (CoreException e) {

                }
            }
        };
    }

    public static Status getDefaultErrorStatus() {
        return new OperationStatus(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, OperationStatus.ERROR, "Failed to generate Update Site. Check the logs for more details.", null);
    }

    public static DevkitCallback openFileInBrower(final IFile file) {
        return new DevkitCallback() {

            public void execute() {
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
        ICompilationUnit connectorElement = null;

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
                                connectorElement = (ICompilationUnit) element;
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
        
        if (methods == null){
            throw new FileNotFoundException("Unable to locate the Connector's compilation unit");
        }

        for (MethodDeclaration method : methods) {
            Iterator<IExtendedModifier> modifiers = method.modifiers().iterator();
            boolean isProcessor = false;
            while(modifiers.hasNext() && !isProcessor){
                IExtendedModifier modifier = modifiers.next(); 
                if (modifier.isAnnotation())
                    isProcessor = ((Annotation)modifier).getTypeName().toString().equals(PROCESSOR);
            }
            if (isProcessor)
                processors.add(method);    
        }
        
        return processors;
    }
}
