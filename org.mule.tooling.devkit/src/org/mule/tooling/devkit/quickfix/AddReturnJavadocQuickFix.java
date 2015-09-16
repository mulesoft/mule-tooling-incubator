package org.mule.tooling.devkit.quickfix;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class AddReturnJavadocQuickFix extends QuickFix {

    AddReturnJavadocQuickFix(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
    }

    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(errorMarkerStart);

        unit.accept(visitor);

        if (visitor.getNode() != null) {
            AST ast = unit.getAST();
            rewrite = ASTRewrite.create(ast);

            MethodDeclaration method = (MethodDeclaration) visitor.getNode();

            ast = method.getAST();

            @SuppressWarnings("rawtypes")
            List parameters = method.parameters();
            String paramName = "";
            for (Object node : parameters) {
                VariableDeclaration item = (VariableDeclaration) node;
                if (item.getName().getStartPosition() == errorMarkerStart) {
                    paramName = item.getName().toString();
                }
            }

            Javadoc javadoc = method.getJavadoc();
            if (javadoc != null) {
                addJavadocForParam(ast, rewrite, method, paramName, javadoc);
            }
        }
        return rewrite;
    }

    @SuppressWarnings("unchecked")
    private void addJavadocForParam(AST ast, ASTRewrite rewrite, MethodDeclaration method, String paramName, Javadoc javadoc) {
        TagElement newTagElement = ast.newTagElement();
        newTagElement.setTagName(TagElement.TAG_RETURN);
        TextElement comment = ast.newTextElement();
        comment.setText("Return comments");
        newTagElement.fragments().add(comment);
        ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);
        TagElement after = getLastParamTag(javadoc);
        if (after != null && method.getReturnType2().equals(PrimitiveType.VOID)) {
            tagsRewriter.insertBefore(newTagElement, after, null);
        } else {
            tagsRewriter.insertLast(newTagElement, null);
        }
    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
    }

    public static TagElement getLastParamTag(Javadoc javadoc) {

        @SuppressWarnings("rawtypes")
        List tags = javadoc.tags();

        int nTags = tags.size();
        TagElement retVal = null;
        for (int i = 0; i < nTags; i++) {

            TagElement curr = (TagElement) tags.get(i);

            String currName = curr.getTagName();

            if (TagElement.TAG_PARAM.equals(currName)) {
                retVal = curr;
            }
        }
        return retVal;
    }
}
