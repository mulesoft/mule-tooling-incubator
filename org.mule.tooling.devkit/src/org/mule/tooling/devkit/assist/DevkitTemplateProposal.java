package org.mule.tooling.devkit.assist;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.InclusivePositionUpdater;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IEditorPart;
import org.mule.tooling.devkit.DevkitUIPlugin;

@SuppressWarnings("restriction")
public class DevkitTemplateProposal implements IJavaCompletionProposal, ICompletionProposalExtension2, ICompletionProposalExtension3, ICompletionProposalExtension4,
        ICompletionProposalExtension6 {

    private Template fTemplate;
    private TemplateContext fContext;
    private String label;
    int relevance;
    CompilationUnit compilationUnit;
    private InclusivePositionUpdater fUpdater;

    public DevkitTemplateProposal(String label) {
        this.label = label;
        relevance = 0;
        compilationUnit = null;

    }

    public DevkitTemplateProposal(String label, int relevance) {
        this.label = label;
        this.relevance = relevance;
        compilationUnit = null;
    }

    public DevkitTemplateProposal(String label, int relevance, CompilationUnit unit) {
        this.label = label;
        this.relevance = relevance;
        compilationUnit = unit;
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
        String templateString = "${imp:import (org.mule.api.annotations.Processor,org.mule.api.annotations.Optional)}\n\t/**" + "\n" + "\t * Description for PUTO  ${method}"
                + "\n" + "\t *" + "\n" + "\t * {@sample.xml ../../../doc/${moduleName}-connector.xml.sample ${moduleName}:${method}" + "\n" + "\t *" + "\n" + "\t */" + "\n"
                + "\t@Processor\n" + "\tpublic void ${method}(){" + "\n" + "\t\t//TODO${cursor}" + "\n" + "\t}" + "\n";
        IDocument document = viewer.getDocument();
        TemplateContextType contextType = new TemplateContextType("java");
        DevkitVariableResolver resolver = new DevkitVariableResolver();
        ImportResolver importResolver = new ImportResolver();
        importResolver.setType("import");
        importResolver.setCompilationUnit(compilationUnit);
        resolver.setCompilationUnit(compilationUnit);
        resolver.setType("moduleName");
        importResolver.setDocument(viewer.getDocument());
        contextType.addResolver(resolver);
        contextType.addResolver(new GlobalTemplateVariables.Cursor());
        contextType.addResolver(importResolver);
        fContext = new DocumentTemplateContext(contextType, viewer.getDocument(), offset, 0);

        fTemplate = new Template("Dummy", "My Description", "java", templateString, true);
        fContext.setReadOnly(false);
        int start;
        TemplateBuffer templateBuffer = null;
        try {
            beginCompoundChange(viewer);
            int importOffset = 0;
            // AST ast = compilationUnit.getAST();
            // ASTRewrite rewrite = ASTRewrite.create(ast);
            // if (addImportIfRequired(compilationUnit, rewrite, "org.mule.api.annotations.Processor")) {
            // importOffset = "org.mule.api.annotations.Processor".length() + "import ; ".length();
            // try {
            // rewrite.rewriteAST(viewer.getDocument(), null).apply(viewer.getDocument());
            // } catch (MalformedTreeException e) {
            // e.printStackTrace();
            // } catch (IllegalArgumentException e) {
            // e.printStackTrace();
            // } catch (BadLocationException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }

            try {
                templateBuffer = fContext.evaluate(fTemplate);
            } catch (TemplateException e1) {
                return;
            } catch (BadLocationException e) {
                DevkitUIPlugin.getDefault().getLog().log(new Status(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, Status.OK, "TemplateError", e));
            }
            importOffset = importResolver.getOffset();
            offset += importOffset;
            int oldReplaceOffset = getReplaceOffset();
            oldReplaceOffset += importOffset;
            // Map options = compilationUnit.getJavaElement().getJavaProject().getOptions(true);
            // CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

            start = offset;
            int shift = start - oldReplaceOffset;
            int end = Math.max(getReplaceEndOffset(), offset + shift);

            // insert template string
            if (end > document.getLength())
                end = offset;
            templateString = templateBuffer.getString();
            document.replace(start, end - start, templateString);
            // TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, document.get(), start, templateString.length(), 0, System.getProperty("line.separator"));
            //
            // try {
            // if (textEdit != null) {
            // textEdit.apply(document);
            // System.out.println(textEdit);
            // String value=document.get(start, textEdit.getLength());
            // System.out.println(value);
            // }
            // } catch (MalformedTreeException e2) {
            // e2.printStackTrace();
            // } catch (BadLocationException e2) {
            // e2.printStackTrace();
            // }

            // translate positions
            LinkedModeModel model = new LinkedModeModel();
            TemplateVariable[] variables = templateBuffer.getVariables();

            boolean hasPositions = false;
            for (int i = 0; i != variables.length; i++) {
                TemplateVariable variable = variables[i];

                if (variable.isUnambiguous())
                    continue;

                LinkedPositionGroup group = new LinkedPositionGroup();

                int[] offsets = variable.getOffsets();
                int length = variable.getLength();

                LinkedPosition first;
                {
                    String[] values = variable.getValues();
                    ICompletionProposal[] proposals = new ICompletionProposal[values.length];
                    for (int j = 0; j < values.length; j++) {
                        ensurePositionCategoryInstalled(document, model);
                        Position pos = new Position(offsets[0] + start, length);
                        document.addPosition(getCategory(), pos);
                        proposals[j] = new PositionBasedCompletionProposal(values[j], pos, length);
                    }

                    if (proposals.length > 1)
                        first = new ProposalPosition(document, offsets[0] + start, length, proposals);
                    else
                        first = new LinkedPosition(document, offsets[0] + start, length);
                }

                for (int j = 0; j != offsets.length; j++)
                    if (j == 0)
                        group.addPosition(first);
                    else
                        group.addPosition(new LinkedPosition(document, offsets[j] + start, length));

                model.addGroup(group);
                hasPositions = true;
            }

            if (hasPositions) {
                model.forceInstall();
                JavaEditor editor = getJavaEditor();
                if (editor != null) {
                    model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
                }

                LinkedModeUI ui = new org.eclipse.ui.texteditor.link.EditorLinkedModeUI(model, viewer);
                ui.setExitPosition(viewer, getCaretOffset(templateBuffer) + start, 0, Integer.MAX_VALUE);
                ui.enter();

            }

        } catch (BadLocationException e) {
            DevkitUIPlugin.getDefault().getLog().log(new Status(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, Status.OK, "TemplateError", e));
        } catch (BadPositionCategoryException e) {
            DevkitUIPlugin.getDefault().getLog().log(new Status(Status.ERROR, DevkitUIPlugin.PLUGIN_ID, Status.OK, "TemplateError", e));
        } finally {
            endCompoundChange(viewer);
        }

        // if (compilationUnit != null) {
        // LocateNode visitor = new LocateNode(offset);
        // compilationUnit.accept(visitor);
        // if (visitor.getNode() != null) {
        // AST ast = compilationUnit.getAST();
        //
        // ASTRewrite rewrite = ASTRewrite.create(ast);
        // FieldDeclaration field = (FieldDeclaration) ((VariableDeclarationFragment) ((SimpleName) visitor.getNode()).getParent()).getParent();
        // NormalAnnotation replacement = ast.newNormalAnnotation();
        // replacement.setTypeName(ast.newName("Default"));
        // MemberValuePair valuePair = ast.newMemberValuePair();
        //
        // StringLiteral literal = ast.newStringLiteral();
        // literal.setLiteralValue("value");
        // valuePair.setValue(literal);
        // ListRewrite values = rewrite.getListRewrite(replacement, NormalAnnotation.VALUES_PROPERTY);
        // values.insertFirst(literal, null);
        //
        // ListRewrite annotations = rewrite.getListRewrite(field, FieldDeclaration.MODIFIERS2_PROPERTY);
        // annotations.insertFirst(replacement, null);
        // addImportIfRequired(compilationUnit, rewrite, "org.mule.api.annotations.param.Default");
        // try {
        // rewrite.rewriteAST(viewer.getDocument(), null).apply(viewer.getDocument());
        // } catch (MalformedTreeException e) {
        // e.printStackTrace();
        // } catch (IllegalArgumentException e) {
        // e.printStackTrace();
        // } catch (BadLocationException e) {
        // e.printStackTrace();
        // }
        // }
        // }
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

    protected boolean addImportIfRequired(CompilationUnit compilationUnit, ASTRewrite rewrite, String fullyQualifiedName) {
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
            return true;
        }
        return false;
    }

    private void endCompoundChange(ITextViewer viewer) {
        if (viewer instanceof ITextViewerExtension) {
            ITextViewerExtension extension = (ITextViewerExtension) viewer;
            IRewriteTarget target = extension.getRewriteTarget();
            target.endCompoundChange();
        }
    }

    private void beginCompoundChange(ITextViewer viewer) {
        if (viewer instanceof ITextViewerExtension) {
            ITextViewerExtension extension = (ITextViewerExtension) viewer;
            IRewriteTarget target = extension.getRewriteTarget();
            target.beginCompoundChange();
        }
    }

    protected final int getReplaceEndOffset() {
        int end = 0;
        if (fContext instanceof DocumentTemplateContext) {
            DocumentTemplateContext docContext = (DocumentTemplateContext) fContext;
            end = docContext.getEnd();
        }
        return end;
    }

    protected final int getReplaceOffset() {
        int start = 0;
        if (fContext instanceof DocumentTemplateContext) {
            DocumentTemplateContext docContext = (DocumentTemplateContext) fContext;
            start = docContext.getStart();
        }
        return start;
    }

    private String getCategory() {
        return "TemplateProposalCategory_" + toString(); //$NON-NLS-1$
    }

    private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model) {
        if (!document.containsPositionCategory(getCategory())) {
            document.addPositionCategory(getCategory());
            fUpdater = new InclusivePositionUpdater(getCategory());
            document.addPositionUpdater(fUpdater);

            model.addLinkingListener(new ILinkedModeListener() {

                /*
                 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
                 */
                public void left(LinkedModeModel environment, int flags) {
                    ensurePositionCategoryRemoved(document);
                }

                public void suspend(LinkedModeModel environment) {
                }

                public void resume(LinkedModeModel environment, int flags) {
                }
            });
        }
    }

    private void ensurePositionCategoryRemoved(IDocument document) {
        if (document.containsPositionCategory(getCategory())) {
            try {
                document.removePositionCategory(getCategory());
            } catch (BadPositionCategoryException e) {
                // ignore
            }
            document.removePositionUpdater(fUpdater);
        }
    }

    private JavaEditor getJavaEditor() {
        IEditorPart part = JavaPlugin.getActivePage().getActiveEditor();
        if (part instanceof JavaEditor)
            return (JavaEditor) part;
        else
            return null;
    }

    private int getCaretOffset(TemplateBuffer buffer) {

        TemplateVariable[] variables = buffer.getVariables();
        for (int i = 0; i != variables.length; i++) {
            TemplateVariable variable = variables[i];
            if (variable.getType().equals(GlobalTemplateVariables.Cursor.NAME))
                return variable.getOffsets()[0];
        }

        return buffer.getString().length();
    }
}
