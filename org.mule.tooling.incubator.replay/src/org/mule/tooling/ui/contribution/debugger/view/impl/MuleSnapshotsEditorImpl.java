package org.mule.tooling.ui.contribution.debugger.view.impl;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.ui.contribution.debugger.view.IMuleSnapshotEditor;

public class MuleSnapshotsEditorImpl extends Composite implements IMuleSnapshotEditor {

    private TableViewer snapshotTableViewer;
    private Text snapshotView;
    
    

    public MuleSnapshotsEditorImpl(Composite parent, int style) {
        super(parent, style);
        this.setLayout(new GridLayout());
        this.createControl(this);
    }

    public void createControl(Composite parent) {
        snapshotTableViewer = createBreakpointTable(parent);
    
    }

    public TableViewer createBreakpointTable(final Composite parent) {
        final TableViewer tableViewer = new TableViewer(parent);
        tableViewer.getTable().setLinesVisible(true);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

        // 2nd column with task Description
        final TableColumn idColumn;
        idColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT, 0);
        idColumn.setText("Name");


        parent.addControlListener(new ControlAdapter() {

            public void controlResized(ControlEvent e) {
                Rectangle area = parent.getClientArea();
                Point preferredSize = tableViewer.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
                int scrollBarWidth = 13;
                int width = area.width - 2 * tableViewer.getTable().getBorderWidth() - scrollBarWidth;
                if (preferredSize.y > area.height + tableViewer.getTable().getHeaderHeight()) {
                    // Subtract the scrollbar width from the total column width
                    // if a vertical scrollbar will be required
                    Point vBarSize = tableViewer.getTable().getVerticalBar().getSize();
                    width -= vBarSize.x;
                }
                idColumn.setWidth(width);
                tableViewer.getTable().setSize(area.width, area.height);

            }
        });
        
        snapshotView = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        snapshotView.setLayoutData(new GridData(GridData.FILL_BOTH));

        return tableViewer;
    }

    
    public TableViewer getSnapshotTable() {
        return snapshotTableViewer;
    }
    
    public Text getPreviewText(){
        return snapshotView;
    }

   
}