package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.mule.tooling.incubator.gradle.editors.completion.suggestion.StudioPluginsSuggestionProvider;
import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;


public class MuleComponentsDSLCompletionStrategy extends BaseDSLCompletionStrategy {

    private static final String[] COMPONENTS_DSL_METHODS = {"plugin", "connector", "module"};
    
    @Override
    public List<GroovyCompletionSuggestion> buildSuggestions(DSLMethodAndMap method, Class<?> contextClass, String expectedInputKey) {
        
        if (!ArrayUtils.contains(COMPONENTS_DSL_METHODS, method.getMethodName())) {
            return Collections.emptyList();
        }
        
        if (!StringUtils.isEmpty(expectedInputKey)) {
            return suggestInstalledModules(method, expectedInputKey);
        }
        
        
        List<GroovyCompletionSuggestion> ret = MuleGradleProjectCompletionMetadata.COMPONENTS_BASIC_DSL;
        return filterDslCompletionSuggestions(method, ret);
    }

    private List<GroovyCompletionSuggestion> suggestInstalledModules(DSLMethodAndMap method, String expectedInputKey) {
        StudioPluginsSuggestionProvider provider = new StudioPluginsSuggestionProvider(method, expectedInputKey);
        return provider.buildSuggestions();
    }
    
}
