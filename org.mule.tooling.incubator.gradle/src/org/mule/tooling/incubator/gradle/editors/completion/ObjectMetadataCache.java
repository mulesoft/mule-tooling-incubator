package org.mule.tooling.incubator.gradle.editors.completion;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


public class ObjectMetadataCache {
    
    /**
     * This cache works at infinitum.
     */
    private static HashMap<String, List<GroovyCompletionSuggestion>> cache = new HashMap<String, List<GroovyCompletionSuggestion>>();
    
    public static List<GroovyCompletionSuggestion> buildAndCacheSuggestions(Class<?> cls) {
        
        if (cache.containsKey(cls.getName())) {
            return cache.get(cls.getName());
        }
        
        List<GroovyCompletionSuggestion> ret = new LinkedList<GroovyCompletionSuggestion>();
        //go through the class's public and non synthetic methods
        
        //we want all the public methods.
        Method[] methods = cls.getMethods();
        
        for(Method m : methods) {
            
            if (m.getDeclaringClass().equals(Object.class)) {
                continue;
            }
            
            if (m.getDeclaringClass().equals(GroovyObject.class)) {
                continue;
            }
            
            if (m.getDeclaringClass().equals(GroovyObjectSupport.class)) {
                continue;
            }
            
            if (m.getDeclaringClass().getName().startsWith("groovy.lang")) {
                continue;
            }
            
            //we will not care about mutators
            String propName = getAccessorPropertyName(m);
            
            if (propName != null) {
                addToCollectionIfNotThere(ret, new GroovyCompletionSuggestion(
                        GroovyCompletionSuggestionType.PROPERTY,
                        propName,
                        m.getReturnType().getName()));
                continue;
            }
            
            //we could be in the situation where a set-method is not a mutator
            //but we can take the risk of not presenting all the possibilities
            if (m.isSynthetic() || isPropertyMutator(m)) {
                continue;
            }
            
            addToCollectionIfNotThere(ret, new GroovyCompletionSuggestion(
                    GroovyCompletionSuggestionType.METHOD, 
                    m.getName(), 
                    m.getReturnType().getName()));
            
        }
        
        ret = Collections.unmodifiableList(ret);
        
        cache.put(cls.getName(), ret);
        
        return ret;
    }
    
    /**
     * Convert this method name to a property accessor only if it follows the
     * java beans convention.
     * @param m
     * @return
     */
    private static String getAccessorPropertyName(Method m) {
        
        String ret = null;
        
        if (m.getParameterTypes().length != 0) {
            return ret;
        }
        
        if (m.getName().startsWith("get")) {
            ret = m.getName().substring(3);
        } else if (m.getName().startsWith("is")) {
            //groovy declares is and get so we would not want the is if it is not declared by the user.            
            if (m.getReturnType().equals(Boolean.class) || m.getReturnType().equals(boolean.class)) {
                ret = m.getName().substring(2);
            }
        }
        
        ret = StringUtils.uncapitalize(ret);
        
        return ret;
    }
    
    private static void addToCollectionIfNotThere(List<GroovyCompletionSuggestion> collection, GroovyCompletionSuggestion suggestion) {
        if (!collection.contains(suggestion)) {
            collection.add(suggestion);
        }
    }
    
    
    private static boolean isPropertyMutator(Method m) {
        if (m.getParameterTypes().length != 1) {
            return false;
        }
        
        if (m.getReturnType().equals(Void.class)) {
            return false;
        }
        
        if (m.getName().startsWith("set")) {
            return true;
        }
        
        return false;
    }

    public static Class<?> getArgumentTypeForMethod(String methodName, Class<?> type) {
        
        Method[] methods = type.getMethods();
        
        for(Method m : methods) {
            if (m.getName().equals(methodName)) {
                if (m.getParameterTypes().length != 1) {
                    return null;
                }
                return m.getParameterTypes()[0];
            }
        }
        return null;
    }
    
}
