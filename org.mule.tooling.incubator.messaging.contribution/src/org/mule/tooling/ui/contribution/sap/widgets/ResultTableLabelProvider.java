package org.mule.tooling.ui.contribution.sap.widgets;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mule.common.metadata.MetaDataKey;



public class ResultTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public ResultTableLabelProvider() {
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        MetaDataKey row = (MetaDataKey) element;
        switch (columnIndex) {
        case 0:
            return row.getId();
        case 1:
            return row.getDisplayName();
        default:
            return "";
        }
    }
}
