package org.mule.tooling.ui.contribution.debugger.view.impl;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mule.tooling.core.MuleCorePlugin;
import org.mule.tooling.core.event.CoreEventTypes;
import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.core.event.IMuleProjectChangedListener;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.ui.contribution.debugger.controller.MuleSnapshotsController;
import org.mule.tooling.ui.contribution.debugger.service.MuleDebuggerService;
import org.mule.tooling.ui.contribution.debugger.service.MessageSnapshotService;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;
import org.mule.tooling.ui.contribution.debugger.view.actions.DuplicateSnapshotAction;
import org.mule.tooling.ui.contribution.debugger.view.actions.ReplayFromMessageProcessorAction;
import org.mule.tooling.ui.contribution.debugger.view.actions.ReplayMessageProcessorAction;
import org.mule.tooling.ui.contribution.debugger.view.actions.TakeSnapshotAction;

public class SnapshotsView extends ViewPart {

    private MessageSnapshotService snaphotService;

    public SnapshotsView() {
        super();

    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        this.snaphotService = new MessageSnapshotService(MuleDebuggerService.getDefault().getEventBus());
        MuleCorePlugin.getEventBus().registerListener(CoreEventTypes.ON_MULE_PROJECT_CHANGED, new IMuleProjectChangedListener() {

            @Override
            public void onMuleProjectChanged(IMuleProject muleProject) {
                if (muleProject != null) {
                    snaphotService.loadAllFromProject(muleProject);
                }
            }
        });
        IMuleProject muleProject = MuleCorePlugin.getDesignContext().getMuleProject();
        if (muleProject != null) {
            snaphotService.loadAllFromProject(muleProject);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        final EventBus eventBus = MuleDebuggerService.getDefault().getEventBus();
        final MuleSnapshotsEditorImpl muleSnapshotEditorImpl = new MuleSnapshotsEditorImpl(parent, SWT.NULL);
        new MuleSnapshotsController(snaphotService, muleSnapshotEditorImpl, eventBus);
        createToolBar(muleSnapshotEditorImpl, snaphotService);

    }

    public void createToolBar(IMuleSnapshotEditor editor, MessageSnapshotService snaphotService) {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(new TakeSnapshotAction(snaphotService));
        mgr.add(new ReplayFromMessageProcessorAction(editor, snaphotService));
        mgr.add(new ReplayMessageProcessorAction(editor, snaphotService));
      // mgr.add(new EditSnapshotAction(editor, snaphotService));
        mgr.add(new DuplicateSnapshotAction(editor, snaphotService));

    }

    @Override
    public void setFocus() {

    }

    @Override
    public void dispose() {
        snaphotService.store();
        super.dispose();

    }
}