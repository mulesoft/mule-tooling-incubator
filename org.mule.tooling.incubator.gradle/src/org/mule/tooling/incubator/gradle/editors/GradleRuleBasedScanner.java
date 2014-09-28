package org.mule.tooling.incubator.gradle.editors;


import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class GradleRuleBasedScanner extends RuleBasedScanner {
	
	private static final Color COMMENT = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
	private static final Color KEYWORD = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA); 
	private static final Color STRING = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
	private static final Color DSL_KEYWORD = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN); 
	
	//general groovy keywords
	private static final String[] GROOVY_KEYWORDS = {"boolean", "break", "byte", "case", "catch", "char",
		"class", "continue", "def", "default", "do", "else", "enum", "extends", "false", "final", "for",
		"if", "implements", "int", "interface", "long", "new", "null", "private", "public", "protected",
		"return", "short", "static", "switch", "throw", "throws" , "true", "try", "volatile", "while" };
	
	private static final String[] DSL_KEYWORDS = {"apply", "buildscript", "dependencies", "classpath", 
		"cloudhub", "ivy", "maven", "mmc", "module", "modules", "mule", "project", "repositories", 
		"transports", "url"};
	
	
	public GradleRuleBasedScanner() {
		
		
		
		WordRule wordRule = new WordRule(new IWordDetector() {
			
			@Override
			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}
			
			@Override
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
		}, Token.WHITESPACE);
		
		
		
		Token comment = new Token(new TextAttribute(COMMENT));
		Token string = new Token(new TextAttribute(STRING));
		Token languageKeyword = new Token(new TextAttribute(KEYWORD, null, SWT.BOLD));
		Token dslKeyword = new Token(new TextAttribute(DSL_KEYWORD, null, SWT.BOLD));
		
		for(String groovyKeyword: GROOVY_KEYWORDS) {
			wordRule.addWord(groovyKeyword, languageKeyword);
		}
		
		for(String dslKw: DSL_KEYWORDS) {
			wordRule.addWord(dslKw, dslKeyword);
		}
		
		setRules(new IRule[]{
				new MultiLineRule("/*", "*/", comment, '\\'),
				new SingleLineRule("//", "\n", comment),
				wordRule,
				new SingleLineRule("\"", "\"", string, '\\'),
				new SingleLineRule("'", "'", string, '\\')
		});
	}
}
