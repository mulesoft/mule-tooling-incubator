package org.mule.tooling.devkit.assist.rules;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;

public class ChainASTNodeFactory {

    private static Map<Integer, ChainASTNodeType> cache = new HashMap<Integer, ChainASTNodeType>();

    public static ChainASTNodeType createAtClassVerifier() {
        ChainASTNodeType root = getCompilationUnitVerifier();
        ChainASTNodeType typeDeclaration = getTypeDeclarationVerifier();
        ChainASTNodeType name = getSimpleNameVerifier();
        root.setNext(typeDeclaration);
        typeDeclaration.setNext(name);
        return root;
    }

    public static ChainASTNodeType createAtClassBodyVerifier() {
        ChainASTNodeType root = getCompilationUnitVerifier();
        ChainASTNodeType typeDeclaration = getTypeDeclarationVerifier();
        root.setNext(typeDeclaration);
        return root;
    }

    public static ChainASTNodeType createAtFieldVerifier() {
        ChainASTNodeType root = getCompilationUnitVerifier();
        ChainASTNodeType typeDeclaration = getTypeDeclarationVerifier();
        ChainASTNodeType fieldDeclaration = getFieldDeclarationVerifier();
        ChainASTNodeType variableDeclarationFragment = getVariableDeclarationFragmentDeclarationVerifier();
        ChainASTNodeType name = getSimpleNameVerifier();
        root.setNext(typeDeclaration);
        typeDeclaration.setNext(fieldDeclaration);
        fieldDeclaration.setNext(variableDeclarationFragment);
        variableDeclarationFragment.setNext(name);
        return root;
    }

    public static ChainASTNodeType createAtMethodVerifier() {
        ChainASTNodeType root = getCompilationUnitVerifier();
        ChainASTNodeType typeDeclaration = getTypeDeclarationVerifier();
        ChainASTNodeType methodDeclaration = getMethodDeclarationVerifier();
        ChainASTNodeType simpleName = getSimpleNameVerifier();
        root.setNext(typeDeclaration);
        typeDeclaration.setNext(methodDeclaration);
        methodDeclaration.setNext(simpleName);
        return root;
    }

    public static ChainASTNodeType createAtParameterVerifier() {
        ChainASTNodeType root = getCompilationUnitVerifier();
        ChainASTNodeType typeDeclaration = getTypeDeclarationVerifier();
        ChainASTNodeType methodDeclaration = getMethodDeclarationVerifier();
        ChainASTNodeType singleVariableDeclaration = getSingleVariableDeclarationVerifier();
        ChainASTNodeType simpleName = getSimpleNameVerifier();
        root.setNext(typeDeclaration);
        typeDeclaration.setNext(methodDeclaration);
        methodDeclaration.setNext(singleVariableDeclaration);
        singleVariableDeclaration.setNext(simpleName);
        return root;
    }

    private static ChainASTNodeType getVerifier(int type) {
        ChainASTNodeType node = cache.get(type);
        if (node == null) {
            node = new ChainASTNodeType();
            node.setAstNodeType(type);
        }
        return node;
    }

    private static ChainASTNodeType getVariableDeclarationFragmentDeclarationVerifier() {
        return getVerifier(ASTNode.VARIABLE_DECLARATION_FRAGMENT);
    }

    private static ChainASTNodeType getFieldDeclarationVerifier() {
        return getVerifier(ASTNode.FIELD_DECLARATION);
    }

    private static ChainASTNodeType getSimpleNameVerifier() {
        return getVerifier(ASTNode.SIMPLE_NAME);
    }

    private static ChainASTNodeType getTypeDeclarationVerifier() {
        return getVerifier(ASTNode.TYPE_DECLARATION);
    }

    private static ChainASTNodeType getCompilationUnitVerifier() {
        return getVerifier(ASTNode.COMPILATION_UNIT);
    }

    private static ChainASTNodeType getMethodDeclarationVerifier() {
        return getVerifier(ASTNode.METHOD_DECLARATION);
    }

    private static ChainASTNodeType getSingleVariableDeclarationVerifier() {
        return getVerifier(ASTNode.SINGLE_VARIABLE_DECLARATION);
    }
}
