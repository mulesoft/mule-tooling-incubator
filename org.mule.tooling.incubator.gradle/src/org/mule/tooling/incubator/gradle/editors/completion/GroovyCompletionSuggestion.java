package org.mule.tooling.incubator.gradle.editors.completion;


public class GroovyCompletionSuggestion {
    
    private final GroovyCompletionSuggestionType type;
    private final String suggestion;
    private final String suggestionDescription;
    
    public GroovyCompletionSuggestion(GroovyCompletionSuggestionType type, String suggestion, String suggestionDescription) {
        this.type = type;
        this.suggestion = suggestion;
        this.suggestionDescription = suggestionDescription;
    }

    
    public GroovyCompletionSuggestionType getType() {
        return type;
    }

    
    public String getSuggestion() {
        return suggestion;
    }

    
    public String getSuggestionDescription() {
        return suggestionDescription;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((suggestion == null) ? 0 : suggestion.hashCode());
        result = prime * result + ((suggestionDescription == null) ? 0 : suggestionDescription.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroovyCompletionSuggestion other = (GroovyCompletionSuggestion) obj;
        if (suggestion == null) {
            if (other.suggestion != null)
                return false;
        } else if (!suggestion.equals(other.suggestion))
            return false;
        if (suggestionDescription == null) {
            if (other.suggestionDescription != null)
                return false;
        } else if (!suggestionDescription.equals(other.suggestionDescription))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    
}
