package org.mule.tooling.devkit.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.mule.tooling.devkit.assist.context.SmartContext;
import org.mule.tooling.devkit.assist.context.SmartContextFactory;

/**
 * A computer wrapper for the hippie processor.
 * 
 * @since 3.2
 */
public final class ProposalComputer implements IQuickAssistProcessor {

    public ProposalComputer() {
        // fEngine= new TemplateCompletionProposalComputer();
    }

    @Override
    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return false;
    }

    @Override
    public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {

        List<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
        // CompilationUnit obj = context.getASTRoot();
        // int selectionOffset = context.getSelectionOffset();
        // LocateNode node = new LocateNode(selectionOffset);
        // obj.accept(node);
        // for (ASTNode nodeItem : node.getStackNodes()) {
        // System.out.print(nodeItem.getClass().getSimpleName() + ":" + (nodeItem.getParent() == null ? "" : nodeItem.getParent().getNodeType()) + "-");
        // }
        // System.out.println("");
        for (SmartContext smartContext : SmartContextFactory.getContexts()) {
            smartContext.addProposals(proposals, context);
        }
        return proposals.toArray(new IJavaCompletionProposal[proposals.size()]);
    }

}
