package org.mule.tooling.properties.editors.completion;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.corext.refactoring.util.NoCommentSourceRangeComputer;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.mule.tooling.properties.extension.PropertyKeySuggestion;
import org.mule.tooling.properties.utils.UIUtils;

public class MulePropertiesContentAssistant implements IContentAssistProcessor, ICompletionListener {
	
	private static final char[] ACTIVATION_CHARS = {'.'};
	private final IContextInformation[] NO_CONTEXTS = { };
	private ICompletionProposal[] NO_COMPLETIONS = { };
	
	private final IResource currentFile;
	private List<PropertyKeySuggestion> suggestions;
	
	public MulePropertiesContentAssistant(IResource currentFile) {
		this.currentFile = currentFile;
	}

	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		suggestions = UIUtils.getContributedSuggestions(currentFile);
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
		suggestions = null;
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal,
			boolean smartToggle) {
		
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		
		ICompletionProposal[] ret = new ICompletionProposal[suggestions.size()];
		
		for(int i = 0; i < ret.length ; i++) {
			ret[i] = UIUtils.build(suggestions.get(i), offset, 0);
		}
		
		return ret;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return NO_CONTEXTS;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return ACTIVATION_CHARS;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return ACTIVATION_CHARS;
	}

	@Override
	public String getErrorMessage() {
		return "No completion information available";
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
