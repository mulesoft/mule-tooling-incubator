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
    
}
