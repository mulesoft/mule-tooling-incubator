package org.mule.tooling.devkit.treeview.model;

import java.util.Arrays;

public class ModelUtils {
	public static String[] SUPPORTED_CLASS_ANNOTATIONS = { "Connector",
			"Module","MetaDataCategory" };

	public static String[] SUPPORTED_METHOD_ANNOTATIONS = { "Processor",
			"Connect", "Disconnect", "ValidateConnection",
			"ConnectionIdentifier" };

	public static String[] SUPPORTED_TRANSFORMER_METHOD = {
		"Transformer", "TransformerResolver" };

	public static String[] SUPPORTED_METADATA_METHOD = {
			"MetaDataKeyRetriever", "MetaDataRetriever","MetaDataOutputRetriever"};

	public static boolean isAnnotationSupported(String annotation) {
		return Arrays.asList(SUPPORTED_CLASS_ANNOTATIONS).contains(annotation)
				|| Arrays.asList(SUPPORTED_METHOD_ANNOTATIONS).contains(
						annotation)|| isMetadaMethod(annotation)|| isTransformerMethod(annotation);
	}

	public static boolean isConnectionAnnotation(String annotation) {
		return !annotation.equals(SUPPORTED_METHOD_ANNOTATIONS[0]) && !isMetadaMethod(annotation);
	}
	
	public static boolean isMetadaMethod(String annotation) {
		return Arrays.asList(SUPPORTED_METADATA_METHOD).contains(
				annotation);
	}
	
	public static boolean isTransformerMethod(String annotation) {
		return Arrays.asList(SUPPORTED_TRANSFORMER_METHOD).contains(
				annotation);
	}
}
