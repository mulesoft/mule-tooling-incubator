package org.mule.tooling.ui.contribution.debugger.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.core.utils.CoreUtils;
import org.mule.tooling.core.utils.ProjectClassPathProvider;
import org.mule.tooling.ui.contribution.debugger.controller.events.ISnapshotTakenHandler;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotEventTypes;
import org.mule.tooling.ui.contribution.debugger.service.SnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;

import com.mulesoft.mule.debugger.commons.IObjectFactory;
import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class MuleSnapshotsController {

    private IMuleSnapshotEditor editor;
    private EventBus eventBus;
    private SnapshotService service;

    public MuleSnapshotsController(SnapshotService service, IMuleSnapshotEditor editor, EventBus eventBus) {
        super();
        this.service = service;
        this.editor = editor;
        this.eventBus = eventBus;
        bind();
    }

    public void bind() {

        editor.getSnapshotTable().setContentProvider(new ArrayContentProvider());

        editor.getSnapshotTable().setInput(service.getAllDefinedSnapshots().keySet());

        eventBus.registerListener(SnapshotEventTypes.SNAPSHOT_TAKEN, new ISnapshotTakenHandler() {

            @Override
            public void onSnapshotTaken(final String name, MessageSnapshot snapshot) {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        editor.getSnapshotTable().add(name);
                    }
                });

            }
        });

        editor.getSnapshotTable().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
                    String name = (String) structuredSelection.getFirstElement();
                    MessageSnapshot snapshot = service.getSnapshot(name);
                    IObjectFactory payload = snapshot.getPayload();
                    ProjectClassPathProvider projectClassPathProvider = new ProjectClassPathProvider();
                    String appName = snapshot.getAppName();
                    IMuleProject muleProject = CoreUtils.getMuleProject(appName);
                    try {
                        if (muleProject != null) {
                            URL[] classPathWithServer = projectClassPathProvider.getClassPathWithServer(muleProject);
                            URLClassLoader newClassLoader = new URLClassLoader(classPathWithServer, this.getClass().getClassLoader());
                            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                            try {
                                Thread.currentThread().setContextClassLoader(newClassLoader);
                                String previewText = "MP : " + snapshot.getPath() + "\n" + "Payload : " + payload.createObject();
                                editor.getPreviewText().setText(previewText);
                            } finally {
                                Thread.currentThread().setContextClassLoader(contextClassLoader);
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }

                } else {
                    editor.getPreviewText().setText("");
                }

            }
        });
    }
}