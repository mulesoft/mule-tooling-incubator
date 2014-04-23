package org.mule.tooling.ui.contribution.munit.editors.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mule.tooling.ui.widgets.action.IModificationListener;
import org.mule.tooling.ui.widgets.table.MapTableComposite.ModificationType;
import org.mule.tooling.ui.widgets.util.WidgetUtils;

/**
 * <p>
 * Visual table to show {@link MockProperties}
 * </p>
 */
public class MockPropertiesTable extends Composite {

    private static final String VALUE_PROPERTY = "Value";
    private static final String NAME_PROPERTY = "Key";
    private static final String TYPE_PROPERTY = "Type";

    private String[] columns;
    private Action delete;
    private Action add;
    private List<MockProperties> data;
    private TableViewer tableViewer;
    private List<IModificationListener> listeners;

    public MockPropertiesTable(Composite parent, int style, String... columns) {
        super(parent, style);
        this.columns = columns;
        this.data = new ArrayList<MockProperties>();
        WidgetUtils widgetUtils = new WidgetUtils();
        this.setLayout(widgetUtils.createNoBorderGridLayout(1, true));
        createControl(this, widgetUtils);
        this.listeners = new ArrayList<IModificationListener>();

    }

    public void addModificationListener(IModificationListener listener) {
        this.listeners.add(listener);
    }

    protected void notifyModified(Object value) {
        for (IModificationListener listener : listeners) {
            listener.onModified(value, value, tableViewer, ModificationType.EDIT);
        }
    }

    protected void createControl(Composite parent, WidgetUtils widgetUtils) {
        ToolBar toolBar = createContolMenu(parent);
        toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        tableViewer = widgetUtils.createTableViewer(parent, columns);
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setColumnProperties(new String[] { NAME_PROPERTY, VALUE_PROPERTY, TYPE_PROPERTY });
        ComboBoxViewerCellEditor comboBoxViewerCellEditor = new ComboBoxViewerCellEditor(tableViewer.getTable(), SWT.NONE);
        comboBoxViewerCellEditor.setContentProvider(new ArrayContentProvider());
        comboBoxViewerCellEditor.setLabelProvider(new LabelProvider());
        comboBoxViewerCellEditor.setInput(new String[] { "INBOUND", "OUTBOUND", "INVOCATION" });
        tableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(tableViewer.getTable(), SWT.NONE), new TextCellEditor(tableViewer.getTable(), SWT.NONE),
                comboBoxViewerCellEditor });
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setLabelProvider(new PairLabelProvider());
        tableViewer.setCellModifier(new PairCellModifier());
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                delete.setEnabled(!event.getSelection().isEmpty());
            }

        });
        tableViewer.setInput(data);
    }

    public void setInputData(List<MockProperties> data) {
        this.data = data;
        tableViewer.setInput(data);
        tableViewer.refresh();
    }

    public List<MockProperties> getInputData() {
        return data;
    }

    private ToolBar createContolMenu(Composite parent) {
        final ToolBar ts = new ToolBar(parent, SWT.NONE);
        final ToolBarManager manager = new ToolBarManager(ts);

        delete = new Action("Delete Selected", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) {

            @Override
            public void run() {
                if (!tableViewer.getSelection().isEmpty()) {
                    IStructuredSelection structuredSelection = (IStructuredSelection) tableViewer.getSelection();
                    Object[] elements = structuredSelection.toArray();
                    data.removeAll(Arrays.asList(elements));
                    tableViewer.remove(elements);
                    notifyModified(elements);
                }
            }
        };
        delete.setEnabled(false);

        add = new Action("Add Variable", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD)) {

            @Override
            public void run() {
                MockProperties element = new MockProperties("Name", "Value", "INVOCATION");
                tableViewer.add(element);
                data.add(element);
                notifyModified(element);
            }
        };
        manager.add(add);
        manager.add(delete);
        manager.update(true);
        return ts;
    }

    private static class PairLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(Object element, int columnIndex) {

            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof MockProperties) {
                switch (columnIndex) {
                case 0:
                    return ((MockProperties) element).getName();
                case 1:
                    return ((MockProperties) element).getValue();
                default:
                    return ((MockProperties) element).getType();
                }
            }
            return null;
        }

    }

    private class PairCellModifier implements ICellModifier {

        @Override
        public boolean canModify(Object element, String property) {

            return true;
        }

        @Override
        public Object getValue(Object element, String property) {
            if (element instanceof MockProperties) {
                if (property.equals(NAME_PROPERTY)) {
                    return ((MockProperties) element).getName();
                } else if (property.equals(VALUE_PROPERTY)) {
                    return ((MockProperties) element).getValue();
                } else {
                    return ((MockProperties) element).getType();
                }
            }
            return null;
        }

        @Override
        public void modify(Object element, String property, Object value) {
            TableItem item = (TableItem) element;
            Object data = item.getData();
            if (data instanceof MockProperties) {
                if (property.equals(NAME_PROPERTY)) {
                    ((MockProperties) data).setName(String.valueOf(value));
                } else if (property.equals(VALUE_PROPERTY)) {
                    ((MockProperties) data).setValue(String.valueOf(value));
                } else {
                    ((MockProperties) data).setType(String.valueOf(value));
                }

                tableViewer.update(data, new String[] { property });
                notifyModified(data);
            }

        }
    }
}