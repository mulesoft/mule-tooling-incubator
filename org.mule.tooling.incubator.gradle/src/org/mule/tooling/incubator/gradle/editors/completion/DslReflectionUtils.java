package org.mule.tooling.incubator.gradle.editors.completion;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;


public class DslReflectionUtils {
    
    private static final String METHOD_MISSING_NAME = "methodMissing";
    
    /**
     * Check if the given method name can be utilized as a dsl method.
     * @param methodName
     * @param context
     * @return
     */
    public static boolean contextContainsDSLMethod(String methodName, Class<?> context) {
        
        for(Method m : context.getMethods()) {
           
           if (StringUtils.equals(METHOD_MISSING_NAME, m.getName())) {
               //could be
               return true;
           }
            
           if (!StringUtils.equals(methodName, m.getName())) {
               continue;
           }
           
           if (m.getParameterTypes().length != 1) {
               continue;
           }
           
           return true;
        }
        
        return false;
    }
    
}
