package org.mule.tooling.ui.contribution.debugger.view.actions;

import groovy.lang.GroovyShell;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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
import org.mule.tooling.core.utils.ProjectClassPathProvider;
import org.mule.tooling.ui.contribution.debugger.controller.ReplayImages;
import org.mule.tooling.ui.contribution.debugger.service.SnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;

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
        IStructuredSelection selection = (IStructuredSelection) snapshotEditor.getSnapshotTable().getSelection();
        MessageSnapshot snapshot = service.getSnapshot(String.valueOf(selection.getFirstElement()));
        if (snapshot != null) {

            String appName = snapshot.getAppName();
            IMuleProject muleProject = CoreUtils.getMuleProject(appName);
            try {
                if (muleProject != null) {
                    ProjectClassPathProvider projectClassPathProvider = new ProjectClassPathProvider();
                    URL[] classPathWithServer = projectClassPathProvider.getClassPathWithServer(muleProject);
                    URLClassLoader newClassLoader = new URLClassLoader(classPathWithServer, this.getClass().getClassLoader());
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

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