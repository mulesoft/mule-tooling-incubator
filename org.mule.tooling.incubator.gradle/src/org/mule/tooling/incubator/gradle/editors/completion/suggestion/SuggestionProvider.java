package org.mule.tooling.incubator.gradle.editors.completion.suggestion;

import java.util.List;

import org.mule.tooling.incubator.gradle.editors.completion.GroovyCompletionSuggestion;


public interface SuggestionProvider {
    List<GroovyCompletionSuggestion> buildSuggestions(); 
}
