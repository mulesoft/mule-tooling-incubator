package org.mule.tooling.devkit.assist.context;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.mule.tooling.devkit.assist.AddAnnotationProposal;
import org.mule.tooling.devkit.assist.DevkitTemplateProposal;
import org.mule.tooling.devkit.assist.rules.ASTVisitorDispatcher;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeFactory;
import org.mule.tooling.devkit.assist.rules.ChainASTNodeType;
import org.mule.tooling.devkit.assist.rules.HasAnnotation;
import org.mule.tooling.devkit.assist.rules.LocateNode;

public class MethodDeclarationContext extends SmartContext {

    public MethodDeclarationContext(IInvocationContext context) {
        super(context);
    }

    @Override
    protected ChainASTNodeType getVerifier() {
        return ChainASTNodeFactory.createAtMethodVerifier();
    }

    @Override
    protected void doAddProposals(List<IJavaCompletionProposal> proposals, LocateNode node) {
        CompilationUnit cu = getCompilationUnit();
        int selectionOffset = getOffset();
        AST ast = AST.newAST(AST.JLS4);
        HasAnnotation hasConnect = new HasAnnotation("Connect", selectionOffset).addAnnotation("Disconnect").addAnnotation("ValidateConnection")
                .addAnnotation("ConnectionIdentifier").addAnnotation("MetaDataKeyRetriever").addAnnotation("MetaDataRetriever");
        HasAnnotation hasAnnotation = new HasAnnotation("Processor", selectionOffset);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
        new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasConnect);

        if (!hasAnnotation.applies() && !(hasConnect.applies())) {
            proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.reconnectOn"));
        }
        if (hasAnnotation.applies()) {
            hasAnnotation = new HasAnnotation("ReconnectOn", selectionOffset);
            new ASTVisitorDispatcher(ASTNode.METHOD_DECLARATION).dispactch(node.getStackNodes(), hasAnnotation);
            if (!hasAnnotation.applies()) {
                //proposals.add(new AddAnnotationProposal("Add ReconnectOn", 0, cu, ast.newQualifiedName(ast.newName("org.mule.api.annotations"), ast.newSimpleName("ReconnectOn"))));
                proposals.add(new DevkitTemplateProposal("org.mule.tooling.devkit.templates.reconnectOn",0,cu));
            }
        }
    }

}
