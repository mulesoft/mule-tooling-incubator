package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.List;

import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;

import com.mulesoft.build.cloudhub.CloudhubEnvironment;


public class CloudhubDSLCompletionStrategy extends BaseDSLCompletionStrategy {

    @Override
    public List<GroovyCompletionSuggestion> buildSuggestions(DSLMethodAndMap map, Class<?> contextClass, String expectedInputKey) {
        List<GroovyCompletionSuggestion> ret = super.createGroovyConstructorMapSuggestionsForClass(CloudhubEnvironment.class);
        return super.filterDslCompletionSuggestions(map, ret);
    }

}
