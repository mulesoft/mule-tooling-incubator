package org.mule.tooling.devkit.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.ui.part.FileEditorInput;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.ExistsMethodWithAnnotation;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.IsAbstract;
import org.mule.tooling.devkit.assist.rules.IsFieldType;
import org.mule.tooling.devkit.assist.rules.LocateNode;
import org.mule.tooling.devkit.assist.rules.Negation;

/**
 * A computer wrapper for the hippie processor.
 * 
 * @since 3.2
 */
public final class ProposalComputer implements IQuickAssistProcessor {

    public ProposalComputer() {
        // fEngine= new TemplateCompletionProposalComputer();
    }

    @Override
    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return false;
    }

    @Override
    public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
        CompilationUnit obj = context.getASTRoot();
        List<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
        // List<ASTRule> rules = new ArrayList<ASTRule>();
        // rules.add(new IsAbstract());
        // rules.add(new HasAnnotation("Connector", context.getSelectionOffset()));
        // boolean applies = true;
        //
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (applies) {
        // proposals.add(new DevkitTemplateProposal("Abstract Connector"));
        // }
        // rules = new ArrayList<ASTRule>();
        // rules.add(new IsAbstract());
        // applies = true;
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (applies) {
        // proposals.add(new DevkitTemplateProposal("Just Abstract"));
        // }
        //
        // rules = new ArrayList<ASTRule>();
        // rules.add(new HasAnnotation("Connector", context.getSelectionOffset()));
        // applies = true;
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (applies) {
        // proposals.add(new DevkitTemplateProposal("Has Connector"));
        // }
        // rules = new ArrayList<ASTRule>();
        // rules.add(new HasAnnotation("Configurable", context.getSelectionOffset()));
        // applies = true;
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (applies) {
        // proposals.add(new DevkitTemplateProposal("Has Configurable"));
        // }
        // rules = new ArrayList<ASTRule>();
        // Negation negation = new Negation();
        // IsAbstract temp = new IsAbstract();
        // negation.addRule(temp);
        // rules.add(temp);
        // applies = true;
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (negation.applies()) {
        // proposals.add(new DevkitTemplateProposal("Not Abstract"));
        // }
        //
        // rules = new ArrayList<ASTRule>();
        // rules.add(new HasAnnotation("Processor", context.getSelectionOffset()));
        // applies = true;
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (applies) {
        // proposals.add(new DevkitTemplateProposal("Has Processor"));
        // }
        // rules = new ArrayList<ASTRule>();
        // rules.add(new HasAnnotation("Optional", context.getSelectionOffset()));
        // applies = true;
        // for (ASTRule rule : rules) {
        // obj.accept(rule);
        // applies &= rule.applies();
        // }
        // if (applies) {
        // proposals.add(new DevkitTemplateProposal("Has Optional"));
        // }

        int selectionOffset = context.getSelectionOffset();
        LocateNode node = new LocateNode(selectionOffset);
        obj.accept(node);
        for (ASTNode nodeItem : node.getStackNodes()) {
            System.out.print(nodeItem.getClass().getSimpleName() + ":" + (nodeItem.getParent() == null ? "" : nodeItem.getParent().getNodeType()) + "-");
        }
        System.out.println("");
        ChainASTNodeType verifier = ChainASTNodeFactory.createAtClassVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Connector", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
            if (hasAnnotation.applies()) {
                IsAbstract isAbstract = new IsAbstract();
                obj.accept(isAbstract);
                if (isAbstract.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add Rest Template"));
                    HasAnnotation hasOAuth = new HasAnnotation("OAuth", selectionOffset);
                    obj.accept(hasOAuth);
                    if (!hasOAuth.applies()) {
                        proposals.add(new DevkitTemplateProposal("Add OAuth annotation"));
                    }
                } else {
                    proposals.add(new DevkitTemplateProposal("Add NOT Rest Template"));
                }

            }
        }

        verifier = ChainASTNodeFactory.createAtClassBodyVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Connector", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
            if (hasAnnotation.applies()) {
                proposals.add(new DevkitTemplateProposal("Add New Configurable", 9));
                proposals.add(new DevkitTemplateProposal("Add New Processor", 10));
                proposals.add(new DevkitTemplateProposal("Add New Source", 6));
                proposals.add(new DevkitTemplateProposal("Add New Transformer", 7));
                ExistsMethodWithAnnotation methodCheck = new ExistsMethodWithAnnotation("MetaDataKeyRetriever");
                new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                if (!methodCheck.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add MetaDataKeyRetriever", 5));
                } else {
                    methodCheck = new ExistsMethodWithAnnotation("MetaDataRetriever");
                    new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                    if (!methodCheck.applies()) {
                        proposals.add(new DevkitTemplateProposal("Add MetaDataRetriever", 9));
                    } else {
                        methodCheck = new ExistsMethodWithAnnotation("QueryTranslator");
                        new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), methodCheck);
                        if (!methodCheck.applies()) {
                            proposals.add(new DevkitTemplateProposal("Add QueryTranslator", 9));
                        }
                    }

                }
            }
        }
        verifier = ChainASTNodeFactory.createAtFieldVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Configurable", selectionOffset);
            HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
            HasAnnotation hasInject = new HasAnnotation("Inject", selectionOffset);
            IsFieldType inyectable = new IsFieldType("MuleContext",selectionOffset);
            new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasDefault);
            new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasInject);
            new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), inyectable);
            if (!hasAnnotation.applies()) {
                proposals.add(new DevkitTemplateProposal("Add Configurable"));
            } else {
                Negation negation = new Negation();
                negation.addRule(hasDefault);
                if (negation.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add Default"));
                }
            }
            if(!hasInject.applies() && inyectable.applies()){
                proposals.add(new DevkitTemplateProposal("Add Inject"));
            }
        }

        verifier = ChainASTNodeFactory.createAtMethodVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasConnect = new HasAnnotation("Connect", selectionOffset).addAnnotation("Disconnect").addAnnotation("ValidateConnection")
                    .addAnnotation("ConnectionIdentifier");
            HasAnnotation hasAnnotation = new HasAnnotation("Processor", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasConnect);

            if (!hasAnnotation.applies() && !(hasConnect.applies())) {
                proposals.add(new DevkitTemplateProposal("Add Processor"));
            }
            if (hasAnnotation.applies()) {
                hasAnnotation = new HasAnnotation("ReconnectOn", selectionOffset);
                new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
                if (!hasAnnotation.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add ReconnectOn"));
                }
            }
        }
        verifier = ChainASTNodeFactory.createAtParameterVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Processor", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            if (hasAnnotation.applies()) {
                HasAnnotation hasOptional = new HasAnnotation("Optional", selectionOffset);
                new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(node.getStackNodes(), hasOptional);
                if (!hasOptional.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add Optional"));
                    HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
                    new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(node.getStackNodes(), hasDefault);
                    if (!hasDefault.applies()) {
                        proposals.add(new DevkitTemplateProposal("Add Default"));
                    }
                } else {
                    HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
                    new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(node.getStackNodes(), hasDefault);
                    if (!hasDefault.applies()) {
                        proposals.add(new DevkitTemplateProposal("Add Default"));
                    }
                }
            }
        }
        return proposals.toArray(new IJavaCompletionProposal[proposals.size()]);
    }

    private IDocument getDocument(ICompilationUnit cu) throws JavaModelException {
        IFile file = (IFile) cu.getResource();
        IDocument document = JavaUI.getDocumentProvider().getDocument(new FileEditorInput(file));
        if (document == null) {
            return new Document(cu.getSource()); // only used by test cases
        }
        return document;
    }
}
