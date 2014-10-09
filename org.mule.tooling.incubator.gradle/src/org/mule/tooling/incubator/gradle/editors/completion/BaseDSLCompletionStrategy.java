package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.LinkedList;
import java.util.List;

import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;


public abstract class BaseDSLCompletionStrategy implements DSLCompletionStrategy {
    
    protected List<GroovyCompletionSuggestion> filterDslCompletionSuggestions(DSLMethodAndMap method, List<GroovyCompletionSuggestion> allOptions) {
        
        List<GroovyCompletionSuggestion> ret = new LinkedList<GroovyCompletionSuggestion>();
        
        for (GroovyCompletionSuggestion compl : allOptions) {
            if (!method.getArguments().containsKey(compl.getSuggestion())) {
                ret.add(compl);
            }
        }
        
        return ret;
    }
    
    protected List<GroovyCompletionSuggestion> createGroovyConstructorMapSuggestionsForClass(Class<?> cls) {
        List<GroovyCompletionSuggestion> suggestions = ObjectMetadataCache.buildAndCacheSuggestions(cls);
        
        List<GroovyCompletionSuggestion> ret = new LinkedList<GroovyCompletionSuggestion>();
        
        for(GroovyCompletionSuggestion currentSuggestion : suggestions) {
            if (currentSuggestion.getType() == GroovyCompletionSuggestionType.PROPERTY) {
                ret.add(new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.MAP_ARGUMENT, currentSuggestion.getSuggestion(), currentSuggestion.getSuggestionDescription()));
            }
        }
        
        return ret;   
    }
    
}
