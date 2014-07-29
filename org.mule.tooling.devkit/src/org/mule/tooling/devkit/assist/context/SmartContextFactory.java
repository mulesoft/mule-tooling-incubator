package org.mule.tooling.devkit.assist.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.java.IInvocationContext;

public class SmartContextFactory {

    public static List<SmartContext> getContexts(IInvocationContext context) {
        List<SmartContext> contexts = new ArrayList<SmartContext>();
        contexts.add(new ClassDeclarationContext(context));
        contexts.add(new ClassBodyContext(context));
        contexts.add(new FieldDeclarationContext(context));
        contexts.add(new MethodDeclarationContext(context));
        contexts.add(new ParameterContext(context));
        return contexts;
    }
}
