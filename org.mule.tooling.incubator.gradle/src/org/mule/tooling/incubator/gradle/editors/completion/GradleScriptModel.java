package org.mule.tooling.incubator.gradle.editors.completion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Model helper class to help computing auto-completion suggestions. This class is in charge of parsing
 * the script and extracting all the information that would be necessary. This is as well a best-effort 
 * approach since we don't have the ability to really introspect objects.
 * @author juancavallotti
 */
public class GradleScriptModel {
	private static final char PROPERTY_OPERATOR = '.'; 
	
	
	private final String gradleScript;
	private final String completionWord;
	
	public GradleScriptModel(String gradleScript, String completionWord) {
		this.gradleScript = gradleScript;
		this.completionWord = completionWord;
	}
	
	
	/**
	 * TODO - this method is currently very dummy, we would need to improve this.
	 * @return
	 */
	public List<String> buildSuggestions() {
		
		//this is horrible but at the moment might work.
		if (!gradleScript.contains("apply plugin: 'mule")) {
			return Collections.emptyList();
		}
		
		String lastWord = completionWord;
		
		if (completionWord.trim().length() == 0) {
			//this might be a proprty accessor
			lastWord = parseLeftSide();
		}
		
		if (lastWord.equals(MuleGradleProjectCompletionHelper.MULE_PLUGIN_EXTENSION_NAME)) {
			return Arrays.asList(MuleGradleProjectCompletionHelper.MULE_PLUGIN_EXTENSION_PROPERTIES);
		}
		
		if (MuleGradleProjectCompletionHelper.MULE_PLUGIN_EXTENSION_NAME.startsWith(lastWord)) {
			return Arrays.asList(MuleGradleProjectCompletionHelper.MULE_PLUGIN_EXTENSION_NAME);
		}
		
		return Collections.emptyList();
	}


	private String parseLeftSide() {
		
		if (gradleScript.charAt(gradleScript.length() - 1) != PROPERTY_OPERATOR) {
			return "";
		}
		
		//start parsing the right side
		int i = gradleScript.length() - 1;
		int j = i;
		
		while (j >= 0) {
			char currentChar = gradleScript.charAt(--j);
			if (!Character.isJavaIdentifierStart(currentChar) && !Character.isJavaIdentifierPart(currentChar)) {
				return gradleScript.substring(j+1, i);
			}
		}
		
		return "";
	}
	
}
