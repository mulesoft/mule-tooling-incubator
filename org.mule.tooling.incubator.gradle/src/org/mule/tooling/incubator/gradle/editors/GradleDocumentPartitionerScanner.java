package org.mule.tooling.incubator.gradle.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


public class GradleDocumentPartitionerScanner extends RuleBasedPartitionScanner {
    
    
    public GradleDocumentPartitionerScanner() {
        
        IToken comment = new Token(GradleRuleBasedScanner.MULTILINE_COMMENT_CONTENT_TYPE);
        IToken multilineString = new Token(GradleRuleBasedScanner.MULTILINE_STRING_CONTENT_TYPE);
        
        setPredicateRules(new IPredicateRule[] {
                new MultiLineRule("/*", "*/", comment, (char) 0, true),
                new MultiLineRule("\"\"\"", "\"\"\"", multilineString, (char) 0, true),
        });
        
    }
    
}
