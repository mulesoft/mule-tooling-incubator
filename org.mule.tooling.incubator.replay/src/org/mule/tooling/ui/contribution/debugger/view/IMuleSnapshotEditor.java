package org.mule.tooling.ui.contribution.debugger.view;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Text;

public interface IMuleSnapshotEditor {

    TableViewer getSnapshotTable();
    
    Text getPreviewText();

}