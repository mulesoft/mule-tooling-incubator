package org.mule.tooling.devkit.ui.wsdl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.devkit.ui.SelectWSDLDialog;
import org.mule.tooling.ui.utils.UiUtils;

public class WSDLChooserGroup {

    private static final String EDITOR_TITLE = "WSDL Files";
    private static final String DELETE_ALL_CONFIRMATION_DIALOG_QUESTION = "Are you sure you want to delete all WSDL's?";
    private static final String DELETE_ALL_CONFIRMATION_DIALOG_TITLE = "Remove all WSDL's";

    private static final String ADD_PARAMETER_BUTTON_LABEL = "Add WSDL";
    private static final String EMPTY_PARAMETERS_NOTIFICATION = "Click in the button below to add a WSDL";

    private ScrolledComposite parametersListWrapper;
    private Composite parametersListWrapperContent;
    private List<WsdlRowEntry> parameters;
    private Composite parametersGroup;
    private Composite buttonsWrapper;
    private ToolBar parametersToolbar;
    private Composite emptyCanvasComposite;

    public void createControl(Composite parent) {
        parameters = new ArrayList<WsdlRowEntry>();

        parametersGroup = UiUtils.createGroupWithTitle(parent, "", 2);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(parametersGroup);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).applyTo(parametersGroup);

        createTitle(parametersGroup);

        parametersListWrapper = new ScrolledComposite(parametersGroup, SWT.V_SCROLL);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(parametersListWrapper);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).applyTo(parametersListWrapper);

        GridLayoutFactory.fillDefaults().applyTo(parametersListWrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(parametersListWrapper);

        parametersListWrapperContent = new Composite(parametersListWrapper, SWT.FILL);

        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(parametersListWrapperContent);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, true).applyTo(parametersListWrapperContent);
        createEmptyCanvasInformation();

        buttonsWrapper = new Composite(parametersGroup, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(buttonsWrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonsWrapper);

        Button addParameterButton = new Button(buttonsWrapper, SWT.BUTTON2);
        addParameterButton.setText(getAddParameterButtonLabel());
        addParameterButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                SelectWSDLDialog dialog = new SelectWSDLDialog(Display.getCurrent().getActiveShell());
                dialog.create();
                if (dialog.open() == Window.OK) {
                    addParameter(dialog.getWsdlLocation());
                }
            }
        });
        parametersListWrapper.setMinSize(50, 100);
        parametersListWrapper.setContent(parametersListWrapperContent);
        parametersListWrapper.setExpandHorizontal(true);
        parametersListWrapper.setExpandVertical(true);
        // recalculateEditorSize();
    }

    private void createTitle(final Composite parent) {

        final Composite titleWrapper = new Composite(parent, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).extendedMargins(5, 5, 5, 0).applyTo(titleWrapper);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(titleWrapper);

        Label titleLabel = new Label(titleWrapper, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).grab(true, false).applyTo(titleLabel);
        titleLabel.setText(getEditorTitle());

        parametersToolbar = new ToolBar(titleWrapper, SWT.FILL);
        ToolBarManager parametersToolbarManager = new ToolBarManager(parametersToolbar);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(parametersToolbar);
        parametersToolbarManager.add(new RemoveAllParametersAction());

        parametersToolbarManager.update(true);

        Label separatorLabel = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().applyTo(separatorLabel);
        separatorLabel.setEnabled(false);
    }

    protected void clearParameters() {
        for (Control control : parametersListWrapperContent.getChildren()) {
            if (!control.isDisposed()) {
                control.dispose();
            }
        }
        parameters.clear();

        createEmptyCanvasInformation();

        recalculateEditorSize();

    }

    private void createEmptyCanvasInformation() {

        emptyCanvasComposite = new Composite(parametersListWrapperContent, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).extendedMargins(5, 5, 5, 5).applyTo(emptyCanvasComposite);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(emptyCanvasComposite);

        Label emptyCanvasLabel = new Label(emptyCanvasComposite, SWT.CENTER);
        emptyCanvasLabel.setText(getEmptyParametersMessage());
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).applyTo(emptyCanvasLabel);

        parametersToolbar.setEnabled(false);
    }

    private class RemoveAllParametersAction extends org.eclipse.jface.action.Action {

        public RemoveAllParametersAction() {
            setId(getDeleteAllConfirmationTitle());
            setToolTipText(getDeleteAllConfirmationTitle());
            this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
        }

        @Override
        public void run() {
            boolean sure = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), getDeleteAllConfirmationTitle(), getDeleteAllConfirmationQuestion());
            if (sure) {
                clearParameters();
            }
        }
    }

    protected void addParameter(String wsdlValue) {
        File wsdlFile = new File(wsdlValue);

        if (wsdlFile.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(wsdlFile, new String[] { "wsdl" }, false);
            for (File file : files) {
                WsdlRowEntry row = new WsdlRowEntry(parametersListWrapperContent);
                row.createControl();
                createParameterToolbar(row);
                row.setLocation(file.getAbsolutePath());
                String displayName = Path.fromPortableString(file.getAbsolutePath()).lastSegment();
                row.setDisplayName(displayName.substring(0, displayName.indexOf(".")));
                parameters.add(row);

            }
        } else {

            WsdlRowEntry row = new WsdlRowEntry(parametersListWrapperContent);
            if (parameters.isEmpty()) {
                emptyCanvasComposite.dispose();
            }
            row.createControl();
            createParameterToolbar(row);
            row.setLocation(wsdlValue);
            String displayName = Path.fromPortableString(wsdlValue).lastSegment();
            if (displayName.indexOf("?") != -1) {
                row.setDisplayName(displayName.substring(0, displayName.indexOf("?")));
            } else {
                row.setDisplayName(displayName.substring(0, displayName.indexOf(".")));
            }
            parameters.add(row);
        }
        parametersToolbar.setEnabled(true);
        recalculateEditorSize();
        validate();
    }

    private void validate() {
        for (WsdlRowEntry entry : parameters) {
            entry.validate();
        }
    }

    protected void recalculateEditorSize() {
        Composite parent = parametersGroup.getParent().getParent();
        parent.layout(true, true);
        parent.setRedraw(true);
        if (parent instanceof ScrolledComposite) {
            ScrolledComposite cm = (ScrolledComposite) parent;
            Point computeSize = cm.computeSize(-1, -1);
            cm.setMinSize(computeSize.x, computeSize.y);
        }
    }

    protected String getEditorTitle() {
        return EDITOR_TITLE;
    }

    protected String getEmptyParametersMessage() {
        return EMPTY_PARAMETERS_NOTIFICATION;
    }

    protected String getAddParameterButtonLabel() {
        return ADD_PARAMETER_BUTTON_LABEL;
    }

    protected String getDeleteAllConfirmationQuestion() {
        return DELETE_ALL_CONFIRMATION_DIALOG_QUESTION;
    }

    protected String getDeleteAllConfirmationTitle() {
        return DELETE_ALL_CONFIRMATION_DIALOG_TITLE;
    }

    private ToolBar createParameterToolbar(final WsdlRowEntry entry) {
        ToolBar toolbar = new ToolBar(entry.getControl(), SWT.NONE);
        ToolBarManager parametersToolbarManager = new ToolBarManager(toolbar);
        parametersToolbarManager.add(new RemoveParameterAction(entry));
        parametersToolbarManager.update(true);
        return toolbar;
    }

    private class RemoveParameterAction extends Action {

        WsdlRowEntry entry;

        public RemoveParameterAction(WsdlRowEntry entry) {
            super("Remove Parameter");
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
            this.entry = entry;
        }

        @Override
        public void run() {
            entry.dispose();
            parameters.remove(entry);
            if (parameters.isEmpty()) {
                createEmptyCanvasInformation();
            }
            validate();
            recalculateEditorSize();
        }
    }

    public Map<String, String> getWsdlFiles() {
        Map<String, String> wsdlFiles = new HashMap<String, String>();
        for (WsdlRowEntry entry : this.parameters) {
            wsdlFiles.put(entry.getLocation(), entry.getDisplayName());
        }
        return wsdlFiles;
    }
}
