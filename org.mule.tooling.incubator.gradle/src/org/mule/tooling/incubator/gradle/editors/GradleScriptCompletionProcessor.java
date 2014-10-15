package org.mule.tooling.incubator.gradle.editors;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.gradle.api.specs.Spec;
import org.gradle.util.CollectionUtils;
import org.mule.tooling.incubator.gradle.editors.completion.GradleScriptAutocompleteAnalyzer;
import org.mule.tooling.incubator.gradle.editors.completion.GroovyCompletionProposalBuilder;
import org.mule.tooling.incubator.gradle.editors.completion.GroovyCompletionSuggestion;
import org.mule.tooling.incubator.gradle.parser.GradleMuleBuildModelProvider;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTParser;

public class GradleScriptCompletionProcessor implements IContentAssistProcessor, ICompletionListener{
	
	private static final char[] ACTIVATION_CHARS = {'.', ','};
	private final IContextInformation[] NO_CONTEXTS = { };
	private ICompletionProposal[] NO_COMPLETIONS = { };
	
	private List<GroovyCompletionSuggestion> currentProposals;
	
	private GradleMuleBuildModelProvider modelProvider;
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		try {
			
		    IDocument doc = viewer.getDocument();
			String activeWord = lastWord(doc, offset);
			
			tryParseDocument(doc.get());
			
			GradleScriptAutocompleteAnalyzer model = new GradleScriptAutocompleteAnalyzer(doc, activeWord, offset, modelProvider);
			
			
			List<GroovyCompletionSuggestion> proposals = null;
			if (StringUtils.isEmpty(activeWord) || currentProposals == null) {
			    currentProposals = model.buildSuggestions();
			    proposals = currentProposals;
			}
			
			proposals = filterProposals(activeWord);
			
			
			
			
			return transformCompletions(proposals, offset, StringUtils.length(activeWord));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NO_COMPLETIONS;
	}


	private void tryParseDocument(String script) {
        
	    try {
	        GradleScriptASTParser parser = new GradleScriptASTParser(script);
	        modelProvider = parser.walkScript();
	    } catch (MultipleCompilationErrorsException ex) {
	        //bad luck
	        
	    }
	    
    }


    private List<GroovyCompletionSuggestion> filterProposals(final String activeWord) {
        
	    return CollectionUtils.filter(currentProposals, new Spec<GroovyCompletionSuggestion>() {

            @Override
            public boolean isSatisfiedBy(GroovyCompletionSuggestion word) {
                return word.getSuggestion().contains(activeWord);
            }
	        
	    });
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

	
	private ICompletionProposal[] transformCompletions(List<GroovyCompletionSuggestion> completions, int offset, int currentWordLength) {
		
		ICompletionProposal[] ret = new ICompletionProposal[completions.size()];
		
		int i = 0;
		for(GroovyCompletionSuggestion completion : completions) {			
			ret[i++] = GroovyCompletionProposalBuilder.build(completion, offset, currentWordLength);
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
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}


    @Override
    public void assistSessionStarted(ContentAssistEvent event) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void assistSessionEnded(ContentAssistEvent event) {
        currentProposals = null;
    }


    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
        
    }

}
