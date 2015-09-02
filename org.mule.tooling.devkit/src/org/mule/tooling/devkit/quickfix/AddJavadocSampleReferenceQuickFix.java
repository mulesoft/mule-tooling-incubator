package org.mule.tooling.devkit.quickfix;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.mule.devkit.utils.NameUtils;

@SuppressWarnings("restriction")
public class AddJavadocSampleReferenceQuickFix extends QuickFix {

    AddJavadocSampleReferenceQuickFix(String label, ConditionMarkerEvaluator evaluator) {
        super(label, evaluator);
    }

    @Override
    protected ASTRewrite getFix(CompilationUnit unit, Integer errorMarkerStart) {
        ASTRewrite rewrite = null;
        LocateFieldOrMethodVisitor visitor = new LocateFieldOrMethodVisitor(errorMarkerStart);

        unit.accept(visitor);

        LocateModuleNameVisitor nameFinder = new LocateModuleNameVisitor();
        unit.accept(nameFinder);
        String namespace = nameFinder.getValue();
        if (visitor.getNode() != null) {
            AST ast = unit.getAST();
            rewrite = ASTRewrite.create(ast);

            MethodDeclaration method = (MethodDeclaration) visitor.getNode();

            ast = method.getAST();

            Javadoc javadoc = method.getJavadoc();
            if (javadoc != null) {
                addJavadocForParam(ast, rewrite, method, namespace, javadoc);
            }
        }
        return rewrite;
    }

    private void addJavadocForParam(AST ast, ASTRewrite rewrite, MethodDeclaration method, String namespace, Javadoc javadoc) {

        TextElement comment = ast.newTextElement();
        String defaultNamespace = "${namespace}";
        if (!namespace.isEmpty()) {
            defaultNamespace = namespace;
        }
        comment.setText("\n{@sample.xml ../../../doc/" + defaultNamespace + "-connector.xml.sample " + defaultNamespace + ":" + NameUtils.uncamel(method.getName().toString())
                + "}");

        ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);
        tagsRewriter.insertAt(comment, 1, null);

    }

    @Override
    public Image getImage() {
        return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
    }

}
