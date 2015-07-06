package org.mule.tooling.incubator.gradle.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.mule.tooling.incubator.gradle.parser.GradleMulePlugin;
import org.mule.tooling.incubator.gradle.parser.ast.GradleScriptASTParser;


public class StudioGradleEnabledContentDescriber implements ITextContentDescriber {
    
    @Override
    public int describe(InputStream contents, IContentDescription description) throws IOException {
        
        try {
            GradleScriptASTParser parser = new GradleScriptASTParser(contents);
            PluginsSyntaxDescriberVisitor pluginsVisitor = new PluginsSyntaxDescriberVisitor();
            ApplySyntaxDescriberVisitor visitor = new ApplySyntaxDescriberVisitor();
            
            parser.walkScript(pluginsVisitor);
            
            if (pluginsVisitor.isFoundPlugin()) {
            	return IContentDescriber.VALID;
            }
            
            parser.walkScript(visitor);
            
            if (visitor.isFoundplugin()) {
                return IContentDescriber.VALID;
            }
            
        } catch (MultipleCompilationErrorsException ex) {
            return IContentDescriber.INDETERMINATE;
        }
        return IContentDescriber.INVALID;
    }

    @Override
    public QualifiedName[] getSupportedOptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int describe(Reader contents, IContentDescription description) throws IOException {
        return IContentDescriber.INDETERMINATE;
    }



}
