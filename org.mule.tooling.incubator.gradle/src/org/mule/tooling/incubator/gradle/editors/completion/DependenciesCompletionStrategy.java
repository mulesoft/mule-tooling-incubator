package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.List;

import org.mule.tooling.incubator.gradle.editors.completion.model.SimplifiedDependencyPojo;
import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;


public class DependenciesCompletionStrategy extends BaseDSLCompletionStrategy {
    
    @Override
    public List<GroovyCompletionSuggestion> buildSuggestions(DSLMethodAndMap map, Class<?> contextClass, String expectedInputKey) {
        
        Class<?> argClass = ObjectMetadataCache.getArgumentTypeForMethod(map.getMethodName(), contextClass);
        
        argClass = argClass == null ? SimplifiedDependencyPojo.class : argClass;
        
        List<GroovyCompletionSuggestion> mapOptions = createGroovyConstructorMapSuggestionsForClass(argClass);
        List<GroovyCompletionSuggestion> allComponents = ObjectMetadataCache.buildAndCacheSuggestions(contextClass);
        
        //I would like to include methods as well.
        for(GroovyCompletionSuggestion s : allComponents) {
            if (s.getType() == GroovyCompletionSuggestionType.METHOD) {
                mapOptions.add(s);
            }
        }    
        
        return filterDslCompletionSuggestions(map, mapOptions);
    }

}
