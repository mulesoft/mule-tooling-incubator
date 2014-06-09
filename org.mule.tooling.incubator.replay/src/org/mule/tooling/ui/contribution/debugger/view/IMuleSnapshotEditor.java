package org.mule.tooling.ui.contribution.debugger.view;

import org.eclipse.jface.viewers.TableViewer;

public interface IMuleSnapshotEditor {

    TableViewer getSnapshotTable();
    
    IObjectFieldDefinitionEditor getPreviewSnapshot();

}