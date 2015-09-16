package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class DevkitTemplate extends TemplateProposal implements IJavaCompletionProposal {

    public DevkitTemplate(Template template, TemplateContext context, IRegion region, Image image) {
        super(template, context, region, image);
    }

}
