package org.mule.tooling.devkit.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class PackageSelectionDialog extends ElementListSelectionDialog {

    public PackageSelectionDialog(Shell parent, ILabelProvider renderer, IProject project) {
        super(parent, renderer);
        setElementsFor(project);
        setMultipleSelection(true);

        setMessage("Select Package");
    }

    private void setElementsFor(IProject project) {
        if (project == null) {
            List<IPackageFragment> packages = new ArrayList<IPackageFragment>();
            IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
            for (IProject iProject : projects) {
                if (iProject.isAccessible()) {
                    packages.addAll(Arrays.asList(getPackageFragments(JavaCore.create(iProject), new Vector<String>())));
                }
            }
            setElements(packages.toArray(new IPackageFragment[] {}));
        } else {
            setElements(getPackageFragments(JavaCore.create(project), new Vector<String>()));
        }
    }

    public static IPackageFragment[] getPackageFragments(IJavaProject jProject, Collection<String> existingPackages) {
        Map<String, IPackageFragment> map = getPackageFragmentsHash(jProject, existingPackages);
        return (IPackageFragment[]) map.values().toArray(new IPackageFragment[map.size()]);
    }

    public static Map<String, IPackageFragment> getPackageFragmentsHash(IJavaProject jProject, Collection<String> existingPackages) {
        HashMap<String, IPackageFragment> map = new HashMap<String, IPackageFragment>();
        try {
            IPackageFragmentRoot[] roots = getRoots(jProject);
            for (int i = 0; i < roots.length; i++) {
                IJavaElement[] children = roots[i].getChildren();
                for (int j = 0; j < children.length; j++) {
                    IPackageFragment fragment = (IPackageFragment) children[j];
                    boolean ignore = ignoreFolder(fragment.getPath());

                    if (ignore) {
                        continue;
                    }
                    String name = fragment.getElementName();
                    if (name.length() == 0) {
                        continue;
                    }
                    if (!existingPackages.contains(name)) {
                        if (!(name.equals("java") || name.startsWith("java.")))
                            map.put(fragment.getElementName(), fragment);
                    }
                }
            }
        } catch (JavaModelException e) {
        }
        return map;
    }

    private static boolean ignoreFolder(IPath path) {
        boolean targetOrTestFolder = false;
        if (path.segmentCount() > 0) {
            for (String segment : path.segments()) {
                if (segment.equals("test") || segment.equals("target")) {
                    targetOrTestFolder = true;
                    break;
                }
            }
        }
        return targetOrTestFolder;
    }

    private static IPackageFragmentRoot[] getRoots(IJavaProject jProject) {
        List<IPackageFragmentRoot> result = new ArrayList<IPackageFragmentRoot>();
        try {
            IPackageFragmentRoot[] roots = jProject.getPackageFragmentRoots();
            for (int i = 0; i < roots.length; i++) {
                if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE || jProject.getProject().equals(roots[i].getCorrespondingResource())
                        || (roots[i].isArchive() && !roots[i].isExternal())) {
                    result.add(roots[i]);
                }
            }
        } catch (JavaModelException e) {
        }
        return (IPackageFragmentRoot[]) result.toArray(new IPackageFragmentRoot[result.size()]);
    }

    protected Control createDialogArea(Composite parent) {
        Control control = super.createDialogArea(parent);
        getShell().setText("Select Package");
        return control;
    }
}