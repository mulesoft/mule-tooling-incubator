package org.mule.tooling.ui.contribution.debugger.view.impl;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;
import org.mule.tooling.ui.widgets.util.WidgetUtils;

public class MuleSnapshotsEditorImpl extends Composite implements IMuleSnapshotEditor {

    private TableViewer snapshotTableViewer;
    private ObjectFieldDefinitionComposite snapshotView;

    public MuleSnapshotsEditorImpl(Composite parent, int style) {
        super(parent, style);
        this.setLayout(new GridLayout());
        this.createControl(this);
    }

    public void createControl(Composite parent) {
        final WidgetUtils widgetUtils = new WidgetUtils();
        snapshotTableViewer = widgetUtils.createTableViewer(parent, "Name","App Name");
        snapshotView = new ObjectFieldDefinitionComposite(parent, SWT.NULL);
        snapshotView.setLayoutData(new GridData(GridData.FILL_BOTH));

    }

    public TableViewer getSnapshotTable() {
        return snapshotTableViewer;
    }

    public ObjectFieldDefinitionComposite getPreviewSnapshot() {
        return snapshotView;
    }

}