package org.mule.tooling.ui.contribution.debugger.controller;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDescriptor;

public class MessageSnapshotLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {        
        MessageSnapshotDescriptor messageSnapshot = (MessageSnapshotDescriptor) element;
        switch (columnIndex) {
        case 0:
            return messageSnapshot.getName();
        default:
            return messageSnapshot.getSnapshot().getAppName();
        }

    }

}
