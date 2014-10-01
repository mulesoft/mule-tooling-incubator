package org.mule.tooling.incubator.gradle.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Parser that analyzes the gradle script. Initially the implementation might be very
 * vague, but in the future, real parsing must be seen here.
 * 
 * @author juancavallotti
 *
 */
public class GradleScriptParser {
	
	private final IDocument buildDocument;
	
	public GradleScriptParser(IDocument document) {
		this.buildDocument = document;
		//we assume all the lines have the same delimiter
	}
	
	public GradleScriptDescriptor parse() throws Exception {
		GradleScriptDescriptor ret = new GradleScriptDescriptor();
		
		List<ScriptLine> components = locateAndAnalyzeComponentsSection(); 
		ret.setMuleComponentsSection(components);
		
		return ret;
	}

	private List<ScriptLine> locateAndAnalyzeComponentsSection() throws Exception {
		
		List<ScriptLine> ret = new ArrayList<ScriptLine>();
		FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(buildDocument);
		
		IRegion region = adapter.find(0, GradleParserConstants.COMPONENTS_DSL_START, true, false, true, false);
		
		//nothing
		if (region == null) {
			return ret;
		}
		
		return ScriptParsingUtils.parseScopeLines(buildDocument, region.getOffset() + region.getLength());
	}
	
}
