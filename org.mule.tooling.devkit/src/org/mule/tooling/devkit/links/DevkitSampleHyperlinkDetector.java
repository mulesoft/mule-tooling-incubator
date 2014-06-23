package org.mule.tooling.devkit.links;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("restriction")
public class DevkitSampleHyperlinkDetector extends AbstractHyperlinkDetector {

	Pattern pattern = Pattern.compile(".*\\{@sample.*\\}");

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor = (ITextEditor) getAdapter(ITextEditor.class);

		if (textEditor == null || !(textEditor instanceof JavaEditor))
			return null;

		IDocument document = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());
		FileEditorInput fe = (FileEditorInput) textEditor.getEditorInput();

		try {
			IRegion reg = document.getLineInformationOfOffset(region
					.getOffset());
			String value = document.get(reg.getOffset(), reg.getLength());
			Matcher matcher = pattern.matcher(value);
			if (matcher.find()) {
				int offset = value.indexOf("{@sample");
				int lastChar = value.indexOf("}");
				if ((offset != -1) && (lastChar != -1)) {
					IHyperlink[] links = new IHyperlink[1];
					links[0] = new SampleHyperlink(reg.getOffset() + offset,
							(lastChar - offset) + 1, "Link to devkit Sample",
							"Go to sample", value.substring(value.indexOf("@"),
									value.indexOf("}")), fe.getFile()
									.getProject());
					return links;
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
