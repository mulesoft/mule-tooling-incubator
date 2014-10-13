package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;


public class MuleComponentsDSLCompletionStrategy extends BaseDSLCompletionStrategy {

    private static final String[] COMPONENTS_DSL_METHODS = {"plugin", "component", "module"};
    
    @Override
    public List<GroovyCompletionSuggestion> buildSuggestions(DSLMethodAndMap method, Class<?> contextClass, String expectedInputKey) {
        
        if (!ArrayUtils.contains(COMPONENTS_DSL_METHODS, method.getMethodName())) {
            return Collections.emptyList();
        }
        
        List<GroovyCompletionSuggestion> ret = MuleGradleProjectCompletionMetadata.COMPONENTS_BASIC_DSL;
        return filterDslCompletionSuggestions(method, ret);
    }

}
