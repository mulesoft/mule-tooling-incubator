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
 * 
 */
public final class ProposalComputer implements IQuickAssistProcessor {


    @Override
    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return false;
    }

    @Override
    public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {

        List<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
        
        for (SmartContext smartContext : SmartContextFactory.getContexts(context)) {
            smartContext.addProposals(proposals);
        }

        return proposals.toArray(new IJavaCompletionProposal[proposals.size()]);
    }

}
