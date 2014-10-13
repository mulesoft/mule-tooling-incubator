package org.mule.tooling.incubator.gradle.editors.completion.suggestion;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.module.ExternalContributionMuleModule;
import org.mule.tooling.incubator.gradle.editors.completion.GroovyCompletionSuggestion;
import org.mule.tooling.incubator.gradle.editors.completion.GroovyCompletionSuggestionType;
import org.mule.tooling.incubator.gradle.parser.DSLMethodAndMap;
import org.mule.tooling.incubator.gradle.parser.ScriptParsingUtils;
import org.mule.tooling.maven.dependency.ExternalModuleMavenDependency;
import org.mule.tooling.maven.dependency.MavenDependency;


public class StudioPluginsSuggestionProvider implements SuggestionProvider {

    private final DSLMethodAndMap dslMethod;
    
    private final String completeKey;
    
    private static final String NAME_DSL_OPTION = "name";
    
    private static final String VERSION_DSL_OPTION = "version";
    
    private static final String GROUP_DSL_OPTION = "group";
    
    private static enum COORDINATE {name, group, version}
    
    public StudioPluginsSuggestionProvider(DSLMethodAndMap dslMethod, String completeKey) {
        this.dslMethod = dslMethod;
        this.completeKey = completeKey;
    }

    @Override
    public List<GroovyCompletionSuggestion> buildSuggestions() {
        
        String completeOption = completeKey;
        
        if (StringUtils.equals(NAME_DSL_OPTION, completeOption)) {
            return buildPluginNames();
        }
        
        if (StringUtils.equals(VERSION_DSL_OPTION, completeOption)) {
            return buildPluginVersions();
        }
        
        if (StringUtils.equals(GROUP_DSL_OPTION, completeOption)) {
            return buildPluginGroups();
        }
        
        
        return Collections.emptyList();
    }

    private List<GroovyCompletionSuggestion> buildPluginGroups() {
        String version = dslMethod.getArguments().get(VERSION_DSL_OPTION);
        String name = dslMethod.getArguments().get(NAME_DSL_OPTION);
        
        List<MavenDependencyAndModule> mods = filterInstalledModules(null, name, version);
        
        return buildSuggestionsFromModules(mods, COORDINATE.group);        
    }

    private List<GroovyCompletionSuggestion> buildPluginVersions() {
        String group = dslMethod.getArguments().get(GROUP_DSL_OPTION);
        String name = dslMethod.getArguments().get(NAME_DSL_OPTION);
        
        if (StringUtils.isEmpty(group) && StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        
        List<MavenDependencyAndModule> mods = filterInstalledModules(group, name, null);
        
        return buildSuggestionsFromModules(mods, COORDINATE.version); 
    }

    private List<GroovyCompletionSuggestion> buildPluginNames() {
        
        String version = dslMethod.getArguments().get(VERSION_DSL_OPTION);
        String group = dslMethod.getArguments().get(GROUP_DSL_OPTION);
        
        List<MavenDependencyAndModule> mods = filterInstalledModules(group, null, version);
        
        return buildSuggestionsFromModules(mods, COORDINATE.name);
    }
    
    private List<MavenDependencyAndModule> filterInstalledModules(String group, String name, String version) {
        
        group = ScriptParsingUtils.removeQuotesIfNecessary(group);
        name = ScriptParsingUtils.removeQuotesIfNecessary(name);
        version = ScriptParsingUtils.removeQuotesIfNecessary(version);
        
        List<ExternalContributionMuleModule> modules = MuleCorePlugin.getModuleManager().getExternalModules();
        
        List<MavenDependencyAndModule> ret = new LinkedList<MavenDependencyAndModule>();
        
        for(ExternalContributionMuleModule mod : modules) {
            
            try {
                MavenDependency dep = ExternalModuleMavenDependency.from(mod);
                
                if (!StringUtils.isEmpty(group)) {
                    if (!StringUtils.equals(group, dep.getGroupId())) {
                        continue;
                    }
                }
                
                if (!StringUtils.isEmpty(name)) {
                    if (!StringUtils.equals(name, dep.getArtifactId())) {
                        continue;
                    }
                }
                
                if (!StringUtils.isEmpty(version)) {
                    if (!StringUtils.equals(version, dep.getVersion())) {
                        continue;
                    }
                }
                
                ret.add(new MavenDependencyAndModule(dep, mod));
                
            } catch (Exception ex) {
                //some error happened
            }
        }
        
        return ret;
    }
    
    private List<GroovyCompletionSuggestion> buildSuggestionsFromModules(List<MavenDependencyAndModule> modules, COORDINATE coord) {
        List<GroovyCompletionSuggestion> ret = new LinkedList<GroovyCompletionSuggestion>();
        for(MavenDependencyAndModule mod : modules) {
            
            String val = null;
            
            switch (coord) {
            case group:
                val = mod.getMavenDependency().getGroupId();
                break;
            case name:
                val = mod.getMavenDependency().getArtifactId();
                break;
            case version:
                val = mod.getMavenDependency().getVersion();
                break;
            }
            
            GroovyCompletionSuggestion suggestion = new GroovyCompletionSuggestion(GroovyCompletionSuggestionType.STRING_VALUE, val, mod.getModule().getName());
            ret.add(suggestion);
        }
        return ret;
    }
    
}
