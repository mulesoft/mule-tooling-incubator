package org.mule.tooling.devkit.assist.context;

import java.util.ArrayList;
import java.util.List;

public class SmartContextFactory {

    public static List<SmartContext> getContexts() {
        List<SmartContext> contexts = new ArrayList<SmartContext>();
        contexts.add(new ClassDeclarationContext());
        contexts.add(new ClassBodyContext());
        contexts.add(new FieldDeclarationContext());
        contexts.add(new MethodDeclarationContext());
        contexts.add(new ParameterContext());
        return contexts;
    }
}
