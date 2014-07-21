package org.mule.tooling.devkit.assist;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

@SuppressWarnings("restriction")
public class DevkitTemplateProposal implements IJavaCompletionProposal, ICompletionProposalExtension2, ICompletionProposalExtension3, ICompletionProposalExtension4,
        ICompletionProposalExtension6 {

    private final String label;
    int relevance;

    public DevkitTemplateProposal(String label) {
        this.label = label;
        relevance = 0;
    }

    public DevkitTemplateProposal(String label, int relevance) {
        this.label = label;
        this.relevance = relevance;
    }

    @Override
    public void apply(IDocument document) {
        // TODO Auto-generated method stub

    }

    @Override
    public Point getSelection(IDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        // TODO Auto-generated method stub
        return "Aditiona Info";
    }

    @Override
    public String getDisplayString() {
        // TODO Auto-generated method stub
        return label;
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ANNOTATION);
    }

    @Override
    public IContextInformation getContextInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString ss = new StyledString();
        ss.append(label);
        // ss.append("( Templa )", StyledString.COUNTER_STYLER);
        return ss;
    }

    @Override
    public boolean isAutoInsertable() {
        return true;
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPrefixCompletionStart(IDocument document, int completionOffset) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {

    }

    @Override
    public void selected(ITextViewer viewer, boolean smartToggle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unselected(ITextViewer viewer) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validate(IDocument document, int offset, DocumentEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getRelevance() {
        return relevance;
    }

}
