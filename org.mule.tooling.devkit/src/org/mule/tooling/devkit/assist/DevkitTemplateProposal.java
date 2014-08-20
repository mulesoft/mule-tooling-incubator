package org.mule.tooling.devkit.assist;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
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
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.mule.tooling.devkit.ASTUtils;
import org.mule.tooling.devkit.DevkitUIPlugin;

@SuppressWarnings("restriction")
public class DevkitTemplateProposal implements IJavaCompletionProposal, ICompletionProposalExtension2, ICompletionProposalExtension3, ICompletionProposalExtension4,
        ICompletionProposalExtension6 {

    private Template fTemplate;
    private TemplateContext fContext;
    int relevance;
    IInvocationContext context;
    private InclusivePositionUpdater fUpdater;

    public DevkitTemplateProposal(String id) {
        this(id, 0, null);

    }

    public DevkitTemplateProposal(String id, int relevance) {
        this(id, relevance, null);
    }

    public DevkitTemplateProposal(String id, int relevance, IInvocationContext context) {
        this.relevance = relevance;
        this.context = context;
        fTemplate = DevkitUIPlugin.getDefault().getCodeTemplateStore().findTemplateById(id);
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
        if (fTemplate == null) {
            return "Fixmee";
        }
        return fTemplate.getDescription();
    }

    @Override
    public String getDisplayString() {
        if (fTemplate == null) {
            return "Fixmee";
        }
        return fTemplate.getName();
    }

    @Override
    public Image getImage() {
        return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_ANNOTATION);
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString ss = new StyledString();
        if (fTemplate != null) {
            ss.append(fTemplate.getDescription());
        } else {
            ss.append("FIXME");
        }
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {

        if (context.getCoveringNode() != null) {

            if (context.getCoveringNode().getParent().getParent() instanceof BodyDeclaration) {
                BodyDeclaration node = (BodyDeclaration) context.getCoveringNode().getParent().getParent();
                List<ASTNode> fragments = node.modifiers();
                for (ASTNode obj : fragments) {
                    if (obj instanceof Modifier) {
                        offset = obj.getStartPosition();
                        break;
                    }
                }
            }
            if (context.getCoveringNode().getParent() instanceof VariableDeclaration && context.getCoveringNode().getParent().getParent() instanceof MethodDeclaration) {
                VariableDeclaration var = (VariableDeclaration) context.getCoveringNode().getParent();
                offset = var.getStartPosition();
            }
            if (context.getCoveringNode().getParent() instanceof MethodDeclaration) {
                MethodDeclaration node = (MethodDeclaration) context.getCoveringNode().getParent();
                List<ASTNode> fragments = node.modifiers();
                for (ASTNode obj : fragments) {
                    if (obj instanceof Modifier) {
                        offset = obj.getStartPosition();
                        break;
                    }
                }
            }
        }
        TemplateContextType contextType = DevkitUIPlugin.getDefault().getTemplateContextRegistry().getContextType(fTemplate.getContextTypeId());
        String templateString = "";
        IDocument document = viewer.getDocument();
        Iterator it = contextType.resolvers();
        ImportResolver importResolver = null;
        while (it.hasNext()) {
            Object resolver = it.next();
            if (resolver instanceof ImportResolver) {
                importResolver = (ImportResolver) resolver;
                importResolver.setDocument(document);
                importResolver.setCompilationUnit(context.getASTRoot());
            }
            if (resolver instanceof DevkitVariableResolver) {
                ((DevkitVariableResolver) resolver).setCompilationUnit(context.getASTRoot());
            }
        }
        fContext = new DocumentTemplateContext(contextType, viewer.getDocument(), offset, 0);

        fContext.setReadOnly(false);
        int start;
        TemplateBuffer templateBuffer = null;
        try {
            beginCompoundChange(viewer);
            int importOffset = 0;
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
            NodeFinder finder = new NodeFinder(ASTUtils.parse(context.getCompilationUnit()), offset, 0);
            ASTNode node=finder.getCoveringNode();
            // insert template string
            if (end > document.getLength())
                end = offset;
            if(node.getStartPosition()!=offset){
                offset=node.getStartPosition();
            }
            templateString = templateBuffer.getString();
            document.replace(start, end - start, templateString);

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
