package org.mule.tooling.incubator.gradle.editors;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.mule.tooling.incubator.gradle.editors.completion.GradleScriptModel;

public class GradleScriptCompletionProcessor implements IContentAssistProcessor {
	
	private static final char[] ACTIVATION_CHARS = {'.'};
	private final IContextInformation[] NO_CONTEXTS = { };
	private ICompletionProposal[] NO_COMPLETIONS = { };

	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		try {
			
			IDocument doc = viewer.getDocument();
			String activeWord = lastWord(doc, offset);
		
			GradleScriptModel model = new GradleScriptModel(doc.get(0, offset), activeWord);
			
			List<String> completions = model.buildSuggestions();
			
			return transformCompletions(completions, offset);
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NO_COMPLETIONS;
	}


	/**
	 * Get the last typed word.
	 * @param doc
	 * @param offset
	 * @return
	 */
    private String lastWord(IDocument doc, int offset) {
        try {
           for (int n = offset-1; n >= 0; n--) {
             char c = doc.getChar(n);
             
             if (!Character.isJavaIdentifierPart(c))
               return doc.get(n + 1, offset-n-1);
           }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "";
     }

	
	private ICompletionProposal[] transformCompletions(List<String> completions, int offset) {
		
		ICompletionProposal[] ret = new ICompletionProposal[completions.size()];
		
		int i = 0;
		for(String completion : completions) {			
			ret[i++] = new CompletionProposal(completion, offset, 0, completion.length());
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
	
    //optional methods
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return ACTIVATION_CHARS;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
