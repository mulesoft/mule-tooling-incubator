package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.List;

import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;

import com.mulesoft.build.mmc.MMCEnvironment;


public class MMCDSLCompletionStrategy extends BaseDSLCompletionStrategy {

    @Override
    public List<GroovyCompletionSuggestion> buildSuggestions(DSLMethodAndMap map, Class<?> contextClass, boolean expectsInput) {
        List<GroovyCompletionSuggestion> ret = super.createGroovyConstructorMapSuggestionsForClass(MMCEnvironment.class);
        return super.filterDslCompletionSuggestions(map, ret);
    }

}
