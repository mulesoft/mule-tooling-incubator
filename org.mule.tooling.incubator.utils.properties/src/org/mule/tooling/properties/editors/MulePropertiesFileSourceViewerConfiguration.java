package org.mule.tooling.properties.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.mule.tooling.properties.editors.completion.MulePropertiesContentAssistant;

public class MulePropertiesFileSourceViewerConfiguration extends
		PropertiesFileSourceViewerConfiguration {

	/**
	 * Super ugly but the fastest way of getting this done :D
	 * @param preferenceStore
	 * @param editor
	 */
	public MulePropertiesFileSourceViewerConfiguration(IPreferenceStore preferenceStore, ITextEditor editor) {
		super(JavaPlugin.getDefault().getJavaTextTools().getColorManager(), preferenceStore, editor, IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING);
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		
		IResource file = ((IFileEditorInput) getEditor().getEditorInput()).getFile();		
		MulePropertiesContentAssistant completionProcessor = new MulePropertiesContentAssistant(file);
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(completionProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);
		assistant.addCompletionListener(completionProcessor);
		return assistant;
	}
}
