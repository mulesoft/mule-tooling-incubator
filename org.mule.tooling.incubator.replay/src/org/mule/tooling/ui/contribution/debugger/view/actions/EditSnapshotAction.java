package org.mule.tooling.ui.contribution.debugger.view.actions;

import groovy.lang.GroovyShell;

import java.util.concurrent.Callable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.metadata.utils.MetadataUtils;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.service.SnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;
import org.mule.tooling.ui.widgets.util.SilentRunner;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class EditSnapshotAction extends Action {

    private IMuleSnapshotEditor snapshotEditor;
    private SnapshotService service;

    public EditSnapshotAction(IMuleSnapshotEditor snapshotEditor, SnapshotService service) {
        super();
        this.snapshotEditor = snapshotEditor;
        this.service = service;
        setText("Edit Snapshot");
        setToolTipText("Edit Snapshot");
        setEnabled(false);
        setImageDescriptor(ReplayImages.getDebuggerImages().getImageDescriptor(ReplayImages.EDIT));
        snapshotEditor.getSnapshotTable().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setEnabled(!event.getSelection().isEmpty());

            }
        });
    }

    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) snapshotEditor.getSnapshotTable().getSelection();
        final MessageSnapshot snapshot = service.getSnapshot(String.valueOf(selection.getFirstElement()));
        if (snapshot != null) {

            final String appName = snapshot.getAppName();
            final IMuleProject muleProject = CoreUtils.getMuleProject(appName);
            SilentRunner.run(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    if (muleProject != null) {
                        final ClassLoader newClassLoader = MetadataUtils.createMuleClassLoader(muleProject);
                        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                        try {
                            Thread.currentThread().setContextClassLoader(newClassLoader);
                            GroovyShell groovyShell = new GroovyShell();
                            ScriptDialog scriptDialog = new ScriptDialog(Display.getCurrent().getActiveShell());
                            int open = scriptDialog.open();
                            if (open == Window.OK) {
                                groovyShell.setVariable("payload", snapshot.getPayload().createObject());
                                Object payload = groovyShell.evaluate(scriptDialog.getScript());
                                snapshot.getPayload().updateObject(payload);
                            }
                        } finally {
                            Thread.currentThread().setContextClassLoader(contextClassLoader);
                        }
                    }
                    return null;
                }
            }, null);

        }
    }

    private static class ScriptDialog extends TitleAreaDialog {

        private Text scriptText;
        private String script;

        public ScriptDialog(Shell parentShell) {
            super(parentShell);

        }

        @Override
        public void create() {
            super.create();
            // Set the title
            setTitle("Edit the payload");
            // Set the message
            setMessage("Type the groovy script to assign to the payload", IMessageProvider.INFORMATION);

        }

        @Override
        protected boolean isResizable() {
            return true;
        }

        @Override
        protected Point getInitialSize() {

            return super.getInitialSize();
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            scriptText = new Text(parent, SWT.MULTI | SWT.BORDER);

            scriptText.setLayoutData(new GridData(GridData.FILL_BOTH));
            return super.createDialogArea(parent);
        }

        @Override
        protected void okPressed() {
            script = scriptText.getText();
            super.okPressed();
        }

        public String getScript() {
            return script;
        }

    }
}