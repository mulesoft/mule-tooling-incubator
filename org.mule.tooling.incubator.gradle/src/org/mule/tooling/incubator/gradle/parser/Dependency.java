package org.mule.tooling.incubator.gradle.parser;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * POJO that encapsulates dependencies found there.
 * @author juancavallotti
 */
public class Dependency {
	
	public static final String DEFAULT_GROUP = "org.mule.modules";
	
	private String group;
	private String artifact;
	private String version;
	private String classifier;
	private String extension;
	
	private ScriptLine scriptLine;
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getArtifact() {
		return artifact;
	}
	
	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getClassifier() {
		return classifier;
	}
	
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}
	
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public ScriptLine getScriptLine() {
		return scriptLine;
	}
	
	public void setScriptLine(ScriptLine scriptLine) {
		this.scriptLine = scriptLine;
	}
	
	/**
	 * Compares the file in a maven-style fashion, with the fields we have available.
	 * 
	 * so format should be name-version-classifier.extension.
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean matchesFilename(String fileName) {
	    
	    StringBuilder builder = new StringBuilder();
	    
	    builder.append(artifact);
	    
	    if (version != null) {
	        builder.append("-");
	        builder.append(version);
	    }
	    
	    if (classifier != null) {
	        builder.append("-");
	        builder.append(classifier);
	    }
	    
	    if (extension != null) {
	        builder.append(".");
	        builder.append(extension);
	    }
	    
	    return StringUtils.equals(builder.toString(), fileName);
	}
	
}
