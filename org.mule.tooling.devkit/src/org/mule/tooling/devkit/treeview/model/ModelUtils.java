package org.mule.tooling.devkit.treeview.model;import org.eclipse.jdt.core.dom.AST;import org.eclipse.jdt.core.dom.ASTMatcher;import org.eclipse.jdt.core.dom.Name;import org.eclipse.jdt.core.dom.QualifiedName;import org.eclipse.jdt.core.dom.SimpleName;public class ModelUtils {    public static final String ORG_MULE_API_ANNOTATIONS = "org.mule.api.annotations";    public static final String ORG_MULE_API_PARAM_ANNOTATIONS = ORG_MULE_API_ANNOTATIONS + ".param";    public static final String ORG_MULE_API_ANNOTATIONS_COMPONENTS = ORG_MULE_API_ANNOTATIONS + ".components";    public static final QualifiedName DEFAULT_ANNOTATION = createAnnotation(ORG_MULE_API_PARAM_ANNOTATIONS, "Default");    public static final QualifiedName OPTIONAL_ANNOTATION = createAnnotation(ORG_MULE_API_PARAM_ANNOTATIONS, "Optional");    public static final QualifiedName CONFIGURABLE_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Configurable");    public static final QualifiedName SOURCE_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Source");    public static final QualifiedName CONNECTOR_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Connector");    public static final QualifiedName MODULE_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Module");    public static final QualifiedName METADATA_CATEGORY_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS_COMPONENTS, "MetaDataCategory");    public static final QualifiedName PROCESSOR_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Processor");    public static final QualifiedName CONNECT_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Connect");    public static final QualifiedName VALIDATE_CONNECTION_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Disconnect");    public static final QualifiedName CONNECTION_IDENTIFIER_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "ConnectionIdentifier");    public static final QualifiedName DISCONNECT_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "ValidateConnection");    public static final QualifiedName TRANSFORMER_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "Transformer");    public static final QualifiedName TRANSFORMER_RESOLVER_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "TransformerResolver");    public static final QualifiedName METADATA_KEY_RETRIEVER_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "MetaDataKeyRetriever");    public static final QualifiedName METADATA_RETRIEVER_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "MetaDataRetriever");    public static final QualifiedName METADATA_OUTPUT_RETRIEVER_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "MetaDataOutputRetriever");    public static final QualifiedName CONNECTION_STRATEGY_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS, "ConnectionStrategy");    // Components    public static QualifiedName BASIC_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS_COMPONENTS, "Basic");    public static QualifiedName BASIC_AUTH_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS_COMPONENTS, "ConnectionManagement");    public static QualifiedName OAUTH_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS + ".oauth", "OAuth2");    public static QualifiedName HTTP_BASIC_AUTH_ANNOTATION = createAnnotation(ORG_MULE_API_ANNOTATIONS_COMPONENTS, "HttpBasicAuth");    private static VerifyChainNode buildSupportedChain() {        VerifyChainNode next = new VerifyChainNode(METADATA_OUTPUT_RETRIEVER_ANNOTATION, null);        next = new VerifyChainNode(METADATA_RETRIEVER_ANNOTATION, next);        next = new VerifyChainNode(METADATA_KEY_RETRIEVER_ANNOTATION, next);        next = new VerifyChainNode(TRANSFORMER_RESOLVER_ANNOTATION, next);        next = new VerifyChainNode(TRANSFORMER_ANNOTATION, next);        next = new VerifyChainNode(DISCONNECT_ANNOTATION, next);        next = new VerifyChainNode(CONNECTION_IDENTIFIER_ANNOTATION, next);        next = new VerifyChainNode(VALIDATE_CONNECTION_ANNOTATION, next);        next = new VerifyChainNode(CONNECT_ANNOTATION, next);        next = new VerifyChainNode(PROCESSOR_ANNOTATION, next);        next = new VerifyChainNode(METADATA_CATEGORY_ANNOTATION, next);        next = new VerifyChainNode(MODULE_ANNOTATION, next);        next = new VerifyChainNode(CONNECTOR_ANNOTATION, next);        next = new VerifyChainNode(SOURCE_ANNOTATION, next);        next = new VerifyChainNode(BASIC_ANNOTATION, next);        next = new VerifyChainNode(BASIC_AUTH_ANNOTATION, next);        next = new VerifyChainNode(HTTP_BASIC_AUTH_ANNOTATION, next);        next = new VerifyChainNode(OAUTH_ANNOTATION, next);        next = new VerifyChainNode(CONNECTION_STRATEGY_ANNOTATION, next);        return next;    }    private static VerifyChainNode buildConnectionChain() {        VerifyChainNode next = new VerifyChainNode(DISCONNECT_ANNOTATION, null);        next = new VerifyChainNode(CONNECTION_IDENTIFIER_ANNOTATION, next);        next = new VerifyChainNode(VALIDATE_CONNECTION_ANNOTATION, next);        next = new VerifyChainNode(CONNECT_ANNOTATION, next);        return next;    }    private static VerifyChainNode buildMetadataChain() {        VerifyChainNode next = new VerifyChainNode(METADATA_OUTPUT_RETRIEVER_ANNOTATION, null);        next = new VerifyChainNode(METADATA_RETRIEVER_ANNOTATION, next);        next = new VerifyChainNode(METADATA_KEY_RETRIEVER_ANNOTATION, next);        return next;    }    private static VerifyChainNode buildTransformerChain() {        VerifyChainNode next = new VerifyChainNode(TRANSFORMER_ANNOTATION, null);        next = new VerifyChainNode(TRANSFORMER_RESOLVER_ANNOTATION, next);        return next;    }    public static boolean isAnnotationSupported(Name annotationName) {        return buildSupportedChain().isSupported(annotationName);    }    public static boolean isConnectionAnnotation(Name annotationName) {        return buildConnectionChain().isSupported(annotationName);    }    public static boolean isMetadaMethod(Name annotationName) {        return buildMetadataChain().isSupported(annotationName);    }    public static boolean isTransformerMethod(Name annotationName) {        return buildTransformerChain().isSupported(annotationName);    }    public static boolean isSourceMethod(Name annotationName) {        return annotationMatches(annotationName, SOURCE_ANNOTATION);    }    public static QualifiedName createAnnotation(String qualifier, String name) {        @SuppressWarnings("deprecation")        AST astFactory = AST.newAST(AST.JLS3);        Name qualifierName = astFactory.newName(qualifier);        SimpleName simpleName = astFactory.newSimpleName(name);        QualifiedName annotation = astFactory.newQualifiedName(qualifierName, simpleName);        return annotation;    }    public static boolean annotationMatches(Name annotation, QualifiedName typeToVerify) {        boolean typeMatches = annotation.subtreeMatch(new ASTMatcher(), typeToVerify);        if (!typeMatches) {            annotation.resolveBinding();            if (annotation.resolveTypeBinding() != null && annotation.resolveTypeBinding().getBinaryName() != null) {                typeMatches = annotation.resolveTypeBinding().getBinaryName().equals(typeToVerify.getFullyQualifiedName());            }else{                //Compare names                typeMatches = typeToVerify.getFullyQualifiedName().endsWith("."+annotation.getFullyQualifiedName());            }        }        return typeMatches;    }}