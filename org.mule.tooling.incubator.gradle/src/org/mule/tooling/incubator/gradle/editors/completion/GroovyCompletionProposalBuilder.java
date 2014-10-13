package org.mule.tooling.incubator.gradle.editors.completion;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.ui.modules.core.autocomplete.AutocompleteImages;

public class GroovyCompletionProposalBuilder {
    
    
    public static CompletionProposal build(GroovyCompletionSuggestion suggestion, int offset, int currentWordLength) {
        
        String completion = buildCompletion(suggestion);
        String displayName = buildDisplayName(suggestion);
        Image image = buildImage(suggestion);
        int completionLength = StringUtils.length(completion);
        
        return new CompletionProposal(
                completion, 
                offset - currentWordLength, 
                currentWordLength, 
                completionLength,
                image,
                displayName,
                null,
                suggestion.getSuggestionDescription());
    }

    private static Image buildImage(GroovyCompletionSuggestion suggestion) {
        
        String imgCode = null;
        
        switch (suggestion.getType()) {
        case MAP_ARGUMENT:
            imgCode = AutocompleteImages.IMG_ELEMENT;
            break;
        case METHOD:
            imgCode = AutocompleteImages.IMG_FUNCTION;
            break;
        case PROPERTY:
            imgCode = AutocompleteImages.IMG_ATTRIBUTE;
            break;
        case STRING_VALUE:
        case RAW_VALUE:
            imgCode = AutocompleteImages.IMG_TEMPLATE;
        default:
            return null;
        }
        
        
        return AutocompleteImages.getImage(imgCode);
        
    }

    private static String buildDisplayName(GroovyCompletionSuggestion suggestion) {
        
        String ret = null;
        
        switch (suggestion.getType()) {
        case MAP_ARGUMENT:
            ret = suggestion.getSuggestion() + " - " + suggestion.getSuggestionDescription();
            break;
        case METHOD:
            ret = suggestion.getSuggestion() + ": " + suggestion.getSuggestionDescription();
            break;
        case PROPERTY:
            ret = suggestion.getSuggestion() + ": " + suggestion.getSuggestionDescription();
            break;
        case STRING_VALUE:
        case RAW_VALUE:
            ret = suggestion.getSuggestion() + " - " + suggestion.getSuggestionDescription();
            break;
        default:
            return null;
        }
        
        
        return ret;
    }

    private static String buildCompletion(GroovyCompletionSuggestion suggestion) {
        
        String ret = null;
        
        switch (suggestion.getType()) {
        case MAP_ARGUMENT:
            ret = suggestion.getSuggestion() + ": ";
            break;
        case STRING_VALUE:
            ret = "'" + suggestion.getSuggestion() + "'";
            break;
        case METHOD:
        case PROPERTY:
        case RAW_VALUE:
            ret = suggestion.getSuggestion();
            break;
        default:
            return null;
        }
        
        return ret;
    }
    
    
    
}
