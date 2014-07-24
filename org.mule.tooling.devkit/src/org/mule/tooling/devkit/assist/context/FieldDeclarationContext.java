package org.mule.tooling.devkit.assist.context;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.AddAnnotationProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.IsFieldType;
import org.mule.tooling.devkit.assist.rules.LocateNode;
import org.mule.tooling.devkit.assist.rules.Negation;

public class FieldDeclarationContext extends SmartContext {

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtFieldVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node, CompilationUnit cu, int selectionOffset) {
        HasAnnotation hasAnnotation = new HasAnnotation("Configurable", selectionOffset);
        HasAnnotation hasDefault = new HasAnnotation("Default", selectionOffset);
        HasAnnotation hasInject = new HasAnnotation("Inject", selectionOffset);
        IsFieldType inyectable = new IsFieldType("MuleContext", selectionOffset).addType("ObjectStoreManager").addType("ObjectStore").addType("TransactionManager")
                .addType("QueueManager").addType("MuleConfiguration").addType("LifecycleManager").addType("ClassLoader").addType("ExpressionManager").addType(" EndpointFactory")
                .addType("MuleClient").addType("SystemExceptionHandler").addType("SecurityManager").addType(" WorkManager").addType("Registry").addType("MuleRegistry");

        AST ast = AST.newAST(AST.JLS4);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasDefault);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), hasInject);
        new ASTVisitorDispatcher(ASTNode.FIELD_DECLARATION).dispactch(node.getStackNodes(), inyectable);
        if (!hasAnnotation.applies() && !hasInject.applies() && !inyectable.applies()) {
            proposals.add(new AddAnnotationProposal("Add Configurable", 0, cu, ast.newQualifiedName(ast.newName("org.mule.api.annotations"), ast.newSimpleName("Configurable"))));
        } else {
            Negation negation = new Negation();
            negation.addRule(hasDefault);
            if (negation.applies() && !hasInject.applies() && !inyectable.applies()) {
                proposals.add(new DevkitTemplateProposal("Add Default", 0, cu));
            }
        }
        if (!hasInject.applies() && inyectable.applies()) {
            //TODO: Check ASTRewriteCorrectionProposal rewriteProposal = new ASTRewriteCorrectionProposal();
            proposals.add(new AddAnnotationProposal("Add Inject", 0, cu, ast.newQualifiedName(ast.newName("javax.inject"), ast.newSimpleName("Inject"))));
        }
    }

}
