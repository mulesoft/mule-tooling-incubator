package org.mule.tooling.incubator.gradle.editors;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class GradleProjectHighlightConfiguration extends SourceViewerConfiguration {
	
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer repairer = new DefaultDamagerRepairer(new GradleRuleBasedScanner());
		
		reconciler.setDamager(repairer, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(repairer, IDocument.DEFAULT_CONTENT_TYPE);
		
		
		
		//multi line comments repairer
		repairer = new DefaultDamagerRepairer(new SingleTokenTextColorScanner(GradleRuleBasedScanner.COMMENT));
		
		reconciler.setDamager(repairer, GradleRuleBasedScanner.MULTILINE_COMMENT_CONTENT_TYPE);
		reconciler.setRepairer(repairer, GradleRuleBasedScanner.MULTILINE_COMMENT_CONTENT_TYPE);
		
		//multi line strings repairer
		repairer = new DefaultDamagerRepairer(new SingleTokenTextColorScanner(GradleRuleBasedScanner.STRING));
		
		reconciler.setDamager(repairer, GradleRuleBasedScanner.MULTILINE_STRING_CONTENT_TYPE);
		reconciler.setRepairer(repairer, GradleRuleBasedScanner.MULTILINE_STRING_CONTENT_TYPE);
		
		return reconciler;
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new GradleScriptCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);
		
		return assistant;
	}
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		
		IAutoEditStrategy[] ret = {
				new SimpleIndentBracesOnEnterStrategy(),
				new SimpleCharBalancingAutoEditStrategy()
		};
		return ret; 
	}
	
	
}
