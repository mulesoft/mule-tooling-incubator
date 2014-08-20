package org.mule.tooling.devkit.assist;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MalformedTreeException;

@SuppressWarnings("restriction")
public class AddAnnotationProposal implements IJavaCompletionProposal, ICompletionProposalExtension2, ICompletionProposalExtension3, ICompletionProposalExtension4,
        ICompletionProposalExtension6 {

    private final String label;
    int relevance;
    IInvocationContext context;
    final QualifiedName annotation;

    public AddAnnotationProposal(String label, int relevance, IInvocationContext context, QualifiedName annotation) {
        this.label = label;
        this.relevance = relevance;
        this.context = context;
        this.annotation = annotation;
    }

    @Override
    public void apply(IDocument document) {

    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return "Aditiona Info";
    }

    @Override
    public String getDisplayString() {
        return label;
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ANNOTATION);
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString ss = new StyledString();
        ss.append(label);
        // ss.append("( Templa )", StyledString.COUNTER_STYLER);
        return ss;
    }

    @Override
    public boolean isAutoInsertable() {
        return true;
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        return null;
    }

    @Override
    public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
        return null;
    }

    @Override
    public int getPrefixCompletionStart(IDocument document, int completionOffset) {
        return 0;
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        ASTNode node = context.getCoveringNode();
        if (context.getASTRoot() != null) {
            if (node != null) {
                AST ast = context.getASTRoot().getAST();

                ASTRewrite rewrite = ASTRewrite.create(ast);
                populateRewrite(node, ast, rewrite);
                try {
                    rewrite.rewriteAST(viewer.getDocument(), null).apply(viewer.getDocument());
                } catch (MalformedTreeException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void populateRewrite(ASTNode node, AST ast, ASTRewrite rewrite) {
        if (node.getParent() instanceof MethodDeclaration) {

            NormalAnnotation normalAnnotation = ast.newNormalAnnotation();
            normalAnnotation.setTypeName(ast.newName(annotation.getName().toString()));
            MemberValuePair memberValuePair = ast.newMemberValuePair();
            memberValuePair.setName(ast.newSimpleName("exceptions"));
            TypeLiteral value = ast.newTypeLiteral();
            SimpleType arrayType = ast.newSimpleType(ast.newName("Exception"));
            value.setType(arrayType);
            memberValuePair.setValue(value);
            normalAnnotation.values().add(memberValuePair);

            ListRewrite annotations = rewrite.getListRewrite(node.getParent(), MethodDeclaration.MODIFIERS2_PROPERTY);
            annotations.insertFirst(normalAnnotation, null);
        }
        addImportIfRequired(context.getASTRoot(), rewrite, annotation.getFullyQualifiedName());
    }

    @Override
    public void selected(ITextViewer viewer, boolean smartToggle) {

    }

    @Override
    public void unselected(ITextViewer viewer) {

    }

    @Override
    public boolean validate(IDocument document, int offset, DocumentEvent event) {
        return false;
    }

    @Override
    public int getRelevance() {
        return relevance;
    }

    protected void addImportIfRequired(CompilationUnit compilationUnit, ASTRewrite rewrite, String fullyQualifiedName) {
        AST ast = compilationUnit.getAST();
        boolean hasConnectorAnnotationImport = false;

        ListRewrite listImports = rewrite.getListRewrite(compilationUnit, CompilationUnit.IMPORTS_PROPERTY);

        for (Object obj : compilationUnit.imports()) {
            ImportDeclaration importDec = (ImportDeclaration) obj;
            if (importDec.getName().getFullyQualifiedName().equals(fullyQualifiedName)) {
                hasConnectorAnnotationImport = true;
            }
        }

        ImportDeclaration id = null;

        if (!hasConnectorAnnotationImport) {
            id = ast.newImportDeclaration();
            id.setName(ast.newName(fullyQualifiedName));
            listImports.insertLast(id, null);
        }
    }
}
