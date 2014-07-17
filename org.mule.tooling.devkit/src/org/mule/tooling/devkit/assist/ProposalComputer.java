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
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContext;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContextType;
import org.eclipse.jdt.internal.corext.template.java.ImportsResolver;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.ui.text.java.TemplateCompletionProposalComputer;
import org.eclipse.jdt.internal.ui.text.template.contentassist.TemplateEngine;
import org.eclipse.jdt.internal.ui.text.template.contentassist.TemplateProposal;
import org.eclipse.ui.part.FileEditorInput;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.IsAbstract;
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

        LocateNode node = new LocateNode(context.getSelectionOffset());
        obj.accept(node);
        for (ASTNode nodeItem : node.getStackNodes()) {
            System.out.print(nodeItem.getClass().getSimpleName() + ":" + (nodeItem.getParent() == null ? "" : nodeItem.getParent().getNodeType()) + "-");
        }
        System.out.println("");
        ChainASTNodeType verifier = ChainASTNodeFactory.createAtClassVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Connector", context.getSelectionOffset());
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
            if (hasAnnotation.applies()) {
                IsAbstract isAbstract = new IsAbstract();
                obj.accept(isAbstract);
                if (isAbstract.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add Rest Template"));
                    HasAnnotation hasOAuth = new HasAnnotation("OAuth", context.getSelectionOffset());
                    obj.accept(hasOAuth);
                    if (!hasOAuth.applies()) {
                        ICompilationUnit cu = context.getCompilationUnit();
                        IDocument document = getDocument(cu);

                        // You can generate template dynamically here!
                        Template template = new Template("sample", "sample description", "java-members",
                                "${imp:import (java.util.List)}/**\n*${name} ${moduleName}\n*/private ${return_type} ${name}(){\r\n"
                                        + "\tSystem.out.println(\"${name}\")\r\n return null;\r\n" + "}\r\n", true);
                        IRegion region = new Region(context.getSelectionOffset(), 0);
                        JavaContextType contextType2 = new JavaContextType();
                        contextType2.setId("java-members");
                        contextType2.initializeContextTypeResolvers();
                        CompilationUnitContext ctx = contextType2.createContext(document, region.getOffset(), 0, cu);
                        ctx.setVariable("selection", null);

                        ctx.setForceEvaluation(true);
                        TemplateProposal temp = new TemplateProposal(template, ctx, region, null);
                        proposals.add(temp);
                        // proposals.add(new DevkitTemplateProposal("Add OAuth annotation"));
                    }
                } else {
                    proposals.add(new DevkitTemplateProposal("Add NOT Rest Template"));
                }

            }
        }

        verifier = ChainASTNodeFactory.createAtClassBodyVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Connector", context.getSelectionOffset());
            new ASTVisitorDispatcher(ASTNode.COMPILATION_UNIT).dispactch(node.getStackNodes(), hasAnnotation);
            if (hasAnnotation.applies()) {
                proposals.add(new DevkitTemplateProposal("Add New Configurable with Default"));
                proposals.add(new DevkitTemplateProposal("Add New Configurable"));
                proposals.add(new DevkitTemplateProposal("Add New Processor"));
            }
        }
        verifier = ChainASTNodeFactory.createAtFieldVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Configurable", context.getSelectionOffset());
            HasAnnotation hasDefault = new HasAnnotation("Default", context.getSelectionOffset());
            new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasDefault);
            if (!hasAnnotation.applies()) {
                proposals.add(new DevkitTemplateProposal("Add Configurable"));
            } else {
                Negation negation = new Negation();
                negation.addRule(hasDefault);
                if (negation.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add Default"));
                }
            }
        }

        verifier = ChainASTNodeFactory.createAtMethodVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasConnect = new HasAnnotation("Connect", context.getSelectionOffset()).addAnnotation("Disconnect").addAnnotation("ValidateConnection")
                    .addAnnotation("ConnectionIdentifier");
            HasAnnotation hasAnnotation = new HasAnnotation("Processor", context.getSelectionOffset());
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasConnect);

            if (!hasAnnotation.applies() && !(hasConnect.applies())) {
                proposals.add(new DevkitTemplateProposal("Add Processor"));
            }
        }
        verifier = ChainASTNodeFactory.createAtParameterVerifier();
        if (verifier.matches(node.getStackNodes().iterator())) {
            HasAnnotation hasAnnotation = new HasAnnotation("Processor", context.getSelectionOffset());
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            if (hasAnnotation.applies()) {
                HasAnnotation hasOptional = new HasAnnotation("Optional", context.getSelectionOffset());
                new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(node.getStackNodes(), hasOptional);
                if (!hasOptional.applies()) {
                    proposals.add(new DevkitTemplateProposal("Add Optional"));
                    HasAnnotation hasDefault = new HasAnnotation("Default", context.getSelectionOffset());
                    new ASTVisitorDispatcher(ASTNode.SINGLE_VARIABLE_DECLARATION).dispactch(node.getStackNodes(), hasDefault);
                    if (!hasDefault.applies()) {
                        proposals.add(new DevkitTemplateProposal("Add Default"));
                    }
                } else {
                    HasAnnotation hasDefault = new HasAnnotation("Default", context.getSelectionOffset());
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
