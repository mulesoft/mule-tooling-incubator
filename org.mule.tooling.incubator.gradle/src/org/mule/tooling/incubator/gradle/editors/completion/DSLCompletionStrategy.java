package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.List;

import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;


public interface DSLCompletionStrategy {
    public List<GroovyCompletionSuggestion> buildSuggestions(DSLMethodAndMap map, Class<?> contextClass, String expectedInputKey);
}
