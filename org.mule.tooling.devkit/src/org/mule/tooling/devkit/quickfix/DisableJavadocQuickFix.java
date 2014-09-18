package org.mule.tooling.devkit.quickfix;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution2;
import org.mule.tooling.utils.SilentRunner;

@SuppressWarnings("restriction")
public class DisableJavadocQuickFix implements IMarkerResolution2, DevkitQuickFix {

    IJavaProject project;
    final ConditionMarkerEvaluator evaluator;

    public DisableJavadocQuickFix(ConditionMarkerEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    public String getLabel() {
        return "Disable Javadoc check";
    }

    @Override
    public void run(IMarker marker) {
        IResource resource = marker.getResource();
        IJavaElement javaElement = JavaCore.create(resource);
        project = javaElement.getJavaProject();
        if (project != null) {
            Boolean enabled = Boolean.FALSE;
            AptConfig.addProcessorOption(project, "enableJavaDocValidation", enabled.toString());

            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    try {
                        project.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
                    } catch (CoreException e) {
                        // Ignore errors
                    }
                }
            });
        }
    }

    @Override
    public boolean hasFixForMarker(IMarker marker) {
        return evaluator.hasFixForMarker(marker);
    }

    @Override
    public String getDescription() {
        return "Disable Javadoc check";
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ANNOTATION);
    }

}
